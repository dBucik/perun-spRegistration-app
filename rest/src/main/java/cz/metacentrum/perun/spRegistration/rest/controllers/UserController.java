package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;
import cz.metacentrum.perun.spRegistration.rest.ViewData;
import cz.metacentrum.perun.spRegistration.service.UserService;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Map;

@RestController
@SessionAttributes("userId")
public class UserController {

	private final UserService service;

	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}

	@RequestMapping(path = "/api/", method = RequestMethod.GET)
	public ViewData userOverview(@SessionAttribute("userId") Long userId) {
		ViewData res = new ViewData();
		res.setFacilities(service.getAllFacilitiesWhereUserIsAdmin(userId));
		res.setRequests(service.getAllRequestsUserCanAccess(userId));

		return res;
	}

	@RequestMapping(path = "/api/register")
	public Long createRegistrationRequest(@SessionAttribute("userId") Long userId,
										  @RequestBody Map<String, Attribute> attributes) {
		return service.createRegistrationRequest(userId, attributes);
	}

	@RequestMapping(path = "/api/changeFacility/{facilityId}")
	public Long createFacilityChangesRequest(@SessionAttribute("userId") Long userId,
											 @RequestBody Map<String, Attribute> attributes,
											 @PathVariable("facilityId") Long facilityId)
			throws UnauthorizedActionException {
		return service.createFacilityChangesRequest(facilityId, userId, attributes);
	}

	@RequestMapping(path = "/api/remove/{facilityId}")
	public Long createRemovalRequest(@SessionAttribute("userId") Long userId,
									 @PathVariable("facilityId") Long facilityId) throws UnauthorizedActionException {
		return service.createRemovalRequest(userId, facilityId);
	}

	@RequestMapping(path = "/api/update/{requestId}")
	public String updateRequest(@SessionAttribute("userId") Long userId,
								@RequestBody Map<String, Attribute> attributes,
								@PathVariable("requestId") Long requestId) throws UnauthorizedActionException {
		if (service.updateRequest(requestId, userId, attributes)) {
			return "Your request has been updated";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/askApproval/{requestId}")
	public String askForApproval(@SessionAttribute("userId") Long userId,
								 @PathVariable("requestId") Long requestId)
			throws CannotChangeStatusException, UnauthorizedActionException
	{
		if (service.askForApproval(requestId, userId)) {
			return "Request has been forwarded to administrator for approval";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/cancel/{requestId}")
	public String cancelRequest(@SessionAttribute("userId") Long userId,
								@PathVariable("requestId") Long requestId)
			throws CannotChangeStatusException, UnauthorizedActionException
	{
		if (service.cancelRequest(requestId, userId)) {
			return "Your request has been canceled";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/renew/{requestId}")
	public String renewRequest(@SessionAttribute("userId") Long userId,
							   @PathVariable("requestId") Long requestId)
			throws CannotChangeStatusException, UnauthorizedActionException
	{
		if (service.renewRequest(requestId, userId)) {
			return "Request has been renewed, now you can edit it or ask for approval";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/moveToProduction/{facilityId}")
	public String moveToProduction(@SessionAttribute("userId") Long userId,
								   @PathVariable("facilityId") Long facilityId)
			throws UnauthorizedActionException
	{
		if (service.moveToProduction(facilityId, userId)) {
			return "Your request for moving the SP to production environment has been created";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/facility/{facilityId}")
	public Facility facilityDetail(@SessionAttribute("userId") Long userId,
								   @PathVariable("facilityId") Long facilityId) throws UnauthorizedActionException {
		return service.getDetailedFacility(facilityId, userId);

	}

	@RequestMapping(path = "/api/request/{requestId}")
	public Request requestDetail(@SessionAttribute("userId") Long userId,
							  @PathVariable("requestId") Long requestId) throws UnauthorizedActionException {
		return service.getDetailedRequest(requestId, userId);
	}

}
