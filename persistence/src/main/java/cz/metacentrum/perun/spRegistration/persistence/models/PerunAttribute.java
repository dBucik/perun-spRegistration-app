package cz.metacentrum.perun.spRegistration.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.metacentrum.perun.spRegistration.persistence.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PerunAttribute {

	public final static String STRING_TYPE = "java.lang.String";
	private final static String INTEGER_TYPE = "java.lang.Long";
	private final static String BOOLEAN_TYPE = "java.lang.Boolean";
	private final static String ARRAY_TYPE = "java.util.ArrayList";
	private final static String MAP_TYPE = "java.util.LinkedHashMap";
	private final static String LARGE_STRING_TYPE = "java.lang.LargeString";
	private final static String LARGE_ARRAY_LIST_TYPE = "java.lang.LargeArrayList";

	private PerunAttributeDefinition definition;
	private Object value;
	private Object oldValue;
	private String comment;

	@JsonIgnore
	private String fullName;

	public PerunAttribute() { }

	public PerunAttribute(PerunAttributeDefinition definition, String fullName, Object value, Object oldValue, String comment) {
		this.definition = definition;
		this.fullName = fullName;
		this.value = value;
		this.oldValue = oldValue;
		this.comment = comment;
	}

	public PerunAttributeDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(PerunAttributeDefinition definition) {
		this.definition = definition;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String valueAsString(boolean isOldValue) {
		if (isOldValue) {
			return valueAsString(oldValue);
		} else {
			return valueAsString(value);
		}
	}

	public Long valueAsLong(boolean isOldValue) {
		if (isOldValue) {
			return valueAsLong(oldValue);
		} else {
			return valueAsLong(value);
		}
	}

	public Boolean valueAsBoolean(boolean isOldValue) {
		if (isOldValue) {
			return valueAsBoolean(oldValue);
		} else {
			return valueAsBoolean(value);
		}
	}

	public List<String> valueAsArray(boolean isOldValue) {
		if (isOldValue) {
			return valueAsArray(oldValue);
		} else {
			return valueAsArray(value);
		}
	}

	public Map<String,String> valueAsMap(boolean isOldValue) {
		if (isOldValue) {
			return valueAsMap(oldValue);
		} else {
			return valueAsMap(value);
		}
	}

	public JSONObject toJsonForPerun() {
		JSONObject json = definition.toJson();
		putValue(json, "value", definition.getType(), false);

		return json;
	}

	public JSONObject toJsonForDb() {
		JSONObject obj = new JSONObject();
		obj.put("type", definition.getType());
		putValue(obj, "oldValue", definition.getType(), true);
		putValue(obj, "newValue", definition.getType(), false);
		obj.put("comment", comment);

		return obj;
	}

	public static PerunAttribute fromJsonOfPerun(JSONObject json) {
		PerunAttributeDefinition definition = PerunAttributeDefinition.fromPerunJson(json);

		Object value = getValue(json, "value", definition.getType());
		PerunAttribute attr = new PerunAttribute();
		attr.setDefinition(definition);
		attr.setValue(value);

		return attr;
	}

	public static PerunAttribute fromJsonOfDb(String name, JSONObject json, Config config) {
		String type = json.getString("type");
		Object newValue = getValue(json, "newValue", type);
		Object oldValue = getValue(json, "oldValue", type);
		String comment = json.optString("comment", null);

		PerunAttributeDefinition def = config.getAttrDefinition(name);
		PerunAttribute attr = new PerunAttribute();

		attr.setDefinition(def);
		attr.setValue(newValue);
		attr.setOldValue(oldValue);
		attr.setComment(comment);

		return attr;
	}

	private void putValue(JSONObject json, String key, String type, boolean isOldValue) {
		switch (type) {
			case STRING_TYPE:
			case LARGE_STRING_TYPE:
				json.put(key, valueAsString(isOldValue));
				break;
			case INTEGER_TYPE:
				json.put(key, valueAsLong(isOldValue));
				break;
			case BOOLEAN_TYPE:
				json.put(key, valueAsBoolean(isOldValue));
				break;
			case ARRAY_TYPE:
			case LARGE_ARRAY_LIST_TYPE:
				List<String> arrValue = valueAsArray(isOldValue);
				JSONArray arr = new JSONArray();
				for (String sub: arrValue) {
					arr.put(sub);
				}

				json.put(key, arr);
				break;
			case MAP_TYPE:
				Map<String, String> mapValue = valueAsMap(isOldValue);
				JSONObject obj = new JSONObject();
				for (Map.Entry<String, String> sub: mapValue.entrySet()) {
					obj.put(sub.getKey(), sub.getValue());
				}

				json.put(key, obj);
				break;
		}
	}

	private static Object getValue(JSONObject json, String key, String type) {
		if (json.get(key) == JSONObject.NULL) {
			return null;
		}
		switch (type) {
			case STRING_TYPE:
			case LARGE_STRING_TYPE:
				return json.getString(key);
			case INTEGER_TYPE:
				return json.getInt(key);
			case BOOLEAN_TYPE:
				return json.getBoolean(key);
			case ARRAY_TYPE:
			case LARGE_ARRAY_LIST_TYPE:
				JSONArray arr = json.getJSONArray(key);
				List<String> arrValue = new ArrayList<>();

				for (int i = 0; i < arr.length(); i++) {
					arrValue.add(arr.get(i).toString());
				}

				return arrValue;
			case MAP_TYPE:
				JSONObject obj = json.getJSONObject(key);
				Map<String, String> mapValue = new LinkedHashMap<>();

				Iterator<String> keys = obj.keys();
				while (keys.hasNext()) {
					String mapKey = keys.next();
					mapValue.put(mapKey, obj.get(mapKey).toString());
				}

				return mapValue;
		}
		return null;
	}

	private String valueAsString(Object value) {
		if (value instanceof String) {
			return (String) value;
		}

		return null;
	}

	private Long valueAsLong(Object value) {
		if (value instanceof Long) {
			return (Long) value;
		}

		return null;
	}

	private Boolean valueAsBoolean(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		return null;
	}

	private List<String> valueAsArray(Object value) {
		if (value instanceof List) {
			List<String> result = new ArrayList<>();
			List val = (List) value;
			for (Object o: val) {
				if (!(o instanceof String)) {
					return null;
				} else {
					result.add((String) o);
				}
			}
			return result;
		}

		return null;
	}

	private Map<String, String> valueAsMap(Object value) {
		if (value instanceof Map) {
			Map<String,String> result = new LinkedHashMap<>();
			Map val = (Map) value;
			for (Object entry: val.entrySet()) {
				if (!(entry instanceof Map.Entry)) {
					return null;
				} else {
					Map.Entry ent = (Map.Entry) entry;
					if (!(ent.getKey() instanceof String) || ! (ent.getValue() instanceof String)) {
						return null;
					} else {
						String key = (String) ent.getKey();
						String pair = (String) ent.getValue();
						result.put(key, pair);
					}
				}
			}
			return result;
		}

		return null;
	}
}
