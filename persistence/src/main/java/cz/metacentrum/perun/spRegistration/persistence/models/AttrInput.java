package cz.metacentrum.perun.spRegistration.persistence.models;

import java.util.List;

public class AttrInput {

	private String name;
	private String displayName;
	private String description;
	private boolean required;
	private String type;
	private List<String> allowedValues;

	public AttrInput(String name, String displayName, String description, String type, boolean required) {
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
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
