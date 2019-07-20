package org.ccfng.datamigration.openmrscleanup;

import java.sql.Date;

public class PatientStatus extends Demographics {

	private Date lastARVDate;

	private Long drugDuration;

	private Date nextAppointmentDate;

	private String status;

	private String reason;

	private String patientPhoneNumber;

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

	public String getPatientPhoneNumber() {
		return patientPhoneNumber;
	}

	public void setPatientPhoneNumber(String patientPhoneNumber) {
		this.patientPhoneNumber = patientPhoneNumber;
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
