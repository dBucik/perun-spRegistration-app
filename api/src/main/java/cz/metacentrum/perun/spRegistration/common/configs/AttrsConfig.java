package cz.metacentrum.perun.spRegistration.common.configs;

import cz.metacentrum.perun.spRegistration.persistence.PersistenceUtils;
import cz.metacentrum.perun.spRegistration.common.enums.AttributeCategory;
import cz.metacentrum.perun.spRegistration.common.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.common.models.AttrInput;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * Configuration class of attribute inputs
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
public class AttrsConfig {

	private List<AttrInput> inputs;

	public AttrsConfig(AppConfig appConfig, PerunConnector perunConnector, Properties attrsProps, AttributeCategory category) throws ConnectorException, UnsupportedEncodingException {
		inputs = PersistenceUtils.initializeAttributes(perunConnector, appConfig, attrsProps, category);
	}

	List<AttrInput> getInputs() {
		return inputs;
	}

	void setInputs(List<AttrInput> inputs) {
		this.inputs = inputs;
	}

	@Override
	public String toString() {
		return "AttrsConfig{" +
				"inputs=" + inputs +
				'}';
	}
}
