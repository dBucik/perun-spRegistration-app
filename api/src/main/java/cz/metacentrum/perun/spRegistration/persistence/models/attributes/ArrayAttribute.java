package cz.metacentrum.perun.spRegistration.persistence.models.attributes;

import com.fasterxml.jackson.databind.JsonNode;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeType;

import java.util.List;

/**
 * Array of Strings attribute from Perun.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class ArrayAttribute extends Attribute {

	private List<String> oldValue;
	private List<String> newValue;

	public ArrayAttribute() {
	}

	public ArrayAttribute(JsonNode definition, String comment, List<String> oldValue, List<String> newValue) {
		super(definition, comment, AttributeType.BOOLEAN);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public List<String> getOldValue() {
		return oldValue;
	}

	public void setOldValue(List<String> oldValue) {
		this.oldValue = oldValue;
	}

	public List<String> getNewValue() {
		return newValue;
	}

	public void setNewValue(List<String> newValue) {
		this.newValue = newValue;
	}
}
