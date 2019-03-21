package cz.metacentrum.perun.spRegistration.rest.controllers.requests;

import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.UserCommandsService;
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

import java.util.List;

@RestController
public class UserRequestsController {

	private static final Logger log = LoggerFactory.getLogger(UserRequestsController.class);

	private final UserCommandsService service;

	@Autowired
	public UserRequestsController(UserCommandsService service) {
		this.service = service;
	}

	@RequestMapping(path = "/api/userRequests", method = RequestMethod.GET)
	public List<Request> userRequests(@SessionAttribute("user") User user) throws SpRegistrationApiException {
		log.debug("userRequests({})", user.getId());
		try {
			return service.getAllRequestsUserCanAccess(user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/register", method = RequestMethod.POST)
	public Long createRegistrationRequest(@SessionAttribute("user") User user,
										  @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("createRegistrationRequest(user: {}, attributes: {})", user.getId(), attributes);
		try {
			return service.createRegistrationRequest(user.getId(), attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/changeFacility/{facilityId}", method = RequestMethod.POST)
	public Long createFacilityChangesRequest(@SessionAttribute("user") User user,
											 @RequestBody List<PerunAttribute> attributes,
											 @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		log.debug("createFacilityChangesRequest(user: {}, facilityId: {}, attributes: {})", user.getId(), facilityId, attributes);
		try {
			return service.createFacilityChangesRequest(facilityId, user.getId(), attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/remove/{facilityId}", method = RequestMethod.POST)
	public Long createRemovalRequest(@SessionAttribute("user") User user,
									 @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		log.debug("createRemovalRequest(user: {}, facilityId: {})", user.getId(), facilityId);
		try {
			return service.createRemovalRequest(user.getId(), facilityId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/update/{requestId}", method = RequestMethod.POST)
	public boolean updateRequest(@SessionAttribute("user") User user,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("updateRequest(user: {}, requestId: {}, attributes: {})", user.getId(), requestId, attributes);
		try {
			return service.updateRequest(requestId, user.getId(), attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
