package org.ccfng.apibasedmigration.models;

public class Facility {

	private String facilityName;

	private String datimCode;

	private String lga;

	private String state;

	public Facility() {
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getDatimCode() {
		return datimCode;
	}

	public void setDatimCode(String datimCode) {
		this.datimCode = datimCode;
	}

	public String getLga() {
		return lga;
	}

	public void setLga(String lga) {
		this.lga = lga;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Facility{" +
				"facilityName='" + facilityName + '\'' +
				", datimCode='" + datimCode + '\'' +
				", lga='" + lga + '\'' +
				", state='" + state + '\'' +
				'}'+"\n";
	}
}
