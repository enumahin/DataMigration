package org.ccfng.openmrsmigration;

import org.ccfng.datamigration.obs.Obs;

import java.util.ArrayList;
import java.util.List;

public class Encounter extends org.ccfng.datamigration.encounter.Encounter {
  List<Obs> obs = new ArrayList<>();

	public Encounter() {
	}

	public Encounter(List<Obs> obs) {
		this.obs = obs;
	}

	@Override
	public List<Obs> getObs() {
		return obs;
	}

	public void setObs(List<Obs> obs) {
		this.obs = obs;
	}
}
