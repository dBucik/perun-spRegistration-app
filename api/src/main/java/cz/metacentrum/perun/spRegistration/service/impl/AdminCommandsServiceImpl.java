package cz.metacentrum.perun.spRegistration.service.impl;

import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
import cz.metacentrum.perun.spRegistration.service.MailsService;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
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
		Set<String> allowedAttrs = appConfig.getPerunAttributeDefinitionsMap().keySet();
		Set<String> requestedAttrs = request.getAttributes().keySet();
		Set<String> notAllowed = requestedAttrs.stream()
				.filter(attrName -> !allowedAttrs.contains(attrName))
				.collect(Collectors.toSet());

		if (!notAllowed.isEmpty()) {
			throw new InternalErrorException("Cannot approve, requested attributes are not allowed");
		}

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
		} else if (! RequestStatus.WAITING_FOR_APPROVAL.equals(request.getStatus())) {
			log.error("Cannot ask for changes, request not marked as WAITING_FOR_APPROVAL");
			throw new CannotChangeStatusException("Cannot ask for changes, request not marked as WAITING_FOR_APPROVAL");
		}

		Map<String, PerunAttribute> convertedAttributes = ServiceUtils.transformListToMapAttrs(attributes, appConfig);
		request.updateAttributes(convertedAttributes, false);
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

		if (testFacilitiesMap != null && !testFacilitiesMap.isEmpty()) {
			testFacilitiesMap.forEach((facId, value) -> {
				if (proxyFacilitiesMap.containsKey(facId)) {
					Facility testFacility = proxyFacilitiesMap.get(facId);
					testFacility.setTestEnv(true);
				}
			});
		}

		log.trace("getAllFacilities returns: {}", proxyFacilities);
		return proxyFacilities;
	}

	/* PRIVATE METHODS */

	private boolean processApprovedRequest(Request request) throws InternalErrorException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
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

	private boolean registerNewFacilityToPerun(Request request) throws InternalErrorException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		log.trace("registerNewFacilityToPerun({})", request);

		String newName = request.getFacilityName();
		String newDesc = request.getFacilityDescription();

		if (Utils.checkParamsInvalid(newName, newDesc)) {
			log.error("Wrong parameters passed: (newName: {}, newDesc: {})", newName, newDesc);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Facility facility = new Facility(null);
		facility.setName(newName);
		facility.setDescription(newDesc);
		facility = perunConnector.createFacilityInPerun(facility.toJson());

		if (facility == null) {
			log.error("Creating facility in Perun failed");
			throw new InternalErrorException("Creating facility in Perun failed");
		}

		request.setFacilityId(facility.getId());

		boolean adminSet = perunConnector.addFacilityAdmin(facility.getId(), request.getReqUserId());

		PerunAttribute testSp = generateTestSpAttribute(true);
		PerunAttribute showOnServiceList = generateShowOnServiceListAttribute(false);
		PerunAttribute proxyIdentifiers = generateProxyIdentifiersAttribute();
		PerunAttribute masterProxyIdentifiers = generateMasterProxyIdentifierAttribute();


		JSONArray attributes = request.getAttributesAsJsonArrayForPerun();
		if (ServiceUtils.isOidcRequest(request, appConfig.getEntityIdAttribute())) {
			PerunAttribute clientId = generateClientIdAttribute();
			attributes.put(clientId.toJson());
			PerunAttribute clientSecret = generateClientSecretAttribute();
			attributes.put(clientSecret.toJson());
		}

		attributes.put(testSp.toJson());
		attributes.put(showOnServiceList.toJson());
		attributes.put(proxyIdentifiers.toJson());
		attributes.put(masterProxyIdentifiers.toJson());
		boolean attributesSet = perunConnector.setFacilityAttributes(request.getFacilityId(), attributes);

		boolean successful = (adminSet && attributesSet);
		if (!successful) {
			log.error("Some operations failed - adminSet: {}, attributesSet: {}", adminSet, attributesSet);
		} else {
			log.info("Facility is all set up");
		}

		log.trace("registerNewFacilityToPerun returns: {}", successful);
		return successful;
	}

	private boolean updateFacilityInPerun(Request request) throws InternalErrorException, ConnectorException {
		log.trace("updateFacilityInPerun({})", request);

		if (Utils.checkParamsInvalid(request)) {
			log.error("Wrong parameters passed: (request: {})", request);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Long facilityId = extractFacilityIdFromRequest(request);

		Facility actualFacility = perunConnector.getFacilityById(facilityId);
		if (actualFacility == null) {
			log.error("Facility with ID: {} does not exist in Perun", facilityId);
			throw new InternalErrorException("Facility with ID: " + facilityId + " does not exist in Perun");
		}

		boolean facilityCoreUpdated = updateFacilityNameAndDesc(actualFacility, request);
		boolean attributesSet = perunConnector.setFacilityAttributes(request.getFacilityId(),
				request.getAttributesAsJsonArrayForPerun());
		boolean mitreIdUpdated = false;

		if (ServiceUtils.isOidcRequest(request, appConfig.getEntityIdAttribute())) {
			// TODO: uncomment when connector implemented
			//PerunAttribute mitreClientId = perunConnector.getFacilityAttribute(facilityId, mitreIdAttrsConfig.getMitreClientIdAttr());
			//mitreIdUpdated = mitreIdConnector.updateClient(mitreClientId.valueAsLong(), request.getAttributes());
			mitreIdUpdated = true;
		}

		boolean successful = (facilityCoreUpdated && attributesSet && mitreIdUpdated);
		if (!successful) {
			if (ServiceUtils.isOidcRequest(request, appConfig.getEntityIdAttribute())) {
				log.error("Some operations failed - facilityCoreUpdated: {}, attributesSet: {}, mitreIdUpdated: {}",
						facilityCoreUpdated, attributesSet, mitreIdUpdated);
			} else {
				log.error("Some operations failed - facilityCoreUpdated: {}, attributesSet: {}",
						facilityCoreUpdated, attributesSet);
			}
		} else {
			log.info("Facility has been updated in Perun");
		}

		log.trace("updateFacilityInPerun returns: {}", successful);
		return successful;
	}

	private boolean deleteFacilityFromPerun(Request request) throws ConnectorException, InternalErrorException {
		log.trace("deleteFacilityFromPerun({})", request);

		if (Utils.checkParamsInvalid(request)) {
			log.error("Wrong parameters passed: (request: {})", request);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Long facilityId = extractFacilityIdFromRequest(request);

		boolean facilityRemoved = perunConnector.deleteFacilityFromPerun(facilityId);

		if (ServiceUtils.isOidcRequest(request, appConfig.getEntityIdAttribute())) {
			String clientId = perunConnector.getFacilityAttribute(facilityId, appConfig.getClientIdAttribute())
					.valueAsString();

			requestManager.deleteClientId(clientId);
		}

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

		JSONArray attributes = request.getAttributesAsJsonArrayForPerun();
		attributes.put(testSp.toJson());
		attributes.put(showOnServiceList.toJson());

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

	private boolean updateFacilityNameAndDesc(Facility actualFacility, Request request) throws ConnectorException {
		log.trace("updateFacilityNameAndDesc(actualFacility: {}, request: {})", actualFacility, request);

		if (Utils.checkParamsInvalid(actualFacility, request)) {
			log.error("Wrong parameters passed: (actualFacility: {}, request: {})", actualFacility, request);
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		String newName = request.getFacilityName();
		String newDesc = request.getFacilityDescription();
		boolean changed = false;
		boolean successful = true;

		if (newName != null && !Objects.equals(actualFacility.getName(), newName)) {
			log.debug("Update facility name requested");
			actualFacility.setName(newName);
			changed = true;
		}

		if (newDesc != null && !Objects.equals(actualFacility.getDescription(), newDesc)) {
			log.debug("Update facility description requested");
			actualFacility.setDescription(newDesc);
			changed = true;
		}

		if (changed) {
			log.debug("Updating facility name and/or description");
			successful = (null != perunConnector.updateFacilityInPerun(actualFacility.toJson()));
		}

		log.trace("updateFacilityNameAndDesc() returns: {}", successful);
		return successful;
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

	private PerunAttribute generateClientIdAttribute() throws InternalErrorException {
		log.trace("generateClientIdAttribute()");

		PerunAttribute attribute = new PerunAttribute();
		attribute.setDefinition(appConfig.getAttrDefinition(appConfig.getClientIdAttribute()));

		String clientId = "";
		boolean res;
		do {
			clientId = ServiceUtils.generateClientId();
			res = requestManager.isClientIdAvailable(clientId);
		} while (!res);

		requestManager.storeClientId(clientId);
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
