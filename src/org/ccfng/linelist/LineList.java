package org.ccfng.linelist;

import org.ccfng.global.Demographics;

import java.sql.Date;

public class LineList extends Demographics {

	private Long ageAtArtStart;

	private Date artStartDate;

	private Date lastPickUpDate;

	private Long daysOfArtRefill;

	private Date nextAppointmentDate;

	private String regimenLineAtStart;

	private String regimenAtStart;

	private String currentRegimenLine;

	private String currentRegimen;

	private String pregnancyStatus;

	private Integer currentViralLoad;

	private Date dateOfCurrentViralLoad;

	private String viralLoadIndication;

	private String currentArtStatus;

	private Long numberOfDaysMissedAppointment;

	private String activeBy28;

	private String activeBy90;

	public LineList() {
	}

	public Long getAgeAtArtStart() {
		return ageAtArtStart;
	}

	public void setAgeAtArtStart(Long ageAtArtStart) {
		this.ageAtArtStart = ageAtArtStart;
	}

	public Date getArtStartDate() {
		return artStartDate;
	}

	public void setArtStartDate(Date artStartDate) {
		this.artStartDate = artStartDate;
	}

	public Date getLastPickUpDate() {
		return lastPickUpDate;
	}

	public void setLastPickUpDate(Date lastPickUpDate) {
		this.lastPickUpDate = lastPickUpDate;
	}

	public Long getDaysOfArtRefill() {
		return daysOfArtRefill;
	}

	public void setDaysOfArtRefill(Long daysOfArtRefill) {
		this.daysOfArtRefill = daysOfArtRefill;
	}

	public Date getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	public void setNextAppointmentDate(Date nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
	}

	public String getRegimenLineAtStart() {
		return regimenLineAtStart;
	}

	public void setRegimenLineAtStart(String regimenLineAtStart) {
		this.regimenLineAtStart = regimenLineAtStart;
	}

	public String getRegimenAtStart() {
		return regimenAtStart;
	}

	public void setRegimenAtStart(String regimenAtStart) {
		this.regimenAtStart = regimenAtStart;
	}

	public String getCurrentRegimenLine() {
		return currentRegimenLine;
	}

	public void setCurrentRegimenLine(String currentRegimenLine) {
		this.currentRegimenLine = currentRegimenLine;
	}

	public String getCurrentRegimen() {
		return currentRegimen;
	}

	public void setCurrentRegimen(String currentRegimen) {
		this.currentRegimen = currentRegimen;
	}

	public String getPregnancyStatus() {
		return pregnancyStatus;
	}

	public void setPregnancyStatus(String pregnancyStatus) {
		this.pregnancyStatus = pregnancyStatus;
	}

	public Integer getCurrentViralLoad() {
		return currentViralLoad;
	}

	public void setCurrentViralLoad(Integer currentViralLoad) {
		this.currentViralLoad = currentViralLoad;
	}

	public Date getDateOfCurrentViralLoad() {
		return dateOfCurrentViralLoad;
	}

	public void setDateOfCurrentViralLoad(Date dateOfCurrentViralLoad) {
		this.dateOfCurrentViralLoad = dateOfCurrentViralLoad;
	}

	public String getViralLoadIndication() {
		return viralLoadIndication;
	}

	public void setViralLoadIndication(String viralLoadIndication) {
		this.viralLoadIndication = viralLoadIndication;
	}

	public String getCurrentArtStatus() {
		return currentArtStatus;
	}

	public void setCurrentArtStatus(String currentArtStatus) {
		this.currentArtStatus = currentArtStatus;
	}

	public Long getNumberOfDaysMissedAppointment() {
		return numberOfDaysMissedAppointment;
	}

	public void setNumberOfDaysMissedAppointment(Long numberOfDaysMissedAppointment) {
		this.numberOfDaysMissedAppointment = numberOfDaysMissedAppointment;
	}

	public String getActiveBy28() {
		return activeBy28;
	}

	public void setActiveBy28(String activeBy28) {
		this.activeBy28 = activeBy28;
	}

	public String getActiveBy90() {
		return activeBy90;
	}

	public void setActiveBy90(String activeBy90) {
		this.activeBy90 = activeBy90;
	}
}
