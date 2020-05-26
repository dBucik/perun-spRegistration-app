package cz.metacentrum.perun.spRegistration.persistence.managers;

import cz.metacentrum.perun.spRegistration.common.exceptions.InternalErrorException;

import java.util.List;

public interface LinkCodeManager {

    /**
     * Checks if code is valid, meaning it is stored in DB. Unused codes are stored, if the code has been used it is removed.
     * @param code code to be validated
     * @return TRUE if valid, FALSE otherwise
     * @throws IllegalArgumentException Thrown when param "code" is NULL or empty.
     */
    boolean validateCode(String code);

    /**
     * Store generated codes for signatures
     * @param codes List of codes to be stored
     * @return Number of codes stored.
     * @throws InternalErrorException Thrown when inserting codes fails.
     * @throws IllegalArgumentException Thrown when param "codes" is NULL or empty.
     */
    int storeCodes(List<String> codes) throws InternalErrorException;

    /**
     * Delete used code.
     * @param code Code to be deleted.
     * @return TRUE if code deleted, FALSE otherwise.
     * @throws InternalErrorException Thrown when more than one code has been deleted.
     * @throws IllegalArgumentException Thrown when param "code" is NULL or empty.
     */
    boolean deleteUsedCode(String code) throws InternalErrorException;
}
