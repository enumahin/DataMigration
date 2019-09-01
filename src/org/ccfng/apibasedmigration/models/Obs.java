package org.ccfng.apibasedmigration.models;

import java.util.HashSet;
import java.util.Set;

public class Obs {

	private Integer conceptId;

	private String valueType;

	private String value;

	private Set<Obs> children = new HashSet<>();

	private boolean isgroup;

	public Obs() {
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Set<Obs> getChildren() {
		return children;
	}

	public void setChildren(Set<Obs> children) {
		this.children = children;
	}

	public boolean isIsgroup() {
		return isgroup;
	}

	public void setIsgroup(boolean isgroup) {
		this.isgroup = isgroup;
	}

	@Override
	public String toString() {
		return "Obs{" +
				"conceptId=" + conceptId +
				", valueType='" + valueType + '\'' +
				", value='" + value + '\'' +
				", children=" + children +
				", isgroup=" + isgroup +
				'}';
	}
}
