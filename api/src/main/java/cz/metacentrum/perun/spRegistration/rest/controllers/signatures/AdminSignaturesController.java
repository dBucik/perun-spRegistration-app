package cz.metacentrum.perun.spRegistration.rest.controllers.signatures;

import cz.metacentrum.perun.spRegistration.service.AdminCommandsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminSignaturesController {

	private static final Logger log = LoggerFactory.getLogger(AdminSignaturesController.class);

	private final AdminCommandsService service;

	@Autowired
	public AdminSignaturesController(AdminCommandsService service) {
		this.service = service;
	}

}
