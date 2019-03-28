package cz.metacentrum.perun.spRegistration.persistence.mitreid;

import java.util.Map;

public interface MitreIdConnector {

	void setCredentials(String username, String password);

	void setCreateClientEndpoint(String url);

	void setUpdateClientEndpoint(String url);

	void setDeleteClientEndpoint(String url);

	void setGetClientEndpoint(String url);

	Map<String, String> createClient();

	boolean updateClient();

	boolean deleteClient();
}
