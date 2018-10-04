package cz.metacentrum.perun.spRegistration.persistence.rpc.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.mappers.Utils;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Connects to Perun via RPC.
 *
 * @author Dominik František Bučík bucik@ics.muni.cz
 */
public class PerunConnectorRpc implements PerunConnector {

	private static final Logger log = LoggerFactory.getLogger(PerunConnectorRpc.class);

	private String perunRpcUrl;
	private String perunRpcUser;
	private String perunRpcPassword;

	private static final String USERS_MANAGER = "usersManager";
	private static final String FACILITIES_MANAGER = "facilitiesManager";
	private static final String ATTRIBUTES_MANAGER = "attributesManager";
	private static final String SEARCHER = "searcher";


	public void setPerunRpcUrl(String perunRpcUrl) {
		this.perunRpcUrl = perunRpcUrl;
	}

	public void setPerunRpcUser(String perunRpcUser) {
		this.perunRpcUser = perunRpcUser;
	}

	public void setPerunRpcPassword(String perunRpcPassword) {
		this.perunRpcPassword = perunRpcPassword;
	}

	@Override
	public Facility createFacilityInPerun(String facilityJson) throws RPCException {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityJson);

		JsonNode res = makeRpcCall(FACILITIES_MANAGER, "createFacility", params);
		return Utils.mapFacility(res, false);
	}

	@Override
	public Facility updateFacilityInPerun(String facilityJson) throws RPCException {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityJson);

		JsonNode res = makeRpcCall(FACILITIES_MANAGER, "updateFacility", params);
		return Utils.mapFacility(res, false);
	}

	@Override
	public boolean deleteFacilityFromPerun(Long facilityId) throws RPCException {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);

		makeRpcCall(FACILITIES_MANAGER, "deleteFacility", params);
		return true;
	}

	@Override
	public Facility getFacilityById(Long facilityId) throws RPCException {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("id", facilityId);

		JsonNode res = makeRpcCall(FACILITIES_MANAGER, "getFacilityById", params);
		return Utils.mapFacility(res, false);
	}

	@Override
	public List<Facility> getFacilitiesViaSearcher(Map<String, String> attributesWithSearchingValues) throws RPCException {
		log.debug("getFacilitiesViaSearcher: {}", attributesWithSearchingValues);
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("attributesWithSearchingValues", attributesWithSearchingValues);

		JsonNode res = makeRpcCall(SEARCHER, "getFacilities", params);
		log.debug("response: {}", res);
		return Utils.mapFacilities(res, false);
	}

	@Override
	public List<Facility> getFacilitiesWhereUserIsAdmin(Long userId) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("user", userId);

		JsonNode res = makeRpcCall(FACILITIES_MANAGER, "getFacilities", params);
		return Utils.mapFacilities(res, false);
	}

	@Override
	public Attribute getFacilityAttribute(Long facilityId, String attrName) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attributeName", attrName);

		JsonNode res = makeRpcCall(ATTRIBUTES_MANAGER, "getAttribute", params);
		return Utils.mapAttribute(res);
	}

	@Override
	public Map<String, Attribute> getFacilityAttributes(Long facilityId) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);

		JsonNode res = makeRpcCall(ATTRIBUTES_MANAGER, "getAttributes", params);
		return Utils.mapAttributes(res);
	}

	@Override
	public Map<String, Attribute> getFacilityAttributes(Long facilityId, List<String> attrNames) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attrNames", attrNames);

		JsonNode res = makeRpcCall(ATTRIBUTES_MANAGER, "getAttributes", params);
		return Utils.mapAttributes(res);
	}

	@Override
	public boolean setFacilityAttribute(Long facilityId, String attrJson) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attribute", attrJson);

		makeRpcCall(ATTRIBUTES_MANAGER, "setAttribute", params);
		return true;
	}

	@Override
	public boolean setFacilityAttributes(Long facilityId, List<String> attrsJsons) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attributes", attrsJsons);

		makeRpcCall(ATTRIBUTES_MANAGER, "setAttributes", params);
		return true;
	}

	@Override
	public User getRichUser(Long userId) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("user", userId);

		JsonNode res = makeRpcCall(USERS_MANAGER, "getRichUser", params);
		return Utils.mapUser(res, true);
	}

	@Override
	public boolean addFacilityAdmin(Long facilityId, Long userId) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("user", userId);

		makeRpcCall(FACILITIES_MANAGER, "addAdmin", params);
		return true;
	}

	@Override
	public boolean removeFacilityAdmin(Long facilityId, Long userId) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("user", userId);

		makeRpcCall(FACILITIES_MANAGER, "removeAdmin", params);
		return true;
	}

	@Override
	public Set<Long> getFacilityIdsWhereUserIsAdmin(Long userId) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("user", userId);

		JsonNode res = makeRpcCall(FACILITIES_MANAGER, "getFacilitiesWhereUserIsAdmin", params);
		Set<Long> ids = new HashSet<>();

		for (int i = 0; i < res.size(); i++) {
			JsonNode facility = res.get(i);
			ids.add(facility.get("id").asLong());
		}

		return ids;
	}

	private JsonNode makeRpcCall(String manager, String method, Map<String, Object> map) {
		//prepare basic auth
		RestTemplate restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptors =
				Collections.singletonList(new BasicAuthorizationInterceptor(perunRpcUser, perunRpcPassword));
		restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), interceptors));
		String actionUrl = perunRpcUrl + "/json/" + manager + '/' + method;
		//make the call

		try {
			return restTemplate.postForObject(actionUrl, map, JsonNode.class);
		} catch (HttpClientErrorException ex) {
			MediaType contentType = ex.getResponseHeaders().getContentType();
			String body = ex.getResponseBodyAsString();
			if ("json".equals(contentType.getSubtype())) {
				try {
					new ObjectMapper().readValue(body, JsonNode.class).path("message").asText();
				} catch (IOException e) {
					log.error("cannot parse error message from JSON",e);
				}
			} else {
				log.error(ex.getMessage());
			}
			throw new RuntimeException("cannot connect to Perun RPC",ex);
		}
	}
}
