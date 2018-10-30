package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;
import cz.metacentrum.perun.spRegistration.persistence.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(
		origins = "http://localhost:4200",
		allowCredentials = "true",
		allowedHeaders = "origin, content-type, accept, authorization",
		methods = {
				RequestMethod.GET,
				RequestMethod.POST,
				RequestMethod.PUT,
				RequestMethod.DELETE,
				RequestMethod.OPTIONS,
				RequestMethod.HEAD
		}
)
@RestController
public class GeneralController {

	@Autowired
	private Config config;

	@RequestMapping(path = "/api/config/oidcInputs")
	public List<AttrInput> getInputsForOidc() {
		return config.getOidcInputs();
	}

	@RequestMapping(path = "/api/config/samlInputs")
	public List<AttrInput> getInputsForSaml() {
		return config.getSamlInputs();
	}

	@RequestMapping(path = "/api/config/oidcEnabled")
	public boolean getOidcEnabled() {
		return config.isOidcEnabled();
	}
}
