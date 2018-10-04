package cz.metacentrum.perun.spRegistration.service.impl;

import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import cz.metacentrum.perun.spRegistration.service.UserService;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of UserService.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private RequestManager requestManager;

	@Autowired
	private PerunConnector perunConnector;

	@Override
	public Long createRegistrationRequest(Long userId, Map<String, Attribute> attributes) {
		Request request = new Request();
		request.setReqUserId(userId);
		request.setStatus(RequestStatus.NEW);
		request.setAction(RequestAction.REGISTER_NEW_SP);
		request.setAttributes(attributes);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		Long requestId = requestManager.createRequest(request);
		request.setReqId(requestId);

		return requestId;
	}

	@Override
	public Long createFacilityChangesRequest(Long facilityId, Long userId, Map<String, Attribute> attributes)
			throws UnauthorizedActionException {
		if (! isFacilityAdmin(facilityId, userId)) {
			throw new UnauthorizedActionException("User is not registered as facility admin");
		}


		Request request = new Request();
		request.setFacilityId(facilityId);
		request.setStatus(RequestStatus.NEW);
		request.setAction(RequestAction.UPDATE_FACILITY);
		request.setAttributes(attributes);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		Long requestId = requestManager.createRequest(request);
		request.setReqId(requestId);

		return requestId;
	}

	@Override
	public Long createRemovalRequest(Long userId, Long facilityId) throws UnauthorizedActionException {
		if (! isFacilityAdmin(facilityId, userId)) {
			throw new UnauthorizedActionException("User is not registered as facility admin");
		}

		Request request = new Request();
		request.setFacilityId(facilityId);
		request.setStatus(RequestStatus.NEW);
		request.setAction(RequestAction.DELETE_FACILITY);
		request.setAttributes(null);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		Long requestId = requestManager.createRequest(request);
		request.setReqId(requestId);

		return requestId;
	}

	@Override
	public boolean updateRequest(Long requestId, Long userId, Map<String, Attribute> attributes)
			throws UnauthorizedActionException {
		if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request");
		}

		Request request = requestManager.getRequestByReqId(requestId);

		request.setAttributes(attributes);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));

		return requestManager.updateRequest(request);
	}

	@Override
	public boolean askForApproval(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		if (! RequestStatus.NEW.equals(request.getStatus()) ||
			! RequestStatus.WFC.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot ask for approval, request not marked as NEW nor WAITING_FOR_CHANGES");
		}

		request.setStatus(RequestStatus.WFA);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		requestManager.updateRequest(request);

		return true;
	}

	@Override
	public boolean cancelRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request");
		}

		Request request = requestManager.getRequestByReqId(requestId);
		switch (request.getStatus()) {
			case APPROVED:
			case REJECTED:
			case CANCELED:
				throw new CannotChangeStatusException("Cannot ask for abort, request already has status "
						+ request.getStatus());
		}

		request.setStatus(RequestStatus.WFC);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		return requestManager.updateRequest(request);
	}

	@Override
	public boolean renewRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User is not registered as admin in request");
		}

		Request request = requestManager.getRequestByReqId(requestId);

		if (! RequestStatus.WFC.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot ask for renew, request not marked as WAITING_FOR_CANCEL");
		}

		request.setStatus(RequestStatus.NEW);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		return requestManager.updateRequest(request);
	}

	@Override
	public boolean moveToProduction(Long facilityId, Long userId) {
		//TODO: implement
		return false;
	}

	@Override
	public Request getDetailedRequest(Long requestId, Long userId) throws UnauthorizedActionException {
		if (! isAdminInRequest(requestId, userId)) {
			throw new UnauthorizedActionException("User cannot view request, user is not a requester");
		}
		return requestManager.getRequestByReqId(requestId);
	}

	@Override
	public Facility getDetailedFacility(Long facilityId, Long userId) throws UnauthorizedActionException {
		if (! isFacilityAdmin(facilityId, userId)) {
			throw new UnauthorizedActionException("User cannot view facility, user is not an admin");
		}
		Map<String, Attribute> attrs = perunConnector.getFacilityAttributes(facilityId);
		Facility facility = perunConnector.getFacilityById(facilityId);
		facility.setAttrs(attrs);
		return facility;
	}

	@Override
	public List<Request> getAllRequestsUserCanAccess(Long userId) {
		List<Request> requests = requestManager.getAllRequestsByUserId(userId);
		Set<Long> whereAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);
		requests.addAll(requestManager.getRequestsByFacilityIds(whereAdmin));

		return new ArrayList<>(new HashSet<>(requests));
	}

	@Override
	public List<Facility> getAllFacilitiesWhereUserIsAdmin(Long userId) {
		return perunConnector.getFacilitiesWhereUserIsAdmin(userId);
	}

	private boolean isFacilityAdmin(Long facilityId, Long userId) {
		Set<Long> whereAdmin = perunConnector.getFacilityIdsWhereUserIsAdmin(userId);

		if (whereAdmin == null || whereAdmin.isEmpty()) {
			return false;
		}

		return whereAdmin.contains(facilityId);
	}

	private boolean isAdminInRequest(Long reqUserId, Long userId) {
		return reqUserId.equals(userId);
	}
}
