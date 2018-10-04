package cz.metacentrum.perun.spRegistration.persistence.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of Perun User.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public class User extends PerunEntity {

	private String titleBefore;
	private String firstName;
	private String middleName;
	private String lastName;
	private String titleAfter;

	private String email;
	private List<Long> facilitiesWhereAdmin;
	private boolean isAdmin;

	public User(Long id) {
		super(id);
		facilitiesWhereAdmin = new ArrayList<>();
		isAdmin = false;
	}

	public User(Long id, String titleBefore, String firstName, String middleName, String lastName, String titleAfter) {
		super(id);
		this.titleBefore = titleBefore;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.titleAfter = titleAfter;
	}

	public String getTitleBefore() {
		return titleBefore;
	}

	public void setTitleBefore(String titleBefore) {
		this.titleBefore = titleBefore;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitleAfter() {
		return titleAfter;
	}

	public void setTitleAfter(String titleAfter) {
		this.titleAfter = titleAfter;
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

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public String getName() {
		return firstName + middleName + lastName;
	}
}
