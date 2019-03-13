package org.ccfng.datamigration.personattribute;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Person_attribute")
@EqualsAndHashCode(exclude = {"Uuid",
        "Date_created","Creator","Value",
        "Void_reason"
        ,"Voided","Date_voided",
        "Date_created","Person_id","Creator"
       })
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "person_attribute")
public class PersonAttribute
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Person_attribute_id_seq_gen")
    @SequenceGenerator(name = "Person_attribute_id_seq_gen", sequenceName = "Person_attribute_id_seq", allocationSize = 800)
    @XmlElement(name = "Person_attribute_id")
    private Integer Person_attribute_id;

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

    @XmlElement(name = "Value")
    private String Value;

    @XmlElement(name = "Void_reason")
    private String Void_reason;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @XmlElement(name = "Person_id")
    private Integer Person_id;

    @XmlElement(name = "Creator")
    private Integer Creator;

    @XmlElement(name = "Voided")
    private boolean Voided;

    @XmlElement(name = "Person_attribute_type_id")
    private Integer Person_attribute_type_id;

//    @XmlElement(name = "Voided_by")
//    private Integer Voided_by;

    @XmlElement(name = "Date_voided")
    private Date Date_voided;

    public PersonAttribute() {
    }

    public Integer getPerson_attribute_id() {
        return this.Person_attribute_id;
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public String getValue() {
        return this.Value;
    }

    public String getVoid_reason() {
        return this.Void_reason;
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

    public boolean isVoided() {
        return this.Voided;
    }

    public Integer getPerson_attribute_type_id() {
        return this.Person_attribute_type_id;
    }

    public Date getDate_voided() {
        return this.Date_voided;
    }

    public void setPerson_attribute_id(Integer Person_attribute_id) {
        this.Person_attribute_id = Person_attribute_id;
    }

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }

    public void setVoid_reason(String Void_reason) {
        this.Void_reason = Void_reason;
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

    public void setVoided(boolean Voided) {
        this.Voided = Voided;
    }

    public void setPerson_attribute_type_id(Integer Person_attribute_type_id) {
        this.Person_attribute_type_id = Person_attribute_type_id;
    }

    public void setDate_voided(Date Date_voided) {
        this.Date_voided = Date_voided;
    }

    public String toString() {
        return "PersonAttribute(Person_attribute_id=" + this.getPerson_attribute_id() + ", Uuid=" + this.getUuid() + ", Value=" + this.getValue() + ", Void_reason=" + this.getVoid_reason() + ", Date_created=" + this.getDate_created() + ", Person_id=" + this.getPerson_id() + ", Creator=" + this.getCreator() + ", Voided=" + this.isVoided() + ", Person_attribute_type_id=" + this.getPerson_attribute_type_id() + ", Date_voided=" + this.getDate_voided() + ")";
    }
}