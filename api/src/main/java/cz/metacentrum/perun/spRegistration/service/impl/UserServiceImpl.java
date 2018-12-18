package cz.metacentrum.perun.spRegistration.service.impl;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import cz.metacentrum.perun.spRegistration.service.Mails;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.UserService;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of UserService.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	private final RequestManager requestManager;
	private final PerunConnector perunConnector;
	private final AppConfig appConfig;
	private final Properties messagesProperties;

	@Autowired
	public UserServiceImpl(RequestManager requestManager, PerunConnector perunConnector, AppConfig appConfig, Properties messagesProperties) {
		this.requestManager = requestManager;
		this.perunConnector = perunConnector;
		this.appConfig = appConfig;
		this.messagesProperties = messagesProperties;
	}

	@Override
	public Long createRegistrationRequest(Long userId, List<PerunAttribute> attributes) {
		if (userId == null || attributes == null) {
			throw new IllegalArgumentException("Illegal input - userId: " + userId + ", attributes: " + attributes);
		}

		addProxyIdentifierAttr(attributes);

		Request req = createRequest(null, userId, attributes);
		Mails.userCreateRequestNotify(req.getReqId(), req.getFacilityName(), req.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return req.getReqId();
	}

	@Override
	public Long createFacilityChangesRequest(Long facilityId, Long userId, List<PerunAttribute> attributes)
			throws UnauthorizedActionException, RPCException {
		if (facilityId == null || userId == null || attributes == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", userId: " + userId + ", attributes: " + attributes);
		} else if (! isFacilityAdmin(facilityId, userId)) {
			throw new UnauthorizedActionException("User is not registered as facility admin, cannot create request");
		}

		Request req = createRequest(facilityId, userId, attributes);
		Mails.userCreateRequestNotify(req.getReqId(), req.getFacilityName(), req.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return req.getReqId();
	}

	@Override
	public Long createRemovalRequest(Long userId, Long facilityId) throws UnauthorizedActionException, RPCException {
		if (facilityId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", userId: " + userId);
		} else if (! isFacilityAdmin(facilityId, userId)) {
			throw new UnauthorizedActionException("User is not registered as facility admin, cannot create request");
		}

		Request req = createRequest(facilityId, userId, new ArrayList<>());
		Mails.userCreateRequestNotify(req.getReqId(), req.getFacilityName(), req.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return req.getReqId();
	}

	@Override
	public boolean updateRequest(Long requestId, Long userId, List<PerunAttribute> attributes)
			throws UnauthorizedActionException, InternalErrorException {
		if (requestId == null || userId == null || attributes == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId + ", attributes: " + attributes);
		} else if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request, cannot update it");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not retrieve request for id: " + requestId);
		}

		Map<String, PerunAttribute> convertedAttributes = ServiceUtils.transformListToMap(attributes, appConfig);
		request.setAttributes(convertedAttributes);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		return requestManager.updateRequest(request);
	}

	@Override
	public boolean askForApproval(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		} else if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request, cannot ask for approval");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not retrieve request for id: " + requestId);
		} else if (! RequestStatus.NEW.equals(request.getStatus()) ||
			! RequestStatus.WFC.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot ask for approval, request not marked as NEW nor WAITING_FOR_CHANGES");
		}

		request.setStatus(RequestStatus.WFA);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		boolean res = requestManager.updateRequest(request);

		Mails.requestStatusUpdateUserNotify(requestId, RequestStatus.WFA,
				request.getAdmins(appConfig.getAdminsAttr()), messagesProperties);
		Mails.requestApprovalAdminNotify(userId, requestId, messagesProperties);

		return res;
	}

	@Override
	public boolean cancelRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		} else if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request, cannot cancel it");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		switch (request.getStatus()) {
			case APPROVED:
			case REJECTED:
			case CANCELED:
				throw new CannotChangeStatusException("Cannot ask for abort, request has got status "
						+ request.getStatus());
		}

		request.setStatus(RequestStatus.WFC);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		boolean res = requestManager.updateRequest(request);

		Mails.requestStatusUpdateUserNotify(requestId, RequestStatus.CANCELED,
				request.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return res;
	}

	@Override
	public boolean renewRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		} else if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request, cannot renew it");
		}

		Request request = requestManager.getRequestByReqId(requestId);

		if (! RequestStatus.WFC.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot ask for renew, request not marked as WAITING_FOR_CANCEL");
		}

		request.setStatus(RequestStatus.NEW);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		boolean res = requestManager.updateRequest(request);

		Mails.requestStatusUpdateUserNotify(requestId, RequestStatus.NEW,
				request.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return res;
	}

	@Override
	public Long moveToProduction(Long facilityId, Long userId, List<String> authorities)
			throws UnauthorizedActionException, InternalErrorException, RPCException {

		if (facilityId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", userId: " + userId);
		} else if (! isFacilityAdmin(facilityId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin for facility, cannot ask for moving to production");
		}

		Facility fac = getDetailedFacility(facilityId, userId);
		if (fac == null) {
			throw new InternalErrorException("Could not retrieve facility for id: " + facilityId);
		}

		Map<String, PerunAttribute> attributes = fac.getAttrs();
		String listAttrName = appConfig.getShowOnServicesListAttribute();
		String testSpAttrName = appConfig.getTestSpAttribute();

		PerunAttribute listAttr = attributes.get(listAttrName);
		listAttr.setOldValue(listAttr.getValue());
		listAttr.setValue(true);

		PerunAttribute testSpAttr = attributes.get(testSpAttrName);
		listAttr.setOldValue(testSpAttr.getValue());
		listAttr.setValue(false);

		Request req = createRequest(facilityId, userId, RequestAction.MOVE_TO_PRODUCTION, fac.getAttrs());
		Mails.transferToProductionUserNotify(req.getReqId(), req.getFacilityName(), req.getAdmins(appConfig.getAdminsAttr()), messagesProperties);
		//TODO: generate approval link
		Mails.authoritiesApproveProductionTransferNotify(null, req.getFacilityName(), authorities, messagesProperties);

		return req.getReqId();
	}

	@Override
	public boolean signTransferToProduction(Long requestId, Long userId, String approvalName) throws InternalErrorException, RPCException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not retrieve request for id: " + requestId);
		}

		User signer = perunConnector.getRichUser(userId);
		if (signer == null) {
			throw new InternalErrorException("Could not find user in Perun for id: " + userId);
		}

		return requestManager.addSignature(requestId, userId, signer.getFullName(), approvalName);
	}

	@Override
	public Request getDetailedRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, InternalErrorException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		} else if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User cannot view request, user is not a requester");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not retrieve request for id: " + requestId);
		}

		return request;
	}

	@Override
	public Facility getDetailedFacility(Long facilityId, Long userId)
			throws UnauthorizedActionException, RPCException, InternalErrorException {

		if (! isFacilityAdmin(facilityId, userId)) {
			throw new UnauthorizedActionException("User cannot view facility, user is not an admin");
		}

		Facility facility = perunConnector.getFacilityById(facilityId);
		if (facility == null) {
			throw new InternalErrorException("Could not retrieve facility for id: " + facilityId);
		}

		Map<String, PerunAttribute> attrs = perunConnector.getFacilityAttributes(facilityId);
		facility.setAttrs(attrs);
		return facility;
	}

	@Override
	public List<Request> getAllRequestsUserCanAccess(Long userId) throws RPCException {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		List<Request> requests = requestManager.getAllRequestsByUserId(userId);
		Set<Long> whereAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);
		requests.addAll(requestManager.getAllRequestsByFacilityIds(whereAdmin));

		return new ArrayList<>(new HashSet<>(requests));
	}

	@Override
	public List<Facility> getAllFacilitiesWhereUserIsAdmin(Long userId) throws RPCException {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		List<Facility> userFacilities = perunConnector.getFacilitiesWhereUserIsAdmin(userId);
		Map<String, String> params = new HashMap<>();
		params.put(appConfig.getIdpAttribute(), appConfig.getIdpAttributeValue());
		List<Facility> proxyFacilities = perunConnector.getFacilitiesViaSearcher(params);

		return userFacilities.stream().filter(proxyFacilities::contains).collect(Collectors.toList());
	}

	private Request createRequest(Long facilityId, Long userId, List<PerunAttribute> attributes) {
		Map<String, PerunAttribute> convertedAttributes = ServiceUtils.transformListToMap(attributes, appConfig);
		return createRequest(facilityId, userId, RequestAction.REGISTER_NEW_SP, convertedAttributes);
	}

	private Request createRequest(Long facilityId, Long userId, RequestAction action, Map<String,PerunAttribute> attributes) {
		Request request = new Request();
		request.setFacilityId(facilityId);
		request.setStatus(RequestStatus.NEW);
		request.setAction(action);
		request.setAttributes(attributes);
		request.setReqUserId(userId);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		Long requestId = requestManager.createRequest(request);
		request.setReqId(requestId);

		return request;
	}

	private boolean isFacilityAdmin(Long facilityId, Long userId) throws RPCException {
		Set<Long> whereAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);

		if (whereAdmin == null || whereAdmin.isEmpty()) {
			return false;
		}

		return whereAdmin.contains(facilityId);
	}

	private boolean isAdminInRequest(Long reqUserId, Long userId) {
		return reqUserId.equals(userId);
	}

	private void addProxyIdentifierAttr(List<PerunAttribute> attributes) {
		String identifierAttrName = appConfig.getIdpAttribute();
		String value = appConfig.getIdpAttributeValue();

		PerunAttribute identifierAttr = new PerunAttribute();
		identifierAttr.setFullName(identifierAttrName);
		identifierAttr.setValue(Collections.singletonList(value));

		attributes.add(identifierAttr);
	}
}
