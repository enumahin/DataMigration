package org.ccfng.apibasedmigration.models;

public class Address {

	private String country;

	private String latitude;

	private String longitude;

	private String address1;

	private String address2;

	private String address3;

	private String cityVillage;

	private String stateProvince;

	private String postalCode;

	private Boolean preferred;

	public Address() {
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
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

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCityVillage() {
		return cityVillage;
	}

	public void setCityVillage(String cityVillage) {
		this.cityVillage = cityVillage;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Boolean getPreferred() {
		return preferred;
	}

	public void setPreferred(Boolean preferred) {
		this.preferred = preferred;
	}

	@Override
	public String toString() {
		return "country='" + country +
				"\n latitude='" + latitude +
				"\n longitude='" + longitude +
				"\n address1='" + address1 +
				"\n address2='" + address2 +
				"\n address3='" + address3 +
				"\n cityVillage='" + cityVillage +
				"\n stateProvince='" + stateProvince +
				"\n preferred='" + preferred +
				"\n postalCode='" + postalCode;
	}
}
