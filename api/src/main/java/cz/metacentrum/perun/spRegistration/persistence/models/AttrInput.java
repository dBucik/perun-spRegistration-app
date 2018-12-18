package cz.metacentrum.perun.spRegistration.persistence.models;

import java.util.List;
import java.util.Map;

public class AttrInput {

	private String name;
	private Map<String, String> displayName;
	private Map<String, String> description;
	private boolean required = true;
	private boolean displayed = true;
	private boolean editable = true;
	private String type;
	private List<String> allowedValues;
	private int displayPosition;
	private String regex;
	private List<String> allowedKeys; // only if the type is Map

	public AttrInput(String name, Map<String, String> displayName, Map<String, String> description,
					 String type) {
		this.name = name;
		this.displayName = displayName;
		this.description = description;
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

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
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

	public List<String> getAllowedKeys() {
		return allowedKeys;
	}

	public void setAllowedKeys(List<String> allowedKeys) {
		this.allowedKeys = allowedKeys;
	}

	public int getDisplayPosition() {
		return displayPosition;
	}

	public void setDisplayPosition(int displayPosition) {
		this.displayPosition = displayPosition;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
}
