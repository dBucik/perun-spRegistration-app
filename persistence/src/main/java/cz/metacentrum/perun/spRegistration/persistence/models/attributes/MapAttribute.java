package cz.metacentrum.perun.spRegistration.persistence.models.attributes;

import com.fasterxml.jackson.databind.JsonNode;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeType;

import java.util.Map;

/**
 * Map (key = String, value = String) attribute from Perun.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class MapAttribute extends Attribute {

	private Map<String, String> oldValue;
	private Map<String, String> newValue;

	public MapAttribute() {

	}

	public MapAttribute(JsonNode definition, String comment, Map<String, String> oldValue, Map<String, String> newValue) {
		super(definition, comment, AttributeType.BOOLEAN);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Map<String, String> getOldValue() {
		return oldValue;
	}

	public void setOldValue(Map<String, String> oldValue) {
		this.oldValue = oldValue;
	}

	public Map<String, String> getNewValue() {
		return newValue;
	}

	public void setNewValue(Map<String, String> newValue) {
		this.newValue = newValue;
	}

}
