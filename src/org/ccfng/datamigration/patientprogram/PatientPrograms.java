package org.ccfng.datamigration.patientprogram;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfPatient_program")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatientPrograms {

    @XmlElement(name = "Patient_program")
    private List<PatientProgram> patient_programs = null;

    public PatientPrograms() {
    }

    public List<PatientProgram> getPatient_programs() {
        return this.patient_programs;
    }

    public void setPatient_programs(List<PatientProgram> patient_programs) {
        this.patient_programs = patient_programs;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PatientPrograms)) return false;
        final PatientPrograms other = (PatientPrograms) o;
        if (!other.canEqual(this)) return false;
        final Object this$patient_programs = this.getPatient_programs();
        final Object other$patient_programs = other.getPatient_programs();
	    return this$patient_programs == null ?
			    other$patient_programs == null :
			    this$patient_programs.equals(other$patient_programs);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $patient_programs = this.getPatient_programs();
        result = result * PRIME + ($patient_programs == null ? 43 : $patient_programs.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PatientPrograms;
    }

    public String toString() {
        return "PatientPrograms(patient_programs=" + this.getPatient_programs() + ")";
    }
}
