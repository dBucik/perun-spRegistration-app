package cz.metacentrum.perun.spRegistration.service.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.BadRequestException;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import cz.metacentrum.perun.spRegistration.service.mails.MailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implementation of AdminCommandsService.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
@Service("adminService")
public class AdminCommandsServiceImpl implements AdminCommandsService {

	private static final Logger log = LoggerFactory.getLogger(AdminCommandsServiceImpl.class);

	private final RequestManager requestManager;
	private final PerunConnector perunConnector;
	private final AppConfig appConfig;

	@Autowired
	private MailsService mailsService;

	@Autowired
	public AdminCommandsServiceImpl(RequestManager requestManager, PerunConnector perunConnector, AppConfig appConfig) {
		this.requestManager = requestManager;
		this.perunConnector = perunConnector;
		this.appConfig = appConfig;
	}

	@Override
	public boolean approveRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, BadRequestException {
		log.trace("approveRequest(requestId: {}, userId: {})", requestId, userId);

		if (Utils.checkParamsInvalid(requestId, userId)) {
			log.error("Wrong parameters passed: (requestId: {}, userId: {})", requestId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! appConfig.isAppAdmin(userId)) {
			log.error("User is not authorized to approve request");
			throw new UnauthorizedActionException("User is not authorized to approve request");
		}

		Request request = requestManager.getRequestById(requestId);
		if (request == null) {
			log.error("Could not fetch request with ID: {} from database", requestId);
			throw new InternalErrorException("Could not fetch request with ID: " + requestId + " from database");
		} else if (!RequestStatus.WAITING_FOR_APPROVAL.equals(request.getStatus())
				&& !RequestStatus.WAITING_FOR_CHANGES.equals(request.getStatus())) {
			log.error("Cannot approve request, request is not in valid status");
			throw new CannotChangeStatusException("Cannot approve request, request is not in valid status");
		}

		request.setStatus(RequestStatus.APPROVED);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		request.updateAttributes(new ArrayList<>(), false, appConfig);

		boolean requestProcessed = processApprovedRequest(request);
		boolean requestUpdated = requestManager.updateRequest(request);
		mailsService.notifyUser(request, MailsService.REQUEST_STATUS_UPDATED);

		boolean successful = (requestProcessed && requestUpdated);

		if (!successful) {
			log.error("some operations failed: requestProcessed: {}, requestUpdated: {} for request: {}",
					requestProcessed, requestUpdated, request);
		} else {
			log.info("Request processed, request updated, notification sent");
		}

		log.trace("approveRequest() returns: {}", successful);
		return successful;
	}

	@Override
	public boolean rejectRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException
	{
		log.trace("rejectRequest(requestId: {}, userId: {})", requestId, userId);

		if (Utils.checkParamsInvalid(requestId, userId)) {
			log.error("Wrong parameters passed: (requestId: {}, userId: {})", requestId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! appConfig.isAppAdmin(userId)) {
			log.error("User is not authorized to reject request");
			throw new UnauthorizedActionException("User is not authorized to reject request");
		}

		Request request = requestManager.getRequestById(requestId);
		if (request == null) {
			log.error("Could not fetch request with ID: {} from database", requestId);
			throw new InternalErrorException("Could not fetch request with ID: " + requestId + " from database");
		} else if (!RequestStatus.WAITING_FOR_APPROVAL.equals(request.getStatus())
				&& !RequestStatus.WAITING_FOR_CHANGES.equals(request.getStatus())) {
			log.error("Cannot reject request, request is not in valid status");
			throw new CannotChangeStatusException("Cannot reject request, request is not in valid status");
		}

		request.setStatus(RequestStatus.REJECTED);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		boolean requestUpdated = requestManager.updateRequest(request);
		mailsService.notifyUser(request, MailsService.REQUEST_STATUS_UPDATED);

		if (! requestUpdated) {
			log.error("some operations failed: requestUpdated: {} for request: {}", requestUpdated, request);
		} else {
			log.info("Request updated, notification sent");
		}

		log.trace("rejectRequest() returns: {}", request);
		return requestUpdated;
	}

	@Override
	public boolean askForChanges(Long requestId, Long userId, List<PerunAttribute> attributes)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException
	{
		log.trace("askForChanges(requestId: {}, userId: {}, attributes: {})", requestId, userId, attributes);

		if (Utils.checkParamsInvalid(requestId, userId)) {
			log.error("Wrong parameters passed: (requestId: {}, userId: {})", requestId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! appConfig.isAppAdmin(userId)) {
			log.error("User is not authorized to ask for changes");
			throw new UnauthorizedActionException("User is not authorized to ask for changes");
		}

		Request request = requestManager.getRequestById(requestId);
		if (request == null) {
			log.error("Could not fetch request with ID: {} from database", requestId);
			throw new InternalErrorException("Could not fetch request with ID: " + requestId + " from database");
		} else if (!RequestStatus.WAITING_FOR_APPROVAL.equals(request.getStatus()) &&
					!RequestStatus.WAITING_FOR_CHANGES.equals(request.getStatus())) {
			log.error("Cannot ask for changes, request not marked as WFA nor WFC");
			throw new CannotChangeStatusException("Cannot ask for changes, request not marked as WFA nor WFC");
		}

		request.updateAttributes(attributes, false, appConfig);
		request.setStatus(RequestStatus.WAITING_FOR_CHANGES);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		boolean requestUpdated = requestManager.updateRequest(request);
		mailsService.notifyUser(request, MailsService.REQUEST_STATUS_UPDATED);

		if (! requestUpdated) {
			log.error("some operations failed: requestUpdated: {} for request: {}",
					requestUpdated, request);
		} else {
			log.info("Request updated, notification sent");
		}

		log.trace("askForChanges() returns: {}", requestUpdated);
		return requestUpdated;
	}

	@Override
	public List<Request> getAllRequests(Long userId) throws UnauthorizedActionException {
		log.trace("getAllRequests({})", userId);

		if (Utils.checkParamsInvalid(userId)) {
			log.error("Wrong parameters passed: (userId: {})", userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! appConfig.isAppAdmin(userId)) {
			log.error("User cannot list all requests, user is not an admin");
			throw new UnauthorizedActionException("User cannot list all requests, user is not an admin");
		}

		List<Request> result = requestManager.getAllRequests();

		log.trace("getAllRequests returns: {}", result);
		return result;
	}

	@Override
	public List<Facility> getAllFacilities(Long userId) throws UnauthorizedActionException, ConnectorException {
		log.trace("getAllFacilities({})", userId);

		if (Utils.checkParamsInvalid(userId)) {
			log.error("Wrong parameters passed: (userId: {})", userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! appConfig.isAppAdmin(userId)) {
			log.error("User cannot list all facilities, user not an admin");
			throw new UnauthorizedActionException("User cannot list all facilities, user does not have role APP_ADMIN");
		}

		List<Facility> proxyFacilities = perunConnector.getFacilitiesByProxyIdentifier(
				appConfig.getProxyIdentifierAttribute(), appConfig.getProxyIdentifierAttributeValue());
		Map<Long, Facility> proxyFacilitiesMap = ServiceUtils.transformListToMapFacilities(proxyFacilities);

		if (proxyFacilitiesMap == null || proxyFacilitiesMap.isEmpty()) {
			return new ArrayList<>();
		}

		List<Facility> testFacilities = perunConnector.getFacilitiesByAttribute(
				appConfig.getIsTestSpAttribute(), "true");
		Map<Long, Facility> testFacilitiesMap = ServiceUtils.transformListToMapFacilities(testFacilities);

		List<Facility> oidcFacilities = perunConnector.getFacilitiesByAttribute(
				appConfig.getIsOidcAttributeName(), "true");
		Map<Long, Facility> oidcFacilitiesMap = ServiceUtils.transformListToMapFacilities(oidcFacilities);

		List<Facility> samlFacilities = perunConnector.getFacilitiesByAttribute(
				appConfig.getIsSamlAttributeName(), "true");
		Map<Long, Facility> samlFacilitiesMap = ServiceUtils.transformListToMapFacilities(samlFacilities);

		proxyFacilitiesMap.forEach((facId, val) -> {
			Facility f = proxyFacilitiesMap.get(facId);
			f.setTestEnv(testFacilitiesMap.containsKey(facId));
			f.setOidc(oidcFacilitiesMap.containsKey(facId));
			f.setSaml(samlFacilitiesMap.containsKey(facId));
		});

		log.trace("getAllFacilities returns: {}", proxyFacilities);
		return proxyFacilities;
	}

	@Override
	public PerunAttribute regenerateClientSecret(Long userId, Long facilityId) throws UnauthorizedActionException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, ConnectorException {
		log.trace("regenerateClientSecret({}, {})", userId, facilityId);

		if (Utils.checkParamsInvalid(userId, facilityId)) {
			log.error("Wrong parameters passed: (userId: {}, facilityId: {})", userId, facilityId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		} else if (! appConfig.isAppAdmin(userId) && !isFacilityAdmin(facilityId, userId)) {
			log.error("User is not authorized to regenerate client secret");
			throw new UnauthorizedActionException("User is not authorized to regenerate client secret");
		}

		PerunAttribute clientSecret = generateClientSecretAttribute();
		perunConnector.setFacilityAttribute(facilityId, clientSecret.toJson());

		String decrypted = ServiceUtils.decrypt(clientSecret.valueAsString(), appConfig.getSecret());
		clientSecret.setValue(decrypted);
		log.trace("regenerateClientSecret({}, {}) returns: {}", userId, facilityId, clientSecret);
		return clientSecret;
	}

	/* PRIVATE METHODS */

	private boolean isFacilityAdmin(Long facilityId, Long userId) throws ConnectorException {
		log.trace("isFacilityAdmin(facilityId: {}, userId: {})", facilityId, userId);

		if (Utils.checkParamsInvalid(facilityId, userId)) {
			log.error("Wrong parameters passed: (facility: {}, userId: {})", facilityId, userId);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Set<Long> whereAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);

		if (whereAdmin == null || whereAdmin.isEmpty()) {
			log.debug("isFacilityAdmin returns: {}", false);
			return false;
		}

		boolean result = whereAdmin.contains(facilityId);
		log.debug("isFacilityAdmin returns:  {}", result);
		return result;
	}

	private boolean processApprovedRequest(Request request) throws InternalErrorException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, BadRequestException {
		switch(request.getAction()) {
			case REGISTER_NEW_SP:
				return registerNewFacilityToPerun(request);
			case UPDATE_FACILITY:
				return updateFacilityInPerun(request);
			case DELETE_FACILITY:
				return deleteFacilityFromPerun(request);
			case MOVE_TO_PRODUCTION:
				return moveToProduction(request);
		}

		return false;
	}

	private boolean registerNewFacilityToPerun(Request request) throws InternalErrorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, BadRequestException {
		log.trace("registerNewFacilityToPerun({})", request);

		String newName = request.getFacilityName(appConfig.getServiceNameAttributeName()).get("en");
		Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
		Pattern pattern2 = Pattern.compile("_+_");

		newName = Normalizer.normalize(newName, Normalizer.Form.NFD).replaceAll("\\p{M}", "");;
		newName = pattern.matcher(newName).replaceAll("_");
		newName = pattern2.matcher(newName).replaceAll("_");
		String newDesc = newName + " registered via SP_REG, use app to manage configuration";

		if (Utils.checkParamsInvalid(newName)) {
			log.error("Wrong parameters passed: (newName: {})", newName);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Facility facility = new Facility(null);
		facility.setPerunName(newName);
		facility.setPerunDescription(newDesc);
		try {
			facility = perunConnector.createFacilityInPerun(facility.toJson());
		} catch (ConnectorException e) {
			throw new InternalErrorException("Creating facility in Perun failed");
		}

		if (facility == null) {
			log.error("Creating facility in Perun failed");
			throw new InternalErrorException("Creating facility in Perun failed");
		}

		request.setFacilityId(facility.getId());
		try {
			boolean adminSet = perunConnector.addFacilityAdmin(facility.getId(), request.getReqUserId());

			PerunAttribute testSp = generateTestSpAttribute(true);
			PerunAttribute showOnServiceList = generateShowOnServiceListAttribute(false);
			PerunAttribute proxyIdentifiers = generateProxyIdentifiersAttribute();
			PerunAttribute masterProxyIdentifiers = generateMasterProxyIdentifierAttribute();
			PerunAttribute authProtocol = generateAuthProtocolAttribute(ServiceUtils.isOidcRequest(request,
					appConfig.getEntityIdAttribute()));

			ArrayNode attributes = request.getAttributesAsJsonArrayForPerun();
			if (ServiceUtils.isOidcRequest(request, appConfig.getEntityIdAttribute())) {
				for (int i = 0; i < 10; i++) {
					PerunAttribute clientId = generateClientIdAttribute();
					try {
						perunConnector.setFacilityAttribute(facility.getId(), clientId.toJson());
						break;
					} catch (ConnectorException e) {
						log.warn("Failed to set attribute clientId with value {} for facility {}",
								clientId.valueAsString(), facility.getId());
					}
				}

				PerunAttribute clientSecret = generateClientSecretAttribute();
				perunConnector.setFacilityAttribute(facility.getId(), clientSecret.toJson());
			}

			attributes.add(testSp.toJson());
			attributes.add(showOnServiceList.toJson());
			attributes.add(proxyIdentifiers.toJson());
			attributes.add(masterProxyIdentifiers.toJson());
			attributes.add(authProtocol.toJson());
			boolean attributesSet = perunConnector.setFacilityAttributes(request.getFacilityId(), attributes);

			boolean successful = (adminSet && attributesSet);
			if (!successful) {
				log.error("Some operations failed - adminSet: {}, attributesSet: {}", adminSet, attributesSet);
			} else {
				log.info("Facility is all set up");
			}
			log.trace("registerNewFacilityToPerun returns: {}", successful);
			return successful;
		} catch (ConnectorException e) {
			log.error("Caught ConnectorException", e);
			try {
				perunConnector.deleteFacilityFromPerun(facility.getId());
			} catch (ConnectorException ex) {
				log.error("Caught ConnectorException", ex);
			}

			return false;
		}
	}

	private boolean updateFacilityInPerun(Request request) throws InternalErrorException, ConnectorException, BadRequestException {
		log.trace("updateFacilityInPerun({})", request);

		if (Utils.checkParamsInvalid(request)) {
			log.error("Wrong parameters passed: (request: {})", request);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Long facilityId = extractFacilityIdFromRequest(request);

		Facility actualFacility = perunConnector.getFacilityById(facilityId);
		Map<String, PerunAttribute> oldAttributes = perunConnector.getFacilityAttributes(facilityId,
				request.getAttributeNames());

		if (actualFacility == null) {
			log.error("Facility with ID: {} does not exist in Perun", facilityId);
			throw new InternalErrorException("Facility with ID: " + facilityId + " does not exist in Perun");
		}

		try {
			//boolean facilityCoreUpdated = updateFacilityNameAndDesc(actualFacility, request);
			boolean attributesSet = perunConnector.setFacilityAttributes(request.getFacilityId(),
					request.getAttributesAsJsonArrayForPerun());
			//boolean successful = (facilityCoreUpdated && attributesSet);
			boolean successful = attributesSet;
			if (!successful) {
				//log.error("Some operations failed - facilityCoreUpdated: {}, attributesSet: {}", facilityCoreUpdated, attributesSet);
				log.error("Some operations failed - attributesSet: {}", attributesSet);
			} else {
				log.info("Facility has been updated in Perun");
			}

			log.trace("updateFacilityInPerun returns: {}", successful);
			return successful;
		} catch (ConnectorException e) {
			log.warn("Caught ConnectorException", e);
			try {
				perunConnector.updateFacilityInPerun(actualFacility.toJson());
				ArrayNode oldAttrsArray = JsonNodeFactory.instance.arrayNode();
				oldAttributes.values().forEach(a -> oldAttrsArray.add(a.toJson()));
				perunConnector.setFacilityAttributes(actualFacility.getId(), oldAttrsArray);
			} catch (ConnectorException ex) {
				log.warn("Caught ConnectorException", ex);
			}
			return false;
		}
	}

	private boolean deleteFacilityFromPerun(Request request) throws ConnectorException, InternalErrorException {
		log.trace("deleteFacilityFromPerun({})", request);

		if (Utils.checkParamsInvalid(request)) {
			log.error("Wrong parameters passed: (request: {})", request);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Long facilityId = extractFacilityIdFromRequest(request);

		boolean facilityRemoved = perunConnector.deleteFacilityFromPerun(facilityId);

		if (!facilityRemoved) {
			log.error("Some operations failed - facilityRemoved: {}", false);
		} else {
			log.info("Facility has been deleted");
		}

		log.trace("deleteFacilityFromPerun returns: {}", facilityRemoved);
		return facilityRemoved;
	}

	private boolean moveToProduction(Request request) throws ConnectorException {
		log.trace("requestMoveToProduction({})", request);

		log.info("Updating facility attributes");
		PerunAttribute testSp = generateTestSpAttribute(false);
		PerunAttribute showOnServiceList = generateShowOnServiceListAttribute(true);

		ArrayNode attributes = request.getAttributesAsJsonArrayForPerun();
		attributes.add(testSp.toJson());
		attributes.add(showOnServiceList.toJson());

		boolean attributesSet = perunConnector.setFacilityAttributes(request.getFacilityId(), attributes);

		log.trace("requestMoveToProduction returns: {}", attributesSet);
		return attributesSet;
	}

	private Long extractFacilityIdFromRequest(Request request) throws InternalErrorException {
		log.trace("extractFacilityIdFromRequest({})", request);

		if (Utils.checkParamsInvalid(request)) {
			log.error("Wrong parameters passed: (request: {})",request);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Long facilityId = request.getFacilityId();
		if (facilityId == null) {
			log.error("Request: {} does not have facilityId", request);
			throw new InternalErrorException(Utils.GENERIC_ERROR_MSG);
		}

		log.trace("extractFacilityIdFromRequest() returns: {}", facilityId);
		return facilityId;
	}

	private PerunAttribute generateAuthProtocolAttribute(boolean isOidc) {
		log.trace("generateAuthProtocolAttribute({})", isOidc);

		PerunAttribute attribute = new PerunAttribute();
		if (isOidc) {
			attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getIsOidcAttributeName()));
		} else {
			attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getIsSamlAttributeName()));
		}
		attribute.setValue(true);

		log.trace("generateAuthProtocolAttribute() returns: {}", attribute);
		return attribute;
	}

	private PerunAttribute generateMasterProxyIdentifierAttribute() {
		log.trace("generateMasterProxyIdentifierAttribute()");

		PerunAttribute attribute = new PerunAttribute();
		attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getMasterProxyIdentifierAttribute()));
		attribute.setValue(appConfig.getMasterProxyIdentifierAttributeValue());

		log.trace("generateMasterProxyIdentifierAttribute() returns: {}", attribute);
		return attribute;
	}

	private PerunAttribute generateProxyIdentifiersAttribute() {
		log.trace("generateProxyIdentifierAttribute()");

		PerunAttribute attribute = new PerunAttribute();
		attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getProxyIdentifierAttribute()));
		attribute.setValue(Collections.singletonList(appConfig.getProxyIdentifierAttributeValue()));

		log.trace("generateProxyIdentifierAttribute() returns: {}", attribute);
		return attribute;
	}

	private PerunAttribute generateShowOnServiceListAttribute(boolean value) {
		log.trace("generateShowOnServiceListAttribute({})", value);

		PerunAttribute attribute = new PerunAttribute();
		attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getShowOnServicesListAttributeName()));
		attribute.setValue(value);

		log.trace("generateShowOnServiceListAttribute() returns: {}", attribute);
		return attribute;
	}

	private PerunAttribute generateTestSpAttribute(boolean value) {
		log.trace("generateTestSpAttribute({})", value);

		PerunAttribute attribute = new PerunAttribute();
		attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getIsTestSpAttribute()));
		attribute.setValue(value);

		log.trace("generateTestSpAttribute() returns: {}", attribute);
		return attribute;
	}

	private PerunAttribute generateClientIdAttribute() {
		log.trace("generateClientIdAttribute()");

		PerunAttribute attribute = new PerunAttribute();
		attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getClientIdAttribute()));

		String clientId = ServiceUtils.generateClientId();
		attribute.setValue(clientId);

		log.trace("generateClientIdAttribute() returns: {}", attribute);
		return attribute;
	}

	private PerunAttribute generateClientSecretAttribute() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		log.trace("generateClientIdAttribute()");

		PerunAttribute attribute = new PerunAttribute();
		attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getClientSecretAttribute()));

		String clientSecret = ServiceUtils.generateClientSecret();
		String encryptedClientSecret = ServiceUtils.encrypt(clientSecret, appConfig.getSecret());

		attribute.setValue(encryptedClientSecret);

		log.trace("generateClientIdAttribute() returns: {}", attribute);
		return attribute;
	}
}
