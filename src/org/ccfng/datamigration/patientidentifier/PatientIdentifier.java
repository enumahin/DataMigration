package org.ccfng.datamigration.patientidentifier;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Patient_identifier")
@EqualsAndHashCode(exclude = {"Uuid",
        "Date_created","Creator","Voided","Date_voided",
        "Voided_by","Void_reason","Void_reason","Date_created",
        "Identifier_type",
        "Patient_identifier_id","Location_id","Patient_id","Preferred","Identifier"})
@Entity
@Table(name = "patient_identifier")
public class PatientIdentifier
{
    @XmlElement(name = "Identifier_type")
    private Integer Identifier_type;

    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    @XmlElement(name = "Uuid")
    private UUID Uuid;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Patient_identifier_id_seq_gen")
    @SequenceGenerator(name = "Patient_identifier_id_seq_gen", sequenceName = "Patient_identifier_id_seq", allocationSize = 800)
    @XmlElement(name = "Patient_identifier_id")
    private Integer Patient_identifier_id;

    @XmlElement(name = "Location_id")
    private Integer Location_id;

    @XmlElement(name = "Patient_id")
    private Integer Patient_id;

    @XmlElement(name = "Preferred")
    private boolean Preferred;

    @XmlElement(name = "Identifier")
    private String Identifier;

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

    public PatientIdentifier() {
    }

    public Integer getIdentifier_type() {
        return this.Identifier_type;
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public Integer getPatient_identifier_id() {
        return this.Patient_identifier_id;
    }

    public Integer getLocation_id() {
        return this.Location_id;
    }

    public Integer getPatient_id() {
        return this.Patient_id;
    }

    public boolean isPreferred() {
        return this.Preferred;
    }

    public String getIdentifier() {
        return this.Identifier;
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

    public void setIdentifier_type(Integer Identifier_type) {
        this.Identifier_type = Identifier_type;
    }

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
    }

    public void setPatient_identifier_id(Integer Patient_identifier_id) {
        this.Patient_identifier_id = Patient_identifier_id;
    }

    public void setLocation_id(Integer Location_id) {
        this.Location_id = Location_id;
    }

    public void setPatient_id(Integer Patient_id) {
        this.Patient_id = Patient_id;
    }

    public void setPreferred(boolean Preferred) {
        this.Preferred = Preferred;
    }

    public void setIdentifier(String Identifier) {
        this.Identifier = Identifier;
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
        return "PatientIdentifier(Identifier_type=" + this.getIdentifier_type() + ", Uuid=" + this.getUuid() + ", Patient_identifier_id=" + this.getPatient_identifier_id() + ", Location_id=" + this.getLocation_id() + ", Patient_id=" + this.getPatient_id() + ", Preferred=" + this.isPreferred() + ", Identifier=" + this.getIdentifier() + ", Voided=" + this.isVoided() + ", Date_changed=" + this.getDate_changed() + ", Date_created=" + this.getDate_created() + ", Date_voided=" + this.getDate_voided() + ", Void_reason=" + this.getVoid_reason() + ", Creator=" + this.getCreator() + ")";
    }

//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;

}
