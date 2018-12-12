package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttribute;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceUtils {

	public static Map<String, PerunAttribute> transformListToMap(List<PerunAttribute> attributes, AppConfig appConfig) {
		Map<String, PerunAttribute> convertedAttributes = new HashMap<>();
		for (PerunAttribute a: attributes) {
<<<<<<< HEAD:api/src/main/java/cz/metacentrum/perun/spRegistration/service/ServiceUtils.java
			PerunAttributeDefinition def = appConfig.getAttrDefinition(a.getFullName());
			if (def == null) {
				System.out.println(a.getFullName());
				System.out.println(a);
			}
=======
			PerunAttributeDefinition def = config.getAttrDefinition(a.getFullName());
>>>>>>> TODO:service/src/main/java/cz/metacentrum/perun/spRegistration/service/ServiceUtils.java
			a.setDefinition(def);
			convertedAttributes.put(a.getFullName(), a);
		}

		return convertedAttributes;
	}
}
