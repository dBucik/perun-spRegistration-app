package cz.metacentrum.perun.spRegistration.persistence.mappers;

import cz.metacentrum.perun.spRegistration.persistence.models.RequestSignature;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestSignatureMapper implements RowMapper<RequestSignature> {

	private static final String REQUEST_ID_KEY = "request_id";
	private static final String USER_ID_KEY = "user_id";
	private static final String SIGNED_AT_KEY = "signed_at";

	@Override
	public RequestSignature mapRow(ResultSet resultSet, int i) throws SQLException {
		RequestSignature approval = new RequestSignature();
		approval.setRequestId(resultSet.getLong(REQUEST_ID_KEY));
		approval.setUserId(resultSet.getLong(USER_ID_KEY));
		approval.setSignedAt(resultSet.getTimestamp(SIGNED_AT_KEY).toLocalDateTime());

		return approval;
	}
}