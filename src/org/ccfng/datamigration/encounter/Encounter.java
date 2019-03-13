package org.ccfng.datamigration.encounter;

import javafx.collections.ObservableArray;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Encounter")
@EqualsAndHashCode(exclude = {"Date_changed","Uuid",
        "Date_created","Creator","Encounter_datetime",
        "Encounter_type","Void_reason","Patient_id","Form_id",
        "Location_id","Visit_id","Voided","Date_voided"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "encounter")
public class Encounter {
    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

    @XmlElement(name = "Visit_id")
    private Integer Visit_id;

    @XmlElement(name = "Date_changed")
    private Date Date_changed;

    @XmlElement(name = "Location_id")
    private Integer Location_id;

    @XmlElement(name = "Patient_id")
    private Integer Patient_id;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @XmlElement(name = "Creator")
    private Integer Creator;

    @XmlElement(name = "Encounter_type")
    private Integer Encounter_type;

//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;

//    @XmlElement(name = "Voided_by")
//    private Integer Voided_by;

    @XmlElement(name = "Void_reason")
    private String Void_reason;

    @XmlElement(name = "Encounter_datetime")
    private Date Encounter_datetime;

    @Id
    @XmlElement(name = "Encounter_id")
    private Integer Encounter_id;

    @XmlElement(name = "Voided")
    private boolean Voided;

    @XmlElement(name = "Form_id")
    private Integer Form_id;

    @XmlElement(name = "Date_voided")
    private Date Date_voided;

    public Encounter() {
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public Integer getVisit_id() {
        return this.Visit_id;
    }

    public Date getDate_changed() {
        return this.Date_changed;
    }

    public Integer getLocation_id() {
        return this.Location_id;
    }

    public Integer getPatient_id() {
        return this.Patient_id;
    }

    public Date getDate_created() {
        return this.Date_created;
    }

    public Integer getCreator() {
        return this.Creator;
    }

    public Integer getEncounter_type() {
        return this.Encounter_type;
    }

    public String getVoid_reason() {
        return this.Void_reason;
    }

    public Date getEncounter_datetime() {
        return this.Encounter_datetime;
    }

    public Integer getEncounter_id() {
        return this.Encounter_id;
    }

    public boolean isVoided() {
        return this.Voided;
    }

    public Integer getForm_id() {
        return this.Form_id;
    }

    public Date getDate_voided() {
        return this.Date_voided;
    }

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
    }

    public void setVisit_id(Integer Visit_id) {
        this.Visit_id = Visit_id;
    }

    public void setDate_changed(Date Date_changed) {
        this.Date_changed = Date_changed;
    }

    public void setLocation_id(Integer Location_id) {
        this.Location_id = Location_id;
    }

    public void setPatient_id(Integer Patient_id) {
        this.Patient_id = Patient_id;
    }

    public void setDate_created(Date Date_created) {
        this.Date_created = Date_created;
    }

    public void setCreator(Integer Creator) {
        this.Creator = Creator;
    }

    public void setEncounter_type(Integer Encounter_type) {
        this.Encounter_type = Encounter_type;
    }

    public void setVoid_reason(String Void_reason) {
        this.Void_reason = Void_reason;
    }

    public void setEncounter_datetime(Date Encounter_datetime) {
        this.Encounter_datetime = Encounter_datetime;
    }

    public void setEncounter_id(Integer Encounter_id) {
        this.Encounter_id = Encounter_id;
    }

    public void setVoided(boolean Voided) {
        this.Voided = Voided;
    }

    public void setForm_id(Integer Form_id) {
        this.Form_id = Form_id;
    }

    public void setDate_voided(Date Date_voided) {
        this.Date_voided = Date_voided;
    }

    public String toString() {
        return "Encounter(Uuid=" + this.getUuid() + ", Visit_id=" + this.getVisit_id() + ", Date_changed=" + this.getDate_changed() + ", Location_id=" + this.getLocation_id() + ", Patient_id=" + this.getPatient_id() + ", Date_created=" + this.getDate_created() + ", Creator=" + this.getCreator() + ", Encounter_type=" + this.getEncounter_type() + ", Void_reason=" + this.getVoid_reason() + ", Encounter_datetime=" + this.getEncounter_datetime() + ", Encounter_id=" + this.getEncounter_id() + ", Voided=" + this.isVoided() + ", Form_id=" + this.getForm_id() + ", Date_voided=" + this.getDate_voided() + ")";
    }
}
