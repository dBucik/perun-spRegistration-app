package cz.metacentrum.perun.spRegistration.persistence.models;

import java.util.Collections;
import java.util.Set;

//TODO: javadoc
public class Config {
	
	private String idpAttribute;
	private String idpAttributeValue;
	private Set<Long> admins;

	public Config(String idpAttribute, String idpAttributeValue, Set<Long> admins) {
		this.idpAttribute = idpAttribute;
		this.idpAttributeValue = idpAttributeValue;
		this.admins = admins;
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

	public boolean isAdmin (Long userId) {
		return admins.contains(userId);
	}

}
