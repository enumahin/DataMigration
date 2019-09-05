package org.ccfng.apibasedmigration.models;

public class Provider {

	private String givenName;

	private String middleName;

	private String surname;

	public Provider(String givenName, String middleName, String surname) {
		this.givenName = givenName;
		this.middleName = middleName;
		this.surname = surname;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}
