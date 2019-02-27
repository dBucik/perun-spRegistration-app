package cz.metacentrum.perun.spRegistration.persistence.configs;

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

public class AppConfig {

	private String idpAttribute;
	private String idpAttributeValue;
	private String testSpAttribute;
	private String adminsAttr;
	private Set<Long> admins;
	private Map<String, PerunAttributeDefinition> perunAttributeDefinitionsMap = new HashMap<>();
	private boolean oidcEnabled;
	private List<String> langs = new ArrayList<>();

	private Properties enLocale = new Properties();
	private Properties csLocale = new Properties();

	private PerunConnector connector;
	private String showOnServicesListAttribute;

	private List<String> signingAuthorities = new ArrayList<>();

	public AppConfig() {
		Resource enLang = new ClassPathResource("localization.properties");
		Resource csLang = new ClassPathResource("localization_cs.properties");
		try (InputStream en = enLang.getInputStream(); InputStream cs = csLang.getInputStream()) {
			enLocale.load(en);
			csLocale.load(cs);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load translations");
		}
	}

	public String getIdpAttribute() {
		return idpAttribute;
	}

	public void setIdpAttribute(String idpAttribute) {
		this.idpAttribute = idpAttribute;
	}

	public String getIdpAttributeValue() {
		return idpAttributeValue;
	}

	public void setIdpAttributeValue(String idpAttributeValue) {
		this.idpAttributeValue = idpAttributeValue;
	}

	public Set<Long> getAdmins() {
		return Collections.unmodifiableSet(admins);
	}

	public void setAdmins(Set<Long> admins) {
		this.admins = admins;
	}

	public boolean isOidcEnabled() {
		return oidcEnabled;
	}

	public void setOidcEnabled(boolean oidcEnabled) {
		this.oidcEnabled = oidcEnabled;
	}

	public List<String> getLangs() {
		return langs;
	}

	public void setLangs(List<String> langs) {
		this.langs = langs;
	}

	public PerunConnector getConnector() {
		return connector;
	}

	public void setConnector(PerunConnector connector) {
		this.connector = connector;
	}

	public String getTestSpAttribute() {
		return testSpAttribute;
	}

	public void setTestSpAttribute(String testSpAttribute) {
		this.testSpAttribute = testSpAttribute;
	}

	public String getShowOnServicesListAttribute() {
		return showOnServicesListAttribute;
	}

	public void setShowOnServicesListAttribute(String showOnServicesListAttribute) {
		this.showOnServicesListAttribute = showOnServicesListAttribute;
	}

	public Map<String, PerunAttributeDefinition> getPerunAttributeDefinitionsMap() {
		return perunAttributeDefinitionsMap;
	}

	public void setPerunAttributeDefinitionsMap(Map<String, PerunAttributeDefinition> perunAttributeDefinitionsMap) {
		this.perunAttributeDefinitionsMap = perunAttributeDefinitionsMap;
	}

	@Override
	public String toString() {
		return "AppConfig{" +
				"idpAttribute='" + idpAttribute + '\'' +
				", idpAttributeValue='" + idpAttributeValue + '\'' +
				", testSpAttribute='" + testSpAttribute + '\'' +
				", adminsAttr='" + adminsAttr + '\'' +
				", admins=" + admins +
				", perunAttributeDefinitionsMap=" + perunAttributeDefinitionsMap +
				", oidcEnabled=" + oidcEnabled +
				", langs=" + langs +
				", enLocale=" + enLocale +
				", csLocale=" + csLocale +
				", connector=" + connector +
				", showOnServicesListAttribute='" + showOnServicesListAttribute + '\'' +
				'}';
	}

	// custom methods

	public Properties getEnLocale() {
		return enLocale;
	}

	public Properties getCsLocale() {
		return csLocale;
	}

	public PerunAttributeDefinition getAttrDefinition(String fullName) {
		return perunAttributeDefinitionsMap.get(fullName);
	}

	public boolean isAdmin (Long userId) {
		return admins.contains(userId);
	}

	public void setAdminsAttr(String adminsAttr) {
		this.adminsAttr = adminsAttr;
	}

	public String getAdminsAttr() {
		return adminsAttr;
	}

	public void setSigningAuthorities(List<String> signingAuthorities) {
		this.signingAuthorities = signingAuthorities;
	}

	public List<String> getSigningAuthorities() {
		return signingAuthorities;
	}
}
