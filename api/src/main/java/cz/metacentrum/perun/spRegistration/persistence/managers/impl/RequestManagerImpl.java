package cz.metacentrum.perun.spRegistration.persistence.managers.impl;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.mappers.RequestApprovalMapper;
import cz.metacentrum.perun.spRegistration.persistence.mappers.RequestMapper;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.RequestApproval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class RequestManagerImpl implements RequestManager {

	private static final Logger log = LoggerFactory.getLogger(RequestManagerImpl.class);
	private static final String REQUESTS_TABLE = " requests ";
	private static final String APPROVALS_TABLE = " approvals ";

	private RequestMapper requestMapper;
	private RequestApprovalMapper requestApprovalMapper;
	private NamedParameterJdbcTemplate jdbcTemplate;

	public RequestManagerImpl() {
		requestApprovalMapper = new RequestApprovalMapper();
	}

	public void setAppConfig(AppConfig config) {
		requestMapper = new RequestMapper(config);
	}

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
	public Long createRequest(Request request) {
		String query = "INSERT INTO" + REQUESTS_TABLE +
				"(facility_id, status, action, requesting_user_id, attributes, modified_by) " +
				"VALUES (:fac_id, :status, :action, :req_user_id, :attributes, :modified_by)";

		KeyHolder key = new GeneratedKeyHolder();
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", request.getFacilityId());
		params.addValue("status", request.getStatus().getAsInt());
		params.addValue("action", request.getAction().getAsInt());
		params.addValue("req_user_id", request.getReqUserId());
		params.addValue("attributes", request.getAttributesAsJsonForDb());
		params.addValue("modified_by", request.getModifiedBy());

		jdbcTemplate.update(query, params, key, new String[] { "id" });
		return (Long) key.getKey();
	}

	@Override
	public boolean updateRequest(Request request) {
		String query = "UPDATE" + REQUESTS_TABLE +
				"SET facility_id = :fac_id, status = :status, action = :action, requesting_user_id = :req_user_id, " +
				"attributes = :attributes, modified_by = :modified_by, modified_at = NOW()" +
				"WHERE id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", request.getFacilityId());
		params.addValue("status", request.getStatus().getAsInt());
		params.addValue("action", request.getAction().getAsInt());
		params.addValue("req_user_id", request.getReqUserId());
		params.addValue("attributes", request.getAttributesAsJsonForDb());
		params.addValue("modified_by", request.getModifiedBy());
		params.addValue("req_id", request.getReqId());

		jdbcTemplate.update(query, params);
		return true;
	}

	@Override
	public boolean deleteRequest(Long reqId) {
		String query = "DELETE FROM" + REQUESTS_TABLE +
				"WHERE id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		jdbcTemplate.update(query, params);
		return true;
	}

	@Override
	public Request getRequestByReqId(Long reqId) {
		String query = "SELECT * FROM" + REQUESTS_TABLE +
				"WHERE id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		return jdbcTemplate.queryForObject(query, params, requestMapper);
	}

	@Override
	public List<Request> getAllRequests() {
		String query = "SELECT * FROM" + REQUESTS_TABLE;

		return jdbcTemplate.query(query, requestMapper);
	}

	@Override
	public List<Request> getAllRequestsByUserId(Long userId) {
		String query = "SELECT * FROM" + REQUESTS_TABLE +
				"WHERE requesting_user_id = :req_user_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_user_id", userId);

		return jdbcTemplate.query(query, params, requestMapper);
	}

	@Override
	public List<Request> getAllRequestsByStatus(RequestStatus status) {
		String query = "SELECT * FROM" + REQUESTS_TABLE +
				"WHERE status = :status";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("status", status.getAsInt());

		return jdbcTemplate.query(query, params, requestMapper);
	}

	@Override
	public List<Request> getAllRequestsByAction(RequestAction action) {
		String query = "SELECT * FROM" + REQUESTS_TABLE +
				"WHERE action = :action";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("action", action.getAsInt());

		return jdbcTemplate.query(query, params, requestMapper);
	}

	@Override
	public Request getRequestByFacilityId(Long facilityId) {
		String query = "SELECT * FROM" + REQUESTS_TABLE +
				"WHERE facility_id = :fac_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", facilityId);

		return jdbcTemplate.queryForObject(query, params, requestMapper);
	}

	@Override
	public List<Request> getAllRequestsByFacilityIds(Set<Long> facilityIds) {
		String query = "SELECT * FROM" + REQUESTS_TABLE +
				"WHERE facility_id IN (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", new ArrayList(facilityIds));

		return jdbcTemplate.query(query, params, requestMapper);
	}

	@Override
	public boolean addSignature(Long requestId, Long userId, String fullName, String approvalName) {
		String query = "INSERT INTO" + APPROVALS_TABLE +
				"(request_id, signer_id, signer_name, signer_input) " +
				"VALUES (:req_id, :signer_id, :signer_name, :signer_input)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", requestId);
		params.addValue("signer_id", userId);
		params.addValue("signer_name", fullName);
		params.addValue("signer_input", approvalName);

		jdbcTemplate.update(query, params);
		return true;
	}

	@Override
	public List<RequestApproval> getApprovalsForRequest(Long requestId) {
		String query = "SELECT * FROM" + APPROVALS_TABLE +
				"WHERE request_id = :req_id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", requestId);

		return jdbcTemplate.query(query, params, requestApprovalMapper);
	}
}
