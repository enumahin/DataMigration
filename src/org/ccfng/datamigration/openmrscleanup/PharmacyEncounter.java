package org.ccfng.datamigration.openmrscleanup;

import java.sql.Date;

public class PharmacyEncounter {

	private Integer PatientID;

	private Integer EncounterID;

	private Integer ObsID;

	private Integer ObsGroupID;

	private String PepfarID;

	private String PatientName;

	private Date EncounterDateTime;

	private String Question;

	private String Answer;


	public PharmacyEncounter() {
	}

	public PharmacyEncounter(Integer patientID, Integer encounterID, Integer obsID, String pepfarID,
			String patientName, Date encounterDateTime, String question, String answer ) {
		this.PatientID = patientID;
		EncounterID = encounterID;
		ObsID = obsID;
		PepfarID = pepfarID;
		PatientName = patientName;
		EncounterDateTime = encounterDateTime;
		Question = question;
		Answer = answer;
	}

	public Integer getPatientID() {
		return PatientID;
	}

	public void setPatientID(Integer patientID) {
		this.PatientID = patientID;
	}

	public Integer getEncounterID() {
		return EncounterID;
	}

	public void setEncounterID(Integer encounterID) {
		EncounterID = encounterID;
	}

	public Integer getObsID() {
		return ObsID;
	}

	public void setObsID(Integer obsID) {
		ObsID = obsID;
	}

	public String getPepfarID() {
		return PepfarID;
	}

	public void setPepfarID(String pepfarID) {
		PepfarID = pepfarID;
	}

	public String getPatientName() {
		return PatientName;
	}

	public void setPatientName(String patientName) {
		PatientName = patientName;
	}

	public Date getEncounterDateTime() {
		return EncounterDateTime;
	}

	public void setEncounterDateTime(Date encounterDateTime) {
		EncounterDateTime = encounterDateTime;
	}

	public String getQuestion() {
		return Question;
	}

	public void setQuestion(String question) {
		Question = question;
	}

	public String getAnswer() {
		return Answer;
	}

	public void setAnswer(String answer) {
		Answer = answer;
	}

	public Integer getObsGroupID() {
		return ObsGroupID;
	}

	public void setObsGroupID(Integer obsGroupID) {
		ObsGroupID = obsGroupID;
	}

	@Override
	public String toString() {
		return "PharmacyEncounter{" +
				"PatientID=" + PatientID +
				", EncounterID=" + EncounterID +
				", ObsID=" + ObsID +
				", PepfarID='" + PepfarID + '\'' +
				", PatientName='" + PatientName + '\'' +
				", EncounterDateTime=" + EncounterDateTime +
				", Question='" + Question + '\'' +
				", Answer='" + Answer + '\'' +
				'}';
	}
}
