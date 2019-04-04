package cz.metacentrum.perun.spRegistration.persistence.configs;

import cz.metacentrum.perun.spRegistration.persistence.models.PerunAttributeDefinition;
import cz.metacentrum.perun.spRegistration.persistence.connectors.PerunConnector;
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

public class AppConfig {

	private String idpAttribute;
	private String idpAttributeValue;
	private String adminsAttr;
	private Set<Long> admins;
	private String extSourceProxy;
	private String userEmailAttr;
	private Map<String, PerunAttributeDefinition> perunAttributeDefinitionsMap = new HashMap<>();
	private boolean oidcEnabled;
	private List<String> langs = new ArrayList<>();

	private final Properties enLocale = new Properties();
	private final Properties csLocale = new Properties();

	private PerunConnector connector;
	private String showOnServicesListAttribute;
	private String testSpAttribute;
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

	public AppConfig() {
		Resource enLang = new ClassPathResource("localization.properties");
		Resource csLang = new ClassPathResource("localization_cs.properties");
		try (InputStream en = enLang.getInputStream(); InputStream cs = csLang.getInputStream()) {
			enLocale.load(en);
			csLocale.load(cs);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load translations", e);
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

	public String getAdminsAttr() {
		return adminsAttr;
	}

	public void setAdminsAttr(String adminsAttr) {
		this.adminsAttr = adminsAttr;
	}

	public String getExtSourceProxy() {
		return extSourceProxy;
	}

	public void setExtSourceProxy(String extSourceProxy) {
		this.extSourceProxy = extSourceProxy;
	}

	public String getUserEmailAttr() {
		return userEmailAttr;
	}

	public void setUserEmailAttr(String userEmailAttr) {
		this.userEmailAttr = userEmailAttr;
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
		secret = fixSecret(secret, 32);
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

	public void setPerunAttributeDefinitionsMap(Map<String, PerunAttributeDefinition> perunAttributeDefinitionsMap) {
		this.perunAttributeDefinitionsMap = perunAttributeDefinitionsMap;
	}

	public void setSignaturesEndpointUrl(String signaturesEndpointUrl) {
		this.signaturesEndpointUrl = signaturesEndpointUrl;
	}

	public String getSignaturesEndpointUrl() {
		return signaturesEndpointUrl;
	}

	public void setAdminsEndpoint(String adminsEndpoint) {
		this.adminsEndpoint = adminsEndpoint;
	}

	public String getAdminsEndpoint() {
		return adminsEndpoint;
	}

	@Override
	public String toString() {
		return "idpAttribute: '" + idpAttribute + "'\n" +
				"idpAttributeValue: '" + idpAttributeValue + "'\n" +
				"adminsAttr: '" + adminsAttr + "'\n" +
				"admins=" + admins + "'\n" +
				"extSourceProxy: '" + extSourceProxy + "'\n" +
				"userEmailAttr: '" + userEmailAttr + "'\n" +
				"perunAttributeDefinitionsMap: " + perunAttributeDefinitionsMap + "'\n" +
				"oidcEnabled=" + oidcEnabled + "'\n" +
				"langs=" + langs + "'\n" +
				"enLocale=" + enLocale + "'\n" +
				"csLocale=" + csLocale + "'\n" +
				"connector=" + connector + "'\n" +
				"showOnServicesListAttribute: '" + showOnServicesListAttribute + "'\n" +
				"testSpAttribute: '" + testSpAttribute + "'\n" +
				"footerHTML: '" + footerHTML + "'\n" +
				"headerLogo: '" + headerLogo + "'\n" +
				"headerTitle: '" + headerTitle + "'\n" +
				"headerHTML: '" + headerHTML + "'\n" +
				"confirmationPeriodDays: " + confirmationPeriodDays + "'\n" +
				"confirmationPeriodHours: " + confirmationPeriodHours + "'\n" +
				"secret: " + secret + "'\n" +
				"specifyAuthoritiesEnabled: " + specifyAuthoritiesEnabled + "'\n" +
				"signaturesEndpointUrl: '" + signaturesEndpointUrl + "'\n" +
				"adminsEndpoint: '" + adminsEndpoint + "'\n";
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

	private String fixSecret(String s, int length) {
		if (s.length() < length) {
			int missingLength = length - s.length();
			StringBuilder sBuilder = new StringBuilder(s);
			for (int i = 0; i < missingLength; i++) {
				sBuilder.append("A");
			}
			s = sBuilder.toString();
		}
		return s.substring(0, length);
	}
}
