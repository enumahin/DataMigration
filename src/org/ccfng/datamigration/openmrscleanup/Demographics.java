package org.ccfng.datamigration.openmrscleanup;

public class Demographics{

	private Integer PatientID;

	private Integer EncounterID;

	private Integer ObsID;

	private Integer ObsGroupID;

	private String PepfarID;

	private String PatientName;

	private Integer PatientAge;

	public Demographics() {
	}

	public Demographics(Integer patientID, Integer encounterID, Integer obsID, Integer obsGroupID, String pepfarID,
			String patientName) {
		PatientID = patientID;
		EncounterID = encounterID;
		ObsID = obsID;
		ObsGroupID = obsGroupID;
		PepfarID = pepfarID;
		PatientName = patientName;
	}

	public Integer getPatientID() {
		return PatientID;
	}

	public void setPatientID(Integer patientID) {
		PatientID = patientID;
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

	public Integer getObsGroupID() {
		return ObsGroupID;
	}

	public void setObsGroupID(Integer obsGroupID) {
		ObsGroupID = obsGroupID;
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

		public Integer getPatientAge() {
			return PatientAge;
		}

		public void setPatientAge(Integer patientAge) {
			PatientAge = patientAge;
		}
}
