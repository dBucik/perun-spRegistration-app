package cz.metacentrum.perun.spRegistration.rest.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Controller
public class AppErrorController implements ErrorController {

	@RequestMapping("/error")
	@GetMapping
	public String handleError(HttpServletRequest request) {
		return "forward:/index.html";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}
