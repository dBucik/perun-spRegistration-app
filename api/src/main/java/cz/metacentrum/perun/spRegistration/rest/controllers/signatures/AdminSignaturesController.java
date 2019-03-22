package cz.metacentrum.perun.spRegistration.rest.controllers.signatures;

import cz.metacentrum.perun.spRegistration.persistence.models.RequestSignature;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@RestController
public class AdminSignaturesController {

	private static final Logger log = LoggerFactory.getLogger(AdminSignaturesController.class);

	private final AdminCommandsService service;

	@Autowired
	public AdminSignaturesController(AdminCommandsService service) {
		this.service = service;
	}

	@GetMapping(path = "/api/viewApprovals/{requestId}")
	public List<RequestSignature> getApprovals(@SessionAttribute("user") User user,
											   @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("getApprovals(user: {}, requestId: {})", user.getId(), requestId);
		try {
			return service.getApprovalsOfProductionTransfer(requestId, user.getId());
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
