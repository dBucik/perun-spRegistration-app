package cz.metacentrum.perun.spRegistration.persistence.rpc.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.mappers.MapperUtils;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import org.json.JSONArray;
import org.json.JSONObject;
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
		if (facilityJson == null) {
			throw new IllegalArgumentException("facilityJson is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityJson);

		JSONObject res = makeRpcCallForObject(FACILITIES_MANAGER, "createFacility", params);
		return MapperUtils.mapFacility(res);
	}

	@Override
	public Facility updateFacilityInPerun(String facilityJson) throws RPCException {
		if (facilityJson == null) {
			throw new IllegalArgumentException("facilityJson is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityJson);

		JSONObject res = makeRpcCallForObject(FACILITIES_MANAGER, "updateFacility", params);
		return MapperUtils.mapFacility(res);
	}

	@Override
	public boolean deleteFacilityFromPerun(Long facilityId) throws RPCException {
		if (facilityId == null) {
			throw new IllegalArgumentException("facilityId is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);

		makeRpcCallForObject(FACILITIES_MANAGER, "deleteFacility", params);
		return true;
	}

	@Override
	public Facility getFacilityById(Long facilityId) throws RPCException {
		if (facilityId == null) {
			throw new IllegalArgumentException("facilityId is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("id", facilityId);

		JSONObject res = makeRpcCallForObject(FACILITIES_MANAGER, "getFacilityById", params);
		return MapperUtils.mapFacility(res);
	}

	@Override
	public List<Facility> getFacilitiesViaSearcher(Map<String, String> attributesWithSearchingValues) throws RPCException {
		if (attributesWithSearchingValues == null) {
			throw new IllegalArgumentException("attriutesWithSearchinValues is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("attributesWithSearchingValues", attributesWithSearchingValues);

		JSONArray res = makeRpcCallForArray(SEARCHER, "getFacilities", params);
		return MapperUtils.mapFacilities(res);
	}

	@Override
	public List<Facility> getFacilitiesWhereUserIsAdmin(Long userId) throws RPCException {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("user", userId);

		JSONArray res = makeRpcCallForArray(FACILITIES_MANAGER, "getFacilitiesWhereUserIsAdmin", params);
		return MapperUtils.mapFacilities(res);
	}

	@Override
	public PerunAttribute getFacilityAttribute(Long facilityId, String attrName) throws RPCException {
		if (facilityId == null || attrName == null) {
			throw new IllegalArgumentException("Illegal input: facilityId: " + facilityId + ", attrName: " + attrName);
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attributeName", attrName);

		JSONObject res = makeRpcCallForObject(ATTRIBUTES_MANAGER, "getAttribute", params);
		return MapperUtils.mapAttribute(res);
	}

	@Override
	public Map<String, PerunAttribute> getFacilityAttributes(Long facilityId) throws RPCException {
		if (facilityId == null) {
			throw new IllegalArgumentException("facilityId is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);

		JSONArray res = makeRpcCallForArray(ATTRIBUTES_MANAGER, "getAttributes", params);
		return MapperUtils.mapAttributes(res);
	}

	@Override
	public Map<String, PerunAttribute> getFacilityAttributes(Long facilityId, List<String> attrNames) throws RPCException {
		if (facilityId == null || attrNames == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", attrNames" + attrNames);
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attrNames", attrNames);

		JSONArray res = makeRpcCallForArray(ATTRIBUTES_MANAGER, "getAttributes", params);
		return MapperUtils.mapAttributes(res);
	}

	@Override
	public boolean setFacilityAttribute(Long facilityId, String attrJson) throws RPCException {
		if (facilityId == null || attrJson == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", attrJson" + attrJson);
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attribute", attrJson);

		makeRpcCallForArray(ATTRIBUTES_MANAGER, "setAttribute", params);
		return true;
	}

	@Override
	public boolean setFacilityAttributes(Long facilityId, List<String> attrsJsons) throws RPCException {
		if (facilityId == null || attrsJsons == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", attrJsons" + attrsJsons);
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("attributes", attrsJsons);

		makeRpcCallForArray(ATTRIBUTES_MANAGER, "setAttributes", params);
		return true;
	}

	@Override
	public User getRichUser(Long userId) throws RPCException {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("user", userId);

		JSONObject res = makeRpcCallForObject(USERS_MANAGER, "getRichUser", params);
		return MapperUtils.mapUser(res, true);
	}

	@Override
	public boolean addFacilityAdmin(Long facilityId, Long userId) throws RPCException {
		if (facilityId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", userId: " + userId);
		}
		return addRemoveFacilityAdmin(facilityId, userId, true);
	}

	@Override
	public boolean removeFacilityAdmin(Long facilityId, Long userId) throws RPCException {
		if (facilityId == null || userId == null) {
			throw new IllegalArgumentException("Illegal input - facilityId: " + facilityId + ", userId: " + userId);
		}
		return addRemoveFacilityAdmin(facilityId, userId, false);
	}

	@Override
	public Set<Long> getFacilityIdsWhereUserIsAdmin(Long userId) throws RPCException {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		List<Facility> facilities = getFacilitiesWhereUserIsAdmin(userId);
		Set<Long> ids = new HashSet<>();

		facilities.forEach(f -> ids.add(f.getId()));
		return ids;
	}

	@Override
	public PerunAttributeDefinition getAttributeDefinition(String attributeName) throws RPCException {
		if (attributeName == null) {
			throw new IllegalArgumentException("attributeName is null");
		}
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("attributeName", attributeName);

		JSONObject res = makeRpcCallForObject(ATTRIBUTES_MANAGER, "getAttributeDefinition", params);
		return PerunAttributeDefinition.fromPerunJson(res);
	}

	private JSONObject makeRpcCallForObject(String manager, String method, Map<String, Object> map) throws RPCException {
		String response = makeRpcCall(manager, method, map);
		return new JSONObject(response);
	}

	private JSONArray makeRpcCallForArray(String manager, String method, Map<String, Object> map) throws RPCException {
		String response = makeRpcCall(manager, method, map);
		return new JSONArray(response);
	}

	private String makeRpcCall(String manager, String method, Map<String, Object> map) throws RPCException {
		//prepare basic auth
		RestTemplate restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptors =
				Collections.singletonList(new BasicAuthorizationInterceptor(perunRpcUser, perunRpcPassword));
		restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), interceptors));
		String actionUrl = perunRpcUrl + "/json/" + manager + '/' + method;

		//make the call
		try {
			JsonNode response = restTemplate.postForObject(actionUrl, map, JsonNode.class);
			return prettyPrintJsonString(response);
		} catch (HttpClientErrorException ex) {
			MediaType contentType = ex.getResponseHeaders().getContentType();
			String body = ex.getResponseBodyAsString();
			if ("json".equals(contentType.getSubtype())) {
				try {
					new ObjectMapper().readValue(body, JsonNode.class).path("message").asText();
				} catch (IOException e) {
					log.error("cannot parse error message from JSON", e);
				}
			} else {
				log.error(ex.getMessage());
			}
			throw new RPCException("cannot connect to Perun RPC", ex);
		} catch (IOException e) {
			log.error("cannot parse response to String", e);
			throw new RPCException("cannot connect to Perun RPC", e);
		}
	}

	private boolean addRemoveFacilityAdmin(Long facilityId, Long userId, boolean add) throws RPCException {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("facility", facilityId);
		params.put("user", userId);

		if (add) {
			makeRpcCallForObject(FACILITIES_MANAGER, "addAdmin", params);
		} else {
			makeRpcCallForObject(FACILITIES_MANAGER, "removeAdmin", params);
		}
		return true;
	}

	private static String prettyPrintJsonString(JsonNode jsonNode) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(jsonNode.toString(), Object.class);

		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	}
}
