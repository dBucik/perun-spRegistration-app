package cz.metacentrum.perun.spRegistration.rest.controllers.facilities;


import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeCategory;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.FacilitiesService;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

/**
 * Controller handling common actions related to Facilities.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
@RestController
public class CommonFacilitiesController {

	private static final Logger log = LoggerFactory.getLogger(CommonFacilitiesController.class);

	private final FacilitiesService facilitiesService;

	@Autowired
	public CommonFacilitiesController(FacilitiesService facilitiesService) {
		this.facilitiesService = facilitiesService;
	}

	@GetMapping(path = "/api/facility/{facilityId}")
	public Facility facilityDetail(@SessionAttribute("user") User user,
								   @PathVariable("facilityId") Long facilityId)
			throws UnauthorizedActionException, InternalErrorException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		log.trace("facilityDetail(user(): {}, facilityId: {})", user.getId(), facilityId);

		Facility facility = facilitiesService.getFacility(facilityId, user.getId(), true, true);

		log.trace("facilityDetail() returns: {}", facility);
		return facility;
	}

	@GetMapping(path = "/api/facility/signature/{facilityId}")
	public Facility facilityDetailSignature(@SessionAttribute("user") User user,
											@PathVariable("facilityId") Long facilityId)
			throws UnauthorizedActionException, InternalErrorException, ConnectorException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		log.trace("facilityDetailSignature(user(): {}, facilityId: {})", user.getId(), facilityId);

		Facility facility = facilitiesService.getFacility(facilityId, user.getId(), false, false);
		facility.getAttributes().get(AttributeCategory.PROTOCOL).clear();
		facility.getAttributes().get(AttributeCategory.ACCESS_CONTROL).clear();

		log.trace("facilityDetailSignature() returns: {}", facility);
		return facility;
	}

}
