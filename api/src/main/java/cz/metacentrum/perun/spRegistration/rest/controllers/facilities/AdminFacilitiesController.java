package cz.metacentrum.perun.spRegistration.rest.controllers.facilities;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling ADMIN actions related to Facilities.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
@RestController
public class AdminFacilitiesController {

	private static final Logger log = LoggerFactory.getLogger(AdminFacilitiesController.class);

	private final AdminCommandsService service;

	@Autowired
	public AdminFacilitiesController(AdminCommandsService service) {
		this.service = service;
	}

	@GetMapping(path = "/api/allFacilities")
	public List<Facility> allFacilities(@SessionAttribute("user") User user)
			throws ConnectorException, UnauthorizedActionException
	{
		log.trace("allFacilities({})", user.getId());

		List<Facility> facilityList = service.getAllFacilities(user.getId());

		log.trace("allFacilities() returns: {}", facilityList);
		return facilityList;
	}

	@GetMapping(path = "api/facility/regenerateClientSecret/{facilityId}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public PerunAttribute generateClientSecret(@SessionAttribute("user") User user,
											   @PathVariable("facilityId") Long facilityId)
			throws UnauthorizedActionException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		log.trace("generateClientSecret(user: {}, facilityId: {})", user, facilityId);
		PerunAttribute clientSecret = service.regenerateClientSecret(user.getId(), facilityId);

		log.trace("generateClientSecret() returns: {}", clientSecret);
		return clientSecret;
	}
}
