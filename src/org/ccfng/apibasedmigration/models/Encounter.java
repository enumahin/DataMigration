package org.ccfng.apibasedmigration.models;

import java.util.List;

public class Encounter {

	private Integer patientID;

	private Integer encounterID;

	private Integer encounterTypeId;

	private Integer encounterLocationId;

	private Provider provider;

	private String encounterDate;

	private Integer formTypeId;

    private List<Obs> obs;

	public Encounter() {
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Encounter(Integer encounterId, Integer encounterTypeId, Integer encounterLocationId, String encounterDate, Integer formTypeId) {
		this.encounterTypeId = encounterTypeId;
		this.encounterID = encounterId;
		this.encounterLocationId = encounterLocationId;
		this.encounterDate = encounterDate;
		this.formTypeId = formTypeId;
	}

	public Integer getEncounterTypeId() {
		return encounterTypeId;
	}

	public void setEncounterTypeId(Integer encounterTypeId) {
		this.encounterTypeId = encounterTypeId;
	}

	public Integer getEncounterLocationId() {
		return encounterLocationId;
	}

	public void setEncounterLocationId(Integer encounterLocationId) {
		this.encounterLocationId = encounterLocationId;
	}

	public String getEncounterDate() {
		return encounterDate;
	}

	public void setEncounterDate(String encounterDate) {
		this.encounterDate = encounterDate;
	}

	public Integer getFormTypeId() {
		return formTypeId;
	}

	public void setFormTypeId(Integer formTypeId) {
		this.formTypeId = formTypeId;
	}

	public List<Obs> getObs() {
		return obs;
	}

	public void setObs(List<Obs> obs) {
		this.obs = obs;
	}

	public Integer getPatientID() {
		return patientID;
	}

	public void setPatientID(Integer patientID) {
		this.patientID = patientID;
	}

	public Integer getEncounterID() {
		return encounterID;
	}

	public void setEncounterID(Integer encounterID) {
		this.encounterID = encounterID;
	}

	@Override
	public String toString() {
		return "Encounter{" +
				"encounterTypeId=" + encounterTypeId +
				", encounterLocationId=" + encounterLocationId +
				", encounterDate=" + encounterDate +
				", formTypeId=" + formTypeId +
				", obs=" + obs +
				'}';
	}
}
