package org.ccfng.datamigration.openmrscleanup;

import java.sql.Date;

public class PatientStatus extends Demographics {

	private Date lastARVDate;

	private Integer drugDuration;

	private Date nextAppointmentDate;

	private String status;

	private String reason;

	public PatientStatus() {
	}

	public PatientStatus(Date lastARVDate, Integer drugDuration, Date nextAppointmentDate, String status,
			String reason) {
		this.lastARVDate = lastARVDate;
		this.drugDuration = drugDuration;
		this.nextAppointmentDate = nextAppointmentDate;
		this.status = status;
		this.reason = reason;
	}

	public PatientStatus(Integer patientID, Integer encounterID, Integer obsID, Integer obsGroupID, String pepfarID,
			String patientName, Date lastARVDate, Integer drugDuration, Date nextAppointmentDate, String status,
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

	public Integer getDrugDuration() {
		return drugDuration;
	}

	public void setDrugDuration(Integer drugDuration) {
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
