package org.ccfng.apibasedmigration.models;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class Patient {

	private Integer patientID;

	private Integer creator;

	private Date dateCreated;

	private String phone;

	private String gender;

	private Date birthDate;

	private boolean birthdateEstimated;

	private boolean dead;

	private Date deathDate;

	private String causeOfDeath;

	private Set<Address> address = new HashSet<>();

	private Set<PatientName> patientname = new HashSet<>();

	private Facility facility;

	private Set<Identifier> identifiers = new HashSet<>();

	private Set<Encounter> encounters = new HashSet<>();

	public Patient() {
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public boolean isBirthdateEstimated() {
		return birthdateEstimated;
	}

	public void setBirthdateEstimated(boolean birthdateEstimated) {
		this.birthdateEstimated = birthdateEstimated;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public String getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(String causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

	public Set<Address> getAddress() {
		return address;
	}

	public void setAddress(Set<Address> address) {
		this.address = address;
	}

	public Set<PatientName> getPatientname() {
		return patientname;
	}

	public void setPatientname(Set<PatientName> patientname) {
		this.patientname = patientname;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public Set<Identifier> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(Set<Identifier> identifiers) {
		this.identifiers = identifiers;
	}

	public Set<Encounter> getEncounters() {
		return encounters;
	}

	public void setEncounters(Set<Encounter> encounters) {
		this.encounters = encounters;
	}

	public Integer getPatientID() {
		return patientID;
	}

	public void setPatientID(Integer patientID) {
		this.patientID = patientID;
	}

	@Override
	public String toString() {
		return "Patient{" +
				"patientID=" + patientID +
				", creator=" + creator +
				", dateCreated=" + dateCreated +
				", phone='" + phone + '\'' +
				", gender='" + gender + '\'' +
				", birthDate=" + birthDate +
				", birthdateEstimated=" + birthdateEstimated +
				", dead=" + dead +
				", deathDate=" + deathDate +
				", causeOfDeath='" + causeOfDeath + '\'' +
				", address=" + address +
				", patientname=" + patientname +
				", facility=" + facility +
				", identifiers=" + identifiers +
				", encounters=" + encounters +
				'}'+"\n";
	}
}
