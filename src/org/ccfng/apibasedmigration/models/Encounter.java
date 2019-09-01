package org.ccfng.apibasedmigration.models;

import java.sql.Date;
import java.util.List;

public class Encounter {

	private Integer encounterTypeId;

	private Integer encounterLocatiionId;

	private Date encounterDate;

	private Integer formTypeId;

    private List<Obs> obs;

	public Encounter() {
	}

	public Integer getEncounterTypeId() {
		return encounterTypeId;
	}

	public void setEncounterTypeId(Integer encounterTypeId) {
		this.encounterTypeId = encounterTypeId;
	}

	public Integer getEncounterLocatiionId() {
		return encounterLocatiionId;
	}

	public void setEncounterLocatiionId(Integer encounterLocatiionId) {
		this.encounterLocatiionId = encounterLocatiionId;
	}

	public Date getEncounterDate() {
		return encounterDate;
	}

	public void setEncounterDate(Date encounterDate) {
		this.encounterDate = encounterDate;
	}

	public Integer getFormTypeId() {
		return formTypeId;
	}

	public void setFormTypeId(Integer formTypeId) {
		this.formTypeId = formTypeId;
	}

	public List<Obs> getObs() {
		return obs;
	}

	public void setObs(List<Obs> obs) {
		this.obs = obs;
	}

	@Override
	public String toString() {
		return "Encounter{" +
				"encounterTypeId=" + encounterTypeId +
				", encounterLocatiionId=" + encounterLocatiionId +
				", encounterDate=" + encounterDate +
				", formTypeId=" + formTypeId +
				", obs=" + obs +
				'}';
	}
}
