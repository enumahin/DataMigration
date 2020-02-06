package org.ccfng.openmrscleanup.models;

import org.ccfng.global.Demographics;

import java.sql.Date;

public class PatientStatus extends Demographics {

	private Date lastARVDate;

	private Long drugDuration;

	private Date nextAppointmentDate;

	private String status;

	private String reason;

	private Integer formEncounter;

	private Date formEncounterDate;

	private String formExists;

	private Date firstEncounterDate;

	private Date artStartDate;

	public PatientStatus() {
	}

	public PatientStatus(Date lastARVDate, Long drugDuration, Date nextAppointmentDate, String status,
			String reason) {
		this.lastARVDate = lastARVDate;
		this.drugDuration = drugDuration;
		this.nextAppointmentDate = nextAppointmentDate;
		this.status = status;
		this.reason = reason;
	}

	public PatientStatus(Integer patientID, Integer encounterID, Integer obsID, Integer obsGroupID, String pepfarID,
			String patientName, Date lastARVDate, Long drugDuration, Date nextAppointmentDate, String status,
			String reason) {
		super(patientID, encounterID, obsID, obsGroupID, pepfarID, patientName);
		this.lastARVDate = lastARVDate;
		this.drugDuration = drugDuration;
		this.nextAppointmentDate = nextAppointmentDate;
		this.status = status;
		this.reason = reason;
	}

	public Date getLastARVDate() {
		return lastARVDate;
	}

	public void setLastARVDate(Date lastARVDate) {
		this.lastARVDate = lastARVDate;
	}

	public Long getDrugDuration() {
		return drugDuration;
	}

	public void setDrugDuration(Long drugDuration) {
		this.drugDuration = drugDuration;
	}

	public Date getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	public void setNextAppointmentDate(Date nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getFormEncounter() {
		return formEncounter;
	}

	public void setFormEncounter(Integer formEncounter) {
		this.formEncounter = formEncounter;
	}

	public String getFormExists() {
		return formExists;
	}

	public void setFormExists(String formExists) {
		this.formExists = formExists;
	}

	public Date getFirstEncounterDate() {
		return firstEncounterDate;
	}

	public void setFirstEncounterDate(Date firstEncounterDate) {
		this.firstEncounterDate = firstEncounterDate;
	}

	public Date getArtStartDate() {
		return artStartDate;
	}

	public void setArtStartDate(Date artStartDate) {
		this.artStartDate = artStartDate;
	}

	public Date getFormEncounterDate() {
		return formEncounterDate;
	}

	public void setFormEncounterDate(Date formEncounterDate) {
		this.formEncounterDate = formEncounterDate;
	}

	@Override
	public String toString() {
		return "PatientStatus{" +
				"lastARVDate=" + lastARVDate +
				", drugDuration=" + drugDuration +
				", nextAppointmentDate=" + nextAppointmentDate +
				", status='" + status + '\'' +
				", reason='" + reason + '\'' +
				'}';
	}
}
