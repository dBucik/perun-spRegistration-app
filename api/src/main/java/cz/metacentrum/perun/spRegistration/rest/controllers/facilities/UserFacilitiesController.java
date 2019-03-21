package cz.metacentrum.perun.spRegistration.rest.controllers.facilities;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.UserCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@RestController
public class UserFacilitiesController {

	private static final Logger log = LoggerFactory.getLogger(UserFacilitiesController.class);

	private final UserCommandsService service;

	public UserFacilitiesController(UserCommandsService service) {
		this.service = service;
	}

	@RequestMapping(path = "/api/userFacilities", method = RequestMethod.GET)
	public List<Facility> userFacilities(@SessionAttribute("user") User user) throws SpRegistrationApiException {
		log.debug("userFacilities({})", user.getId());
		try {
			return service.getAllFacilitiesWhereUserIsAdmin(user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
