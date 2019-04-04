package cz.metacentrum.perun.spRegistration.persistence.connectors;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.MitreIDApiException;
import cz.metacentrum.perun.spRegistration.persistence.models.MitreIdResponse;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;

import java.util.Map;

public interface MitreIdConnector {

	MitreIdResponse createClient(Map<String, PerunAttribute> attrs) throws MitreIDApiException;

	boolean updateClient(Long id, Map<String, PerunAttribute> attrs) throws MitreIDApiException;

	boolean deleteClient(Long id) throws MitreIDApiException;
}
