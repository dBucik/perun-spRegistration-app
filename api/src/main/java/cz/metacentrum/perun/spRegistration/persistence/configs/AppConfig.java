package cz.metacentrum.perun.spRegistration.persistence.configs;

import cz.metacentrum.perun.spRegistration.persistence.exceptions.ConnectorException;
import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Class with application configuration
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
public class AppConfig {

	private Set<Long> appAdminIds;
	private String loginExtSource;
	private Map<String, PerunAttributeDefinition> perunAttributeDefinitionsMap;
	private boolean oidcEnabled;
	private List<String> availableLanguages;
	private PerunConnector perunConnector;
	private String showOnServicesListAttributeName;
	private String isTestSpAttribute;
	private String entityIdAttribute;
	private String clientIdAttribute;
	private String clientSecretAttribute;
	private String masterProxyIdentifierAttribute;
	private String masterProxyIdentifierAttributeValue;
	private String proxyIdentifierAttribute;
	private String proxyIdentifierAttributeValue;
	private String adminsAttributeName;
	private String userEmailAttributeName;
	private Map<String, String> prodTransferAuthoritiesMailsMap;

	private String footerHTML;
	private String headerLogo;
	private String headerTitle;
	private String headerHTML;

	private long confirmationPeriodDays;
	private long confirmationPeriodHours;
	private SecretKeySpec secret;
	private boolean specifyAuthoritiesEnabled;
	private String signaturesEndpointUrl;
	private String adminsEndpoint;

	private Properties enLocale;
	private Properties csLocale;

	@Autowired
	public AppConfig(@Qualifier("enLocale") Properties enLocale, @Qualifier("csLocale") Properties csLocale) {
		this.enLocale = new Properties();
		this.csLocale = new Properties();

		Resource enLang = new ClassPathResource("localization.properties");
		Resource csLang = new ClassPathResource("localization_cs.properties");
		try (InputStream en = enLang.getInputStream(); InputStream cs = csLang.getInputStream()) {
			this.enLocale.load(en);
			this.csLocale.load(cs);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load translations", e);
		}

		if (enLocale != null) {
			this.enLocale.putAll(enLocale);
		}

		if (csLocale != null) {
			this.enLocale.putAll(csLocale);
		}

		this.perunAttributeDefinitionsMap = new HashMap<>();
		this.availableLanguages = new ArrayList<>();
	}

	public Set<Long> getAppAdminIds() {
		return Collections.unmodifiableSet(appAdminIds);
	}

	public void setAppAdminIds(Set<Long> appAdminIds) {
		this.appAdminIds = appAdminIds;
	}

	public boolean isOidcEnabled() {
		return oidcEnabled;
	}

	public void setOidcEnabled(boolean oidcEnabled) {
		this.oidcEnabled = oidcEnabled;
	}

	public List<String> getAvailableLanguages() {
		return availableLanguages;
	}

	public void setAvailableLanguages(List<String> availableLanguages) {
		this.availableLanguages = availableLanguages;
	}

	public PerunConnector getPerunConnector() {
		return perunConnector;
	}

	public void setPerunConnector(PerunConnector perunConnector) {
		this.perunConnector = perunConnector;
	}

	public String getLoginExtSource() {
		return loginExtSource;
	}

	public void setLoginExtSource(String loginExtSource) {
		this.loginExtSource = loginExtSource;
	}

	public String getFooterHTML() {
		return footerHTML;
	}

	public void setFooterHTML(String footerHTML) {
		this.footerHTML = footerHTML;
	}

	public String getHeaderLogo() {
		return headerLogo;
	}

	public void setHeaderLogo(String headerLogo) {
		this.headerLogo = headerLogo;
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}

	public String getHeaderHTML() {
		return headerHTML;
	}

	public void setHeaderHTML(String headerHTML) {
		this.headerHTML = headerHTML;
	}

	public long getConfirmationPeriodDays() {
		return confirmationPeriodDays;
	}

	public void setConfirmationPeriodDays(long confirmationPeriodDays) {
		this.confirmationPeriodDays = confirmationPeriodDays;
	}

	public long getConfirmationPeriodHours() {
		return confirmationPeriodHours;
	}

	public void setConfirmationPeriodHours(long confirmationPeriodHours) {
		this.confirmationPeriodHours = confirmationPeriodHours;
	}

	public SecretKeySpec getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		secret = fixSecret(secret);
		MessageDigest sha;
		try {
			byte[] key = secret.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			this.secret = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public boolean getSpecifyAuthoritiesEnabled() {
		return specifyAuthoritiesEnabled;
	}

	public void setSpecifyAuthoritiesEnabled(boolean specifyAuthoritiesEnabled) {
		this.specifyAuthoritiesEnabled = specifyAuthoritiesEnabled;
	}

	public Map<String, PerunAttributeDefinition> getPerunAttributeDefinitionsMap() {
		return perunAttributeDefinitionsMap;
	}

	public void setPerunAttributeDefinitionsMap(Map<String, PerunAttributeDefinition> perunAttributeDefinitionsMap) {
		this.perunAttributeDefinitionsMap = perunAttributeDefinitionsMap;
	}

	public Map<String, String> getProdTransferAuthoritiesMailsMap() { return prodTransferAuthoritiesMailsMap; }

	public void setProdTransferAuthoritiesMailsMap(Properties prodTransferAuthoritiesMailsMap) {

		this.prodTransferAuthoritiesMailsMap = new HashMap<>();

		for (Map.Entry<Object, Object> entry : prodTransferAuthoritiesMailsMap.entrySet()) {
			this.prodTransferAuthoritiesMailsMap.put((String) entry.getKey(), (String) entry.getValue());
		}
	}

	public String getSignaturesEndpointUrl() {
		return signaturesEndpointUrl;
	}

	public void setSignaturesEndpointUrl(String signaturesEndpointUrl) {
		this.signaturesEndpointUrl = signaturesEndpointUrl;
	}

	public String getAdminsEndpoint() {
		return adminsEndpoint;
	}

	public void setAdminsEndpoint(String adminsEndpoint) {
		this.adminsEndpoint = adminsEndpoint;
	}

	// ATTRIBUTES

	public String getMasterProxyIdentifierAttribute() {
		return masterProxyIdentifierAttribute;
	}

	public void setMasterProxyIdentifierAttribute(String masterProxyIdentifierAttribute) throws ConnectorException {
		this.masterProxyIdentifierAttribute = masterProxyIdentifierAttribute;

		PerunAttributeDefinition def = perunConnector.getAttributeDefinition(masterProxyIdentifierAttribute);
		perunAttributeDefinitionsMap.put(masterProxyIdentifierAttribute, def);
	}

	public String getProxyIdentifierAttribute() {
		return proxyIdentifierAttribute;
	}

	public void setProxyIdentifierAttribute(String proxyIdentifierAttribute) throws ConnectorException {
		this.proxyIdentifierAttribute = proxyIdentifierAttribute;

		PerunAttributeDefinition def = perunConnector.getAttributeDefinition(proxyIdentifierAttribute);
		perunAttributeDefinitionsMap.put(proxyIdentifierAttribute, def);
	}

	public String getIsTestSpAttribute() {
		return isTestSpAttribute;
	}

	public void setIsTestSpAttribute(String isTestSpAttribute) throws ConnectorException {
		this.isTestSpAttribute = isTestSpAttribute;

		PerunAttributeDefinition def = perunConnector.getAttributeDefinition(isTestSpAttribute);
		perunAttributeDefinitionsMap.put(isTestSpAttribute, def);
	}

	public String getShowOnServicesListAttributeName() {
		return showOnServicesListAttributeName;
	}

	public void setShowOnServicesListAttributeName(String showOnServicesListAttributeName) throws ConnectorException {
		this.showOnServicesListAttributeName = showOnServicesListAttributeName;

		PerunAttributeDefinition def = perunConnector.getAttributeDefinition(showOnServicesListAttributeName);
		perunAttributeDefinitionsMap.put(showOnServicesListAttributeName, def);
	}

	public String getAdminsAttributeName() {
		return adminsAttributeName;
	}

	public void setAdminsAttributeName(String adminsAttributeName) {
		this.adminsAttributeName = adminsAttributeName;
	}

	public String getUserEmailAttributeName() {
		return userEmailAttributeName;
	}

	public void setUserEmailAttributeName(String userEmailAttributeName) {
		this.userEmailAttributeName = userEmailAttributeName;
	}

	public String getEntityIdAttribute() {
		return entityIdAttribute;
	}

	public void setEntityIdAttribute(String entityIdAttribute) {
		this.entityIdAttribute = entityIdAttribute;
	}

	public String getClientIdAttribute() {
		return clientIdAttribute;
	}

	public void setClientIdAttribute(String clientIdAttribute) throws ConnectorException {
		this.clientIdAttribute = clientIdAttribute;

		PerunAttributeDefinition def = perunConnector.getAttributeDefinition(clientIdAttribute);
		perunAttributeDefinitionsMap.put(clientIdAttribute, def);
	}

	public String getClientSecretAttribute() {
		return clientSecretAttribute;
	}

	public void setClientSecretAttribute(String clientSecretAttribute) throws ConnectorException {
		this.clientSecretAttribute = clientSecretAttribute;

		PerunAttributeDefinition def = perunConnector.getAttributeDefinition(clientSecretAttribute);
		perunAttributeDefinitionsMap.put(clientSecretAttribute, def);
	}

	// VALUES

	public String getMasterProxyIdentifierAttributeValue() {
		return masterProxyIdentifierAttributeValue;
	}

	public void setMasterProxyIdentifierAttributeValue(String masterProxyIdentifierAttributeValue) {
		this.masterProxyIdentifierAttributeValue = masterProxyIdentifierAttributeValue;
	}

	public String getProxyIdentifierAttributeValue() {
		return proxyIdentifierAttributeValue;
	}

	public void setProxyIdentifierAttributeValue(String proxyIdentifierAttributeValue) {
		this.proxyIdentifierAttributeValue = proxyIdentifierAttributeValue;
	}

	@Override
	public String toString() {
		return	"appAdminIds: " + appAdminIds + "'\n" +
				"loginExtSource: '" + loginExtSource + "'\n" +
				"perunAttributeDefinitionsMap: " + perunAttributeDefinitionsMap + "'\n" +
				"oidcEnabled=" + oidcEnabled + "'\n" +
				"availableLanguages=" + availableLanguages + "'\n" +
				"enLocale=" + enLocale + "'\n" +
				"csLocale=" + csLocale + "'\n" +
				"perunConnector=" + perunConnector + "'\n" +
				"footerHTML: '" + footerHTML + "'\n" +
				"headerLogo: '" + headerLogo + "'\n" +
				"headerTitle: '" + headerTitle + "'\n" +
				"headerHTML: '" + headerHTML + "'\n" +
				"confirmationPeriodDays: " + confirmationPeriodDays + "'\n" +
				"confirmationPeriodHours: " + confirmationPeriodHours + "'\n" +
				"secret: " + secret + "'\n" +
				"specifyAuthoritiesEnabled: " + specifyAuthoritiesEnabled + "'\n" +
				"signaturesEndpointUrl: '" + signaturesEndpointUrl + "'\n" +
				"adminsEndpoint: '" + adminsEndpoint + "'\n" +
				"proxyIdentifierAttribute: '" + proxyIdentifierAttribute + "'\n" +
				"masterProxyIdentifierAttribute: '" + masterProxyIdentifierAttribute + "'\n" +
				"proxyIdentifierAttributeValue: '" + proxyIdentifierAttributeValue + "'\n" +
				"adminsAttributeName: '" + adminsAttributeName + "'\n" +
				"userEmailAttributeName: '" + userEmailAttributeName + "'\n" +
				"showOnServicesListAttributeName: '" + showOnServicesListAttributeName + "'\n" +
				"isTestSpAttribute: '" + isTestSpAttribute + "'\n";
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

	public boolean isAppAdmin(Long userId) {
		return appAdminIds.contains(userId);
	}

	private String fixSecret(String s) {
		if (s.length() < 32) {
			int missingLength = 32 - s.length();
			StringBuilder sBuilder = new StringBuilder(s);
			for (int i = 0; i < missingLength; i++) {
				sBuilder.append('A');
			}
			s = sBuilder.toString();
		}
		return s.substring(0, 32);
	}

}
