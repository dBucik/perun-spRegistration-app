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
			log.error("Illegal parameters passed: template IS NULL");
			throw new IllegalArgumentException();
		} else if (template.getDataSource() == null) {
			log.error("Illegal parameters passed: template.dataSource IS NULL");
			throw new IllegalArgumentException();
		}
		
		this.jdbcTemplate = new NamedParameterJdbcTemplate(template.getDataSource());
	}

	@Override
	public Long createRequest(Request request) throws InternalErrorException, CreateRequestException {
		log.trace("PERS: createRequest({})", request);
		if (request == null) {
			log.error("Illegal parameters passed: request IS NULL");
			throw new IllegalArgumentException();
		}

		Long activeRequestId = this.getActiveRequestIdByFacilityId(request.getFacilityId());
		if (activeRequestId != null) {
			log.error("Active requests already exist for facilityId: {}", request.getFacilityId());
			throw new CreateRequestException();
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

		log.trace("PERS: createRequest returns: {}", result);
		return result;
	}

	@Override
	public boolean updateRequest(Request request) {
		log.trace("PERS: updateRequest({})", request);
		if (request == null) {
			log.error("Illegal parameters passed: request IS NULL");
			throw new IllegalArgumentException();
		}
		
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

		log.trace("PERS: updateRequest returns: {}", true);
		return true;
	}

	@Override
	public boolean deleteRequest(Long reqId) {
		log.trace("PERS: deleteRequest({})", reqId);
		if (reqId == null) {
			log.error("Illegal parameters passed: reqId IS NULL");
			throw new IllegalArgumentException();
		}
		
		String query = "DELETE FROM" + REQUESTS_TABLE + "WHERE id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		jdbcTemplate.update(query, params);

		log.trace("PERS: deleteRequest returns: {}", true);
		return true;
	}

	@Override
	public Request getRequestById(Long reqId) {
		log.trace("PERS: getRequestById({})", reqId);
		if (reqId == null) {
			log.error("Illegal parameters passed: reqId IS NULL");
			throw new IllegalArgumentException();
		}
		
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE id = :req_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_id", reqId);

		Request request = jdbcTemplate.queryForObject(query, params, REQUEST_MAPPER);

		log.trace("PERS: getRequestById returns: {}", request);
		return request;
	}

	@Override
	public List<Request> getAllRequests() {
		log.trace("PERS: getAllRequests()");
		String query = "SELECT * FROM" + REQUESTS_TABLE;

		List<Request> requests = jdbcTemplate.query(query, REQUEST_MAPPER);

		log.trace("PERS: getAllRequests returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByUserId(Long userId) {
		log.trace("PERS: getAllRequestsByUserId({})", userId);
		if (userId == null) {
			log.error("Illegal parameters passed: userId IS NULL");
			throw new IllegalArgumentException();
		}
		
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE requesting_user_id = :req_user_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("req_user_id", userId);

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.trace("PERS: getAllRequestsByUserId returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByStatus(RequestStatus status) {
		log.trace("PERS: getAllRequestsByStatus({})", status);
		if (status == null) {
			log.error("Illegal parameters passed: status IS NULL");
			throw new IllegalArgumentException();
		}
		
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE status = :status";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("status", status.getAsInt());

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.trace("getAllRequestsByStatus returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByAction(RequestAction action) {
		log.trace("PERS: getAllRequestsByAction({})", action);
		if (action == null) {
			log.error("Illegal parameters passed: action IS NULL");
			throw new IllegalArgumentException();
		}
		
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE action = :action";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("action", action.getAsInt());

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.trace("PERS: getAllRequestsByAction returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByFacilityId(Long facilityId) {
		log.trace("PERS: getAllRequestsByFacilityId({})", facilityId);
		if (facilityId == null) {
			log.error("Illegal parameters passed: facilityId IS NULL");
			throw new IllegalArgumentException();
		}

		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE facility_id = :fac_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", facilityId);

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.trace("PERS: getAllRequestsByFacilityId returns: {}", requests);
		return requests;
	}

	@Override
	public List<Request> getAllRequestsByFacilityIds(Set<Long> facilityIds) {
		log.trace("PERS: getAllRequestsByFacilityIds({})", facilityIds);
		if (facilityIds == null || facilityIds.isEmpty()) {
			log.error("Illegal parameters passed: facilityIds IS NULL OR EMPTY: {}", facilityIds);
			throw new IllegalArgumentException();
		}
		
		String query = "SELECT * FROM" + REQUESTS_TABLE + "WHERE facility_id IN (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", new ArrayList<>(facilityIds));

		List<Request> requests = jdbcTemplate.query(query, params, REQUEST_MAPPER);

		log.trace("PERS: getAllRequestsByFacilityIds returns: {}", requests);
		return requests;
	}

	@Override
	public Long getActiveRequestIdByFacilityId(Long facilityId) throws InternalErrorException {
		log.trace("PERS: getActiveRequestIdByFacilityId({})", facilityId);
		if (facilityId == null) {
			return null;
		}

		List<Integer> allowedStatuses = Arrays.asList(RequestStatus.APPROVED.getAsInt(), RequestStatus.REJECTED.getAsInt());

		String query = "SELECT id FROM " + REQUESTS_TABLE +
				"WHERE facility_id = :fac_id AND status NOT IN (:allowed_statuses)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fac_id", facilityId);
		params.addValue("allowed_statuses", allowedStatuses);

		Long result;
		try {
			result = jdbcTemplate.queryForObject(query, params, Long.class);
		} catch (EmptyResultDataAccessException e) {
			result = null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new InternalErrorException("Two active requests for one facility found", e);
		}

		log.trace("PERS: getActiveRequestIdByFacilityId returns: {}", result);
		return result;
	}

	@Override
	public boolean addSignature(Long requestId, Long userId, String userName) {
		log.trace("PERS: addSignature(requestId: {}, userId: {}, userName: {})", requestId, userId, userName);
		if (requestId == null || userId == null || userName == null || userName.isEmpty()) {
			log.error("Wrong parameters passed: (requestId: {}, user:Id {}, userName: {})", requestId, userId, userName);
			throw new IllegalArgumentException();
		}
		
		String query = "INSERT INTO" + APPROVALS_TABLE +
				"(request_id, user_id, name) VALUES (:request_id, :user_id, :username)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("request_id", requestId);
		params.addValue("user_id", userId);
		params.addValue("username", userName);

		int res = jdbcTemplate.update(query, params);
		
		log.trace("PERS: addSignature returns: {}", res == 1);
		return res == 1;
	}

	@Override
	public List<RequestSignature> getRequestSignatures(Long requestId) {
		log.trace("PERS: getRequestSignatures({})", requestId);
		if (requestId == null) {
			log.error("Illegal parameters passed: requestId IS NULL");
			throw new IllegalArgumentException();
		}

		String query = "SELECT * FROM" + APPROVALS_TABLE + "WHERE request_id = :request_id";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("request_id", requestId);

		List<RequestSignature> approvals = jdbcTemplate.query(query, params, REQUEST_SIGNATURE_MAPPER);

		log.trace("PERS: getRequestSignatures returns: {}", approvals);
		return approvals;
	}
}
