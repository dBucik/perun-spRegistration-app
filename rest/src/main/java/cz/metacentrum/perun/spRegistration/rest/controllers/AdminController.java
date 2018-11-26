package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import cz.metacentrum.perun.spRegistration.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@SessionAttributes("userId")
public class AdminController {

	private AdminServiceImpl adminService;

	@Autowired
	public AdminController(AdminServiceImpl adminServiceImpl) {
		this.adminService = adminServiceImpl;
	}

	@RequestMapping(path = "/api")
	public void start(HttpServletRequest request) {
		//TODO: delete method, only for testing purposes
		request.getSession().setAttribute("userId", 62692L);
	}

	@RequestMapping(path = "/api/allFacilities", method = RequestMethod.GET)
	public List<Facility> allFacilities(@SessionAttribute("userId") Long userId) throws UnauthorizedActionException {

		return adminService.getAllFacilities(userId);
	}

	@RequestMapping(path = "/api/allRequests", method = RequestMethod.GET)
	public List<Request> allRequests(@SessionAttribute("userId") Long userId) throws UnauthorizedActionException {

		return adminService.getAllRequests(userId);
	}

	@RequestMapping(path = "/api/approve/{requestId}")
	public String approveRequest(@SessionAttribute("userId") Long userId,
							   @PathVariable("requestId") Long requestId)
			throws CannotChangeStatusException, UnauthorizedActionException
	{
		if (adminService.approveRequest(requestId, userId)) {
			return "The request has been approved";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/reject/{requestId}")
	public String rejectRequest(@SessionAttribute("userId") Long userId,
								@PathVariable("requestId") Long requestId,
								@RequestBody String message)
			throws CannotChangeStatusException, UnauthorizedActionException
	{
		if (adminService.rejectRequest(requestId, userId, message)) {
			return "The request has been rejected";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/askForChanges/{requestId}")
	public String askForChanges(@SessionAttribute("userId") Long userId,
								@PathVariable("requestId") Long requestId,
								@RequestBody List<PerunAttribute> attributes)
			throws CannotChangeStatusException, UnauthorizedActionException
	{
		if (adminService.askForChanges(requestId, userId, attributes)) {
			return "Changes have been requested";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/addAdmins/{facilityId}")
	public String addAdmins(@SessionAttribute("userId") Long userId,
							@PathVariable("facilityId") Long facilityId,
							@RequestBody List<Long> admins)
			throws UnauthorizedActionException
	{
		if (adminService.addAdmins(userId, facilityId, admins)) {
			return "Admins were successfully added";
		}

		return "Error has occurred";
	}

	@RequestMapping(path = "/api/removeAdmins/{facilityId}")
	public String removeAdmins(@SessionAttribute("userId") Long userId,
							   @PathVariable("facilityId") Long facilityId,
							   @RequestBody List<Long> admins)
			throws UnauthorizedActionException
	{
		if (adminService.removeAdmins(userId, facilityId, admins)) {
			return "Admins were successfully removed";
		}

		return "Error has occurred";
	}
}
