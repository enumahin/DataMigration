package org.ccfng.datamigration.visit;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Visit")
@EqualsAndHashCode(exclude = {"Uuid",
        "Date_created","Creator","Voided","Date_voided",
        "Voided_by","Void_reason","Void_reason","Date_created",
        "Date_started",
        "Visit_id","Location_id","Patient_id","Visit_type_id","Indication_concept_id","Date_stopped"})
@Entity
public class Visit
{

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "Visit_id_seq_gen")
    @SequenceGenerator(name = "Visit_id_seq_gen", sequenceName = "Visit_id_seq", allocationSize = 800)
    @XmlElement(name = "Visit_id")
    private Integer Visit_id;

    @XmlElement(name = "Visit_type_id")
    private Integer Visit_type_id;

    @XmlElement(name = "Location_id")
    private Integer Location_id;

    @XmlElement(name = "Patient_id")
    private Integer Patient_id;

//    @XmlElement(name = "Indication_concept_id")
//    private Integer Indication_concept_id;

    @XmlElement(name = "Date_stopped")
    private Date Date_stopped;

    @XmlElement(name = "Date_started")
    private Date Date_started;

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

    public Visit() {
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public Integer getVisit_id() {
        return this.Visit_id;
    }

    public Integer getVisit_type_id() {
        return this.Visit_type_id;
    }

    public Integer getLocation_id() {
        return this.Location_id;
    }

    public Integer getPatient_id() {
        return this.Patient_id;
    }

    public Date getDate_stopped() {
        return this.Date_stopped;
    }

    public Date getDate_started() {
        return this.Date_started;
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

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
    }

    public void setVisit_id(Integer Visit_id) {
        this.Visit_id = Visit_id;
    }

    public void setVisit_type_id(Integer Visit_type_id) {
        this.Visit_type_id = Visit_type_id;
    }

    public void setLocation_id(Integer Location_id) {
        this.Location_id = Location_id;
    }

    public void setPatient_id(Integer Patient_id) {
        this.Patient_id = Patient_id;
    }

    public void setDate_stopped(Date Date_stopped) {
        this.Date_stopped = Date_stopped;
    }

    public void setDate_started(Date Date_started) {
        this.Date_started = Date_started;
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
        return "Visit(Uuid=" + this.getUuid() + ", Visit_id=" + this.getVisit_id() + ", Visit_type_id=" + this.getVisit_type_id() + ", Location_id=" + this.getLocation_id() + ", Patient_id=" + this.getPatient_id() + ", Date_stopped=" + this.getDate_stopped() + ", Date_started=" + this.getDate_started() + ", Voided=" + this.isVoided() + ", Date_changed=" + this.getDate_changed() + ", Date_created=" + this.getDate_created() + ", Date_voided=" + this.getDate_voided() + ", Void_reason=" + this.getVoid_reason() + ", Creator=" + this.getCreator() + ")";
    }

//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;


}
