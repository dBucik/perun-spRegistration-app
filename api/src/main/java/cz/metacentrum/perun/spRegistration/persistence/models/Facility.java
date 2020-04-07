package cz.metacentrum.perun.spRegistration.persistence.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.metacentrum.perun.spRegistration.Utils;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeCategory;

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

	private String name;
	private String description;
	private boolean oidc;
	private boolean saml;
	private Long activeRequestId;
	private boolean testEnv;
	private boolean editable = false;
	private List<User> admins = new ArrayList<>();
	private Map<AttributeCategory, Map<String, PerunAttribute>> attrs = new HashMap<>();

	public Facility(Long id) {
		super(id);
	}

	public Facility(Long id, String name, String description) {
		super(id);
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
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

	public Map<AttributeCategory, Map<String, PerunAttribute>> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<AttributeCategory, Map<String, PerunAttribute>> attrs) {
		this.attrs = attrs;
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
		res.put("name", name);
		res.put("description", description);
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
				", attrs=" + attrs +
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
				Objects.equals(attrs, facility.attrs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, testEnv, activeRequestId, editable, attrs);
	}
}
