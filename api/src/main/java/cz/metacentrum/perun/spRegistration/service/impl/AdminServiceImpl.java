package cz.metacentrum.perun.spRegistration.service.impl;

import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.RequestApproval;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import cz.metacentrum.perun.spRegistration.service.AdminService;
import cz.metacentrum.perun.spRegistration.service.Mails;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Implementation of AdminService.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {

	private final RequestManager requestManager;
	private final PerunConnector perunConnector;
	private final AppConfig appConfig;
	private final Properties messagesProperties;

	@Autowired
	public AdminServiceImpl(RequestManager requestManager, PerunConnector perunConnector, AppConfig appConfig, Properties messagesProperties) {
		this.requestManager = requestManager;
		this.perunConnector = perunConnector;
		this.appConfig = appConfig;
		this.messagesProperties = messagesProperties;
	}

	@Override
	public boolean approveRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException, RPCException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		} else if (! appConfig.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to approve request");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not fetch request with ID: " + requestId + " from database");
		} else if (! RequestStatus.WFA.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot approve request, request not marked as WAITING_FOR_APPROVAL");
		}

		if (! finishRequestApproved(request)) {
			return false;
		}

		request.setStatus(RequestStatus.APPROVED);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		boolean res = requestManager.updateRequest(request);

		Mails.requestStatusUpdateUserNotify(requestId, RequestStatus.APPROVED,
				request.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return res;
	}

	@Override
	public boolean rejectRequest(Long requestId, Long userId, String message)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		} else if (! appConfig.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to reject request");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not fetch request with ID: " + requestId + " from database");
		} else if (! RequestStatus.WFA.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot reject request, request not marked as WAITING_FOR_APPROVAL");
		}

		request.setStatus(RequestStatus.REJECTED);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		boolean res = requestManager.updateRequest(request);

		Mails.requestStatusUpdateUserNotify(requestId, RequestStatus.REJECTED,
				request.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return res;
	}

	@Override
	public boolean askForChanges(Long requestId, Long userId, List<PerunAttribute> attributes)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException {
		if (requestId == null || userId == null || attributes == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId + ", attributes: " + attributes);
		} else if (! appConfig.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to ask for changes");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not fetch request with ID: " + requestId + " from database");
		} else if (! RequestStatus.WFA.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot ask for changes, request not marked as WAITING_FOR_APPROVAL");
		}

		Map<String, PerunAttribute> convertedAttributes = ServiceUtils.transformListToMap(attributes, appConfig);
		request.setStatus(RequestStatus.WFC);
		request.setAttributes(convertedAttributes);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		boolean res = requestManager.updateRequest(request);

		Mails.requestStatusUpdateUserNotify(requestId, RequestStatus.WFC,
				request.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return res;
	}

	@Override
	public boolean approveTransferToProduction(Long requestId, Long userId) throws InternalErrorException, UnauthorizedActionException {
		if (requestId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - requestId: " + requestId + ", userId: " + userId);
		} else if (! appConfig.isAdmin(userId)) {
			throw new UnauthorizedActionException("User cannot approve transfer, user not an admin");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (request == null) {
			throw new InternalErrorException("Could not fetch request with ID: " + requestId + " from database");
		}

		request.setStatus(RequestStatus.APPROVED);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		request.setModifiedBy(userId);
		boolean res = requestManager.updateRequest(request);

		Mails.requestStatusUpdateUserNotify(requestId, RequestStatus.APPROVED,
				request.getAdmins(appConfig.getAdminsAttr()), messagesProperties);

		return res;
	}

	@Override
	public List<RequestApproval> getApprovalsOfProductionTransfer(Long requestId, Long userId) throws UnauthorizedActionException {
		if (userId == null || requestId == null) {
			throw new IllegalArgumentException("Illegal input - userId: " + userId + ", requestId: " + requestId);
		} else if (! appConfig.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to view approvals");
		}

		return requestManager.getApprovalsForRequest(requestId);
	}

	@Override
	public List<Request> getAllRequests(Long adminId) throws UnauthorizedActionException {
		if (adminId == null) {
			throw new IllegalArgumentException("Illegal input - adminId: " + adminId);
		} else if (! appConfig.isAdmin(adminId)) {
			throw new UnauthorizedActionException("User cannot list all requests, user is not an admin");
		}

		return requestManager.getAllRequests();
	}

	@Override
	public List<Facility> getAllFacilities(Long adminId) throws UnauthorizedActionException, RPCException {
		if (adminId == null) {
			throw new IllegalArgumentException("Illegal input - adminId: " + adminId);
		} else if (! appConfig.isAdmin(adminId)) {
			throw new UnauthorizedActionException("User cannot list all facilities, user not an admin");
		}

		Map<String, String> params = new HashMap<>();
		params.put(appConfig.getIdpAttribute(), appConfig.getIdpAttributeValue());
		return perunConnector.getFacilitiesViaSearcher(params);
	}

	@Override
	public boolean addAdmins(Long userId, Long facilityId, List<Long> admins) throws UnauthorizedActionException, RPCException {
		if (userId == null || facilityId == null || admins == null) {
			throw new IllegalArgumentException("Illegal input - userId: " + userId + ", facilityId: " + facilityId + ", admins: " + admins);
		} else if (! appConfig.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to add facility admins");
		}

		for (Long id: admins) {
			perunConnector.addFacilityAdmin(facilityId, id);
		}

		return true;
	}

	@Override
	public boolean removeAdmins(Long userId, Long facilityId, List<Long> admins) throws UnauthorizedActionException, RPCException {
		if (userId == null || facilityId == null || admins == null) {
			throw new IllegalArgumentException("Illegal input - userId: " + userId + ", facilityId: " + facilityId + ", admins: " + admins);
		} else if (! appConfig.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to remove facility admins");
		}

		for (Long id: admins) {
			perunConnector.removeFacilityAdmin(facilityId, id);
		}

		return true;
	}

	private boolean finishRequestApproved(Request request) throws RPCException, InternalErrorException {
		switch(request.getAction()) {
			case REGISTER_NEW_SP:
				return registerNewFacilityToPerun(request);
			case UPDATE_FACILITY:
				return updateFacilityInPerun(request);
			case DELETE_FACILITY:
				return deleteFacilityFromPerun(request);
			case MOVE_TO_PRODUCTION:
				//TODO: set facility in Perun as production SP
		}

		return false;
	}

	private boolean deleteFacilityFromPerun(Request request) throws RPCException {
		if (request == null) {
			throw new IllegalArgumentException("Request is null");
		}

		Long facilityId = request.getFacilityId();
		if (facilityId == null) {
			throw new IllegalArgumentException("Request: " + request.getReqId() + " does not have facilityId");
		}

		return perunConnector.deleteFacilityFromPerun(facilityId);
	}

	private boolean updateFacilityInPerun(Request request) throws RPCException, InternalErrorException {
		if (request == null) {
			throw new IllegalArgumentException("Request is null");
		}

		Long facilityId = request.getFacilityId();
		Facility actualFacility = perunConnector.getFacilityById(facilityId);
		if (actualFacility == null) {
			throw new InternalErrorException("Facility with ID: " + facilityId + " does not exist in Perun");
		}

		perunConnector.setFacilityAttributes(request.getFacilityId(), request.getAttributesAsJsonForPerun());

		String newName = getName(request);
		String newDesc = getDesc(request);

		boolean changed = false;
		if (newName != null && !actualFacility.getName().equals(newName)) {
			actualFacility.setName(newName);
			changed = true;
		}

		if (newDesc != null && !actualFacility.getDescription().equals(newDesc)) {
			actualFacility.setDescription(newDesc);
			changed = true;
		}

		if (changed) {
			perunConnector.updateFacilityInPerun(actualFacility.toJsonString());
		}

		return true;
	}

	private boolean registerNewFacilityToPerun(Request request) throws RPCException {
		Facility facility = new Facility(null);
		String newName = getName(request);
		String newDesc = getDesc(request);

		if (newName == null || newDesc == null) {
			throw new IllegalArgumentException("Cannot register facility without name and description");
		}

		facility.setName(newName);
		facility.setDescription(newDesc);
		facility = perunConnector.createFacilityInPerun(facility.toJsonString());
		request.setFacilityId(facility.getId());

		return perunConnector.setFacilityAttributes(request.getFacilityId(), request.getAttributesAsJsonForPerun());
	}

	private String getDesc(Request request) {
		return request.getFacilityDescription();
	}

	private String getName(Request request) {
		return request.getFacilityName();
	}
}
