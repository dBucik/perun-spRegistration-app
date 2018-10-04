package cz.metacentrum.perun.spRegistration.persistence.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import cz.metacentrum.perun.spRegistration.persistence.models.attributes.Attribute;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utils class for mapping objects from RPC responses.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class Utils {

	/**
	 * Map JSON response from Perun RPC to Facility object.
	 * @param facilityJson JSON from Perun with facility.
	 * @param isRichFacility Boolean value specifying if the JSON contains attributes as well.
	 * @return Mapped Facility object.
	 */
	public static Facility mapFacility(JsonNode facilityJson, boolean isRichFacility) {
		Long id = null;

		if (!(facilityJson.get("id") instanceof NullNode)) {
			id = facilityJson.get("id").asLong();
		}

		if (id == null ) {
			//TODO: log + exception?
			return null;
		}

		Facility facility = new Facility(id);

		if (!(facilityJson.get("name") instanceof NullNode)) {
			facility.setName(facilityJson.get("name").asText());
		}

		if (!(facilityJson.get("description") instanceof NullNode)) {
			facility.setDescription(facilityJson.get("description").asText());
		}

		//TODO: rich facility?

		return facility;
	}

	/**
	 * Map JSON response from Perun RPC to List of Facilities.
	 * @param facilitiesJson JSON from Perun with facilities.
	 * @param isRichFacility Boolean value specifying if the JSON contains attributes as well.
	 * @return Mapped List of Facility objects.
	 */
	public static List<Facility> mapFacilities(JsonNode facilitiesJson, boolean isRichFacility) {
		List<Facility> facilities = new ArrayList<>();
		for (int i = 0; i < facilitiesJson.size(); i++) {
			JsonNode facilityJson = facilitiesJson.get(i);
			facilities.add(mapFacility(facilityJson, isRichFacility));
		}

		return facilities;
	}

	/**
	 * Map JSON response from Perun RPC to User object.
	 * @param userJson JSON from Perun with user.
	 * @param isRichUser Boolean value specifying if the JSON contains attributes as well.
	 * @return Mapped User object.
	 */
	public static User mapUser(JsonNode userJson, boolean isRichUser) {
		Long id = null;
		if (!(userJson.get("id") instanceof NullNode)) {
			id = userJson.get("id").asLong();
		}

		if (id == null) {
			//TODO: log + exception?
			return null;
		}

		User user = new User(id);

		if (!(userJson.get("titleBefore") instanceof NullNode)) {
			user.setTitleBefore(userJson.get("titleBefore").asText());
		}

		if (!(userJson.get("titleAfter") instanceof NullNode)) {
			user.setTitleAfter(userJson.get("titleAfter").asText());
		}

		if (!(userJson.get("firstName") instanceof NullNode)) {
			user.setFirstName(userJson.get("firstName").asText());
		}

		if (!(userJson.get("middleName") instanceof NullNode)) {
			user.setMiddleName(userJson.get("middleName").asText());
		}

		if (!(userJson.get("lastName") instanceof NullNode)) {
			user.setLastName(userJson.get("lastName").asText());
		}

		if (isRichUser && userJson.has("attributes")
				&& !(userJson.get("attributes") instanceof NullNode)) {
			JsonNode attrs = userJson.get("attributes");
			for (int i = 0; i < attrs.size(); i++) {
				JsonNode attrJson = attrs.get(i);
				if (!(attrJson.get("name") instanceof NullNode)
						&& "preferredMail".equals(attrJson.get("name").asText())) {
					user.setEmail(attrJson.get("name").asText());
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
	public static Map<String,Attribute> mapAttributes(JsonNode attrsJson) {
		Map<String, Attribute> attrs = new HashMap<>();
		for (int i = 0; i < attrsJson.size(); i++) {
			JsonNode attrJson = attrsJson.get(i);
			Attribute a = mapAttribute(attrJson);
			if (a != null) {
				attrs.put(a.getFullName(), a);
			}
		}

		return attrs;
	}

	/**
	 * Map JSON from Perun RPC to Attribute.
	 * @param attrJson JSON from Perun with attribute.
	 * @return Mapped Attribute object, null in case the mapping fails.
	 */
	public static Attribute mapAttribute(JsonNode attrJson) {
		try {
			//TODO: log
			return Attribute.fronPerunJsonNode(attrJson);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
