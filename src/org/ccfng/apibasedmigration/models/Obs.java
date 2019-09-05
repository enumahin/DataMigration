package org.ccfng.apibasedmigration.models;

import java.util.ArrayList;
import java.util.List;

public class Obs {

	private Integer obsID;

	private Integer patientID;

	private Integer ecounterID;

	private Integer conceptId;

	private String valueTypeId;

	private String value;

	private List<org.ccfng.apibasedmigration.models.external.Obs> obsChildren = new ArrayList<>();

	private boolean parent;

	private Integer obsGroupId;

	public Obs() {
	}

	public Obs(Integer conceptId, String valueTypeId, String value) {
		this.conceptId = conceptId;
		this.valueTypeId = valueTypeId;
		this.value = value;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public List<org.ccfng.apibasedmigration.models.external.Obs> getObsChildren() {
		return obsChildren;
	}

	public void setObsChildren(List<org.ccfng.apibasedmigration.models.external.Obs> obsChildren) {
		this.obsChildren = obsChildren;
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	public String getValueTypeId() {
		return valueTypeId;
	}

	public void setValueTypeId(String valueTypeId) {
		this.valueTypeId = valueTypeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getEcounterID() {
		return ecounterID;
	}

	public void setEcounterID(Integer ecounterID) {
		this.ecounterID = ecounterID;
	}

	public Integer getObsGroupId() {
		return obsGroupId;
	}

	public void setObsGroupId(Integer obsGroupId) {
		this.obsGroupId = obsGroupId;
	}

	public Integer getObsID() {
		return obsID;
	}

	public void setObsID(Integer obsID) {
		this.obsID = obsID;
	}

	public Integer getPatientID() {
		return patientID;
	}

	public void setPatientID(Integer patientID) {
		this.patientID = patientID;
	}

	@Override
	public String toString() {
		return "Obs{" +
				"conceptId=" + conceptId +
				", valueTypeId='" + valueTypeId + '\'' +
				", value='" + value + '\'' +
				", obsChildren=" + obsChildren +
				'}';
	}
}
