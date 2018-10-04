package cz.metacentrum.perun.spRegistration.persistence.models.attributes;

import com.fasterxml.jackson.databind.JsonNode;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeType;

/**
 * String attribute from Perun.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class StringAttribute extends Attribute {

	private String oldValue;
	private String newValue;

	public StringAttribute() {

	}

	public StringAttribute(JsonNode definition, String comment, String oldValue, String newValue) {
		super(definition, comment, AttributeType.STRING);
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
