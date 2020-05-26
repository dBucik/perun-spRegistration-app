package cz.metacentrum.perun.spRegistration.common.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import cz.metacentrum.perun.spRegistration.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Representation of Perun User.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>;
 */
public class User extends PerunEntity {

	private String name;
	private String email;
	@JsonIgnore private List<Long> facilitiesWhereAdmin = new ArrayList<>();
	@JsonProperty("isAppAdmin") private boolean isAdmin = false;

	public User(Long id) {
		super(id);
	}

	public User(Long id, String name, String email, List<Long> facilitiesWhereAdmin, boolean isAdmin) {
		super(id);
		this.name = name;
		this.email = email;
		this.facilitiesWhereAdmin = facilitiesWhereAdmin;
		this.isAdmin = isAdmin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Long> getFacilitiesWhereAdmin() {
		return facilitiesWhereAdmin;
	}

	public void setFacilitiesWhereAdmin(List<Long> facilitiesWhereAdmin) {
		this.facilitiesWhereAdmin = facilitiesWhereAdmin;
	}

	@JsonIgnore
	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	/**
	 * Parse user from JSON obtained from Perun
	 * @param json JSON from Perun
	 * @return User or null
	 */
	public static User fromPerunJson(JsonNode json) {
		if (Utils.checkParamsInvalid(json)) {
			throw new IllegalArgumentException(Utils.GENERIC_ERROR_MSG);
		}

		Long id = json.get("id").asLong();
		String firstName = json.hasNonNull("firstName") ? json.get("firstName").textValue() : null;
		String middleName = json.hasNonNull("middleName") ? json.get("middleName").textValue() : null;
		String lastName = json.hasNonNull("lastName") ? json.get("lastName").textValue() : null;
		String titleBefore = json.hasNonNull("titleBefore") ? json.get("titleBefore").textValue() : null;
		String titleAfter = json.hasNonNull("titleAfter") ? json.get("titleAfter").textValue() : null;

		StringJoiner joiner = new StringJoiner(" ");
		if (titleBefore != null && !titleBefore.isEmpty()) {
			joiner.add(titleBefore);
		}
		if (firstName != null && !firstName.isEmpty()) {
			joiner.add(firstName);
		}
		if (middleName != null && !middleName.isEmpty()) {
			joiner.add(middleName);
		}
		if (lastName != null && !lastName.isEmpty()) {
			joiner.add(lastName);
		}
		if (titleAfter != null && !titleAfter.isEmpty()) {
			joiner.add(titleAfter);
		}

		User user = new User(id);
		user.setName(joiner.toString());
		return user;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", email='" + email + '\'' +
				", facilitiesWhereAdmin=" + facilitiesWhereAdmin +
				", isAdmin=" + isAdmin +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return isAdmin == user.isAdmin &&
				Objects.equals(name, user.name) &&
				Objects.equals(email, user.email) &&
				Objects.equals(facilitiesWhereAdmin, user.facilitiesWhereAdmin);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, email, facilitiesWhereAdmin, isAdmin);
	}
}
