package cz.metacentrum.perun.spRegistration.rest.controllers.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Controller
public class AppErrorController implements ErrorController {

	private static final Logger log = LoggerFactory.getLogger(AppErrorController.class);

	@GetMapping(value = "/error")
	public String handleError(HttpServletRequest request) {
		log.debug("handleError({})", request.getRequestURL());
		return "forward:/index.html";
	}

	@Override
	public String getErrorPath() {
		log.debug("getErrorPath()");
		return "/error";
	}
}