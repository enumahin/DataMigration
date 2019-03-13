package org.ccfng.datamigration.patientprogram;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Patient_program")
@EqualsAndHashCode(exclude = {"Uuid",
        "Date_created","Creator","Voided","Date_voided",
        "Voided_by","Void_reason","Void_reason","Date_created",
        "Date_completed",
        "Outcome_concept_id","Location_id","Patient_id","Date_enrolled"})
@Entity
@Table(name = "patient_program")
public class PatientProgram
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Patient_program_id_seq_gen")
    @SequenceGenerator(name = "Patient_program_id_seq_gen", sequenceName = "Patient_program_id_seq", allocationSize = 800)
    @XmlElement(name = "Patient_program_id")
    private Integer Patient_program_id;

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

    @XmlElement(name = "Date_completed")
    private Date Date_completed;

    @XmlElement(name = "Outcome_concept_id")
    private Integer Outcome_concept_id;

    @XmlElement(name = "Location_id")
    private Integer Location_id;

    @XmlElement(name = "Date_enrolled")
    private Date Date_enrolled;

    @XmlElement(name = "Patient_id")
    private Integer Patient_id;

    @XmlElement(name = "Program_id")
    private Integer Program_id;

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

    public PatientProgram() {
    }

    public Integer getPatient_program_id() {
        return this.Patient_program_id;
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public Date getDate_completed() {
        return this.Date_completed;
    }

    public Integer getOutcome_concept_id() {
        return this.Outcome_concept_id;
    }

    public Integer getLocation_id() {
        return this.Location_id;
    }

    public Date getDate_enrolled() {
        return this.Date_enrolled;
    }

    public Integer getPatient_id() {
        return this.Patient_id;
    }

    public Integer getProgram_id() {
        return this.Program_id;
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

    public void setPatient_program_id(Integer Patient_program_id) {
        this.Patient_program_id = Patient_program_id;
    }

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
    }

    public void setDate_completed(Date Date_completed) {
        this.Date_completed = Date_completed;
    }

    public void setOutcome_concept_id(Integer Outcome_concept_id) {
        this.Outcome_concept_id = Outcome_concept_id;
    }

    public void setLocation_id(Integer Location_id) {
        this.Location_id = Location_id;
    }

    public void setDate_enrolled(Date Date_enrolled) {
        this.Date_enrolled = Date_enrolled;
    }

    public void setPatient_id(Integer Patient_id) {
        this.Patient_id = Patient_id;
    }

    public void setProgram_id(Integer Program_id) {
        this.Program_id = Program_id;
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
        return "PatientProgram(Patient_program_id=" + this.getPatient_program_id() + ", Uuid=" + this.getUuid() + ", Date_completed=" + this.getDate_completed() + ", Outcome_concept_id=" + this.getOutcome_concept_id() + ", Location_id=" + this.getLocation_id() + ", Date_enrolled=" + this.getDate_enrolled() + ", Patient_id=" + this.getPatient_id() + ", Program_id=" + this.getProgram_id() + ", Voided=" + this.isVoided() + ", Date_changed=" + this.getDate_changed() + ", Date_created=" + this.getDate_created() + ", Date_voided=" + this.getDate_voided() + ", Void_reason=" + this.getVoid_reason() + ", Creator=" + this.getCreator() + ")";
    }

//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;

}
