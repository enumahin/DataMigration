package org.ccfng.datamigration.patient;

import lombok.EqualsAndHashCode;

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
    //@GeneratedValue(strategy = GenerationType.AUTO, generator = "Patient_id_seq_gen")
    //@SequenceGenerator(name = "Patient_id_seq_gen", sequenceName = "Patient_id_seq", allocationSize = 800)
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

//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;

}
