package cz.metacentrum.perun.spRegistration.persistence.models;

import java.util.List;
import java.util.Map;

public class AttrInput {

	private String name;
	private Map<String, String> displayName;
	private Map<String, String> description;
	private boolean required;
	private String type;
	private List<String> allowedValues;

	public AttrInput(String name, Map<String, String> displayName, Map<String, String> description,
					 String type, boolean required) {
		this.name = name;
		this.displayName = displayName;
		this.description = description;
		this.required = required;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getDisplayName() {
		return displayName;
	}

	public void setDisplayName(Map<String, String> displayName) {
		this.displayName = displayName;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(List<String> allowedValues) {
		this.allowedValues = allowedValues;
	}
}
