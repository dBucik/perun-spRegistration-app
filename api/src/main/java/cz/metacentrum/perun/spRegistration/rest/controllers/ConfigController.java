package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.configs.Config;
import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ConfigController {

	private static final Logger log = LoggerFactory.getLogger(ConfigController.class);

	private final Config config;

	@Autowired
	public ConfigController(Config config) {
		this.config = config;
	}

	@RequestMapping(path = "/api/config/oidcInputs", method = RequestMethod.GET)
	public List<List<AttrInput>> getInputsForOidc() {
		log.debug("getInputsForOidc()");
		List<List<AttrInput>> inputs = new ArrayList<>();
		inputs.add(config.getServiceInputs());
		inputs.add(config.getOrganizationInputs());
		inputs.add(config.getOidcInputs());
		inputs.add(config.getMembershipInputs());
		return inputs;
	}

	@RequestMapping(path = "/api/config/samlInputs", method = RequestMethod.GET)
	public List<List<AttrInput>> getInputsForSaml() {
		log.debug("getInputsForSaml()");
		List<List<AttrInput>> inputs = new ArrayList<>();
		inputs.add(config.getServiceInputs());
		inputs.add(config.getOrganizationInputs());
		inputs.add(config.getSamlInputs());
		inputs.add(config.getMembershipInputs());
		return inputs;
	}

	@RequestMapping(path = "/api/config/oidcEnabled", method = RequestMethod.GET)
	public boolean getOidcEnabled() {
		log.debug("getOidcEnabled()");
		return config.getAppConfig().isOidcEnabled();
	}

	@RequestMapping(path = "/api/config/langs", method = RequestMethod.GET)
	public List<String> getLangs() {
		log.debug("getLangs()");
		return config.getAppConfig().getLangs();
	}

	@RequestMapping(path = "/api/config/isUserAdmin", method = RequestMethod.GET)
	public boolean isUserAdmin(@SessionAttribute("user") User user) {
		log.debug("isUserAdmin()");
		return config.getAppConfig().isAdmin(user.getId());
	}

	@RequestMapping(path = "/api/config/pageConfig", method = RequestMethod.GET)
	public Map<String, String> getPageConfig() {
		log.debug("getPageConfig()");
		Map<String, String> pageConfig = new HashMap<>();
		pageConfig.put("logoUrl", config.getAppConfig().getHeaderLogo());
		pageConfig.put("headerLabel", config.getAppConfig().getHeaderTitle());
		pageConfig.put("footerHtml", config.getAppConfig().getFooterHTML());
		pageConfig.put("headerHtml", config.getAppConfig().getHeaderHTML());

		return pageConfig;
	}

	@RequestMapping(path = "/api/config/specifyAuthoritiesEnabled", method = RequestMethod.GET)
	public boolean getSpecifyAuthoritiesEnabled() {
		log.debug("getSpecifyAuthoritiesEnabled()");
		return config.getAppConfig().getSpecifyAuthoritiesEnabled();
	}
}
