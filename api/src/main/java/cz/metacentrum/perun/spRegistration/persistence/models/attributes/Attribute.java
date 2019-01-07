package cz.metacentrum.perun.spRegistration.persistence.models.attributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.AttributeToJsonException;
import cz.metacentrum.perun.spRegistration.persistence.enums.AttributeType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Abstract representation of Perun Attribute. Contains attribute definition in JSON format.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public abstract class Attribute {

	private JsonNode definition;
	private String comment;
	private AttributeType type;

	public Attribute() { }

	public Attribute(JsonNode definition, String comment, AttributeType type) {
		this.definition = definition;
		this.comment = comment;
		this.type = type;
	}

	public JsonNode getDefinition() {
		return definition;
	}

	public void setDefinition(JsonNode definition) {
		this.definition = definition;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public AttributeType getType() {
		return type;
	}

	public void setType(AttributeType type) {
		this.type = type;
	}

	/**
	 * Get full name of attribute in URN format.
	 * @return URN name of Attribute
	 */
	public String getFullName() {
		String namespace = definition.get("namespace").asText();
		String friendlyName = definition.get("friendlyName").asText();

		return namespace + ':' + friendlyName;
	}

	/**
	 * Convert attribute to JSON String suitable for storing into DB.
	 * @return JSON String.
	 */
	public String toStringAsJsonForDb() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode oldValue;
		JsonNode newValue;
		if (this instanceof StringAttribute) {
			StringAttribute a = (StringAttribute) this;
			oldValue = mapper.valueToTree(a.getOldValue());
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof BooleanAttribute) {
			BooleanAttribute a = (BooleanAttribute) this;
			oldValue = mapper.valueToTree(a.getOldValue());
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof IntegerAttribute) {
			IntegerAttribute a = (IntegerAttribute) this;
			oldValue = mapper.valueToTree(a.getOldValue());
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof MapAttribute) {
			MapAttribute a = (MapAttribute) this;
			oldValue = mapper.valueToTree(a.getOldValue());
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof ArrayAttribute) {
			ArrayAttribute a = (ArrayAttribute) this;
			oldValue = mapper.valueToTree(a.getOldValue());
			newValue = mapper.valueToTree(a.getNewValue());
		} else {
			throw new AttributeToJsonException("Attribute is not specific, cannot be mapped to json");
		}

		return toStringAsJsonForDb(definition, comment, type.toString(), oldValue, newValue);
	}

	/**
	 * Convert attribute to JSON String suitable for stroing into Perun.
	 * @return JSON String.
	 */
	public String toStringAsJsonForPerun() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode newValue;
		if (this instanceof StringAttribute) {
			StringAttribute a = (StringAttribute) this;
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof BooleanAttribute) {
			BooleanAttribute a = (BooleanAttribute) this;
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof IntegerAttribute) {
			IntegerAttribute a = (IntegerAttribute) this;
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof MapAttribute) {
			MapAttribute a = (MapAttribute) this;
			newValue = mapper.valueToTree(a.getNewValue());
		} else if (this instanceof ArrayAttribute) {
			ArrayAttribute a = (ArrayAttribute) this;
			newValue = mapper.valueToTree(a.getNewValue());
		} else {
			throw new AttributeToJsonException("Attribute is not specific, cannot be mapped to json");
		}

		return toStringAsJsonForPerun(definition, newValue);
	}

	/**
	 * Parse attribute from JsonNode object retrieved from DB.
	 * @param attrJson JsonNode with attribute.
	 * @return Parsed attribute, NULL if the type of the attribute cannot be decided.
	 * @throws IOException in case the parsing fails.
	 */
	public static Attribute fromDBJsonNode(JsonNode attrJson) throws IOException {
		JsonNode definition = attrJson.get("definition");
		AttributeType type = AttributeType.valueOf(definition.get("type").asText());
		JsonNode oldValue = attrJson.get("oldValue");
		JsonNode newValue = attrJson.get("newValue");
		String comment = attrJson.get("comment").asText();
		ObjectMapper mapper = new ObjectMapper();

		switch (type) {
			case STRING: {
				String oldVal = mapper.readValue(oldValue.textValue(), String.class);
				String newVal = mapper.readValue(newValue.textValue(), String.class);
				return new StringAttribute(definition, comment, oldVal, newVal);
			} case INTEGER: {
				Integer oldVal = mapper.readValue(oldValue.textValue(), Integer.class);
				Integer newVal = mapper.readValue(newValue.textValue(), Integer.class);
				return new IntegerAttribute(definition, comment, oldVal, newVal);
			} case BOOLEAN: {
				Boolean oldVal = mapper.readValue(oldValue.textValue(), Boolean.class);
				Boolean newVal = mapper.readValue(newValue.textValue(), Boolean.class);
				return new BooleanAttribute(definition, comment, oldVal, newVal);
			} case ARRAY: {
				List<String> oldVal = mapper.readValue(oldValue.textValue(), new TypeReference<List<String>>() {});
				List<String> newVal = mapper.readValue(oldValue.textValue(), new TypeReference<List<String>>() {});
				return new ArrayAttribute(definition, comment, oldVal, newVal);
			} case MAP: {
				Map<String, String> oldVal = mapper.readValue(oldValue.textValue(),
						new TypeReference<Map<String, String>>() {});
				Map<String, String> newVal = mapper.readValue(oldValue.textValue(),
						new TypeReference<Map<String, String>>() {});
				return new MapAttribute(definition, comment, oldVal, newVal);
			}
		}

		return null;
	}

	/**
	 * Parse attribute from JsonNode retrieved from Perun RPC.
	 * @param perunJson JsonNode with attribute.
	 * @return Parsed attribute, NULL if the type of the attribute cannot be decided.
	 * @throws IOException in case the parsing fails.
	 */
	public static Attribute fronPerunJsonNode(JsonNode perunJson) throws IOException {
		if (! (perunJson instanceof ObjectNode)) {
			//TODO throw exception
			return null;
		}

		ObjectNode attrJson = (ObjectNode) perunJson;
		JsonNode value = attrJson.get("value");
		attrJson.remove("value");
		String typeStr = attrJson.get("type").asText();
		AttributeType type = AttributeType.fromPerunType(typeStr);
		ObjectMapper mapper = new ObjectMapper();
		switch (type) {
			case STRING: {
				String val = mapper.readValue(value.textValue(), String.class);
				return new StringAttribute(attrJson, null, val, null);
			} case INTEGER: {
				Integer val = mapper.readValue(value.textValue(), Integer.class);
				return new IntegerAttribute(attrJson, null, val, null);
			} case BOOLEAN: {
				Boolean val = mapper.readValue(value.textValue(), Boolean.class);
				return new BooleanAttribute(attrJson, null, val, null);
			} case ARRAY: {
				List<String> val = mapper.readValue(value.textValue(), new TypeReference<List<String>>() {});
				return new ArrayAttribute(attrJson, null, val, null);
			} case MAP: {
				Map<String, String> val = mapper.readValue(value.textValue(),
						new TypeReference<Map<String, String>>() {});
				return new MapAttribute(attrJson, null, val, null);
			}
		}

		return null;
	}

	//convert to JSON String for storing to DB
	private String toStringAsJsonForDb(JsonNode definition, String comment, String type, JsonNode oldValue,
									   JsonNode newValue) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();

		json.set("definition", definition);
		json.put("comment", comment);
		json.put("type", type);
		json.set("oldValue", oldValue);
		json.set("newValue", newValue);

		try {
			return mapper.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			throw new AttributeToJsonException("Error when mapping attribute to json", e);
		}
	}

	//convert to JSON String for storing into Perun
	private String toStringAsJsonForPerun(JsonNode definition, JsonNode value) {
		if (! (definition instanceof ObjectNode)) {
			throw new AttributeToJsonException("Definition is not instance of ObjectNode");
		}

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode perunAttr = (ObjectNode) definition;
		perunAttr.set("value", value);

		try {
			return mapper.writeValueAsString(perunAttr);
		} catch (JsonProcessingException e) {
			throw new AttributeToJsonException("Error when mapping attribute to json", e);
		}
	}

	@Override
	public String toString() {
		return "Attribute{" +
				"definition=" + definition +
				", comment='" + comment + '\'' +
				", type=" + type +
				'}';
	}
}
