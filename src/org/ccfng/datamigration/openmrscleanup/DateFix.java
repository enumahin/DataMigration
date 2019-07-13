package org.ccfng.datamigration.openmrscleanup;

import java.sql.Date;

public class DateFix extends Demographics {

	private Date artStartDate;

	private Integer artStartEncounterId;

	private Integer artStartConceptId;

	private Date hivConfirmedDate;

	private Integer hivConfirmedEncounterId;

	private Integer hivConfirmedConceptId;

	private Date firstARVDate;

	private Date dateEnrolled;

	public DateFix() {
	}

	public DateFix(Date artStartDate, Integer artStartEncounterId, Date hivConfirmedDate,
			Integer hivConfirmedEncounterId, Date firstARVDate, Date dateEnrolled) {
		this.artStartDate = artStartDate;
		this.artStartEncounterId = artStartEncounterId;
		this.hivConfirmedDate = hivConfirmedDate;
		this.hivConfirmedEncounterId = hivConfirmedEncounterId;
		this.firstARVDate = firstARVDate;
		this.dateEnrolled = dateEnrolled;
	}

	public DateFix(Integer patientID, Integer encounterID, Integer obsID, Integer obsGroupID, String pepfarID,
			String patientName, Date artStartDate, Integer artStartEncounterId, Date hivConfirmedDate,
			Integer hivConfirmedEncounterId, Date firstARVDate, Date dateEnrolled) {
		super(patientID, encounterID, obsID, obsGroupID, pepfarID, patientName);
		this.artStartDate = artStartDate;
		this.artStartEncounterId = artStartEncounterId;
		this.hivConfirmedDate = hivConfirmedDate;
		this.hivConfirmedEncounterId = hivConfirmedEncounterId;
		this.firstARVDate = firstARVDate;
		this.dateEnrolled = dateEnrolled;
	}

	public Date getArtStartDate() {
		return artStartDate;
	}

	public void setArtStartDate(Date artStartDate) {
		this.artStartDate = artStartDate;
	}

	public Integer getArtStartEncounterId() {
		return artStartEncounterId;
	}

	public void setArtStartEncounterId(Integer artStartEncounterId) {
		this.artStartEncounterId = artStartEncounterId;
	}

	public Date getHivConfirmedDate() {
		return hivConfirmedDate;
	}

	public void setHivConfirmedDate(Date hivConfirmedDate) {
		this.hivConfirmedDate = hivConfirmedDate;
	}

	public Integer getHivConfirmedEncounterId() {
		return hivConfirmedEncounterId;
	}

	public void setHivConfirmedEncounterId(Integer hivConfirmedEncounterId) {
		this.hivConfirmedEncounterId = hivConfirmedEncounterId;
	}

	public Date getFirstARVDate() {
		return firstARVDate;
	}

	public void setFirstARVDate(Date firstARVDate) {
		this.firstARVDate = firstARVDate;
	}

	public Date getDateEnrolled() {
		return dateEnrolled;
	}

	public void setDateEnrolled(Date dateEnrolled) {
		this.dateEnrolled = dateEnrolled;
	}

	public Integer getArtStartConceptId() {
		return artStartConceptId;
	}

	public void setArtStartConceptId(Integer artStartConceptId) {
		this.artStartConceptId = artStartConceptId;
	}

	public Integer getHivConfirmedConceptId() {
		return hivConfirmedConceptId;
	}

	public void setHivConfirmedConceptId(Integer hivConfirmedConceptId) {
		this.hivConfirmedConceptId = hivConfirmedConceptId;
	}

	@Override
	public String toString() {
		return "DateFix{" +
				"artStartDate=" + artStartDate +
				", artStartEncounterId=" + artStartEncounterId +
				", hivConfirmedDate=" + hivConfirmedDate +
				", hivConfirmedEncounterId=" + hivConfirmedEncounterId +
				", firstARVDate=" + firstARVDate +
				", dateEnrolled=" + dateEnrolled +
				'}';
	}
}
