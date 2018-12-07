package cz.metacentrum.perun.spRegistration.persistence.mappers;

import cz.metacentrum.perun.spRegistration.persistence.models.RequestApproval;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestApprovalMapper implements RowMapper<RequestApproval> {

	private static final String REQUEST_ID_KEY = "request_id";
	private static final String SIGNER_ID_KEY = "signer_id";
	private static final String SIGNER_NAME_KEY = "signer_name";
	private static final String SIGNER_INPUT_KEY = "signer_input";
	private static final String SIGNED_AT_KEY = "signed_at";

	@Override
	public RequestApproval mapRow(ResultSet resultSet, int i) throws SQLException {
		RequestApproval approval = new RequestApproval();
		approval.setRequestId(resultSet.getLong(REQUEST_ID_KEY));
		approval.setSignerId(resultSet.getLong(SIGNER_ID_KEY));
		approval.setSignerName(resultSet.getString(SIGNER_NAME_KEY));
		approval.setSignerInput(resultSet.getString(SIGNER_INPUT_KEY));
		approval.setSignedAt(resultSet.getTimestamp(SIGNED_AT_KEY));

		return approval;
	}
}