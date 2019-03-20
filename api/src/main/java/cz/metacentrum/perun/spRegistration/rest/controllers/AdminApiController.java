package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.RequestSignature;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
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

import java.util.List;

@RestController
@SessionAttributes("user")
public class AdminApiController {

	private static final Logger log = LoggerFactory.getLogger(AdminApiController.class);
	private final AdminCommandsService adminCommandsService;

	@Autowired
	public AdminApiController(AdminCommandsService adminCommandsService) {
		this.adminCommandsService = adminCommandsService;
	}

	@RequestMapping(path = "/api/allFacilities", method = RequestMethod.GET)
	public List<Facility> allFacilities(@SessionAttribute("user") User user) throws SpRegistrationApiException {
		log.debug("allFacilities({})", user.getId());
		try {
			return adminCommandsService.getAllFacilities(user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/allRequests", method = RequestMethod.GET)
	public List<Request> allRequests(@SessionAttribute("user") User user) throws SpRegistrationApiException {
		log.debug("allRequests({})", user.getId());
		try {
			return adminCommandsService.getAllRequests(user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}


	@RequestMapping(path = "/api/approve/{requestId}", method = RequestMethod.POST)
	public boolean approveRequest(@SessionAttribute("user") User user,
								  @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("approveRequest(user: {}, requestId: {})", user.getId(), requestId);
		try {
			return adminCommandsService.approveRequest(requestId, user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/reject/{requestId}", method = RequestMethod.POST)
	public boolean rejectRequest(@SessionAttribute("user") User user,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody String message) throws SpRegistrationApiException {
		log.debug("rejectRequest(user: {}, requestId: {}, message: {})", user.getId(), requestId, message);
		try {
			return adminCommandsService.rejectRequest(requestId, user.getId(), message);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/askForChanges/{requestId}", method = RequestMethod.POST)
	public boolean askForChanges(@SessionAttribute("user") User user,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("askForChanges(user: {}, requestId: {}, attributes: {})", user.getId(), requestId, attributes);
		try {
			return adminCommandsService.askForChanges(requestId, user.getId(), attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/viewApprovals/{requestId}", method = RequestMethod.GET)
	public List<RequestSignature> getApprovals(@SessionAttribute("user") User user,
											   @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("getApprovals(user: {}, requestId: {})", user.getId(), requestId);
		try {
			return adminCommandsService.getApprovalsOfProductionTransfer(requestId, user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/addAdmins/{facilityId}", method = RequestMethod.POST)
	public boolean addAdmins(@SessionAttribute("user") User user,
							 @PathVariable("facilityId") Long facilityId,
							 @RequestBody List<Long> admins) throws SpRegistrationApiException {
		log.debug("addAdmins(user: {}, facilityId: {}, admins: {})", user.getId(), facilityId, admins);
		try {
			return adminCommandsService.addAdmins(user.getId(), facilityId, admins);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/removeAdmins/{facilityId}", method = RequestMethod.POST)
	public boolean removeAdmins(@SessionAttribute("user") User user,
								@PathVariable("facilityId") Long facilityId,
								@RequestBody List<Long> admins) throws SpRegistrationApiException {
		log.debug("removeAdmins(user: {}, facilityId: {}, admins: {})", user.getId(), facilityId, admins);
		try {
			return adminCommandsService.removeAdmins(user.getId(), facilityId, admins);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
