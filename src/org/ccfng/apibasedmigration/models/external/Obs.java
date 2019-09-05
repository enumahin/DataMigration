package org.ccfng.apibasedmigration.models.external;

public class Obs {

	private Integer conceptId;

	private String valueTypeId;

	private String value;

	private Integer obsGroupId;

	public Obs() {
	}

	public Obs(Integer conceptId, String valueType, String value, Integer obsGroupId) {
		this.conceptId = conceptId;
		this.valueTypeId = valueType;
		this.value = value;
		this.obsGroupId = obsGroupId;
	}

	public Integer getObsGroupId() {
		return obsGroupId;
	}

	public void setObsGroupId(Integer obsGroupId) {
		this.obsGroupId = obsGroupId;
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

	public void setValueTypeId(String valueType) {
		this.valueTypeId = valueType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
