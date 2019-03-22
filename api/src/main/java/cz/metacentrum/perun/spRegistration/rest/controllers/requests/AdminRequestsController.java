package cz.metacentrum.perun.spRegistration.rest.controllers.requests;

import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@RestController
public class AdminRequestsController {
	
	private static final Logger log = LoggerFactory.getLogger(AdminRequestsController.class);
	
	private final AdminCommandsService service;
	
	@Autowired
	public AdminRequestsController(AdminCommandsService service) {
		this.service = service;
	}

	@GetMapping(path = "/api/allRequests")
	public List<Request> allRequests(@SessionAttribute("user") User user) throws SpRegistrationApiException {
		log.debug("allRequests({})", user.getId());
		try {
			return service.getAllRequests(user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@PostMapping(path = "/api/approve/{requestId}")
	public boolean approveRequest(@SessionAttribute("user") User user,
								  @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("approveRequest(user: {}, requestId: {})", user.getId(), requestId);
		try {
			return service.approveRequest(requestId, user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@PostMapping(path = "/api/reject/{requestId}")
	public boolean rejectRequest(@SessionAttribute("user") User user,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody String message) throws SpRegistrationApiException {
		log.debug("rejectRequest(user: {}, requestId: {}, message: {})", user.getId(), requestId, message);
		try {
			return service.rejectRequest(requestId, user.getId(), message);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@PostMapping(path = "/api/askForChanges/{requestId}")
	public boolean askForChanges(@SessionAttribute("user") User user,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("askForChanges(user: {}, requestId: {}, attributes: {})", user.getId(), requestId, attributes);
		try {
			return service.askForChanges(requestId, user.getId(), attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
