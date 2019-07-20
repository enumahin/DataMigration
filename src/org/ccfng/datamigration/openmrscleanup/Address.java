package org.ccfng.datamigration.openmrscleanup;

public class Address extends Demographics {

	private String state;

	private String town;

	private String ward;

	private String address1;

	private String address2;

	public Address() {
	}

	public Address(String state, String town, String ward) {
		this.state = state;
		this.town = town;
		this.ward = ward;
	}

	public Address(Integer patientID, Integer encounterID, Integer obsID, Integer obsGroupID, String pepfarID,
			String patientName, String state, String town, String ward) {
		super(patientID, encounterID, obsID, obsGroupID, pepfarID, patientName);
		this.state = state;
		this.town = town;
		this.ward = ward;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@Override
	public String toString() {
		return "Address{" +
				"state='" + state + '\'' +
				", town='" + town + '\'' +
				", ward='" + ward + '\'' +
				'}';
	}
}
