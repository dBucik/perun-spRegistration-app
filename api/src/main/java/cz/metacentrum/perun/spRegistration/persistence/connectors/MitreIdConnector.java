package cz.metacentrum.perun.spRegistration.persistence.connectors;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.models.MitreIdResponse;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;

import java.util.Map;

/**
 * Connects to MitreID API
 *
 * @author Dominik Frantisek Bucik &lt;bucik@ics.muni.cz&gt;
 */
public interface MitreIdConnector {

	/**
	 * Create client in mitreid
	 * @param attrs Attributes to be set
	 * @return Response from mitreId containing some interesting fields
	 * @throws ConnectorException When error occurs
	 */
	MitreIdResponse createClient(Map<String, PerunAttribute> attrs) throws ConnectorException;

	/**
	 * Update client in mitreid
	 * @param id id of client in mitreid (not client_id)
	 * @param attrs attributes to be set
	 * @return True if update was successful, false otherwise
	 * @throws ConnectorException When error occurs
	 */
	boolean updateClient(Long id, Map<String, PerunAttribute> attrs) throws ConnectorException;

	/**
	 * Delete client from mitreid
	 * @param id id of client in mitreid (not client_id)
	 * @return True if delete was successful, false otherwise
	 * @throws ConnectorException When error occurs
	 */
	boolean deleteClient(Long id) throws ConnectorException;
}
