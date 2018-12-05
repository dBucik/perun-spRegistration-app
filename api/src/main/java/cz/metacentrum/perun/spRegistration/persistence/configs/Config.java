package cz.metacentrum.perun.spRegistration.persistence.configs;

import cz.metacentrum.perun.spRegistration.persistence.models.AttrInput;

import java.util.List;

public class Config {

	private AppConfig appConfig;
	private AttrsConfig facilityServiceConfig;
	private AttrsConfig facilityOrganizationConfig;
	private AttrsConfig facilityMembershipConfig;
	private AttrsConfig facilityOidcConfig;
	private AttrsConfig facilitySamlConfig;

	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setFacilityServiceConfig(AttrsConfig facilityServiceConfig) {
		this.facilityServiceConfig = facilityServiceConfig;
	}

	public void setFacilityOrganizationConfig(AttrsConfig facilityOrganizationConfig) {
		this.facilityOrganizationConfig = facilityOrganizationConfig;
	}

	public void setFacilityMembershipConfig(AttrsConfig facilityMembershipConfig) {
		this.facilityMembershipConfig = facilityMembershipConfig;
	}

	public void setFacilityOidcConfig(AttrsConfig facilityOidcConfig) {
		this.facilityOidcConfig = facilityOidcConfig;
	}

	public void setFacilitySamlConfig(AttrsConfig samlConfig) {
		this.facilitySamlConfig = samlConfig;
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
