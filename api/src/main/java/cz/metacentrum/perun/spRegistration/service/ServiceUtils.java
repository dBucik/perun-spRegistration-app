package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.configs.MitreIdAttrsConfig;
import cz.metacentrum.perun.spRegistration.persistence.models.Facility;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.models.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class containing methods for services.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class ServiceUtils {

	public static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);

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
		log.trace("filterFacilityAttrs(attrsMap: {}, toKeep: {})", attrsMap, toKeep);
		Map<String, PerunAttribute> filteredAttributes = new HashMap<>();
		for (String keptAttr: toKeep) {
			filteredAttributes.put(keptAttr, attrsMap.get(keptAttr));
		}

		log.trace("filterFacilityAttrs() returns: {}", filteredAttributes);
		return filteredAttributes;
	}

	/**
	 * Decide if request is for OIDC service.
	 * @param request Request
	 * @param entityIdAttr Identifier of entity id attr.
	 * @return True if is OIDC service request, false otherwise.
	 */
	public static boolean isOidcRequest(Request request, String entityIdAttr) {
		log.trace("isOidcRequest(request: {})", request);
		boolean isOidc = true;
		if (request.getAttributes().containsKey(entityIdAttr)) {
			isOidc = (null == request.getAttributes().get(entityIdAttr).getValue());
		}

		log.trace("isOidcRequest() returns: {}", isOidc);
		return isOidc;
	}

	/**
	 * Decide if facility represents OIDC service.
	 * @param facility facility
	 * @param entityIdAttr Identifier of entity id attr.
	 * @return True if is OIDC service, false otherwise.
	 */
	public static boolean isOidcFacility(Facility facility, String entityIdAttr) {
		log.trace("isOidcFacility(facility: {})", facility);
		boolean isOidc = true;
		if (facility.getAttrs().containsKey(entityIdAttr)) {
			isOidc = (null == facility.getAttrs().get(entityIdAttr).getValue());
		}

		log.trace("isOidcFacility() returns: {}", isOidc);
		return isOidc;
	}

	/**
	 * Decide if attributes are of OIDC service.
	 * @param attributes Map of attributes, key is name, value is attribute
	 * @param entityIdAttr Identifier of entity id attr.
	 * @return True if attributes are of OIDC service, false otherwise.
	 */
	public static boolean isOidcAttributes(Map<String, PerunAttribute> attributes, String entityIdAttr) {
		log.trace("isOidcAttributes(attributes: {})", attributes);
		boolean isOidc = true;
		if (attributes.containsKey(entityIdAttr)) {
			isOidc = (null == attributes.get(entityIdAttr).getValue());
		}

		log.trace("isOidcAttributes() returns: {}", isOidc);
		return isOidc;
	}
}
