package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.configs.Config;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Controller
public class HomeController {

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private Config config;
	@Autowired
	private PerunConnector connector;

	@RequestMapping(path = {"/", "/home"}, method = RequestMethod.GET)
	public String getLoggedUser(HttpServletRequest req) throws RPCException {
		String userEmailAttr = config.getAppConfig().getUserEmailAttr();
		String extSourceProxy = config.getAppConfig().getExtSourceProxy();
		log.debug("settingUser");
		String sub = req.getRemoteUser();
		if (sub != null && !sub.isEmpty()) {
			log.debug("found userId: {} ", sub);
			User user = connector.getUserWithEmail(sub, extSourceProxy, userEmailAttr);
			log.debug("found user: {}", user);

			req.getSession().setAttribute("user", user);
		}

		return "forward:/index.html";
	}
}
