package cz.metacentrum.perun.spRegistration.persistence.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for Request object. Maps result from retrieved from DB to Request object.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class RequestMapper implements RowMapper<Request> {

	@Override
	public Request mapRow(ResultSet resultSet, int i) throws SQLException {
		Request request = new Request();

		request.setReqId(resultSet.getLong("req_id"));
		request.setFacilityId(resultSet.getLong("facility_id"));
		request.setStatus(RequestStatus.resolve(resultSet.getInt("status")));
		request.setAction(RequestAction.resolve(resultSet.getInt("action")));
		request.setReqUserId(resultSet.getLong("req_user_id"));

		String attrsJsonStr = resultSet.getString("attributes");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Attribute> attrs = new HashMap<>();
		JsonNode attrsJson;
		try {
			attrsJson = mapper.readTree(attrsJsonStr);
			for (int j = 0; j < attrsJson.size(); j++) {
				JsonNode attrJson = attrsJson.get(j);
				Attribute attr = Attribute.fromDBJsonNode(attrJson);
				if (attr != null) {
					attrs.put(attr.getFullName(), attr);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}


		request.setAttributes(attrs);
		request.setModifiedAt(resultSet.getTimestamp("modified_at"));
		request.setModifiedBy(resultSet.getLong("modified_by"));

		return request;
	}
}
