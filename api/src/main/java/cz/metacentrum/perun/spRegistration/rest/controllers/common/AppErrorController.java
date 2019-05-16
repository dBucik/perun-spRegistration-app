package cz.metacentrum.perun.spRegistration.rest.controllers.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller handling errors
 *
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Controller
public class AppErrorController implements ErrorController {

	private static final Logger log = LoggerFactory.getLogger(AppErrorController.class);

	@GetMapping(value = "/error")
	public String handleError(HttpServletRequest request) {
		log.trace("handleError({})", request.getRequestURL());
		return "forward:/index.html";
	}

	@Override
	public String getErrorPath() {
		log.trace("getErrorPath()");
		return "/error";
	}
}