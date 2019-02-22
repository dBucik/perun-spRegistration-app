package cz.metacentrum.perun.spRegistration.persistence.models;

import java.sql.Timestamp;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (! (o instanceof RequestApproval)) {
			return false;
		}

		RequestApproval them = (RequestApproval) o;
		return Objects.equals(this.requestId, them.requestId)
				&& Objects.equals(this.signerId, them.signerId)
				&& Objects.equals(this.signerName, them.signerName)
				&& Objects.equals(this.signerInput, them.signerInput)
				&& Objects.equals(this.signedAt, them.signedAt);
	}

	@Override
	public int hashCode() {
		long res = 31 * requestId;
		res *= 31 * signerId.hashCode();
		res *= 31 * signerName.hashCode();
		res *= 31 * signerInput.hashCode();
		res *= 31 * signedAt.hashCode();

		return (int) res;
	}
}
