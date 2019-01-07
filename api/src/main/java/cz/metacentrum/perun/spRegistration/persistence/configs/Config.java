package cz.metacentrum.perun.spRegistration.persistence.configs;

import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class Config {

	private static final Logger log = LoggerFactory.getLogger(Config.class);

	private AppConfig appConfig;
	private AttrsConfig facilityServiceConfig;
	private AttrsConfig facilityOrganizationConfig;
	private AttrsConfig facilityMembershipConfig;
	private AttrsConfig facilityOidcConfig;
	private AttrsConfig facilitySamlConfig;
	private Properties messagesConfig;

	public void setAppConfig(AppConfig appConfig) {
		log.debug("setting app config: {}", appConfig);
		this.appConfig = appConfig;
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setFacilityServiceConfig(AttrsConfig facilityServiceConfig) {
		log.debug("setting facility service config: {}", facilityServiceConfig);
		this.facilityServiceConfig = facilityServiceConfig;
	}

	public void setFacilityOrganizationConfig(AttrsConfig facilityOrganizationConfig) {
		log.debug("setting facility organization config: {}", facilityOrganizationConfig);
		this.facilityOrganizationConfig = facilityOrganizationConfig;
	}

	public void setFacilityMembershipConfig(AttrsConfig facilityMembershipConfig) {
		log.debug("setting facility membership config: {}", facilityMembershipConfig);
		this.facilityMembershipConfig = facilityMembershipConfig;
	}

	public void setFacilityOidcConfig(AttrsConfig facilityOidcConfig) {
		log.debug("setting facility oidc config: {}", facilityOidcConfig);
		this.facilityOidcConfig = facilityOidcConfig;
	}

	public void setFacilitySamlConfig(AttrsConfig facilitySamlConfig) {
		log.debug("setting facility saml config: {}", facilitySamlConfig);
		this.facilitySamlConfig = facilitySamlConfig;
	}

	public void setMessagesConfig(Properties messagesConfig) {
		log.debug("setting messages config: {}", messagesConfig);
		this.messagesConfig = messagesConfig;
	}

	public Properties getMessagesConfig() {
		return messagesConfig;
	}

	public List<AttrInput> getServiceInputs() {
		return this.facilityServiceConfig.getInputs();
	}

	public List<AttrInput> getOrganizationInputs() {
		return this.facilityOrganizationConfig.getInputs();
	}

	public List<AttrInput> getSamlInputs() {
		return this.facilitySamlConfig.getInputs();
	}

	public List<AttrInput> getOidcInputs() {
		return this.facilityOidcConfig.getInputs();
	}

	public List<AttrInput> getMembershipInputs() {
		return this.facilityMembershipConfig.getInputs();
	}

}
