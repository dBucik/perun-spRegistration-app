package cz.metacentrum.perun.spRegistration.rest.controllers.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RouteToAngular {

	@RequestMapping(path = { "/", "", "/auth", "/auth/**" })
	public String forwardToAngular(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/index.html";
	}
}
