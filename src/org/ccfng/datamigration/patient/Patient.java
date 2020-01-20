package org.ccfng.datamigration.patient;

import lombok.EqualsAndHashCode;
import org.ccfng.datamigration.encounter.Encounter;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.patientidentifier.PatientIdentifier;
import org.ccfng.datamigration.person.Person;
import org.ccfng.global.DBMiddleMan;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "Patient")
@EqualsAndHashCode(exclude = {"Voided"})
@Entity
public class Patient
{
    @Id
    @XmlElement(name = "Patient_id")
    private Integer Patient_id;

    @XmlElement(name = "Allergy_status")
    private String Allergy_status;

    @XmlElement(name = "Voided")
    private boolean Voided;

//    @XmlElement(name = "Voided_by")
//    private Integer Voided_by;

    @XmlElement(name = "Date_changed")
    private Date Date_changed;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @XmlElement(name = "Date_voided")
    private Date Date_voided;

    @XmlElement(name = "Void_reason")
    private String Void_reason;

    @XmlElement(name = "Creator")
    private Integer Creator;

    public Patient() {
    }

    public Integer getPatient_id() {
        return this.Patient_id;
    }

    public String getAllergy_status() {
        return this.Allergy_status;
    }

    public boolean isVoided() {
        return this.Voided;
    }

    public Date getDate_changed() {
        return this.Date_changed;
    }

    public Date getDate_created() {
        return this.Date_created;
    }

    public Date getDate_voided() {
        return this.Date_voided;
    }

    public String getVoid_reason() {
        return this.Void_reason;
    }

    public Integer getCreator() {
        return this.Creator;
    }

    public void setPatient_id(Integer Patient_id) {
        this.Patient_id = Patient_id;
    }

    public void setAllergy_status(String Allergy_status) {
        this.Allergy_status = Allergy_status;
    }

    public void setVoided(boolean Voided) {
        this.Voided = Voided;
    }

    public void setDate_changed(Date Date_changed) {
        this.Date_changed = Date_changed;
    }

    public void setDate_created(Date Date_created) {
        this.Date_created = Date_created;
    }

    public void setDate_voided(Date Date_voided) {
        this.Date_voided = Date_voided;
    }

    public void setVoid_reason(String Void_reason) {
        this.Void_reason = Void_reason;
    }

    public void setCreator(Integer Creator) {
        this.Creator = Creator;
    }

    public String toString() {
        return "Patient(Patient_id=" + this.getPatient_id() + ", Allergy_status=" + this.getAllergy_status() + ", Voided=" + this.isVoided() + ", Date_changed=" + this.getDate_changed() + ", Date_created=" + this.getDate_created() + ", Date_voided=" + this.getDate_voided() + ", Void_reason=" + this.getVoid_reason() + ", Creator=" + this.getCreator() + ")";
    }

    public PatientIdentifier getPepfarID(){
       return DBMiddleMan.allPatientIdentifiers.stream()
                .filter(patientIdentifier ->
                        patientIdentifier.getPatient_id().equals(this.getPatient_id()) &&
                                patientIdentifier.getIdentifier_type() == 4
                ).findFirst().orElse(null);
    }

    public PatientIdentifier getHospitalNumber(){
        return DBMiddleMan.allPatientIdentifiers.stream()
                .filter(patientIdentifier ->
                        patientIdentifier.getPatient_id().equals(this.getPatient_id()) &&
                                patientIdentifier.getIdentifier_type() == 5
                ).findFirst().orElse(null);
    }

    public Person getPerson(){
        return DBMiddleMan.allPeople.stream().filter(person -> person.getPerson_id().equals(this.getPatient_id()))
                .findFirst().orElse(null);
    }

    public Encounter lastArtCareCardEncounter(){
       return DBMiddleMan.allEncounters.stream()
                .filter(encounter -> encounter.getForm_id() == 14 && encounter.getPatient_id().equals(this.getPatient_id())
                        && encounter.getOb(165708) != null)
                .reduce((first, second) -> second).orElse(null);
    }

    public Obs exited(){
       return DBMiddleMan.allObs.stream().filter(obs -> obs.getPerson_id()
                .equals(this.getPatient_id()) && (obs.getConcept_id() == 165470))
                .findFirst().orElse(null);
//       if(null != terminated){
//            return DBMiddleMan.allObs.stream().filter(obs -> obs.getEncounter_id()
//                    .equals(terminated.getEncounter_id()) && (obs.getConcept_id() == 165470))
//                    .findFirst().orElse(null);
//        }
//        return null;
    }


//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;

}
