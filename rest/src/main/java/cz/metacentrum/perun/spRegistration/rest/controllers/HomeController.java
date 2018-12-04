package cz.metacentrum.perun.spRegistration.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
@Controller
@RequestMapping("/home")
public class HomeController {

	@GetMapping
	public String home() {
		return "forward:/index.html";
	}
}
