package org.ccfng.datamigration.obs;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Obs")
@EqualsAndHashCode(exclude = {"Encounter_id","Uuid",
        "Date_created","Creator","Value_coded_name_id","Value_text",
        "Visit_id","Void_reason","Location_id","Value_drug",
        "Value_group_id","Value_complex","Voided","Date_voided",
        "Concept_id","Comments","Date_created","Person_id","Creator","Form_namespace_and_path",
        "Value_modifier","Value_coded","Accession_number","Obs_datetime","Value_numeric",
        "Value_datetime"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Obs
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obs_id_seq_gen")
    @SequenceGenerator(name = "obs_id_seq_gen", sequenceName = "obs_id_seq", allocationSize = 800)
    @XmlElement(name = "Obs_id")
    private Integer Obs_id;

    //@Lob
    @XmlElement(name = "Value_text")
    @Column(name = "value_text", columnDefinition = "TEXT")
    private String Value_text;

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

    @XmlElement(name = "Visit_id")
    private Integer Visit_id;

    @XmlElement(name = "Location_id")
    private Integer Location_id;

    @XmlElement(name = "Value_drug")
    private Integer Value_drug;

    @XmlElement(name = "Value_group_id")
    private Integer Value_group_id;

    @XmlElement(name = "Voided_by")
    private Integer Voided_by;

    @XmlElement(name = "Void_reason")
    private String Void_reason;

    @XmlElement(name = "Value_complex")
    private String Value_complex;

    @XmlElement(name = "Encounter_id")
    private Integer Encounter_id;

    @XmlElement(name = "Value_coded_name_id")
    private Integer Value_coded_name_id;

    @XmlElement(name = "Previous_version")
    private Integer Previous_version;

    @XmlElement(name = "Obs_group_id")
    private Integer Obs_group_id;

    @XmlElement(name = "Concept_id")
    private Integer Concept_id;

    @XmlElement(name = "Order_id")
    private Integer Order_id;

    @XmlElement(name = "Comments")
    private String Comments;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @XmlElement(name = "Person_id")
    private Integer Person_id;

    @XmlElement(name = "Creator")
    private Integer Creator;

    @XmlElement(name = "Form_namespace_and_path")
    private String Form_namespace_and_path;

    @XmlElement(name = "Value_modifier")
    private String Value_modifier;

    @XmlElement(name = "Value_coded")
    @Column(name = "value_coded", nullable = true)
    private Integer Value_coded;

    @XmlElement(name = "Accession_number")
    private String Accession_number;

    @XmlElement(name = "Obs_datetime")
    private Date Obs_datetime;

    @XmlElement(name = "Voided")
    private boolean Voided;

    @XmlElement(name = "Value_numeric")
    private Double Value_numeric;

    @XmlElement(name = "Date_voided")
    private Date Date_voided;

    @XmlElement(name = "Value_datetime")
    private Date Value_datetime;

    public Obs() {
    }

    public Integer getObs_id() {
        return this.Obs_id;
    }

    public String getValue_text() {
        return this.Value_text;
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public Integer getVisit_id() {
        return this.Visit_id;
    }

    public Integer getLocation_id() {
        return this.Location_id;
    }

    public Integer getValue_drug() {
        return this.Value_drug;
    }

    public Integer getValue_group_id() {
        return this.Value_group_id;
    }

    public String getVoid_reason() {
        return this.Void_reason;
    }

    public String getValue_complex() {
        return this.Value_complex;
    }

    public Integer getEncounter_id() {
        return this.Encounter_id;
    }

    public Integer getValue_coded_name_id() {
        return this.Value_coded_name_id;
    }

    public Integer getConcept_id() {
        return this.Concept_id;
    }

    public String getComments() {
        return this.Comments;
    }

    public Date getDate_created() {
        return this.Date_created;
    }

    public Integer getPerson_id() {
        return this.Person_id;
    }

    public Integer getCreator() {
        return this.Creator;
    }

    public String getForm_namespace_and_path() {
        return this.Form_namespace_and_path;
    }

    public String getValue_modifier() {
        return this.Value_modifier;
    }

    public Integer getValue_coded() {
        return this.Value_coded;
    }

    public String getAccession_number() {
        return this.Accession_number;
    }

    public Date getObs_datetime() {
        return this.Obs_datetime;
    }

    public boolean isVoided() {
        return this.Voided;
    }

    public Double getValue_numeric() {
        return this.Value_numeric;
    }

    public Date getDate_voided() {
        return this.Date_voided;
    }

    public Date getValue_datetime() {
        return this.Value_datetime;
    }

    public void setObs_id(Integer Obs_id) {
        this.Obs_id = Obs_id;
    }

    public void setValue_text(String Value_text) {
        this.Value_text = Value_text;
    }

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
    }

    public void setVisit_id(Integer Visit_id) {
        this.Visit_id = Visit_id;
    }

    public void setLocation_id(Integer Location_id) {
        this.Location_id = Location_id;
    }

    public void setValue_drug(Integer Value_drug) {
        this.Value_drug = Value_drug;
    }

    public void setValue_group_id(Integer Value_group_id) {
        this.Value_group_id = Value_group_id;
    }

    public void setVoid_reason(String Void_reason) {
        this.Void_reason = Void_reason;
    }

    public void setValue_complex(String Value_complex) {
        this.Value_complex = Value_complex;
    }

    public void setEncounter_id(Integer Encounter_id) {
        this.Encounter_id = Encounter_id;
    }

    public void setValue_coded_name_id(Integer Value_coded_name_id) {
        this.Value_coded_name_id = Value_coded_name_id;
    }

    public void setConcept_id(Integer Concept_id) {
        this.Concept_id = Concept_id;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public void setDate_created(Date Date_created) {
        this.Date_created = Date_created;
    }

    public void setPerson_id(Integer Person_id) {
        this.Person_id = Person_id;
    }

    public void setCreator(Integer Creator) {
        this.Creator = Creator;
    }

    public void setForm_namespace_and_path(String Form_namespace_and_path) {
        this.Form_namespace_and_path = Form_namespace_and_path;
    }

    public Integer getVoided_by() {
        return Voided_by;
    }

    public void setVoided_by(Integer voided_by) {
        Voided_by = voided_by;
    }

    public Integer getPrevious_version() {
        return Previous_version;
    }

    public void setPrevious_version(Integer previous_version) {
        Previous_version = previous_version;
    }

    public Integer getObs_group_id() {
        return Obs_group_id;
    }

    public void setObs_group_id(Integer obs_group_id) {
        Obs_group_id = obs_group_id;
    }

    public Integer getOrder_id() {
        return Order_id;
    }

    public void setOrder_id(Integer order_id) {
        Order_id = order_id;
    }

    public void setValue_modifier(String Value_modifier) {
        this.Value_modifier = Value_modifier;
    }

    public void setValue_coded(Integer Value_coded) {
        this.Value_coded = Value_coded;
    }

    public void setAccession_number(String Accession_number) {
        this.Accession_number = Accession_number;
    }

    public void setObs_datetime(Date Obs_datetime) {
        this.Obs_datetime = Obs_datetime;
    }

    public void setVoided(boolean Voided) {
        this.Voided = Voided;
    }

    public void setValue_numeric(Double Value_numeric) {
        this.Value_numeric = Value_numeric;
    }

    public void setDate_voided(Date Date_voided) {
        this.Date_voided = Date_voided;
    }

    public void setValue_datetime(Date Value_datetime) {
        this.Value_datetime = Value_datetime;
    }

    public String toString() {
        return "Obs(Obs_id=" + this.getObs_id() + ", Value_text=" + this.getValue_text() + ", Uuid=" + this.getUuid() + ", Visit_id=" + this.getVisit_id() + ", Location_id=" + this.getLocation_id() + ", Value_drug=" + this.getValue_drug() + ", Value_group_id=" + this.getValue_group_id() + ", Void_reason=" + this.getVoid_reason() + ", Value_complex=" + this.getValue_complex() + ", Encounter_id=" + this.getEncounter_id() + ", Value_coded_name_id=" + this.getValue_coded_name_id() + ", Concept_id=" + this.getConcept_id() + ", Comments=" + this.getComments() + ", Date_created=" + this.getDate_created() + ", Person_id=" + this.getPerson_id() + ", Creator=" + this.getCreator() + ", Form_namespace_and_path=" + this.getForm_namespace_and_path() + ", Value_modifier=" + this.getValue_modifier() + ", Value_coded=" + this.getValue_coded() + ", Accession_number=" + this.getAccession_number() + ", Obs_datetime=" + this.getObs_datetime() + ", Voided=" + this.isVoided() + ", Value_numeric=" + this.getValue_numeric() + ", Date_voided=" + this.getDate_voided() + ", Value_datetime=" + this.getValue_datetime() + ")";
    }
}
