package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.common.exceptions.CodeNotStoredException;
import cz.metacentrum.perun.spRegistration.common.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.common.exceptions.ExpiredCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.MalformedCodeException;
import cz.metacentrum.perun.spRegistration.common.exceptions.UnauthorizedActionException;
import cz.metacentrum.perun.spRegistration.common.models.PerunAttribute;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

public interface UtilsService {

    /**
     * Regenerate Client secret for OIDC facility
     * @param userId id of admin calling method
     * @param facilityId id of facility
     * @return generated and encrypted client secret
     * @throws UnauthorizedActionException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws ConnectorException
     */
    PerunAttribute regenerateClientSecret(Long userId, Long facilityId) throws UnauthorizedActionException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, ConnectorException;

    /**
     * Validate code for signature
     * @param code code to be validated
     * @return True if valid, false otherwise
     */
    boolean validateCode(String code) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, MalformedCodeException, ExpiredCodeException, CodeNotStoredException;

    boolean isFacilityAdmin(Long facilityId, Long userId) throws ConnectorException;

    PerunAttribute generateClientSecretAttribute() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException;

    boolean isAdminInRequest(Long reqUserId, Long userId);
}
