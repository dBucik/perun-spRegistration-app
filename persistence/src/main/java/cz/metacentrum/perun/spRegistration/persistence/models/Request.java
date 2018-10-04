package cz.metacentrum.perun.spRegistration.persistence.models;

import cz.metacentrum.perun.spRegistration.persistence.enums.RequestAction;
import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Class represents request made by user. It contains all the data that needs to be stored.
 * It also keeps track of the modification time.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class Request {

	private Long reqId;
	private Long facilityId;
	private RequestStatus status;
	private RequestAction action;
	private Long reqUserId;
	private Map<String, Attribute> attributes = new HashMap<>();
	private Timestamp modifiedAt;
	private Long modifiedBy;

	public Long getReqId() {
		return reqId;
	}

	public void setReqId(Long reqId) {
		this.reqId = reqId;
	}

	public Long getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	public RequestAction getAction() {
		return action;
	}

	public void setAction(RequestAction action) {
		this.action = action;
	}

	public Long getReqUserId() {
		return reqUserId;
	}

	public void setReqUserId(Long reqUserId) {
		this.reqUserId = reqUserId;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}

	public Timestamp getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Timestamp modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * Convert attributes to JSON format suitable for storing into DB.
	 * @return JSON with attributes.
	 */
	public String getAttributesAsJsonForDb() {
		if (attributes == null || attributes.isEmpty()) {
			return null;
		}

		String res = "{ [ ";
		StringJoiner joiner = new StringJoiner(" , ");
		for (Attribute a: attributes.values()) {
			joiner.add(a.toStringAsJsonForDb());
		}
		res += joiner.toString();
		res += " ] }";

		return res;
	}

	/**
	 * Convert attributes to JSON format suitable for storing into Perun.
	 * @return JSON with attributes.
	 */
	public List<String> getAttributesAsJsonForPerun() {
		if (attributes == null || attributes.isEmpty()) {
			return null;
		}

		List<String> res = new ArrayList<>();
		for (Attribute a: attributes.values()) {
			res.add(a.toStringAsJsonForPerun());
		}

		return res;
	}
}
