package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import cz.metacentrum.perun.spRegistration.service.exceptions.UnauthorizedActionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.util.List;

public interface FacilitiesService {

    /**
     * Get all facilities stored in Perun.
     * @param adminId ID of admin.
     * @return List of found facilities.
     * @throws UnauthorizedActionException when user is not authorized to perform this action.
     * @throws ConnectorException Thrown when problem while communicating with Perun RPC occur.
     * @throws IllegalArgumentException Thrown when param "adminId" is NULL.
     */
    List<Facility> getAllFacilities(Long adminId) throws UnauthorizedActionException, ConnectorException;

    /**
     * Get all facilities from Perun where user is admin (manager).
     * @param userId ID of user.
     * @return List of facilities (empty or filled).
     * @throws ConnectorException Thrown when problem while communicating with Perun RPC occur.
     * @throws IllegalArgumentException Thrown when param "userId" is NULL.
     */
    List<Facility> getAllUserFacilities(Long userId) throws ConnectorException;

    /**
     * Get detailed facility.
     * @param facilityId ID of facility.
     * @param userId ID of user.
     * @return Found facility.
     * @throws UnauthorizedActionException Thrown when user is not authorized to perform this action.
     * @throws ConnectorException Thrown when problem while communicating with Perun RPC occur.
     * @throws IllegalArgumentException Thrown when param "facilityId" is NULL, when param "userId" is NULL.
     */
    Facility getFacility(Long facilityId, Long userId, boolean checkAdmin, boolean includeClientCredentials)
            throws UnauthorizedActionException, ConnectorException, InternalErrorException, BadPaddingException,
            InvalidKeyException, IllegalBlockSizeException;

    /**
     * Get detailed facility.
     * @param facilityId ID of facility.
     * @param userId ID of user.
     * @return Found facility.
     * @throws UnauthorizedActionException when user is not authorized to perform this action.
     * @throws ConnectorException Thrown when problem while communicating with Perun RPC occur.
     * @throws IllegalArgumentException Thrown when param "facilityId" is NULL, when param "userId" is NULL.
     */
    Facility getFacilityWithInputs(Long facilityId, Long userId)
            throws UnauthorizedActionException, ConnectorException, InternalErrorException, BadPaddingException,
            InvalidKeyException, IllegalBlockSizeException;
}
