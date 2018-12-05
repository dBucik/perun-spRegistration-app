package cz.metacentrum.perun.spRegistration.persistence.managers;

import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.DatabaseException;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Set;

/**
 * Interface for working with the Request in the database.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public interface RequestManager {

	void setJdbcTemplate(JdbcTemplate template);

	/**
	 * Create request in DB.
	 * @param request Request object to be stored.
	 * @return Generated ID of stored Request.
	 */
	Long createRequest(Request request) throws DatabaseException;

	/**
	 * Update request in DB.
	 * @param request Request object with updated data.
	 * @return True if everything went OK.
	 */
	boolean updateRequest(Request request) throws DatabaseException;

	/**
	 * Delete request from DB.
	 * @param reqId ID of Request to be deleted.
	 * @return True if everything went OK.
	 */
	boolean deleteRequest(Long reqId) throws DatabaseException;

	/**
	 * Get request specified by ID.
	 * @param reqId ID of request.
	 * @return Found Request object.
	 */
	Request getRequestByReqId(Long reqId);

	/**
	 * Get all requests from DB.
	 * @return List of found Request objects.
	 */
	List<Request> getAllRequests();

	/**
	 * Get all requests from DB where specified user is a requester.
	 * @param userId ID of user.
	 * @return List of found Request objects.
	 */
	List<Request> getAllRequestsByUserId(Long userId) throws DatabaseException;

	/**
	 * Get all requests from DB with specified status.
	 * @param status Status of Requests.
	 * @return List of found Request objects.
	 */
	List<Request> getAllRequestsByStatus(RequestStatus status) throws DatabaseException;

	/**
	 * Get all requests from DB associated with facility specified by ID.
	 * @param facilityId ID of associated facility.
	 * @return List of found Request objects.
	 */
	Request getRequestByFacilityId(Long facilityId) throws DatabaseException;

	/**
	 * Get all requests from DB associated with facilities specified by IDs.
	 * @param facilityIds IDs of associated facilities.
	 * @return List of found Request objects.
	 */
	List<Request> getAllRequestsByFacilityIds(Set<Long> facilityIds);

}
