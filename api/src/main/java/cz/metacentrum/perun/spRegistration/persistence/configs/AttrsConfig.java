package cz.metacentrum.perun.spRegistration.persistence.configs;

import cz.metacentrum.perun.spRegistration.persistence.Utils;
import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;
import cz.metacentrum.perun.spRegistration.persistence.rpc.PerunConnector;

import java.util.List;
import java.util.Properties;

public class AttrsConfig {

	private List<AttrInput> inputs;

	public AttrsConfig(AppConfig appConfig, PerunConnector connector, Properties attrsProps) {
		inputs = Utils.initializeAttributes(connector, appConfig, attrsProps);
	}

	public List<AttrInput> getInputs() {
		return inputs;
	}

	public void setInputs(List<AttrInput> inputs) {
		this.inputs = inputs;
	}
}
