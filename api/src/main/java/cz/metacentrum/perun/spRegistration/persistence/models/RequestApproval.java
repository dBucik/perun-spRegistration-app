package cz.metacentrum.perun.spRegistration.persistence.models;

import java.sql.Timestamp;

/**
 * Approval for request of transferring service into production environment.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class RequestApproval {

	private Long requestId;
	private Long signerId;
	private String signerName;
	private String signerInput;
	private Timestamp signedAt;

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getSignerId() {
		return signerId;
	}

	public void setSignerId(Long signerId) {
		this.signerId = signerId;
	}

	public String getSignerName() {
		return signerName;
	}

	public void setSignerName(String signerName) {
		this.signerName = signerName;
	}

	public String getSignerInput() {
		return signerInput;
	}

	public void setSignerInput(String signerInput) {
		this.signerInput = signerInput;
	}

	public Timestamp getSignedAt() {
		return signedAt;
	}

	public void setSignedAt(Timestamp signedAt) {
		this.signedAt = signedAt;
	}

	@Override
	public String toString() {
		return "RequestApproval{" +
				"requestId=" + requestId +
				", signerId=" + signerId +
				", signerName='" + signerName + '\'' +
				", signerInput='" + signerInput + '\'' +
				", signedAt=" + signedAt +
				'}';
	}
}
