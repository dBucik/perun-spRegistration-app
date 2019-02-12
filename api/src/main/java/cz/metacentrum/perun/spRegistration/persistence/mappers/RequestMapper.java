package cz.metacentrum.perun.spRegistration.persistence.mappers;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapper for Request object. Maps result retrieved from DB to Request object.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class RequestMapper implements RowMapper<Request> {

	private static final String ATTRIBUTES_KEY = "attributes";
	private static final String ID_KEY = "id";
	private static final String FACILITY_ID_KEY = "facility_id";
	private static final String STATUS_KEY = "status";
	private static final String ACTION_KEY = "action";
	private static final String REQUESTING_USER_ID_KEY = "requesting_user_id";
	private static final String MODIFIED_BY_KEY = "modified_by";
	private static final String MODIFIED_AT_KEY = "modified_at";

	@Autowired
	private AppConfig appConfig;

	public RequestMapper(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	@Override
	public Request mapRow(ResultSet resultSet, int i) throws SQLException {
		Request request = new Request();
		request.setReqId(resultSet.getLong(ID_KEY));
		request.setFacilityId(resultSet.getLong(FACILITY_ID_KEY));
		if (resultSet.wasNull()) {
			request.setFacilityId(null);
		}
		request.setStatus(RequestStatus.resolve(resultSet.getInt(STATUS_KEY)));
		request.setAction(RequestAction.resolve(resultSet.getInt(ACTION_KEY)));
		request.setReqUserId(resultSet.getLong(REQUESTING_USER_ID_KEY));
		String attrsJsonStr = resultSet.getString(ATTRIBUTES_KEY);
		Map<String, PerunAttribute> attrs = mapAttributes(attrsJsonStr);
		request.setAttributes(attrs);
		request.setModifiedAt(resultSet.getTimestamp(MODIFIED_AT_KEY));
		request.setModifiedBy(resultSet.getLong(MODIFIED_BY_KEY));

		return request;
	}

	private Map<String, PerunAttribute> mapAttributes(String attrsJsonStr) {
		Map<String, PerunAttribute> attributes = new HashMap<>();
		JSONObject json = new JSONObject(attrsJsonStr);
		Iterator<String> keys = json.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			PerunAttribute mappedAttribute = PerunAttribute.fromJsonOfDb(key, json.getJSONObject(key), appConfig);
			attributes.put(key, mappedAttribute);
		}

		return attributes;
	}
}
