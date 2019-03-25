package cz.metacentrum.perun.spRegistration.persistence.managers.impl;

import cz.metacentrum.perun.spRegistration.persistence.configs.Config;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.CreateRequestException;
import cz.metacentrum.perun.spRegistration.persistence.managers.RequestManager;
import cz.metacentrum.perun.spRegistration.persistence.mappers.RequestMapper;
import cz.metacentrum.perun.spRegistration.persistence.mappers.RequestSignatureMapper;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import cz.metacentrum.perun.spRegistration.persistence.models.RequestSignature;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import cz.metacentrum.perun.spRegistration.service.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class RequestManagerImpl implements RequestManager {

	private static final Logger log = LoggerFactory.getLogger(RequestManagerImpl.class);
	private static final String REQUESTS_TABLE = " requests ";
	private static final String APPROVALS_TABLE = " approvals ";

	private final RequestMapper REQUEST_MAPPER;
	private final RequestSignatureMapper REQUEST_SIGNATURE_MAPPER;
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public RequestManagerImpl(Config config) {
		REQUEST_SIGNATURE_MAPPER = new RequestSignatureMapper();
		REQUEST_MAPPER = new RequestMapper(config);
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
	public Long createRequest(Request request) throws InternalErrorException, CreateRequestException {
		log.debug("createRequest({})", request);

		if (request.getFacilityId() != null) {
			Long activeRequestId = this.getActiveRequestIdByFacilityId(request.getFacilityId());
			if (activeRequestId != null) {
				throw new CreateRequestException("Active requests already exist for facility");
			}
		}

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
		Long result = (Long) key.getKey();

		log.debug("createRequest returns: {}", result);
		return result;
	}

	@Override
	public boolean updateRequest(Request request) {
		log.debug("updateRequest({})", request);
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

		log.debug("updateRequest returns: {}", true);
		return true;
	}

	@Override
	public boolean deleteRequest(Long reqId) {
		log.debug("deleteRequest({})", reqId);
		String query = "DELETE FROM" + REQUESTS_TABLE + "WHERE id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		jdbcTemplate.update(query, params);

		log.debug("deleteRequest returns: {}", true);
		return true;
	}

	@Override
	public Request getRequestByReqId(Long reqId) {
		log.debug("getRequestByReqId({})", reqId);
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		Request request = jdbcTemplate.queryForObject(query, params, REQUEST_MAPPER);

		log.debug("getRequestByReqId returns: {}", request);
		return request;
	}

	@Override
	public List<Request> getAllRequests() {
		log.debug("getAllRequests()");
		String query = "SELECT * FROM" + REQUESTS_TABLE;

		List<Request> requests = jdbcTemplate.query(query, REQUEST_MAPPER);

		log.debug("getAllRequests returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByUserId(Long userId) {
		log.debug("getAllRequestsByUserId({})", userId);
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE requesting_user_id = :req_user_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_user_id", userId);

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.debug("getAllRequestsByUserId returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByStatus(RequestStatus status) {
		log.debug("getAllRequestsByStatus({})", status);
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE status = :status";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("status", status.getAsInt());

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.debug("getAllRequestsByStatus returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByAction(RequestAction action) {
		log.debug("getAllRequestsByAction({})", action);
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE action = :action";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("action", action.getAsInt());

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.debug("getAllRequestsByAction returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByFacilityId(Long facilityId) {
		log.debug("getAllRequestsByFacilityId({})", facilityId);
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE facility_id = :fac_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", facilityId);

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.debug("getAllRequestsByFacilityId returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByFacilityIds(Set<Long> facilityIds) {
		log.debug("getAllRequestsByFacilityIds({})", facilityIds);
		if (facilityIds == null || facilityIds.isEmpty()) {
			return new ArrayList<>();
		}
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE facility_id IN (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", new ArrayList<>(facilityIds));

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.debug("getAllRequestsByFacilityIds returns: {}", requests);
		return requests;
	}

	@Override
	public boolean addSignature(Long requestId, User user) {
		log.debug("addSignature(requestId: {}, user: {})", requestId, user);
		String query = "INSERT INTO" + APPROVALS_TABLE +
				"(request_id, user_id) VALUES (:request_id, :user_id)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("request_id", requestId);
		params.addValue("user_id", user.getId());

		int res = jdbcTemplate.update(query, params);

		if (res == 1) {
			log.debug("addSignature returns: {}", true);
			return true;
		}

		return false;
	}

	@Override
	public List<RequestSignature> getRequestSignatures(Long requestId) {
		log.debug("getRequestSignatures({})", requestId);
		String query = "SELECT * FROM" + APPROVALS_TABLE + "WHERE request_id = :request_id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("request_id", requestId);

		List<RequestSignature> approvals = jdbcTemplate.query(query, params, REQUEST_SIGNATURE_MAPPER);

		log.debug("getRequestSignatures returns: {}", approvals);
		return approvals;
	}

	@Override
	public boolean storeApprovalLink(String authority, String hash, Long facilityId, String link, LocalDateTime validUntil) {
		log.debug("storeApprovalLink(authority: {}, hash {}, facilityId: {}, link: {}, validUntil: {})",
				authority, hash, facilityId, link, validUntil);
		String query = "INSERT INTO" + APPROVALS_TABLE +
				"(facility_id, link, hash, user_email, valid_until) " +
				"VALUES (:fac_id, :link, :hash, :user_email, :valid_until)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", facilityId);
		params.addValue("link", link);
		params.addValue("hash", hash);
		params.addValue("user_email", authority);
		params.addValue("valid_until", Timestamp.valueOf(validUntil));

		jdbcTemplate.update(query, params);

		log.debug("storeApprovalLink returns: {}", true);
		return true;
	}

	@Override
	public Long getActiveRequestIdByFacilityId(Long facilityId) throws InternalErrorException {
		log.debug("getActiveRequestIdByFacilityId({})", facilityId);
		String query = "SELECT id FROM " + REQUESTS_TABLE +
				"WHERE facility_id = :fac_id AND status NOT IN (:allowed_statuses)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", facilityId);
		List<Integer> allowedStatuses = Arrays.asList(RequestStatus.APPROVED.getAsInt(), RequestStatus.REJECTED.getAsInt());
		params.addValue("allowed_statuses", allowedStatuses);

		try {
			return jdbcTemplate.queryForObject(query, params, Long.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new InternalErrorException("Two active requests for one facility found", e);
		}
	}
}
