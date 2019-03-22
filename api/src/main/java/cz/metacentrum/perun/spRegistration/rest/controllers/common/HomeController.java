package cz.metacentrum.perun.spRegistration.rest.controllers.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Controller
public class HomeController {

	@GetMapping(path = "/home")
	public String getLoggedUser() {
		return "forward:/index.html";
	}
}
