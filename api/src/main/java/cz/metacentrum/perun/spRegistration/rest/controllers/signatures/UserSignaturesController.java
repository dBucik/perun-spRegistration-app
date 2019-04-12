package cz.metacentrum.perun.spRegistration.rest.controllers.signatures;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.CreateRequestException;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.UserCommandsService;
import cz.metacentrum.perun.spRegistration.service.exceptions.ExpiredCodeException;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.MalformedCodeException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.List;

@RestController
public class UserSignaturesController {

	private static final Logger log = LoggerFactory.getLogger(UserSignaturesController.class);

	private final UserCommandsService service;

	@Autowired
	public UserSignaturesController(UserCommandsService service) {
		this.service = service;
	}

	@PostMapping(path = "/api/moveToProduction/createRequest/{facilityId}")
	public Long moveToProduction(@SessionAttribute("user") User user,
								 @PathVariable("facilityId") Long facilityId,
								 @RequestBody List<String> authorities)
			throws BadPaddingException, InvalidKeyException, ConnectorException, IllegalBlockSizeException,
			UnsupportedEncodingException, InternalErrorException, CreateRequestException, UnauthorizedActionException 
	{
		log.debug("moveToProduction(user: {}, facilityId: {} authorities: {})", user.getId(), facilityId, authorities);
		
		Long generatedId = service.requestMoveToProduction(facilityId, user.getId(), authorities);

		log.trace("moveToProduction() returns: {}", generatedId);
		return generatedId;
	}

	@GetMapping(path = "/api/moveToProduction/getFacilityDetails", params = "code")
	public Request signRequestGetData(String code)
			throws BadPaddingException, ConnectorException, IllegalBlockSizeException, MalformedCodeException,
			InvalidKeyException, ExpiredCodeException, UnsupportedEncodingException
	{
		log.debug("signRequestGetData({})", code);
		if (code.startsWith("\"")) {
			code = code.substring(1, code.length() - 1);
		}
		code = URLDecoder.decode(code, StandardCharsets.UTF_8.toString());
		Request request = service.getRequestDetailsForSignature(code);

		log.trace("signRequestGetData() returns: {}", request);
		return request;
	}

	@PostMapping(path = "/api/moveToProduction/approve")
	public boolean signApprovalForProduction(@SessionAttribute("user") User user,
											 @RequestBody String code)
			throws UnsupportedEncodingException, BadPaddingException, ExpiredCodeException, IllegalBlockSizeException,
			MalformedCodeException, InternalErrorException, InvalidKeyException
	{
		log.debug("signApprovalForProduction(user: {}, code: {})", user, code);
		if (code.startsWith("\"")) {
			code = code.substring(1, code.length() - 1);
		}
		code = URLDecoder.decode(code, StandardCharsets.UTF_8.toString());
		boolean successful = service.signTransferToProduction(user, code);

		log.trace("signApprovalForProduction() returns: {}", successful);
		return successful;
	}
}
