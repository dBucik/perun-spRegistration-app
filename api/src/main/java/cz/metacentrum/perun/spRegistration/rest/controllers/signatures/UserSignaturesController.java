package cz.metacentrum.perun.spRegistration.rest.controllers.signatures;

import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.UserCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class UserSignaturesController {

	private static final Logger log = LoggerFactory.getLogger(UserSignaturesController.class);

	private final UserCommandsService service;

	@Autowired
	public UserSignaturesController(UserCommandsService service) {
		this.service = service;
	}

	@RequestMapping(path = "/api/moveToProduction/createRequest/{facilityId}", method = RequestMethod.POST)
	public Long moveToProduction(@SessionAttribute("user") User user,
								 @PathVariable("facilityId") Long facilityId,
								 @RequestBody List<String> authorities) throws SpRegistrationApiException {
		log.debug("moveToProduction(user: {}, facilityId: {} authorities: {})", user.getId(), facilityId, authorities);
		try {
			return service.requestMoveToProduction(facilityId, user.getId(), authorities);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@GetMapping(path = "/api/moveToProduction/getFacilityDetails", params = "code")
	public Request signRequestGetData(String code) throws SpRegistrationApiException {
		log.debug("signRequestGetData({})", code);
		try {
			code = URLDecoder.decode(code, StandardCharsets.UTF_8.toString());
			code = code.replaceAll(" ", "+");
			return service.getRequestDetailsForSignature(code);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/moveToProduction/approve", method = RequestMethod.POST)
	public boolean signApprovalForProduction(@SessionAttribute("user") User user,
											 @RequestBody String code) throws SpRegistrationApiException {
		log.debug("signApprovalForProduction(user: {}, code: {})", user, code);
		try {
			code = URLDecoder.decode(code, StandardCharsets.UTF_8.toString());
			code = code.replaceAll(" ", "+");
			return service.signTransferToProduction(user, code);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}
}
