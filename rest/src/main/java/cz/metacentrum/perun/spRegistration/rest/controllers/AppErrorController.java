package cz.metacentrum.perun.spRegistration.rest.controllers;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	@Override
	public String getErrorPath() {
		return null;
	}
}
