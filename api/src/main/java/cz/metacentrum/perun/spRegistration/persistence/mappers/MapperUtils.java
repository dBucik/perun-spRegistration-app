package cz.metacentrum.perun.spRegistration.persistence.mappers;

import com.fasterxml.jackson.databind.node.NullNode;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.models.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MapperUtils class for mapping objects from RPC responses.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class MapperUtils {

	/**
	 * Map JSON response from Perun RPC to Facility object.
	 * @param facilityJson JSON from Perun with facility.
	 * @return Mapped Facility object.
	 */
	public static Facility mapFacility(JSONObject facilityJson) {
		return Facility.fromPerunJson(facilityJson);
	}

	/**
	 * Map JSON response from Perun RPC to List of Facilities.
	 * @param facilitiesJson JSON from Perun with facilities.
	 * @return Mapped List of Facility objects.
	 */
	public static List<Facility> mapFacilities(JSONArray facilitiesJson) {
		List<Facility> facilities = new ArrayList<>();
		for (int i = 0; i < facilitiesJson.length(); i++) {
			JSONObject facilityJson = facilitiesJson.getJSONObject(i);
			Facility facility = Facility.fromPerunJson(facilityJson);
			facilities.add(facility);
		}

		return facilities;
	}

	/**
	 * Map JSON response from Perun RPC to User object.
	 * @param json JSON from Perun with user.
	 * @param isRichUser Boolean value specifying if the JSON contains attributes as well.
	 * @return Mapped User object, null in case of failure.
	 */
	public static User mapUser(JSONObject json, boolean isRichUser) {
		User user = User.fromPerunJson(json);
		if (isRichUser) {
			JSONArray attrs = json.getJSONArray("attributes");
			for (int i = 0; i < attrs.length(); i++) {
				JSONObject attrJson = attrs.getJSONObject(i);
				if (!(attrJson.get("name") instanceof NullNode)
						&& "preferredMail".equals(attrJson.getString("name"))) {
					user.setEmail(attrJson.getString("name"));
				}
			}
		}
		return user;
	}

	/**
	 * Map JSON from Perun RPC to Map of Attributes (key = attribute name, value = attribute)
	 * @param attrsJson JSON from Perun with attributes.
	 * @return Map of Attributes (key = attribute name, value = attribute).
	 */
	public static Map<String, PerunAttribute> mapAttributes(JSONArray attrsJson) {
		Map<String, PerunAttribute> attrs = new HashMap<>();
		for (int i = 0; i < attrsJson.length(); i++) {
			JSONObject attrJson = attrsJson.getJSONObject(i);
			PerunAttribute a = PerunAttribute.fromJsonOfPerun(attrJson);
			if (a != null) {
				PerunAttributeDefinition def = a.getDefinition();
				a.setDefinition(def);
				attrs.put(a.getFullName(), a);
			}
		}

		return attrs;
	}

	/**
	 * Map JSON from Perun RPC to PerunAttribute.
	 * @param attrJson JSON from Perun with attribute.
	 * @return Mapped PerunAttribute object, null in case the mapping fails.
	 */
	public static PerunAttribute mapAttribute(JSONObject attrJson) {
		return PerunAttribute.fromJsonOfPerun(attrJson);
	}

}
