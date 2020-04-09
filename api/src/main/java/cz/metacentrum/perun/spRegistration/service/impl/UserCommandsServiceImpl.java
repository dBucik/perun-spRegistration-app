package cz.metacentrum.perun.spRegistration.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.configs.Config;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeCategory;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.ActiveRequestExistsException;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.RequestSignature;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.MailsService;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.UserCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.CodeNotStoredException;
import cz.metacentrum.perun.spRegistration.service.exceptions.ExpiredCodeException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.MalformedCodeException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.metacentrum.perun.spRegistration.service.MailsService.REQUEST_CREATED;
import static cz.metacentrum.perun.spRegistration.service.MailsService.REQUEST_MODIFIED;
import static cz.metacentrum.perun.spRegistration.service.MailsService.REQUEST_SIGNED;

/**
 * Implementation of UserCommandsService.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
@SuppressWarnings("Duplicates")
@Service("userService")
public class UserCommandsServiceImpl implements UserCommandsService {

	private static final Logger log = LoggerFactory.getLogger(UserCommandsServiceImpl.class);

	private static final String REQUEST_ID_KEY = "requestId";
	private static final String FACILITY_ID_KEY = "facilityId";
	private static final String CREATED_AT_KEY = "createdAt";
	private static final String REQUESTED_MAIL_KEY = "requestedMail";

	private final RequestManager requestManager;
	private final PerunConnector perunConnector;
	private final AppConfig appConfig;
	private final Config config;
	private final Properties messagesProperties;
	
	@Autowired
	private MailsService mailsService;

	@Autowired
	public UserCommandsServiceImpl(RequestManager requestManager, PerunConnector perunConnector, Config config,
								   AppConfig appConfig, Properties messagesProperties) {
		this.requestManager = requestManager;
		this.perunConnector = perunConnector;
		this.appConfig = appConfig;
		this.config = config;
		this.messagesProperties = messagesProperties;
	}

	@Override
	public Long createRegistrationRequest(Long userId, List<PerunAttribute> attributes) throws InternalErrorException {
		log.trace("createRegistrationRequest(userId: {}, attributes: {})", userId, attributes);

		if (Utils.checkParamsInvalid(userId, attributes)) {
			log.error("Wrong parameters passed: (userId: {}, attributes: {})", userId, attributes);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Request req = null;
		try {
			req = createRequest(null, userId, RequestAction.REGISTER_NEW_SP, attributes);
		} catch (ActiveRequestExistsException e) {
			//this cannot happen as the registration is for new service and thus facility id will be always null
		}

		if (req == null || req.getReqId() == null) {
			log.error("Could not create request");
			throw new InternalErrorException("Could not create request");
		}

		mailsService.notifyUser(req, REQUEST_CREATED);
		mailsService.notifyAppAdmins(req, REQUEST_CREATED);

		log.trace("createRegistrationRequest returns: {}", req.getReqId());
		return req.getReqId();
	}

	@Override
	public Long createFacilityChangesRequest(Long facilityId, Long userId, List<PerunAttribute> attributes)
			throws UnauthorizedActionException, ConnectorException, InternalErrorException, ActiveRequestExistsException
	{
		log.trace("createFacilityChangesRequest(facility: {}, userId: {}, attributes: {})", facilityId, userId, attributes);

		if (Utils.checkParamsInvalid(facilityId, userId, attributes)) {
			log.error("Wrong parameters passed: (facilityId: {}, userId: {}, attributes: {})",
					facilityId, userId, attributes);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! isFacilityAdmin(facilityId, userId)) {
			log.error("User is not registered as facility admin, cannot create request");
			throw new UnauthorizedActionException("User is not registered as facility admin, cannot create request");
		}

		Facility facility = perunConnector.getFacilityById(facilityId);
		if (facility == null) {
			log.error("Could not fetch facility for facilityId: {}", facilityId);
			throw new InternalErrorException("Could not fetch facility for facilityId: " + facilityId);
		}

		boolean attrsChanged = false;
		List<String> attrNames = attributes.stream().map(PerunAttribute::getFullName).collect(Collectors.toList());
		Map<String, PerunAttribute> actualAttrs = perunConnector.getFacilityAttributes(facilityId, attrNames);
		for (PerunAttribute a: attributes) {
			if (actualAttrs.containsKey(a.getFullName())) {
				PerunAttribute actualA = actualAttrs.get(a.getFullName());
				if (!Objects.equals(actualA, a)) {
					attrsChanged = true;
					break;
				}
			}
		}

		if (!attrsChanged) {
			return null;
		}

		Request req = createRequest(facilityId, userId, RequestAction.UPDATE_FACILITY, attributes);
		if (req.getReqId() == null) {
			log.error("Could not create request");
			throw new InternalErrorException("Could not create request");
		}

		mailsService.notifyUser(req, REQUEST_CREATED);
		mailsService.notifyAppAdmins(req, REQUEST_CREATED);

		log.trace("createFacilityChangesRequest returns: {}", req.getReqId());
		return req.getReqId();
	}

	@Override
	public Long createRemovalRequest(Long userId, Long facilityId)
			throws UnauthorizedActionException, ConnectorException, InternalErrorException, ActiveRequestExistsException
	{
		log.trace("createRemovalRequest(userId: {}, facilityId: {})", userId, facilityId);

		if (Utils.checkParamsInvalid(userId, facilityId)) {
			log.error("Wrong parameters passed: (facilityId: {}, userId: {})", facilityId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! isFacilityAdmin(facilityId, userId)) {
			log.error("User is not registered as facility admin, cannot create request");
			throw new UnauthorizedActionException("User is not registered as facility admin, cannot create request");
		}

		List<String> attrsToFetch = new ArrayList<>(appConfig.getPerunAttributeDefinitionsMap().keySet());
		Map<String, PerunAttribute> attrs = perunConnector.getFacilityAttributes(facilityId, attrsToFetch);
		boolean isOidc = ServiceUtils.isOidcAttributes(attrs, appConfig.getEntityIdAttribute());
		List<String> keptAttrs = getAttrsToKeep(isOidc);
		List<PerunAttribute> facilityAttributes = ServiceUtils.filterFacilityAttrs(attrs, keptAttrs);

		Request req = createRequest(facilityId, userId, RequestAction.DELETE_FACILITY, facilityAttributes);
		if (req.getReqId() == null) {
			log.error("Could not create request");
			throw new InternalErrorException("Could not create request");
		}

		mailsService.notifyUser(req, REQUEST_CREATED);
		mailsService.notifyAppAdmins(req, REQUEST_CREATED);

		log.trace("createRemovalRequest returns: {}", req.getReqId());
		return req.getReqId();
	}

	@Override
	public boolean updateRequest(Long requestId, Long userId, List<PerunAttribute> attributes)
			throws UnauthorizedActionException, InternalErrorException {
		log.trace("updateRequest(requestId: {}, userId: {}, attributes: {})", requestId, userId, attributes);
		if (requestId == null || userId == null || attributes == null) {
			log.error("Illegal input - requestId: {}, userId: {}, attributes: {}", requestId, userId, attributes);
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId +
					", attributes: " + attributes);
		}

		Request request = requestManager.getRequestById(requestId);
		if (request == null) {
			log.error("Could not retrieve request for id: {}", requestId);
			throw new InternalErrorException("Could not retrieve request for id: " + requestId);
		} else if (! isAdminInRequest(request.getReqUserId(), userId)) {
			log.error("User is not registered as admin in request, cannot update it");
			throw new UnauthorizedActionException("User is not registered as admin in request, cannot update it");
		}

		log.info("Updating request");
		request.updateAttributes(attributes, true, appConfig);

		request.setStatus(RequestStatus.WAITING_FOR_APPROVAL);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		boolean requestUpdated = requestManager.updateRequest(request);

		mailsService.notifyUser(request, REQUEST_MODIFIED);
		mailsService.notifyAppAdmins(request, REQUEST_MODIFIED);

		log.trace("updateRequest returns: {}", requestUpdated);
		return requestUpdated;
	}

	@Override
	public Long requestMoveToProduction(Long facilityId, Long userId, List<String> authorities)
			throws UnauthorizedActionException, InternalErrorException, ConnectorException, ActiveRequestExistsException,
			BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException
	{
		log.trace("requestMoveToProduction(facilityId: {}, userId: {}, authorities: {})", facilityId, userId, authorities);

		if (Utils.checkParamsInvalid(facilityId, userId)) {
			log.error("Wrong parameters passed: (facilityId: {}, userId: {})", facilityId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! isFacilityAdmin(facilityId, userId)) {
			log.error("User is not registered as admin for facility, cannot ask for moving to production");
			throw new UnauthorizedActionException("User is not registered as admin for facility, cannot ask for moving to production");
		}

		Facility fac = getDetailedFacility(facilityId, userId, true);
		if (fac == null) {
			log.error("Could not retrieve facility for id: {}", facilityId);
			throw new InternalErrorException("Could not retrieve facility for id: " + facilityId);
		}

		List<PerunAttribute> filteredAttributes = filterNotNullAttributes(fac);

		Request req = createRequest(facilityId, userId, RequestAction.MOVE_TO_PRODUCTION, filteredAttributes);

		Map<String, String> authoritiesCodesMap = generateCodesForAuthorities(req, authorities);
		Map<String, String> authoritiesLinksMap = generateLinksForAuthorities(authoritiesCodesMap, req);

		mailsService.notifyUser(req, REQUEST_CREATED);
		mailsService.notifyAppAdmins(req, REQUEST_CREATED);
		mailsService.notifyAuthorities(req, authoritiesLinksMap);

		log.trace("requestMoveToProduction returns: {}", req.getReqId());
		return req.getReqId();
	}

	@Override
	public Request getRequestDetailsForSignature(String code)
			throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, MalformedCodeException,
			ExpiredCodeException, InternalErrorException
	{
		log.trace("getRequestDetailsForSignature({})", code);

		if (Utils.checkParamsInvalid(code)) {
			log.error("Wrong parameters passed: (code: {})", code);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		JsonNode decryptedCode = decryptRequestCode(code);
		boolean isExpired = isExpiredCode(decryptedCode);

		if (isExpired) {
			log.error("User trying to approve request with expired code: {}", decryptedCode);
			throw new ExpiredCodeException("Code has expired");
		}

		Long requestId = decryptedCode.get(REQUEST_ID_KEY).asLong();
		log.debug("Fetching request for id: {}", requestId);
		Request request = requestManager.getRequestById(requestId);

		if (request == null) {
			log.error("Cannot find request from code");
			throw new InternalErrorException("Cannot find request from code");
		}

		log.trace("getRequestDetailsForSignature returns: {}", request);
		return request;
	}

	@Override
	public boolean signTransferToProduction(User user, String code, boolean approved)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, MalformedCodeException,
			ExpiredCodeException, InternalErrorException
	{
		log.trace("signTransferToProduction(user: {}, code: {})", user, code);

		if (Utils.checkParamsInvalid(user, code)) {
			log.error("Wrong parameters passed: (user: {}, code: {})", user, code);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		JsonNode decryptedCode = decryptRequestCode(code);
		boolean isExpired = isExpiredCode(decryptedCode);

		if (isExpired) {
			log.error("User trying to approve request with expired code: {}", decryptedCode);
			throw new ExpiredCodeException("Code has expired");
		}

		Long requestId = decryptedCode.get(REQUEST_ID_KEY).asLong();
		boolean signed = requestManager.addSignature(requestId, user.getId(), user.getName(), approved, code);
		Request req = requestManager.getRequestById(requestId);

		log.info("Sending mail notification");
		mailsService.notifyUser(req, REQUEST_SIGNED);
		mailsService.notifyAppAdmins(req, REQUEST_SIGNED);

		log.trace("signTransferToProduction returns: {}", signed);
		return signed;
	}

	@Override
	public List<RequestSignature> getApprovalsOfProductionTransfer(Long requestId, Long userId)
			throws UnauthorizedActionException, InternalErrorException
	{
		log.trace("getApprovalsOfProductionTransfer(requestId: {}, userId: {})", requestId, userId);

		if (Utils.checkParamsInvalid(requestId, userId)) {
			log.error("Wrong parameters passed: (requestId: {}, userId: {})" , requestId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Request request = requestManager.getRequestById(requestId);
		if (request == null) {
			log.error("Could not retrieve request for id: {}", requestId);
			throw new InternalErrorException(Utils.GENERIC_ERROR_MSG);
		} else if (!appConfig.isAppAdmin(userId) && !isAdminInRequest(request.getReqUserId(), userId)) {
			log.error("User is not authorized to view approvals for request: {}", requestId);
			throw new UnauthorizedActionException(Utils.GENERIC_ERROR_MSG);
		}

		List<RequestSignature> result = requestManager.getRequestSignatures(requestId);

		log.trace("getApprovalsOfProductionTransfer returns: {}", result);
		return result;
	}

	@Override
	public Request getDetailedRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, InternalErrorException
	{
		log.trace("getDetailedRequest(requestId: {}, userId: {})", requestId, userId);

		if (Utils.checkParamsInvalid(requestId, userId)) {
			log.error("Wrong parameters passed: (requestId: {}, userId: {})", requestId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Request request = requestManager.getRequestById(requestId);
		if (request == null) {
			log.error("Could not retrieve request for id: {}", requestId);
			throw new InternalErrorException("Could not retrieve request for id: " + requestId);
		} else if (!appConfig.isAppAdmin(userId) && !isAdminInRequest(request.getReqUserId(), userId)) {
			log.error("User cannot view request, user is not requester nor appAdmin");
			throw new UnauthorizedActionException("User cannot view request, user is not a requester");
		}

		log.trace("getDetailedRequest returns: {}", request);
		return request;
	}

	@Override
	public Facility getDetailedFacility(Long facilityId, Long userId, boolean checkAdmin)
			throws UnauthorizedActionException, ConnectorException, InternalErrorException
	{
		log.trace("getDetailedFacility(facilityId: {}, userId: {}, checkAdmin: {})", facilityId, userId, checkAdmin);

		if (Utils.checkParamsInvalid(facilityId, userId)) {
			log.error("Wrong parameters passed: (facilityId: {}, userId: {})", facilityId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (checkAdmin && !isFacilityAdmin(facilityId, userId)) {
			log.error("User cannot view facility, user is not an admin");
			throw new UnauthorizedActionException("User cannot view facility, user is not an admin");
		}

		Facility facility = perunConnector.getFacilityById(facilityId);
		if (facility == null) {
			log.error("Could not retrieve facility for id: {}", facilityId);
			throw new InternalErrorException("Could not retrieve facility for id: " + facilityId);
		}

		Long activeRequestId = requestManager.getActiveRequestIdByFacilityId(facilityId);
		facility.setActiveRequestId(activeRequestId);

		List<String> attrsToFetch = new ArrayList<>(appConfig.getPerunAttributeDefinitionsMap().keySet());
		Map<String, PerunAttribute> attrs = perunConnector.getFacilityAttributes(facilityId, attrsToFetch);
		boolean isOidc = ServiceUtils.isOidcAttributes(attrs, appConfig.getEntityIdAttribute());
		List<String> keptAttrs = getAttrsToKeep(isOidc);
		List<PerunAttribute> filteredAttributes = ServiceUtils.filterFacilityAttrs(attrs, keptAttrs);
		Map<AttributeCategory, Map<String, PerunAttribute>> facilityAttributes = convertToStruct(filteredAttributes, appConfig);
		facility.setAttributes(facilityAttributes);

		boolean inTest = attrs.get(appConfig.getIsTestSpAttribute()).valueAsBoolean();
		facility.setTestEnv(inTest);

		Map<String, PerunAttribute> protocolAttrs = perunConnector.getFacilityAttributes(facilityId, Arrays.asList(
				appConfig.getIsOidcAttributeName(), appConfig.getIsSamlAttributeName(), appConfig.getMasterProxyIdentifierAttribute()));
		facility.setOidc(protocolAttrs.get(appConfig.getIsOidcAttributeName()).valueAsBoolean());
		facility.setSaml(protocolAttrs.get(appConfig.getIsSamlAttributeName()).valueAsBoolean());

		PerunAttribute proxyAttrs = protocolAttrs.get(appConfig.getMasterProxyIdentifierAttribute());
		boolean canBeEdited = appConfig.getMasterProxyIdentifierAttributeValue().equals(proxyAttrs.valueAsString());
		facility.setEditable(canBeEdited);

		log.trace("getDetailedFacility returns: {}", facility);
		return facility;
	}

	@Override
	public Facility getDetailedFacilityWithInputs(Long facilityId, Long userId)
			throws UnauthorizedActionException, ConnectorException, InternalErrorException
	{
		log.trace("getDetailedFacilityWithInputs(facilityId: {}, userId: {})", facilityId, userId);

		if (Utils.checkParamsInvalid(facilityId, userId)) {
			log.error("Wrong parameters passed: (facilityId: {}, userId: {})", facilityId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Facility facility = getDetailedFacility(facilityId, userId, true);
		if (facility == null || facility.getAttributes() == null) {
			log.error("Could not fetch facility for id: {}", facilityId);
			throw new InternalErrorException("Could not fetch facility for id: " + facilityId);
		}

		facility.getAttributes()
				.values()
				.forEach(
						attrsInCategory -> attrsInCategory.values()
								.forEach(attr -> attr.setInput(config.getInputMap().get(attr.getFullName())))
				);

		log.trace("getDetailedFacilityWithInputs() returns: {}", facility);
		return facility;
	}

	@Override
	public List<Request> getAllRequestsUserCanAccess(Long userId) throws ConnectorException {
		log.trace("getAllRequestsUserCanAccess({})", userId);

		if (Utils.checkParamsInvalid(userId)) {
			log.error("Wrong parameters passed: (userId: {})", userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Set<Request> requests = new HashSet<>();

		List<Request> userRequests = requestManager.getAllRequestsByUserId(userId);
		if (userRequests != null && !userRequests.isEmpty()) {
			requests.addAll(userRequests);
		}

		Set<Long> facilityIdsWhereUserIsAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);
		if (facilityIdsWhereUserIsAdmin != null && !facilityIdsWhereUserIsAdmin.isEmpty()) {
			List<Request> facilitiesRequests = requestManager.getAllRequestsByFacilityIds(facilityIdsWhereUserIsAdmin);
			if (facilitiesRequests != null && !facilitiesRequests.isEmpty()) {
				requests.addAll(facilitiesRequests);
			}
		}

		List<Request> unique = new ArrayList<>(requests);
		log.trace("getAllRequestsUserCanAccess returns: {}", unique);
		return unique;
	}

	@Override
	public List<Facility> getAllFacilitiesWhereUserIsAdmin(Long userId) throws ConnectorException {
		log.trace("getAllFacilitiesWhereUserIsAdmin({})", userId);

		if (Utils.checkParamsInvalid(userId)) {
			log.error("Wrong parameters passed: (userId: {})", userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		List<Facility> proxyFacilities = perunConnector.getFacilitiesByProxyIdentifier(
				appConfig.getProxyIdentifierAttribute(), appConfig.getProxyIdentifierAttributeValue());
		Map<Long, Facility> proxyFacilitiesMap = ServiceUtils.transformListToMapFacilities(proxyFacilities);
		if (proxyFacilitiesMap == null || proxyFacilitiesMap.isEmpty()) {
			return new ArrayList<>();
		}

		List<Facility> userFacilities = perunConnector.getFacilitiesWhereUserIsAdmin(userId);
		if (userFacilities == null || userFacilities.isEmpty()) {
			return new ArrayList<>();
		}

		List<Facility> testFacilities = perunConnector.getFacilitiesByAttribute(
				appConfig.getIsTestSpAttribute(), "true");
		Map<Long, Facility> testFacilitiesMap = ServiceUtils.transformListToMapFacilities(testFacilities);
		if (testFacilitiesMap == null) {
			testFacilitiesMap = new HashMap<>();
		}

		List<Facility> oidcFacilities = perunConnector.getFacilitiesByAttribute(
				appConfig.getIsOidcAttributeName(), "true");
		Map<Long, Facility> oidcFacilitiesMap = ServiceUtils.transformListToMapFacilities(oidcFacilities);

		List<Facility> samlFacilities = perunConnector.getFacilitiesByAttribute(
				appConfig.getIsSamlAttributeName(), "true");
		Map<Long, Facility> samlFacilitiesMap = ServiceUtils.transformListToMapFacilities(samlFacilities);

		List<Facility> filteredFacilities = new ArrayList<>();

		for (Facility f : userFacilities) {
			if (proxyFacilitiesMap.containsKey(f.getId())) {
				filteredFacilities.add(f);

				f.setOidc(oidcFacilitiesMap.containsKey(f.getId()));
				f.setSaml(samlFacilitiesMap.containsKey(f.getId()));
				f.setTestEnv(testFacilitiesMap.containsKey(f.getId()));
			}
		}

		log.trace("getAllFacilitiesWhereUserIsAdmin returns: {}", filteredFacilities);
		return filteredFacilities;
	}

	@Override
	public boolean addAdminsNotify(User user, Long facilityId, List<String> admins)
			throws UnauthorizedActionException, ConnectorException, BadPaddingException, InvalidKeyException,
			IllegalBlockSizeException, UnsupportedEncodingException, InternalErrorException
	{
		log.trace("addAdminsNotify(user: {}, facilityId: {}, admins: {}", user, facilityId, admins);

		if (Utils.checkParamsInvalid(user, facilityId, admins) || admins.isEmpty()) {
			log.error("Wrong parameters passed (user: {}, facilityId: {}, admins: {})", user, facilityId, admins);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! isFacilityAdmin(facilityId, user.getId())) {
			log.error("User cannot request adding admins to facility, user is not an admin");
			throw new UnauthorizedActionException("User cannot request adding admins to facility, user is not an admin");
		}

		Facility facility = perunConnector.getFacilityById(facilityId);
		if (facility == null) {
			log.error("Could not fetch facility for id: {}", facilityId);
			throw new InternalErrorException("Could not find facility for id: " + facilityId);
		}

		Map<String, String> adminCodeMap = generateCodesForAdmins(admins, facilityId);
		Map<String, String> adminLinkMap = generateLinksForAdmins(facilityId, adminCodeMap);
		boolean successful = mailsService.notifyNewAdmins(facility, adminLinkMap);

		log.debug("addAdminsNotify returns: {}", successful);
		return successful;
	}

	@Override
	public boolean confirmAddAdmin(User user, String code)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, MalformedCodeException,
			ExpiredCodeException, ConnectorException, InternalErrorException, CodeNotStoredException {
		log.debug("confirmAddAdmin({})", code);

		if (Utils.checkParamsInvalid(user, code)) {
			log.error("Wrong parameters passed: (user: {}, code: {})", user, code);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		JsonNode decrypted = decryptAddAdminCode(code);
		boolean isValid = (!isExpiredCode(decrypted) && validateCode(code));

		if (! isValid) {
			log.error("User trying to become admin with invalid code: {}", decrypted);
			throw new ExpiredCodeException("Code is invalid");
		}

		Long facilityId = decrypted.get(FACILITY_ID_KEY).asLong();
		boolean added = perunConnector.addFacilityAdmin(facilityId, user.getId());
		boolean deletedCode = requestManager.deleteUsedCode(code);
		boolean successful = (added && deletedCode);

		if (!successful) {
			log.error("some operations failed: added: {}, deletedCode: {}", added, deletedCode);
		} else {
			log.info("Admin added, code deleted");
		}

		log.debug("confirmAddAdmin returns: {}", successful);
		return added;
	}

	@Override
	public boolean rejectAddAdmin(User user, String code)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, MalformedCodeException,
			ExpiredCodeException, InternalErrorException, CodeNotStoredException {
		log.debug("rejectAddAdmin(user: {}, code: {})", user, code);

		if (Utils.checkParamsInvalid(user, code)) {
			log.error("Wrong parameters passed: (user: {}, code: {})", user, code);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		JsonNode decrypted = decryptAddAdminCode(code);
		boolean isValid = (!isExpiredCode(decrypted) && validateCode(code));

		if (! isValid) {
			log.error("User trying to become reject becoming with invalid code: {}", decrypted);
			throw new ExpiredCodeException("Code is invalid");
		}

		boolean deletedCode = requestManager.deleteUsedCode(code);

		log.debug("rejectAddAdmin() returns: {}", deletedCode);
		return deletedCode;
	}

	@Override
	public boolean validateCode(String code) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			MalformedCodeException, ExpiredCodeException, CodeNotStoredException {
		log.trace("validateCode({})", code);

		if (Utils.checkParamsInvalid(code)) {
			log.error("Wrong parameters passed: (code: {})", code);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (isExpiredCode(decryptRequestCode(code))) {
			throw new ExpiredCodeException("Code expired");
		} else if (!requestManager.validateCode(code)) {
			throw new CodeNotStoredException("Code not found");
		}
		boolean isValid = (!isExpiredCode(decryptRequestCode(code)) && requestManager.validateCode(code));

		log.trace("validateCode() returns: {}", isValid);
		return isValid;
	}

	@Override
	public Map<String, PerunAttribute> getOidcDetails(Long facilityId, Long id) throws ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		log.trace("getOidcClientIdAndSercret({}, {})", facilityId, id);
		Set<String> attrNames = config.getOidcInputs().stream().map(AttrInput::getName).collect(Collectors.toSet());
		attrNames.add(appConfig.getClientIdAttribute());
		attrNames.add(appConfig.getClientSecretAttribute());

		Map<String, PerunAttribute> attrs = perunConnector.getFacilityAttributes(facilityId, new ArrayList<>(attrNames));
		PerunAttribute clientSecret = attrs.get(appConfig.getClientSecretAttribute());
		String value = ServiceUtils.decrypt(clientSecret.valueAsString(), appConfig.getSecret());
		clientSecret.setValue(value);

		log.trace("getOidcClientIdAndSecret() returns: {}", attrs);
		return attrs;
	}

	@Override
	public Map<String, PerunAttribute> getSamlDetails(Long facilityId, Long id) throws ConnectorException {
		log.trace("getOidcClientIdAndSercret({}, {})", facilityId, id);
		Set<String> attrNames = config.getSamlInputs().stream().map(AttrInput::getName).collect(Collectors.toSet());


		Map<String, PerunAttribute> attrs = perunConnector.getFacilityAttributes(facilityId, new ArrayList<>(attrNames));

		log.trace("getOidcClientIdAndSercret() returns: {}", attrs);
		return attrs;
  }

	/* PRIVATE METHODS */

	private Request createRequest(Long facilityId, Long userId, RequestAction action, List<PerunAttribute> attributes)
			throws InternalErrorException, ActiveRequestExistsException
	{
		log.trace("createRequest(facilityId: {}, userId: {}, action: {}, attributes: {})",
				facilityId, userId, action, attributes);

		if (Utils.checkParamsInvalid(userId, action, attributes)) {
			log.error("Wrong parameters passed: (userId: {}, action: {}, attributes: {})", userId, action, attributes);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Request request = new Request();
		request.setFacilityId(facilityId);
		request.setStatus(RequestStatus.WAITING_FOR_APPROVAL);
		request.setAction(action);
		request.updateAttributes(attributes, true, appConfig);
		request.setReqUserId(userId);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		log.debug("creating request in DB");
		Long requestId = requestManager.createRequest(request);
		if (requestId == null) {
			log.error("Could not create request: {} in DB", request);
			throw new InternalErrorException("Could not create request in DB");
		}

		request.setReqId(requestId);

		log.debug("createRequest returns: {}", request);
		return request;
	}

	private boolean isFacilityAdmin(Long facilityId, Long userId) throws ConnectorException {
		log.trace("isFacilityAdmin(facilityId: {}, userId: {})", facilityId, userId);

		if (Utils.checkParamsInvalid(facilityId, userId)) {
			log.error("Wrong parameters passed: (facility: {}, userId: {})", facilityId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		boolean result = false;

		if (appConfig.isAppAdmin(userId)) {
			result = true;
		} else {
			Set<Long> whereAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);

			if (whereAdmin == null || whereAdmin.isEmpty()) {
				log.debug("isFacilityAdmin returns: {}", false);
				return false;
			}

			result = whereAdmin.contains(facilityId);
		}

		log.debug("isFacilityAdmin returns:  {}", result);
		return result;
	}

	private boolean isAdminInRequest(Long reqUserId, Long userId) {
		log.debug("isAdminInRequest(reqUserId: {}, userId: {})", reqUserId, userId);

		if (Utils.checkParamsInvalid(reqUserId, userId)) {
			log.error("Wrong parameters passed: (reqUserId: {}, userId: {})", reqUserId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		boolean res = reqUserId.equals(userId) || appConfig.isAppAdmin(userId);

		log.debug("isAdminInRequest returns: {}", res);
		return res;
	}

	private JsonNode decryptRequestCode(String code) throws InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, MalformedCodeException
	{
		log.trace("decryptRequestCode({})", code);

		if (Utils.checkParamsInvalid(code)) {
			log.error("Wrong parameters passed: (code: {})", code);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		String decrypted = ServiceUtils.decrypt(code, appConfig.getSecret());

		try {
			JsonNode decryptedAsJson = new ObjectMapper().readTree(decrypted);

			log.trace("decryptRequestCode() returns: {}", decryptedAsJson);
			return decryptedAsJson;
		} catch (JsonProcessingException e) {
			throw new MalformedCodeException();
		}
	}

	private String createRequestCode(Long requestId, Long facilityId, String requestedMail)
			throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException
	{
		log.trace("createRequestCode(requestId: {}, facilityId: {}, requestedMail: {})", requestId, facilityId, requestedMail);

		if (Utils.checkParamsInvalid(requestId, facilityId, requestedMail)) {
			log.error("Wrong parameters passed: (requestId: {}, facilityId: {}, requestedMail: {})",
					requestId, facilityId, requestedMail);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		ObjectNode object = JsonNodeFactory.instance.objectNode();
		object.put(REQUEST_ID_KEY, requestId);
		object.put(FACILITY_ID_KEY, facilityId);
		object.put(CREATED_AT_KEY, LocalDateTime.now().toString());
		object.put(REQUESTED_MAIL_KEY, requestedMail);

		String strToEncrypt = object.toString();
		String encoded = ServiceUtils.encrypt(strToEncrypt, appConfig.getSecret());

		log.trace("createRequestCode() returns: {}", encoded);
		return encoded;
	}

	private JsonNode decryptAddAdminCode(String code)
			throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, MalformedCodeException
	{
		log.trace("decryptAddAdminCode({})", code);

		if (Utils.checkParamsInvalid(code)) {
			log.error("Wrong parameters passed: (code: {})", code);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		String decrypted = ServiceUtils.decrypt(code, appConfig.getSecret());

		try {
			JsonNode decryptedAsJson = new ObjectMapper().readTree(decrypted);
			log.trace("decryptAddAdminCode() returns: {}", decryptedAsJson);
			return decryptedAsJson;
		} catch (JsonProcessingException e) {
			throw new MalformedCodeException();
		}
	}

	private String createAddAdminCode(Long facilityId, String requestedMail)
			throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException
	{
		log.trace("createRequestCode(facilityId: {}, requestedMail: {})", facilityId, requestedMail);

		if (Utils.checkParamsInvalid(facilityId, requestedMail)) {
			log.error("Wrong parameters passed: (facilityId: {}, requestedMail: {})", facilityId, requestedMail);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		ObjectNode object = JsonNodeFactory.instance.objectNode();
		object.put(FACILITY_ID_KEY, facilityId);
		object.put(CREATED_AT_KEY, LocalDateTime.now().toString());
		object.put(REQUESTED_MAIL_KEY, requestedMail);

		String strToEncrypt = object.toString();
		String encoded = ServiceUtils.encrypt(strToEncrypt, appConfig.getSecret());

		log.trace("createRequestCode() returns: {}", encoded);
		return encoded;
	}

	private boolean isExpiredCode(JsonNode codeInJson) {
		log.trace("isExpiredCode({})", codeInJson);

		if (Utils.checkParamsInvalid(codeInJson)) {
			log.error("Wrong parameters passed: (codeInJson: {})", codeInJson);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		long daysValidPeriod = appConfig.getConfirmationPeriodDays();
		long hoursValidPeriod = appConfig.getConfirmationPeriodHours();

		LocalDateTime createdAt = LocalDateTime.parse(codeInJson.get(CREATED_AT_KEY).textValue());
		LocalDateTime validUntil = createdAt.plusDays(daysValidPeriod).plusHours(hoursValidPeriod);

		boolean isExpired = LocalDateTime.now().isAfter(validUntil);

		log.trace("isExpiredCode() returns: {}", isExpired);
		return isExpired;
	}

	private List<PerunAttribute> filterNotNullAttributes(Facility facility) {
		log.trace("filterNotNullAttributes({})", facility);

		if (Utils.checkParamsInvalid(facility)) {
			log.error("Wrong parameters passed: (facility: {})", facility);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		List<PerunAttribute> filteredAttributes = new ArrayList<>();
		facility.getAttributes()
				.values()
				.forEach(
						attrsInCategory -> attrsInCategory.values()
						.stream()
						.filter(attr -> attr.getValue() != null)
						.forEach(filteredAttributes::add)
				);

		log.trace("filterNotNullAttributes() returns: {}", filteredAttributes);
		return filteredAttributes;
	}

	private List<String> getAttrsToKeep(boolean isOidc) {
		List<String> keptAttrs = new ArrayList<>();

		keptAttrs.addAll(config.getServiceInputs().stream()
				.map(AttrInput::getName)
				.collect(Collectors.toList()));

		keptAttrs.addAll(config.getOrganizationInputs().stream()
				.map(AttrInput::getName)
				.collect(Collectors.toList()));

		keptAttrs.addAll(config.getMembershipInputs().stream()
				.map(AttrInput::getName)
				.collect(Collectors.toList()));

		if (isOidc) {
			keptAttrs.addAll(config.getOidcInputs()
					.stream()
					.map(AttrInput::getName)
					.collect(Collectors.toList())
			);
			keptAttrs.add(appConfig.getClientIdAttribute());
			keptAttrs.add(appConfig.getClientSecretAttribute());
		} else {
			keptAttrs.addAll(config.getSamlInputs()
					.stream()
					.map(AttrInput::getName)
					.collect(Collectors.toList())
			);
		}

		return keptAttrs;
	}

	private Map<String, String> generateCodesForAuthorities(Request request, List<String> authorities)
			throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InternalErrorException
	{
		log.trace("generateCodesForAuthorities(request: {}, authorities: {})", request, authorities);

		List<String> emails = new ArrayList<>();
		if (Utils.checkParamsInvalid(request, authorities)) {
			log.error("Wrong parameters passed: (request: {}, authorities: {})", request, authorities);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		if (authorities == null || authorities.isEmpty()) {
			String prop = messagesProperties.getProperty("moveToProduction.authorities");
			emails = Arrays.asList(prop.split(","));
		} else {
			Map<String, List<String>> authsMap = appConfig.getProdTransferAuthoritiesMailsMap();
			for (String authoritiesInput: authorities) {
				if (authsMap.containsKey(authoritiesInput)) {
					emails.addAll(authsMap.get(authoritiesInput));
				}
			}
		}

		List<String> codes = new ArrayList<>();
		Map<String, String> authsCodesMap = new HashMap<>();

		for (String authority : emails) {
			String code = createRequestCode(request.getReqId(), request.getFacilityId(), authority);
			codes.add(code);
			authsCodesMap.put(authority, code);
		}

		requestManager.storeCodes(codes);

		log.trace("generateCodesForAuthorities() returns: {}", authsCodesMap);
		return authsCodesMap;
	}

	private Map<String, String> generateCodesForAdmins(List<String> admins, Long facilityId)
			throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InternalErrorException
	{
		log.trace("generateCodesForAdmins(facilityId: {}, admins: {})", facilityId, admins);

		if (Utils.checkParamsInvalid(admins, facilityId)) {
			log.error("Wrong parameters passed: (admins: {}, facilityId: {})", admins, facilityId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		List<String> codes = new ArrayList<>();
		Map<String, String> adminCodesMap = new HashMap<>();

		for (String admin : admins) {
			String code = createAddAdminCode(facilityId, admin);
			codes.add(code);
			adminCodesMap.put(admin, code);
		}

		requestManager.storeCodes(codes);

		log.trace("generateCodesForAdmins() returns: {}", adminCodesMap);
		return adminCodesMap;
	}

	private Map<String, String> generateLinksForAdmins(Long facilityId, Map<String, String> adminCodeMap)
			throws ConnectorException, UnsupportedEncodingException
	{
		log.trace("generateLinksForAdmins(adminCodeMap: {}, facilityId: {})", adminCodeMap, facilityId);

		if (Utils.checkParamsInvalid(facilityId, adminCodeMap)) {
			log.error("Wrong parameters passed: (facilityId: {}, adminCodeMap: {})", facilityId, adminCodeMap);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Facility facility = perunConnector.getFacilityById(facilityId);
		Map<String, String> linksMap = new HashMap<>();

		for (Map.Entry<String, String> entry : adminCodeMap.entrySet()) {
			String code = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
			String link = appConfig.getAdminsEndpoint()
					.concat("?facilityName=").concat(facility.getName())
					.concat("&code=").concat(code);
			linksMap.put(entry.getKey(), link);
			log.debug("Generated code: {}", code); //TODO: remove
		}

		log.trace("generateLinksForAdmins() returns: {}", linksMap);
		return linksMap;
	}

	private Map<String, String> generateLinksForAuthorities(Map<String, String> authorityCodeMap, Request request)
			throws UnsupportedEncodingException, ConnectorException
	{
		log.trace("generateLinksForAuthorities(authorityCodeMap: {}, request: {})", authorityCodeMap, request);

		if (Utils.checkParamsInvalid(authorityCodeMap, request)) {
			log.error("Wrong parameters passed: (authorityCodeMap: {}, request: {})", authorityCodeMap, request);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Facility facility = perunConnector.getFacilityById(request.getFacilityId());
		Map<String, String> linksMap = new HashMap<>();

		for (Map.Entry<String, String> entry : authorityCodeMap.entrySet()) {
			String code = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
			String link = appConfig.getSignaturesEndpointUrl()
					.concat("?facilityName=").concat(facility.getName())
					.concat("&code=").concat(code);
			linksMap.put(entry.getKey(), link);
			log.debug("Generated code: {}", code); //TODO: remove
		}

		log.trace("generateLinksForAuthorities() returns: {}", linksMap);
		return linksMap;
	}

	private Map<AttributeCategory, Map<String, PerunAttribute>> convertToStruct(List<PerunAttribute> filteredAttributes, AppConfig appConfig) {
		if (filteredAttributes == null) {
			return null;
		}

		Map<AttributeCategory, Map<String, PerunAttribute>> map = new HashMap<>();
		map.put(AttributeCategory.SERVICE, new HashMap<>());
		map.put(AttributeCategory.ORGANIZATION, new HashMap<>());
		map.put(AttributeCategory.PROTOCOL, new HashMap<>());
		map.put(AttributeCategory.ACCESS_CONTROL, new HashMap<>());

		if (!filteredAttributes.isEmpty()) {
			for (PerunAttribute attribute : filteredAttributes) {
				AttributeCategory category = appConfig.getAttrCategory(attribute.getFullName());
				attribute.setInput(config.getInputMap().get(attribute.getFullName()));
				map.get(category).put(attribute.getFullName(), attribute);
			}
		}

		return map;
	}
}
