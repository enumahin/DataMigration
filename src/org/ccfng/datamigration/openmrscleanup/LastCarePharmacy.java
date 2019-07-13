package org.ccfng.datamigration.openmrscleanup;

import java.sql.Date;

public class LastCarePharmacy extends Demographics {

	private Date lastCareEncounterDate;

	private Integer lastCareRegimenLineId;

	private String lastCareRegimenLine;

	private Integer lastCareRegimenId;

	private String lastCareRegimenName;

	private Date lastPharmacyEncounterDate;

	private Integer currentRegimenLineId;

	private String currentRegimenLine;

	private Integer currentRegimenId;

	private String currentRegimenName;

	private Integer currentDrugId;

	private String currentDrugName;

	private Integer nextAppointment;

	private Integer lastPharmEncounterID;

	private Long nextAppointmentDays;

	private Date nextAppointmentDate;

	public LastCarePharmacy() {
	}

	public LastCarePharmacy(Date lastCareEncounterDate, Integer lastCareRegimenLineId, String lastCareRegimenLine,
			Integer lastCareRegimenId, String lastCareRegimenName, Date lastPharmacyEncounterDate,
			Integer currentRegimenLineId, String currentRegimenLine, Integer currentRegimenId,
			String currentRegimenName, Integer currentDrugId, String currentDrugName) {
		this.lastCareEncounterDate = lastCareEncounterDate;
		this.lastCareRegimenLineId = lastCareRegimenLineId;
		this.lastCareRegimenLine = lastCareRegimenLine;
		this.lastCareRegimenId = lastCareRegimenId;
		this.lastCareRegimenName = lastCareRegimenName;
		this.lastPharmacyEncounterDate = lastPharmacyEncounterDate;
		this.currentRegimenLineId = currentRegimenLineId;
		this.currentRegimenLine = currentRegimenLine;
		this.currentRegimenId = currentRegimenId;
		this.currentRegimenName = currentRegimenName;
		this.currentDrugId = currentDrugId;
		this.currentDrugName = currentDrugName;
	}

	public LastCarePharmacy(Integer patientID, Integer encounterID, Integer obsID, Integer obsGroupID, String pepfarID,
			String patientName, Date encounterDateTime, Date lastCareEncounterDate, Integer lastCareRegimenLineId,
			String lastCareRegimenLine, Integer lastCareRegimenId, String lastCareRegimenName,
			Date lastPharmacyEncounterDate, Integer currentRegimenLineId, String currentRegimenLine,
			Integer currentRegimenId, String currentRegimenName, Integer currentDrugId, String currentDrugName) {
		super(patientID, encounterID, obsID, obsGroupID, pepfarID, patientName);
		this.lastCareEncounterDate = lastCareEncounterDate;
		this.lastCareRegimenLineId = lastCareRegimenLineId;
		this.lastCareRegimenLine = lastCareRegimenLine;
		this.lastCareRegimenId = lastCareRegimenId;
		this.lastCareRegimenName = lastCareRegimenName;
		this.lastPharmacyEncounterDate = lastPharmacyEncounterDate;
		this.currentRegimenLineId = currentRegimenLineId;
		this.currentRegimenLine = currentRegimenLine;
		this.currentRegimenId = currentRegimenId;
		this.currentRegimenName = currentRegimenName;
		this.currentDrugId = currentDrugId;
		this.currentDrugName = currentDrugName;
	}

	public LastCarePharmacy(Integer patientID, Integer encounterID, Integer obsID, Integer obsGroupID, String pepfarID,
			String patientName, Date encounterDateTime) {
		super(patientID, encounterID, obsID, obsGroupID, pepfarID, patientName);
	}

	public Date getLastCareEncounterDate() {
		return lastCareEncounterDate;
	}

	public void setLastCareEncounterDate(Date lastCareEncounterDate) {
		this.lastCareEncounterDate = lastCareEncounterDate;
	}

	public Integer getLastCareRegimenLineId() {
		return lastCareRegimenLineId;
	}

	public void setLastCareRegimenLineId(Integer lastCareRegimenLineId) {
		this.lastCareRegimenLineId = lastCareRegimenLineId;
	}

	public String getLastCareRegimenLine() {
		return lastCareRegimenLine;
	}

	public void setLastCareRegimenLine(String lastCareRegimenLine) {
		this.lastCareRegimenLine = lastCareRegimenLine;
	}

	public Integer getLastCareRegimenId() {
		return lastCareRegimenId;
	}

	public void setLastCareRegimenId(Integer lastCareRegimenId) {
		this.lastCareRegimenId = lastCareRegimenId;
	}

	public String getLastCareRegimenName() {
		return lastCareRegimenName;
	}

	public void setLastCareRegimenName(String lastCareRegimenName) {
		this.lastCareRegimenName = lastCareRegimenName;
	}

	public Date getLastPharmacyEncounterDate() {
		return lastPharmacyEncounterDate;
	}

	public void setLastPharmacyEncounterDate(Date lastPharmacyEncounterDate) {
		this.lastPharmacyEncounterDate = lastPharmacyEncounterDate;
	}

	public Integer getCurrentRegimenLineId() {
		return currentRegimenLineId;
	}

	public void setCurrentRegimenLineId(Integer currentRegimenLineId) {
		this.currentRegimenLineId = currentRegimenLineId;
	}

	public String getCurrentRegimenLine() {
		return currentRegimenLine;
	}

	public void setCurrentRegimenLine(String currentRegimenLine) {
		this.currentRegimenLine = currentRegimenLine;
	}

	public Integer getCurrentRegimenId() {
		return currentRegimenId;
	}

	public void setCurrentRegimenId(Integer currentRegimenId) {
		this.currentRegimenId = currentRegimenId;
	}

	public String getCurrentRegimenName() {
		return currentRegimenName;
	}

	public void setCurrentRegimenName(String currentRegimenName) {
		this.currentRegimenName = currentRegimenName;
	}

	public Integer getCurrentDrugId() {
		return currentDrugId;
	}

	public void setCurrentDrugId(Integer currentDrugId) {
		this.currentDrugId = currentDrugId;
	}

	public String getCurrentDrugName() {
		return currentDrugName;
	}

	public void setCurrentDrugName(String currentDrugName) {
		this.currentDrugName = currentDrugName;
	}

	public Integer getNextAppointment() {
		return nextAppointment;
	}

	public void setNextAppointment(Integer nextAppointment) {
		this.nextAppointment = nextAppointment;
	}

	public Integer getLastPharmEncounterID() {
		return lastPharmEncounterID;
	}

	public Long getNextAppointmentDays() {
		return nextAppointmentDays;
	}

	public void setNextAppointmentDays(Long nextAppointmentDays) {
		this.nextAppointmentDays = nextAppointmentDays;
	}

	public Date getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	public void setNextAppointmentDate(Date nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
	}

	public void setLastPharmEncounterID(Integer lastPharmEncounterID) {
		this.lastPharmEncounterID = lastPharmEncounterID;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
