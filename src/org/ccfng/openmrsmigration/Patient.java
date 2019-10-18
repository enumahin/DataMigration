package org.ccfng.openmrsmigration;

import java.util.List;
import org.ccfng.datamigration.patientidentifier.PatientIdentifier;
import org.ccfng.datamigration.patientprogram.PatientProgram;
import org.ccfng.datamigration.person.Person;
import org.ccfng.datamigration.personaddress.PersonAddress;
import org.ccfng.datamigration.personattribute.PersonAttribute;
import org.ccfng.datamigration.personname.PersonName;

public class Patient extends org.ccfng.datamigration.patient.Patient {

	private Person person;

    private List<PersonAddress> personAddresses;

    private List<PersonAttribute> personAttributes;

    private List<PersonName> personNames;

    private List<PatientIdentifier> patientIdentifiers;

    private List<PatientProgram> patientPrograms;

    private List<Visit> visits;

	@Override
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<PersonAddress> getPersonAddresses() {
		return personAddresses;
	}

	public void setPersonAddresses(List<PersonAddress> personAddresses) {
		this.personAddresses = personAddresses;
	}

	public List<PersonAttribute> getPersonAttributes() {
		return personAttributes;
	}

	public void setPersonAttributes(List<PersonAttribute> personAttributes) {
		this.personAttributes = personAttributes;
	}

	public List<PersonName> getPersonNames() {
		return personNames;
	}

	public void setPersonNames(List<PersonName> personNames) {
		this.personNames = personNames;
	}

	public List<PatientIdentifier> getPatientIdentifiers() {
		return patientIdentifiers;
	}

	public void setPatientIdentifiers(List<PatientIdentifier> patientIdentifiers) {
		this.patientIdentifiers = patientIdentifiers;
	}

	public List<PatientProgram> getPatientPrograms() {
		return patientPrograms;
	}

	public void setPatientPrograms(List<PatientProgram> patientPrograms) {
		this.patientPrograms = patientPrograms;
	}

	public List<Visit> getVisits() {
		return visits;
	}

	public void setVisits(List<Visit> visits) {
		this.visits = visits;
	}

	@Override
	public String toString() {
		return "Patient{" +
				"person=" + person +
				", personAddresses=" + personAddresses +
				", personAttributes=" + personAttributes +
				", personNames=" + personNames +
				", patientIdentifiers=" + patientIdentifiers +
				", patientPrograms=" + patientPrograms +
				", visits=" + visits +
				'}';
	}
}
