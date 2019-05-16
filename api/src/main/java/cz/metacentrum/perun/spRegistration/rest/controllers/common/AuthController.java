package cz.metacentrum.perun.spRegistration.rest.controllers.common;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller handling authentication
 *
 * @author Dominik Frantisek Bucik &lt;bucik@ics.muni.cz&gt;
 */
@RestController
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	private final AppConfig appConfig;
	private final PerunConnector connector;

	@Value("${dev.enabled}")
	private boolean devEnabled;

	@Autowired
	public AuthController(AppConfig appConfig, PerunConnector connector) {
		this.appConfig = appConfig;
		this.connector = connector;
	}


	@GetMapping(path = "/api/setUser")
	public void setUser(HttpServletRequest req) throws ConnectorException {
		log.trace("setUser()");
		String userEmailAttr = appConfig.getUserEmailAttributeName();
		String extSourceProxy = appConfig.getLoginExtSource();
		log.debug("settingUser");
		String sub;
		if (devEnabled) {
			sub = req.getHeader("fake-usr-hdr");
			log.debug("setting fake user: {}", sub);
		} else {
			sub = req.getRemoteUser();
			log.debug("setting user: {}", sub);
		}

		if (sub != null && !sub.isEmpty()) {
			log.debug("found userId: {} ", sub);
			User user = connector.getUserWithEmail(sub, extSourceProxy, userEmailAttr);
			user.setAdmin(appConfig.isAppAdmin(user.getId()));
			log.debug("found user: {}", user);

			req.getSession().setAttribute("user", user);
		}
	}

	@GetMapping(path = "/api/getUser")
	public User getUser(@SessionAttribute("user") User user) {
		log.trace("getUser() returns: {}", user);
		return user;
	}

}
