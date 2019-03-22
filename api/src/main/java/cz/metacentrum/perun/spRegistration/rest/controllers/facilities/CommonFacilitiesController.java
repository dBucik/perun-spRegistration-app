package cz.metacentrum.perun.spRegistration.rest.controllers.facilities;


import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.UserCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
public class CommonFacilitiesController {

	private static final Logger log = LoggerFactory.getLogger(CommonFacilitiesController.class);

	private final UserCommandsService service;

	@Autowired
	public CommonFacilitiesController(UserCommandsService service) {
		this.service = service;
	}

	@GetMapping(path = "/api/facility/{facilityId}")
	public Facility facilityDetail(@SessionAttribute("user") User user,
								   @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		log.debug("facilityDetail(user(): {}, facilityId: {})", user.getId(), facilityId);
		try {
			return service.getDetailedFacility(facilityId, user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
