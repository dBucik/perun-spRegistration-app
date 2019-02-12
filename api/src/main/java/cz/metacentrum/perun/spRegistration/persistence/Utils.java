package cz.metacentrum.perun.spRegistration.persistence;

import cz.metacentrum.perun.spRegistration.persistence.configs.AppConfig;
import cz.metacentrum.perun.spRegistration.persistence.exceptions.RPCException;
import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Utils {

	private final static String ATTR_NAME = "attrName";
	private final static String IS_DISPLAYED = "isDisplayed";
	private final static String IS_EDITABLE = "isEditable";
	private final static String IS_REQUIRED = "isRequired";
	private final static String ALLOWED_VALUES = "allowedValues";
	private final static String ALLOWED_KEYS = "allowedKeys";
	private final static String POSITION = "position";
	private static final String REGEX = "regex" ;

	public static List<AttrInput> initializeAttributes(PerunConnector connector, AppConfig appConfig, Properties props) throws RPCException {
		List<AttrInput> inputs = new ArrayList<>();
		Properties en = appConfig.getEnLocale();
		Properties cs = appConfig.getCsLocale();
		boolean isCsDisabled = cs.isEmpty();

		for (String prop: props.stringPropertyNames()) {
			if (! prop.contains(ATTR_NAME)) {
				continue;
			}

			String attrName = props.getProperty(prop);

			PerunAttributeDefinition def = connector.getAttributeDefinition(attrName);
			appConfig.getPerunAttributeDefinitionsMap().put(def.getFullName(), def);

			Map<String, String> name = new HashMap<>();
			Map<String, String> desc = new HashMap<>();

			String nameKey = attrName.replaceAll(":", ".") + ".name";
			String descKey = attrName.replaceAll(":", ".") + ".desc";

			name.put("en", en.getProperty(nameKey));
			desc.put("en", en.getProperty(descKey));

			if (! isCsDisabled) {
				name.put("cs", cs.getProperty(nameKey));
				desc.put("cs", cs.getProperty(descKey));
			}
			AttrInput input = new AttrInput(def.getFullName(), name, desc, def.getType());

			setAdditionalOptions(props, input, prop);

			inputs.add(input);
		}

		return inputs;
	}

	private static void setAdditionalOptions(Properties props, AttrInput input, String prop) {
		String isRequiredProp = prop.replaceAll(ATTR_NAME, IS_REQUIRED);
		if (props.containsKey(isRequiredProp)) {
			boolean val = Boolean.parseBoolean(props.getProperty(isRequiredProp));
			input.setRequired(val);
		}

		String isDisplayedProp = prop.replaceAll(ATTR_NAME, IS_DISPLAYED);
		if (props.containsKey(isDisplayedProp)) {
			boolean val = Boolean.parseBoolean(props.getProperty(isDisplayedProp));
			input.setDisplayed(val);
		}

		String isEditableProp = prop.replaceAll(ATTR_NAME, IS_EDITABLE);
		if (props.containsKey(isEditableProp)) {
			boolean val = Boolean.parseBoolean(props.getProperty(isEditableProp));
			input.setEditable(val);
		}

		String allowedValuesProp = prop.replaceAll(ATTR_NAME, ALLOWED_VALUES);
		if (props.containsKey(allowedValuesProp)) {
			String val = props.getProperty(allowedValuesProp);
			input.setAllowedValues(Arrays.asList(val.split(",")));
		}

		String allowedKeysProp = prop.replaceAll(ATTR_NAME, ALLOWED_KEYS);
		if (props.containsKey(allowedKeysProp)) {
			String val = props.getProperty(allowedKeysProp);
			input.setAllowedKeys(Arrays.asList(val.split(",")));
		}

		String positionProp = prop.replaceAll(ATTR_NAME, POSITION);
		if (props.containsKey(positionProp)) {
			int val = Integer.parseInt(props.getProperty(positionProp));
			input.setDisplayPosition(val);
		}

		String regexProp = prop.replaceAll(ATTR_NAME, REGEX);
		if (props.containsKey(regexProp)) {
			String val = props.getProperty(regexProp);
			input.setRegex(val);
		}
	}
}
