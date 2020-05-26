package cz.metacentrum.perun.spRegistration.common.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.common.enums.AttributeCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Representation of Perun Facility.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class Facility extends PerunEntity {

	private String perunName;
	private String perunDescription;
	private Map<String, String> name = new HashMap<>();
	private Map<String, String> description = new HashMap<>();;
	private boolean oidc;
	private boolean saml;
	private Long activeRequestId;
	private boolean testEnv;
	private boolean editable = false;
	private List<User> admins = new ArrayList<>();
	private Map<AttributeCategory, Map<String, PerunAttribute>> attributes = new HashMap<>();

	public Facility(Long id) {
		super(id);
	}

	public Facility(Long id, String perunName, String perunDescription) {
		super(id);
		this.perunName = perunName;
		this.perunDescription = perunDescription;
	}

	public String getPerunName() {
		return perunName;
	}

	public void setPerunName(String perunName) {
		this.perunName = perunName;
	}

	public void setPerunDescription(String perunDescription) {
		this.perunDescription = perunDescription;
	}

	public String getPerunDescription() {
		return perunDescription;
	}

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public boolean isTestEnv() {
		return testEnv;
	}

	public void setTestEnv(boolean testEnv) {
		this.testEnv = testEnv;
	}

	public Long getActiveRequestId() {
		return activeRequestId;
	}

	public void setActiveRequestId(Long activeRequestId) {
		this.activeRequestId = activeRequestId;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isOidc() {
		return oidc;
	}

	public void setOidc(boolean oidc) {
		this.oidc = oidc;
	}

	public boolean isSaml() {
		return saml;
	}

	public void setSaml(boolean saml) {
		this.saml = saml;
	}

	public List<User> getAdmins() {
		return admins;
	}

	public void setAdmins(List<User> admins) {
		this.admins = admins;
	}

	public Map<AttributeCategory, Map<String, PerunAttribute>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<AttributeCategory, Map<String, PerunAttribute>> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Convert object to JSON representation
	 * @return JSON String
	 */
	public JsonNode toJson() {
		ObjectNode res = JsonNodeFactory.instance.objectNode();
		if (this.getId() == null) {
			res.set("id", JsonNodeFactory.instance.nullNode());
		} else {
			res.put("id", getId());
		}
		res.put("name", perunName);
		res.put("description", perunDescription);
		res.put("beanName", "facility");

		return res;
	}

	/**
	 * Convert JSON from perun to facility object
	 * @param json JSON from Perun
	 * @return Facility object or null
	 */
	public static Facility fromPerunJson(JsonNode json) {
		if (Utils.checkParamsInvalid(json)) {
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Long id = json.get("id").asLong();
		String name = json.get("name").textValue();
		String description = json.hasNonNull("description") ? json.get("description").textValue() : null;

		return new Facility(id, name, description);
	}

	@Override
	public String toString() {
		return "Facility{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				", testEnv=" + testEnv +
				", activeRequestId=" + activeRequestId +
				", canEdit=" + editable +
				", attrs=" + attributes +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Facility facility = (Facility) o;
		return testEnv == facility.testEnv &&
				editable == facility.editable &&
				Objects.equals(name, facility.name) &&
				Objects.equals(description, facility.description) &&
				Objects.equals(activeRequestId, facility.activeRequestId) &&
				Objects.equals(attributes, facility.attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, testEnv, activeRequestId, editable, attributes);
	}
}
