package cz.metacentrum.perun.spRegistration.persistence;

import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Config {

	private	Resource enLang = new ClassPathResource("localization.properties");
	private Resource csLang = new ClassPathResource("localization_cs.properties");

	private String idpAttribute;
	private String idpAttributeValue;
	private Set<Long> admins;
	private Map<String, PerunAttributeDefinition> perunAttributeDefinitionsMap = new HashMap<>();
	private List<AttrInput> oidcInputs = new ArrayList<>();
	private List<AttrInput> samlInputs = new ArrayList<>();
	private List<String> samlAttributes;
	private List<String> oidcAttributes;
	private boolean oidcEnabled;

	private static final String SAML_ATTRS = "urn:perun:facility:attribute-def:def:requiredAttributes";
	private static final String OIDC_SCOPES = "urn:perun:facility:attribute-def:def:requiredScopes";

	private PerunConnector connector;

	public Config(PerunConnector connector, String idpAttribute, String idpAttributeValue, Set<Long> admins,
				  Properties facilityAttrsProperties, List<String> samlAttributes, List<String> oidcAttributes,
				  boolean oidcEnabled) {
		this.connector = connector;
		this.idpAttribute = idpAttribute;
		this.idpAttributeValue = idpAttributeValue;
		this.admins = admins;
		this.samlAttributes = samlAttributes;
		this.oidcAttributes = oidcAttributes;
		initializeAttributes(facilityAttrsProperties);
		this.oidcEnabled = oidcEnabled;
	}

	public String getIdpAttribute() {
		return idpAttribute;
	}

	public String getIdpAttributeValue() {
		return idpAttributeValue;
	}

	public Set<Long> getAdmins() {
		return Collections.unmodifiableSet(admins);
	}

	public PerunAttributeDefinition getAttrDefinition(String fullName) {
		return perunAttributeDefinitionsMap.get(fullName);
	}

	public boolean isAdmin (Long userId) {
		return admins.contains(userId);
	}

	public List<AttrInput> getOidcInputs() {
		return oidcInputs;
	}

	public List<AttrInput> getSamlInputs() {
		return samlInputs;
	}

	public boolean isOidcEnabled() {
		return oidcEnabled;
	}

	private void initializeAttributes(Properties facilityAttrsProperties) {
		Properties enProps = new Properties();
		Properties csProps = new Properties();
		try (InputStream en = enLang.getInputStream(); InputStream cs = csLang.getInputStream()) {
			enProps.load(en);
			csProps.load(cs);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load translations");
		}

		for (String prop: facilityAttrsProperties.stringPropertyNames()) {
			if (prop.contains("isRequired")) {
				continue;
			}
			String attrName = facilityAttrsProperties.getProperty(prop);

			String isRequiredProp = prop.replaceAll("attrName", "isRequired");
			boolean required = Boolean.parseBoolean(facilityAttrsProperties.getProperty(isRequiredProp));

			PerunAttributeDefinition def = connector.getAttributeDefinition(attrName);
			perunAttributeDefinitionsMap.put(def.getFullName(), def);

			Map<String, String> name = new HashMap<>();
			Map<String, String> desc = new HashMap<>();

			String nameKey = attrName.replaceAll(":", ".") + ".name";
			String descKey = attrName.replaceAll(":", ".") + ".desc";

			name.put("en", enProps.getProperty(nameKey));
			name.put("cs", csProps.getProperty(nameKey));

			desc.put("en", enProps.getProperty(descKey));
			desc.put("cs", csProps.getProperty(descKey));

			AttrInput input = new AttrInput(def.getFullName(), name, desc, def.getType(), required);
			if (SAML_ATTRS.equals(attrName)) {
				input.setAllowedValues(samlAttributes);
			} else if (OIDC_SCOPES.equals(attrName)) {
				input.setAllowedValues(oidcAttributes);
			}

			if (prop.startsWith("attributes.common")) {
				oidcInputs.add(input);
				samlInputs.add(input);
			} else if (prop.startsWith("attributes.saml")) {
				samlInputs.add(input);
			} else if (prop.startsWith("attributes.oidc")) {
				oidcInputs.add(input);
			}
		}
	}
}
