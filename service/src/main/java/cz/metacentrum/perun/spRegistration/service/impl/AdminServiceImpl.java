package cz.metacentrum.perun.spRegistration.service.impl;

import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.Config;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import cz.metacentrum.perun.spRegistration.service.AdminService;
import cz.metacentrum.perun.spRegistration.service.ServiceUtils;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AdminService.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {

	@Autowired
	private RequestManager requestManager;

	@Autowired
	private PerunConnector perunConnector;

	@Autowired
	private Config config;

	@Override
	public boolean approveRequest(Long requestId, Long userId)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (! config.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to approve request");
		}

		Request request = requestManager.getRequestByReqId(requestId);

		if (! RequestStatus.WFA.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot approve request, request not marked as WAITING_FOR_APPROVAL");
		}

		if (! finishRequestApproved(request)) {
			return false;
		}

		request.setStatus(RequestStatus.APPROVED);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		requestManager.updateRequest(request);

		return true;
	}

	@Override
	public boolean rejectRequest(Long requestId, Long userId, String message)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (! config.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to reject request");
		}

		Request request = requestManager.getRequestByReqId(requestId);

		if (! RequestStatus.WFA.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot reject request, request not marked as WAITING_FOR_APPROVAL");
		}

		request.setStatus(RequestStatus.REJECTED);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		requestManager.updateRequest(request);

		return true;
	}

	@Override
	public boolean askForChanges(Long requestId, Long userId, List<PerunAttribute> attributes)
			throws UnauthorizedActionException, CannotChangeStatusException {
		if (! config.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to ask for changes");
		}

		Request request = requestManager.getRequestByReqId(requestId);

		if (! RequestStatus.WFA.equals(request.getStatus())) {
			throw new CannotChangeStatusException("Cannot ask for changes, request not marked as WAITING_FOR_APPROVAL");
		}

		Map<String, PerunAttribute> convertedAttributes = ServiceUtils.transformListToMap(attributes, config);
		request.setStatus(RequestStatus.WFC);
		request.setAttributes(convertedAttributes);
		request.setModifiedBy(userId);
		request.setModifiedAt(new Timestamp(System.currentTimeMillis()));
		return requestManager.updateRequest(request);
	}

	@Override
	public List<Request> getAllRequests(Long adminId) throws UnauthorizedActionException {
		if (! config.isAdmin(adminId)) {
			throw new UnauthorizedActionException("User cannot list all requests, user is not an admin");
		}

		return requestManager.getAllRequests();
	}

	@Override
	public List<Facility> getAllFacilities(Long adminId) throws UnauthorizedActionException {
		if (! config.isAdmin(adminId)) {
			throw new UnauthorizedActionException("User cannot list all facilities, user not an admin");
		}

		Map<String, String> params = new HashMap<>();
		params.put(config.getIdpAttribute(), config.getIdpAttributeValue());
		return perunConnector.getFacilitiesViaSearcher(params);
	}

	@Override
	public boolean addAdmins(Long userId, Long facilityId, List<Long> admins) throws UnauthorizedActionException {
		if (! config.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to add facility admins");
		}

		for (Long id: admins) {
			perunConnector.addFacilityAdmin(facilityId, id);
		}

		return true;
	}

	@Override
	public boolean removeAdmins(Long userId, Long facilityId, List<Long> admins) throws UnauthorizedActionException {
		if (! config.isAdmin(userId)) {
			throw new UnauthorizedActionException("User is not authorized to remove facility admins");
		}

		for (Long id: admins) {
			perunConnector.removeFacilityAdmin(facilityId, id);
		}

		return true;
	}

	private boolean finishRequestApproved(Request request) {
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

	private boolean deleteFacilityFromPerun(Request request) {
		Long facilityId = request.getFacilityId();
		return perunConnector.deleteFacilityFromPerun(facilityId);
	}

	private boolean updateFacilityInPerun(Request request) {
		Facility actualFacility = perunConnector.getFacilityById(request.getFacilityId());
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

	private boolean registerNewFacilityToPerun(Request request) {
		Facility facility = new Facility(null);
		String newName = getName(request);
		String newDesc = getDesc(request);

		if (newName == null || newDesc == null) {
			//TODO: maybe exception?
			return false;
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
