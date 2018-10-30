package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.persistence.Config;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceUtils {

	public static Map<String, PerunAttribute> transformListToMap(List<PerunAttribute> attributes, Config config) {
		Map<String, PerunAttribute> convertedAttributes = new HashMap<>();
		for (PerunAttribute a: attributes) {
			PerunAttributeDefinition def = config.getAttrDefinition(a.getFullName());
			a.setDefinition(def);
			convertedAttributes.put(a.getFullName(), a);
		}

		return convertedAttributes;
	}
}
