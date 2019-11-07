package org.ccfng.datamigration.patient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfPatient")
@XmlAccessorType(XmlAccessType.FIELD)
public class Patients {

    @XmlElement(name = "Patient")
    private List<Patient> patients = null;

    public Patients() {
    }

    public List<Patient> getPatients() {
        return this.patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Patients)) return false;
        final Patients other = (Patients) o;
        if (!other.canEqual(this)) return false;
        final Object this$patients = this.getPatients();
        final Object other$patients = other.getPatients();
	    return this$patients == null ? other$patients == null : this$patients.equals(other$patients);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $patients = this.getPatients();
        result = result * PRIME + ($patients == null ? 43 : $patients.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Patients;
    }

    public String toString() {
        return "Patients(patients=" + this.getPatients() + ")";
    }
}
