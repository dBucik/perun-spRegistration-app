package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.service.UserService;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@RestController
@SessionAttributes("userId")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserService service;

	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}

	@RequestMapping(path = "/api/userFacilities", method = RequestMethod.GET)
	public List<Facility> userFacilities(@SessionAttribute("userId") Long userId) throws SpRegistrationApiException {
		log.debug("userFacilities({})", userId);
		try {
			return service.getAllFacilitiesWhereUserIsAdmin(userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/userRequests", method = RequestMethod.GET)
	public List<Request> userRequests(@SessionAttribute("userId") Long userId) throws SpRegistrationApiException {
		log.debug("userRequests({})", userId);
		try {
			return service.getAllRequestsUserCanAccess(userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/register", method = RequestMethod.POST)
	public Long createRegistrationRequest(@SessionAttribute("userId") Long userId,
										  @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("createRegistrationRequest(userId: {}, attributes: {})", userId, attributes);
		try {
			return service.createRegistrationRequest(userId, attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/changeFacility/{facilityId}", method = RequestMethod.POST)
	public Long createFacilityChangesRequest(@SessionAttribute("userId") Long userId,
											 @PathVariable("facilityId") Long facilityId,
											 @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("createFacilityChangesRequest(userId: {}, facilityId: {}, attributes: {})", userId, facilityId, attributes);
		try {
			return service.createFacilityChangesRequest(facilityId, userId, attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/remove/{facilityId}", method = RequestMethod.POST)
	public Long createRemovalRequest(@SessionAttribute("userId") Long userId,
									 @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		log.debug("createRemovalRequest(userId: {}, facilityId: {})", userId, facilityId);
		try {
			return service.createRemovalRequest(userId, facilityId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/update/{requestId}", method = RequestMethod.POST)
	public boolean updateRequest(@SessionAttribute("userId") Long userId,
								 @PathVariable("requestId") Long requestId,
								 @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		log.debug("updateRequest(userId: {}, requestId: {}, attributes: {})", userId, requestId, attributes);
		try {
			return service.updateRequest(requestId, userId, attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/askApproval/{requestId}", method = RequestMethod.GET)
	public boolean askForApproval(@SessionAttribute("userId") Long userId,
								  @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("askForApproval(userId: {}, requestId: {})", userId, requestId);
		try {
			return service.askForApproval(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/cancel/{requestId}", method = RequestMethod.GET)
	public boolean cancelRequest(@SessionAttribute("userId") Long userId,
								 @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("cancelRequest(userId: {}, requestId: {})", userId, requestId);
		try {
			return service.cancelRequest(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/renew/{requestId}", method = RequestMethod.GET)
	public boolean renewRequest(@SessionAttribute("userId") Long userId,
							    @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("renewRequest(userId: {}, requestId: {})", userId, requestId);
		try {
			return service.renewRequest(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/moveToProduction/{facilityId}", method = RequestMethod.GET)
	public Long moveToProduction(@SessionAttribute("userId") Long userId,
								 @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		log.debug("moveToProduction(userId: {}, facilityId: {})", userId, facilityId);
		try {
			return service.moveToProduction(facilityId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/moveToProduction/{requestId}", method = RequestMethod.POST)
	public boolean signApprovalForProduction(@SessionAttribute("userId") Long userId,
											 @PathVariable("requestId") Long requestId,
											 @RequestBody String signerInput) throws SpRegistrationApiException {
		log.debug("signApprovalForProduction(userId: {}, requestId: {}, signerInput: {})", userId, requestId, signerInput);
		try {
			return service.signTransferToProduction(requestId, userId, signerInput);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/facility/{facilityId}", method = RequestMethod.GET)
	public Facility facilityDetail(@SessionAttribute("userId") Long userId,
								   @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		log.debug("facilityDetail(userId: {}, facilityId: {})", userId, facilityId);
		try {
			return service.getDetailedFacility(facilityId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/request/{requestId}", method = RequestMethod.GET)
	public Request requestDetail(@SessionAttribute("userId") Long userId,
								 @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("requestDetail(userId: {}, requestId: {})", userId, requestId);
		try {
			return service.getDetailedRequest(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/sign/{requestId}", method = RequestMethod.GET)
	public Request signRequestGetData(@PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		log.debug("signRequestGetData(requestId: {})", requestId);
		try {
			return service.getRequestDetailsForSignature(requestId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/sign/{requestId}", method = RequestMethod.POST)
	public boolean signRequest(@SessionAttribute Long userId,
							   @PathVariable("requestId") Long requestId,
							   @RequestBody String personInput) throws SpRegistrationApiException {
		log.debug("signRequest(requestId: {})", requestId);
		try {
			return service.signTransferToProduction(requestId, userId, personInput);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

}
