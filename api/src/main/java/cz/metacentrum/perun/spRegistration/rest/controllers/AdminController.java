package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.RequestApproval;
import cz.metacentrum.perun.spRegistration.service.AdminService;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static final Logger log = LoggerFactory.getLogger(AdminController.class);
	private AdminService adminService;

	@Autowired
	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@RequestMapping(path = "/api")
	public void start(HttpServletRequest request) {
		//TODO: delete method, only for testing purposes
//		request.getSession().setAttribute("userId", 62692L);
	}

	@RequestMapping(path = "/api/allFacilities", method = RequestMethod.GET)
	public List<Facility> allFacilities(@SessionAttribute("userId") Long userId) throws SpRegistrationApiException {
		log.debug("allFacilities({})", userId);
		try {
			return adminService.getAllFacilities(userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/allRequests", method = RequestMethod.GET)
	public List<Request> allRequests(@SessionAttribute("userId") Long userId) throws SpRegistrationApiException {
		log.debug("allRequests({})", userId);
		try {
			return adminService.getAllRequests(userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/approve/{requestId}")
	public boolean approveRequest(@SessionAttribute("userId") Long userId,
							   @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("approveRequest(userId: {}, requestId: {})", userId, requestId);
		try {
			return adminService.approveRequest(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/reject/{requestId}")
	public boolean rejectRequest(@SessionAttribute("userId") Long userId, @PathVariable("requestId") Long requestId,
								@RequestBody String message) throws SpRegistrationApiException {
		log.debug("rejectRequest(userId: {}, requestId: {}, message: {})", userId, requestId, message);
		try {
			return adminService.rejectRequest(requestId, userId, message);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/askForChanges/{requestId}")
	public boolean askForChanges(@SessionAttribute("userId") Long userId, @PathVariable("requestId") Long requestId,
								@RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("askForChanges(userId: {}, requestId: {}, attributes: {})", userId, requestId, attributes);
		try {
			return adminService.askForChanges(requestId, userId, attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/viewApprovals/{requestId}")
	public List<RequestApproval> getApprovals(@SessionAttribute("userId") Long userId,
											  @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("getApprovals(userId: {}, requestId: {})", userId, requestId);
		try {
			return adminService.getApprovalsOfProductionTransfer(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/addAdmins/{facilityId}")
	public boolean addAdmins(@SessionAttribute("userId") Long userId, @PathVariable("facilityId") Long facilityId,
							@RequestBody List<Long> admins) throws SpRegistrationApiException {
		log.debug("addAdmins(userId: {}, facilityId: {}, admins: {})", userId, facilityId, admins);
		try {
			return adminService.addAdmins(userId, facilityId, admins);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/removeAdmins/{facilityId}")
	public boolean removeAdmins(@SessionAttribute("userId") Long userId, @PathVariable("facilityId") Long facilityId,
							   @RequestBody List<Long> admins) throws SpRegistrationApiException {
		log.debug("removeAdmins(userId: {}, facilityId: {}, admins: {})", userId, facilityId, admins);
		try {
			return adminService.removeAdmins(userId, facilityId, admins);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
