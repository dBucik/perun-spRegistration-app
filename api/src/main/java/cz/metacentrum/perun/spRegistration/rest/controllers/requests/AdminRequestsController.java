package cz.metacentrum.perun.spRegistration.rest.controllers.requests;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public List<Request> allRequests(@SessionAttribute("user") User user)
			throws UnauthorizedActionException
	{
		log.debug("allRequests({})", user.getId());

		List<Request> requestList = service.getAllRequests(user.getId());
		log.trace("allRequests() returns: {}", requestList);
		return requestList;
	}

	@PostMapping(path = "/api/approve/{requestId}")
	public boolean approveRequest(@SessionAttribute("user") User user,
								  @PathVariable("requestId") Long requestId)
			throws ConnectorException, CannotChangeStatusException, InternalErrorException, UnauthorizedActionException
	{
		log.debug("approveRequest(user: {}, requestId: {})", user.getId(), requestId);
		
		boolean successful = service.approveRequest(requestId, user.getId());
		log.trace("approveRequest() returns: {}", successful);
		return successful;
	}

	@PostMapping(path = "/api/reject/{requestId}")
	public boolean rejectRequest(@SessionAttribute("user") User user,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody String message)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException
	{
		log.debug("rejectRequest(user: {}, requestId: {}, message: {})", user.getId(), requestId, message);
		
		boolean successful = service.rejectRequest(requestId, user.getId(), message);
		log.trace("rejectRequest() returns: {}", successful);
		return successful;
	}

	@PostMapping(path = "/api/askForChanges/{requestId}")
	public boolean askForChanges(@SessionAttribute("user") User user,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody List<PerunAttribute> attributes)
			throws UnauthorizedActionException, CannotChangeStatusException, InternalErrorException
	{
		log.debug("askForChanges(user: {}, requestId: {}, attributes: {})", user.getId(), requestId, attributes);
		
		boolean successful = service.askForChanges(requestId, user.getId(), attributes);
		log.trace("askForChanges() returns: {}", successful);
		return successful;
	}
}
