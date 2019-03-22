package cz.metacentrum.perun.spRegistration.rest.controllers.common;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;

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
	public void setUser(HttpServletRequest req) throws RPCException {
		String userEmailAttr = appConfig.getUserEmailAttr();
		String extSourceProxy = appConfig.getExtSourceProxy();
		log.debug("settingUser");
		String sub;
		if (devEnabled) {
			sub = req.getHeader("fake-usr-hdr");
		} else {
			sub = req.getRemoteUser();
		}

		if (sub != null && !sub.isEmpty()) {
			log.debug("found userId: {} ", sub);
			User user = connector.getUserWithEmail(sub, extSourceProxy, userEmailAttr);
			log.debug("found user: {}", user);

			req.getSession().setAttribute("user", user);
		}
	}

	@GetMapping(path = "/api/getUser")
	public User getUser(@SessionAttribute("user") User user) {
		return user;
	}

}
