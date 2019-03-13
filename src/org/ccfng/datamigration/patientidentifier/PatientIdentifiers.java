package org.ccfng.datamigration.patientidentifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfPatient_identifier")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatientIdentifiers {

    @XmlElement(name = "Patient_identifier")
    private List<PatientIdentifier> patient_identifiers = null;

    public PatientIdentifiers() {
    }


    public List<PatientIdentifier> getPatient_identifiers() {
        return this.patient_identifiers;
    }

    public void setPatient_identifiers(List<PatientIdentifier> patient_identifiers) {
        this.patient_identifiers = patient_identifiers;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PatientIdentifiers)) return false;
        final PatientIdentifiers other = (PatientIdentifiers) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$patient_identifiers = this.getPatient_identifiers();
        final Object other$patient_identifiers = other.getPatient_identifiers();
        if (this$patient_identifiers == null ? other$patient_identifiers != null : !this$patient_identifiers.equals(other$patient_identifiers))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $patient_identifiers = this.getPatient_identifiers();
        result = result * PRIME + ($patient_identifiers == null ? 43 : $patient_identifiers.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PatientIdentifiers;
    }

    public String toString() {
        return "PatientIdentifiers(patient_identifiers=" + this.getPatient_identifiers() + ")";
    }
}