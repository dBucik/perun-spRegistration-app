package cz.metacentrum.perun.spRegistration.persistence.managers.impl;

import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.DatabaseException;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.mappers.RequestMapper;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class RequestManagerImpl implements RequestManager {

	private static final Logger log = LoggerFactory.getLogger(RequestManagerImpl.class);

	private NamedParameterJdbcTemplate jdbcTemplate;
	private static final RequestMapper MAPPER = new RequestMapper();

	@Override
	public void setJdbcTemplate(JdbcTemplate template) {
		if (template == null) {
			log.error("Trying to provide NULL as jdbcTemplate");
			throw new IllegalArgumentException("JDBCTemplate is null");
		} else if (template.getDataSource() == null) {
			log.error("Trying to provide jdbcTemplate without assigned dataSource");
			throw new IllegalArgumentException("JDBCTemplate does not contain dataSource");
		}
		this.jdbcTemplate = new NamedParameterJdbcTemplate(template.getDataSource());
	}

	@Override
	public Long createRequest(Request request) throws DatabaseException {
		String query = "INSERT INTO spregistration.registrations(" +
				"(facility_id, status, action, req_user_id, attributes, modified_by, modified_at) " +
				"VALUES (:fac_id, :status, :action, :req_user_id, :attributes, :modified_by, :modified_at)";

		KeyHolder key = new GeneratedKeyHolder();
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", request.getFacilityId());
		params.addValue("status", request.getStatus().getAsInt());
		params.addValue("action", request.getAction().getAsInt());
		params.addValue("req_user_id", request.getReqUserId());
		params.addValue("attributes", request.getAttributesAsJsonForDb());
		params.addValue("modified_by", request.getModifiedBy());
		params.addValue("modified_at", request.getModifiedAt());

		jdbcTemplate.update(query, params, key);
		return (Long) key.getKey();
	}

	@Override
	public boolean updateRequest(Request request) throws DatabaseException {
		String query = "UPDATE spregistration.registrations(" +
				"SET facility_id = :fac_id, status = :status, action = :action, req_user_id = :req_user_id, " +
				"attributes = :attributes, modified_by = :modified_by, modified_at = :modified_at " +
				"WHERE req_id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", request.getFacilityId());
		params.addValue("status", request.getStatus().getAsInt());
		params.addValue("action", request.getAction().getAsInt());
		params.addValue("req_user_id", request.getReqUserId());
		params.addValue("attributes", request.getAttributesAsJsonForDb());
		params.addValue("modified_by", request.getModifiedBy());
		params.addValue("modified_at", request.getModifiedAt());
		params.addValue("req_id", request.getReqId());

		jdbcTemplate.update(query, params);
		return true;
	}

	@Override
	public boolean deleteRequest(Long reqId) throws DatabaseException {
		String query = "DELETE FROM spregistration.requests " +
				"WHERE req_id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		jdbcTemplate.update(query, params);
		return true;
	}

	@Override
	public Request getRequestByReqId(Long reqId) {
		String query = "SELECT * FROM spregistration.requests " +
				"WHERE req_id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		return jdbcTemplate.queryForObject(query, params, MAPPER);
	}

	@Override
	public List<Request> getAllRequests() {
		String query = "SELECT * FROM spregistration.requests";

		return jdbcTemplate.query(query, MAPPER);
	}

	@Override
	public List<Request> getAllRequestsByUserId(Long userId) throws DatabaseException {
		String query = "SELECT * FROM spregistration.requests " +
				"WHERE req_user_id = :req_user_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_user_id", userId);

		return jdbcTemplate.query(query, params, MAPPER);
	}

	@Override
	public List<Request> getAllRequestsByStatus(RequestStatus status) throws DatabaseException {
		String query = "SELECT * FROM spregistration.requests " +
				"WHERE status = :status";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("status", status.getAsInt());

		return jdbcTemplate.query(query, params, MAPPER);
	}

	@Override
	public Request getRequestByFacilityId(Long facilityId) throws DatabaseException {
		String query = "SELECT * FROM spregistration.requests " +
				"WHERE facility_id = :fac_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", facilityId);

		return jdbcTemplate.queryForObject(query, params, MAPPER);
	}

	@Override
	public List<Request> getRequestsByFacilityIds(Set<Long> facilityIds) {
		String query = "SELECT * FROM spregistration.requests " +
				"WHERE ";
		StringJoiner joiner = new StringJoiner(" OR ");
		MapSqlParameterSource params = new MapSqlParameterSource();
		int counter = 0;
		for (Long id : facilityIds) {
			counter++;
			joiner.add("facility_id = :fac" + counter);
			params.addValue(String.valueOf("fac" + counter), id);
		}

		query += joiner.toString();

		return jdbcTemplate.query(query, params, MAPPER);
	}
}
