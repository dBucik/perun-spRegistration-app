package cz.metacentrum.perun.spRegistration.persistence.rpc;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Connects to Perun and obtains information.
 *
 * @author Dominik František Bučík <bucik@ics.muni.cz>
 */
public interface PerunConnector {

	/**
	 * Create facility in Perun.
	 * @param facilityJson JSON of facility to be created.
	 * @return Created facility.
	 */
	Facility createFacilityInPerun(String facilityJson) throws RPCException;

	/**
	 * Update existing facility in Perun.
	 * @param facilityJson JSON of facility to be created.
	 * @return Updated facility.
	 */
	Facility updateFacilityInPerun(String facilityJson) throws RPCException;

	/**
	 * Delete facility from Perun.
	 * @param facilityId ID of facility to be deleted.
	 * @return True if everything went OK.
	 */
	boolean deleteFacilityFromPerun(Long facilityId) throws RPCException;

	/**
	 * Get facility from Perun with specified ID.
	 * @param facilityId ID of facility.
	 * @return Retrieved facility.
	 */
	Facility getFacilityById(Long facilityId) throws RPCException;

	/**
	 * Get facilities having specified attribute.
	 * @param attributesWithSearchingValues Map of attributes: key = attrName, value = attrValue.
	 * @return List of found facilities.
	 */
	List<Facility> getFacilitiesViaSearcher(Map<String, String> attributesWithSearchingValues) throws RPCException;

	/**
	 * Get facilities where user is admin (manager).
	 * @param userId ID of user.
	 * @return List of found facilities.
	 */
	List<Facility> getFacilitiesWhereUserIsAdmin(Long userId);

	/**
	 * Get attribute of facility.
	 * @param facilityId ID of facility.
	 * @param attrName Name of the attribute.
	 * @return Retrieved attribute.
	 */
	Attribute getFacilityAttribute(Long facilityId, String attrName);

	/**
	 * Get all attributes of facility.
	 * @param facilityId ID of facility.
	 * @return Map (key = attribute name, value = attribute) of facility attributes.
	 */
	Map<String, Attribute> getFacilityAttributes(Long facilityId);

	/**
	 * Get specified attributes for facility.
	 * @param facilityId ID of facility.
	 * @param attrNames Names of attributes to be retrieved.
	 * @return Map (key = attribute name, value = attribute) of facility attributes.
	 */
	Map<String, Attribute> getFacilityAttributes(Long facilityId, List<String> attrNames);

	/**
	 * Get IDs of facilities where user is admin (manager).
	 * @param userId ID of user.
	 * @return Set of facility IDs.
	 */
	Set<Long> getFacilityIdsWhereUserIsAdmin(Long userId);

	/**
	 * Set attribute for facility in Perun.
	 * @param facilityId ID of facility.
	 * @param attrJson JSON representation of attribute.
	 * @return True if everything went OK.
	 */
	boolean setFacilityAttribute(Long facilityId, String attrJson);

	/**
	 * Set attributes for facility in Perun.
	 * @param facilityId ID of facility.
	 * @param attrsJsons List of JSON representations of attributes.
	 * @return True if everything went OK.
	 */
	boolean setFacilityAttributes(Long facilityId, List<String> attrsJsons);

	/**
	 * Get user from Perun.
	 * @param userId ID of user.
	 * @return Retrieved user object.
	 */
	User getRichUser(Long userId);

	/**
	 * Add user as an admin (manager) of facility.
	 * @param facilityId ID of facility.
	 * @param userId ID of user.
	 * @return True if everything went OK.
	 */
	boolean addFacilityAdmin(Long facilityId, Long userId);

	/**
	 * Remove user from admins (managers) of facility.
	 * @param facilityId ID of facility.
	 * @param userId ID of user.
	 * @return True if everything went OK.
	 */
	boolean removeFacilityAdmin(Long facilityId, Long userId);

}