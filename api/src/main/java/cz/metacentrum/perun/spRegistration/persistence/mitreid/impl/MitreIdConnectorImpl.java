package cz.metacentrum.perun.spRegistration.persistence.mitreid.impl;

import cz.metacentrum.perun.spRegistration.persistence.mitreid.MitreIdConnector;

import java.util.Map;

public class MitreIdConnectorImpl implements MitreIdConnector {

	//TODO: get endpoints and credentials from application-context.xm
	//create method for sending requests to mitre
	//add attribute for storing client ID (not client_id) in Perun
	//create method to update that attribute
	//info we want to store in mitre:
	//clientId, clienSecret, redirectUris, clientName, clientUri, contacts, scope, grantTypes, responseTypes,
	//allowIntrospection, clientDescription, policyUri,

	private String username;
	private String password;

	private String getClientEndpoint;
	private String createClientEndpoint;
	private String updateClientEndpoint;
	private String deleteClientEndpoint;

	@Override
	public void setCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void setCreateClientEndpoint(String url) {
		this.createClientEndpoint = url;
	}

	@Override
	public void setUpdateClientEndpoint(String url) {
		this.updateClientEndpoint = url;
	}

	@Override
	public void setDeleteClientEndpoint(String url) {
		this.deleteClientEndpoint = url;
	}

	@Override
	public void setGetClientEndpoint(String url) {
		this.getClientEndpoint = url;
	}

	@Override
	public Map<String, String> createClient() {
		//TODO: map values from form to keys in JSON that will be sent to mitreid
		//send JSON
		//get client_id, client_secret, id
		//store ID in Perun
		//show client_id and client_secret to the user

		return null;
	}

	@Override
	public boolean updateClient() {
		//TODO: get id of client from Perun (not client_id)
		//get client as JSON from mitre
		//update values in JSON
		//send JSON to Mitre to update the client
		//return true if all went OK

		return false;
	}

	@Override
	public boolean deleteClient() {
		//TODO: get id of client from Perun (not client_id)
		//delete client in Mitre
		//return true if all went OK

		return false;
	}
}
