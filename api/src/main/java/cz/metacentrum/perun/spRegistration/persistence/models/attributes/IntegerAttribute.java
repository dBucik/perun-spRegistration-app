package cz.metacentrum.perun.spRegistration.persistence.models.attributes;

import com.fasterxml.jackson.databind.JsonNode;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeType;

/**
 * Integer attribute from Perun.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class IntegerAttribute extends Attribute {

	private Integer oldValue;
	private Integer newValue;

	public IntegerAttribute() {

	}

	public IntegerAttribute(JsonNode definition, String comment, Integer oldValue, Integer newValue) {
		super(definition, comment, AttributeType.BOOLEAN);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Integer getOldValue() {
		return oldValue;
	}

	public void setOldValue(Integer oldValue) {
		this.oldValue = oldValue;
	}

	public Integer getNewValue() {
		return newValue;
	}

	public void setNewValue(Integer newValue) {
		this.newValue = newValue;
	}
}
