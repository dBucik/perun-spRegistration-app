package cz.metacentrum.perun.spRegistration.persistence.models.attributes;

import com.fasterxml.jackson.databind.JsonNode;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeType;

/**
 * Boolean attribute from Perun.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class BooleanAttribute extends Attribute {

	private Boolean oldValue;
	private Boolean newValue;

	public BooleanAttribute() {

	}

	public BooleanAttribute(JsonNode definition, String comment, Boolean oldValue, Boolean newValue) {
		super(definition, comment, AttributeType.BOOLEAN);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Boolean getOldValue() {
		return oldValue;
	}

	public void setOldValue(Boolean oldValue) {
		this.oldValue = oldValue;
	}

	public Boolean getNewValue() {
		return newValue;
	}

	public void setNewValue(Boolean newValue) {
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return "BooleanAttribute{" +
				super.toString() +
				", oldValue=" + oldValue +
				", newValue=" + newValue +
				'}';
	}
}
