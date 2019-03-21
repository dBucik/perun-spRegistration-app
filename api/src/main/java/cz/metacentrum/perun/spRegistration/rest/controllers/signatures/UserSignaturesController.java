package cz.metacentrum.perun.spRegistration.rest.controllers.signatures;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
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
public class UserSignaturesController {

	private static final Logger log = LoggerFactory.getLogger(UserSignaturesController.class);

	private final UserCommandsService service;

	@Autowired
	public UserSignaturesController(UserCommandsService service) {
		this.service = service;
	}

	@RequestMapping(path = "/api/moveToProduction/createRequest/{facilityId}", method = RequestMethod.POST)
	public Long moveToProduction(@SessionAttribute("user") User user,
								 @PathVariable("facilityId") Long facilityId,
								 @RequestBody List<String> authorities) throws SpRegistrationApiException {
		log.debug("moveToProduction(user: {}, facilityId: {} authorities: {})", user.getId(), facilityId, authorities);
		try {
			return service.requestMoveToProduction(facilityId, user.getId(), authorities);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/moveToProduction/getFacilityDetails/{facilityId}", method = RequestMethod.GET)
	public Facility signRequestGetData(@PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		log.debug("signRequestGetData(facilityId: {})", facilityId);
		try {
			return service.getFacilityDetailsForSignature(facilityId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/moveToProduction/approve/{facilityId}", method = RequestMethod.POST)
	public boolean signApprovalForProduction(@SessionAttribute("user") User user,
											 @PathVariable("facilityId") Long facilityId,
											 @RequestBody String hash) throws SpRegistrationApiException {
		log.debug("signApprovalForProduction(user: {}, facilityId: {}, hash: {})", user, facilityId, hash);
		try {
			return service.signTransferToProduction(facilityId, hash, user);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
