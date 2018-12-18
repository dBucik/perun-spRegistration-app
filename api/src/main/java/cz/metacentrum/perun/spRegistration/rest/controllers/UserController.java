package cz.metacentrum.perun.spRegistration.rest.controllers;

import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.service.UserService;
import cz.metacentrum.perun.spRegistration.service.exceptions.CannotChangeStatusException;
import cz.metacentrum.perun.spRegistration.service.exceptions.SpRegistrationApiException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;
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

	private final UserService service;

	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}

	@RequestMapping(path = "/api/userFacilities", method = RequestMethod.GET)
	public List<Facility> userFacilities(@SessionAttribute("userId") Long userId) throws SpRegistrationApiException {
		try {
			return service.getAllFacilitiesWhereUserIsAdmin(userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/userRequests", method = RequestMethod.GET)
	public List<Request> userRequests(@SessionAttribute("userId") Long userId) throws SpRegistrationApiException {
		try {
			return service.getAllRequestsUserCanAccess(userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/register")
	public Long createRegistrationRequest(@SessionAttribute("userId") Long userId,
										  @RequestBody List<PerunAttribute> attributes) throws SpRegistrationApiException {
		try {
			return service.createRegistrationRequest(userId, attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/changeFacility/{facilityId}")
	public Long createFacilityChangesRequest(@SessionAttribute("userId") Long userId, @RequestBody List<PerunAttribute> attributes,
											 @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		try {
			return service.createFacilityChangesRequest(facilityId, userId, attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/remove/{facilityId}")
	public Long createRemovalRequest(@SessionAttribute("userId") Long userId,
									 @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		try {
			return service.createRemovalRequest(userId, facilityId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/update/{requestId}")
	public boolean updateRequest(@SessionAttribute("userId") Long userId, @RequestBody List<PerunAttribute> attributes,
								@PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		try {
			return service.updateRequest(requestId, userId, attributes);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/askApproval/{requestId}")
	public boolean askForApproval(@SessionAttribute("userId") Long userId,
								 @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		try {
			return service.askForApproval(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/cancel/{requestId}")
	public boolean cancelRequest(@SessionAttribute("userId") Long userId,
								@PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		try {
			return service.cancelRequest(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/renew/{requestId}")
	public boolean renewRequest(@SessionAttribute("userId") Long userId,
							    @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		try {
			return service.renewRequest(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/moveToProduction/{facilityId},{authorities}")
	public Long moveToProduction(@SessionAttribute("userId") Long userId, @PathVariable("facilityId") Long facilityId,
								 @PathVariable("authorities") List<String> authorities) throws SpRegistrationApiException {
		try {
			return service.moveToProduction(facilityId, userId, authorities);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/moveToProduction/{requestId}")
	public boolean signApprovalForProduction(@SessionAttribute("userId") Long userId, @PathVariable("requestId") Long requestId,
											 @RequestBody String signerInput) throws SpRegistrationApiException {
		try {
			return service.signTransferToProduction(requestId, userId, signerInput);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

	@RequestMapping(path = "/api/facility/{facilityId}")
	public Facility facilityDetail(@SessionAttribute("userId") Long userId,
								   @PathVariable("facilityId") Long facilityId) throws SpRegistrationApiException {
		try {
			return service.getDetailedFacility(facilityId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}

	}

	@RequestMapping(path = "/api/request/{requestId}")
	public Request requestDetail(@SessionAttribute("userId") Long userId,
							  @PathVariable("requestId") Long requestId) throws SpRegistrationApiException {
		try {
			return service.getDetailedRequest(requestId, userId);
		} catch (Exception e) {
			throw new SpRegistrationApiException(e);
		}
	}

}
