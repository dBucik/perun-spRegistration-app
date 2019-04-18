package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.configs.MitreIdAttrsConfig;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class containing methods for services.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class ServiceUtils {

	/**
	 * Transform list of attributes to map. Also add definitions to attributes.
	 * @param attributes Attributes
	 * @param appConfig config containing definitions map
	 * @return converted attributes as map
	 */
	public static Map<String, PerunAttribute> transformListToMap(List<PerunAttribute> attributes, AppConfig appConfig) {
		Map<String, PerunAttribute> convertedAttributes = new HashMap<>();
		for (PerunAttribute a: attributes) {
			PerunAttributeDefinition def = appConfig.getAttrDefinition(a.getFullName());
			a.setDefinition(def);
			convertedAttributes.put(a.getFullName(), a);
		}

		return convertedAttributes;
	}

	/**
	 * Filter facility attributes and keep only ones with names in list
	 * @param attrsMap attributes to be filtered
	 * @param toKeep names of attributes that should be kept
	 * @return filtered attributes as map
	 */
	public static Map<String, PerunAttribute> filterFacilityAttrs(Map<String, PerunAttribute> attrsMap, List<String> toKeep) {
		Map<String, PerunAttribute> filteredAttributes = new HashMap<>();
		for (String keptAttr: toKeep) {
			filteredAttributes.put(keptAttr, attrsMap.get(keptAttr));
		}

		return filteredAttributes;
	}

	/**
	 * Decide if request is for OIDC service.
	 * @param request Request
	 * @param mitreIdAttrsConfig Configuration containing mitreid attrs.
	 * @return True if is OIDC service request, false otherwise.
	 */
	public static boolean isOidcRequest(Request request, MitreIdAttrsConfig mitreIdAttrsConfig) {
		if (request.getAttributes().containsKey(mitreIdAttrsConfig.getGrantTypesAttrs())) {
			return null != request.getAttributes().get(mitreIdAttrsConfig.getGrantTypesAttrs()).getValue();
		}

		return false;
	}

	/**
	 * Decide if facility represents OIDC service.
	 * @param facility facility
	 * @param mitreIdAttrsConfig Configuration containing mitreid attrs.
	 * @return True if is OIDC service, false otherwise.
	 */
	public static boolean isOidcFacility(Facility facility, MitreIdAttrsConfig mitreIdAttrsConfig) {
		if (facility.getAttrs().containsKey(mitreIdAttrsConfig.getGrantTypesAttrs())) {
			return null != facility.getAttrs().get(mitreIdAttrsConfig.getGrantTypesAttrs()).getValue();
		}

		return false;
	}

	/**
	 * Decide if attributes are of OIDC service.
	 * @param attributes Map of attributes, key is name, value is attribute
	 * @param mitreIdAttrsConfig Configuration containing mitreid attrs.
	 * @return True if attributes are of OIDC service, false otherwise.
	 */
	public static boolean isOidcAttributes(Map<String, PerunAttribute> attributes, MitreIdAttrsConfig mitreIdAttrsConfig) {
		if (attributes.containsKey(mitreIdAttrsConfig.getGrantTypesAttrs())) {
			return null != attributes.get(mitreIdAttrsConfig.getGrantTypesAttrs()).getValue();
		}

		return false;
	}
}
