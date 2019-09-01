package org.ccfng.global;

public class Location {

	private Integer locationID;

	private String location;

	public Location() {
	}

	public Location(Integer locationID, String location) {
		this.locationID = locationID;
		this.location = location;
	}

	public Integer getLocationID() {
		return locationID;
	}

	public void setLocationID(Integer locationID) {
		this.locationID = locationID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return location;
	}
}
