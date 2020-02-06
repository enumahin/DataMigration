package org.ccfng.openmrsmigration;

import java.util.List;

public class Visit extends org.ccfng.datamigration.visit.Visit {
	private List<Encounter> encounters;

	public List<Encounter> getEncounters() {
		return encounters;
	}

	public void setEncounters(List<Encounter> encounters) {
		this.encounters = encounters;
	}
}
