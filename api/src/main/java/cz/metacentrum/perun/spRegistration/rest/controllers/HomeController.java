package cz.metacentrum.perun.spRegistration.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Controller
public class HomeController {

	@RequestMapping(path = "/home", method = RequestMethod.GET)
	public String getLoggedUser() {
		return "forward:/index.html";
	}
}
