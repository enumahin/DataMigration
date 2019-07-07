package org.ccfng.datamigration.openmrscleanup;

public class Regimen {

	private Integer conceptID;

	private String regimenName;

	public Regimen() {
	}

	public Regimen(Integer conceptID, String regimenName) {
		this.conceptID = conceptID;
		this.regimenName = regimenName;
	}

	public Integer getConceptID() {
		return conceptID;
	}

	public void setConceptID(Integer conceptID) {
		this.conceptID = conceptID;
	}

	public String getRegimenName() {
		return regimenName;
	}

	public void setRegimenName(String regimenName) {
		this.regimenName = regimenName;
	}

	@Override
	public String toString() {
		return regimenName;
	}
}
