package org.ccfng.datamigration.datamapping;

public class Mapper {

	private Integer openmrsId;

	private String openmrsName;

	private Integer nmrsId;

	private String nmrsName;

	public Mapper() {
	}

	public Mapper(Integer openmrsId, String openmrsName, Integer nmrsId, String nmrsName) {
		this.openmrsId = openmrsId;
		this.openmrsName = openmrsName;
		this.nmrsId = nmrsId;
		this.nmrsName = nmrsName;
	}

	public Integer getOpenmrsId() {
		return openmrsId;
	}

	public void setOpenmrsId(Integer openmrsId) {
		this.openmrsId = openmrsId;
	}

	public String getOpenmrsName() {
		return openmrsName;
	}

	public void setOpenmrsName(String openmrsName) {
		this.openmrsName = openmrsName;
	}

	public Integer getNmrsId() {
		return nmrsId;
	}

	public void setNmrsId(Integer nmrsId) {
		this.nmrsId = nmrsId;
	}

	public String getNmrsName() {
		return nmrsName;
	}

	public void setNmrsName(String nmrsName) {
		this.nmrsName = nmrsName;
	}
}
