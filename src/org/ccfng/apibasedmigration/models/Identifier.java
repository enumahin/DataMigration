package org.ccfng.apibasedmigration.models;

public class Identifier {

	private Integer patientID;

	private String identifier;

	private Integer identifierType;

	private Integer locationId;

	private boolean preferred;

	public Identifier(String identifier, Integer identifierType, Integer locationId, boolean preferred) {
		this.identifier = identifier;
		this.identifierType = identifierType;
		this.locationId = locationId;
		this.preferred = preferred;
	}

	public Identifier() {
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Integer getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(Integer identifierType) {
		this.identifierType = identifierType;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}

	public Integer getPatientID() {
		return patientID;
	}

	public void setPatientID(Integer patientID) {
		this.patientID = patientID;
	}

	@Override
	public String toString() {
		return "Identifier{" +
				"identifier='" + identifier + '\'' +
				", identifierType=" + identifierType +
				", locationId=" + locationId +
				", preferred=" + preferred +
				'}';
	}
}
