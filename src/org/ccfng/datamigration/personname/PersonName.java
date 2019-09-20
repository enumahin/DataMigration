package org.ccfng.datamigration.personname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

@XmlRootElement(name = "Person_name")
@EqualsAndHashCode(exclude = {"Uuid",
        "Date_created","Creator","Voided","Date_voided",
        "Preferred","Void_reason","Void_reason","Date_created",
        "Given_name",
        "Prefix","Family_name_suffix","Family_name_prefix","Family_name","Middle_name","Degree","Family_name2"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "person_name")
public class PersonName{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Person_name_id_seq_gen")
    @SequenceGenerator(name = "Person_name_id_seq_gen", sequenceName = "Person_name_id_seq", allocationSize = 800)
    @XmlElement(name = "Person_name_id")
    private Integer Person_name_id;

    @XmlElement(name = "Person_id")
    private Integer Person_id;

    @XmlElement(name = "Given_name")
    private String Given_name;

    @XmlElement(name = "Prefix")
    private String Prefix;

    @XmlElement(name = "Family_name_suffix")
    private String Family_name_suffix;

    @XmlElement(name = "Family_name_prefix")
    private String Family_name_prefix;

    @XmlElement(name = "Family_name")
    private String Family_name;

    @XmlElement(name = "Middle_name")
    private String Middle_name;

    @XmlElement(name = "Degree")
    private String Degree;

    @XmlElement(name = "Preferred")
    private boolean Preferred;

    @XmlElement(name = "Family_name2")
    private String Family_name2;

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

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

    public PersonName() {
    }

    public Integer getPerson_name_id() {
        return this.Person_name_id;
    }

    public Integer getPerson_id() {
        return this.Person_id;
    }

    public String getGiven_name() {
        return this.Given_name;
    }

    public String getPrefix() {
        return this.Prefix;
    }

    public String getFamily_name_suffix() {
        return this.Family_name_suffix;
    }

    public String getFamily_name_prefix() {
        return this.Family_name_prefix;
    }

    public String getFamily_name() {
        return this.Family_name;
    }

    public String getMiddle_name() {
        return this.Middle_name;
    }

    public String getDegree() {
        return this.Degree;
    }

    public boolean isPreferred() {
        return this.Preferred;
    }

    public String getFamily_name2() {
        return this.Family_name2;
    }

    public UUID getUuid() {
        return this.Uuid;
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

    public void setPerson_name_id(Integer Person_name_id) {
        this.Person_name_id = Person_name_id;
    }

    public void setPerson_id(Integer Person_id) {
        this.Person_id = Person_id;
    }

    public void setGiven_name(String Given_name) {
        this.Given_name = Given_name;
    }

    public void setPrefix(String Prefix) {
        this.Prefix = Prefix;
    }

    public void setFamily_name_suffix(String Family_name_suffix) {
        this.Family_name_suffix = Family_name_suffix;
    }

    public void setFamily_name_prefix(String Family_name_prefix) {
        this.Family_name_prefix = Family_name_prefix;
    }

    public void setFamily_name(String Family_name) {
        this.Family_name = Family_name;
    }

    public void setMiddle_name(String Middle_name) {
        this.Middle_name = Middle_name;
    }

    public void setDegree(String Degree) {
        this.Degree = Degree;
    }

    public void setPreferred(boolean Preferred) {
        this.Preferred = Preferred;
    }

    public void setFamily_name2(String Family_name2) {
        this.Family_name2 = Family_name2;
    }

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
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
        return "PersonName(Person_name_id=" + this.getPerson_name_id() + ", Person_id=" + this.getPerson_id() + ", Given_name=" + this.getGiven_name() + ", Prefix=" + this.getPrefix() + ", Family_name_suffix=" + this.getFamily_name_suffix() + ", Family_name_prefix=" + this.getFamily_name_prefix() + ", Family_name=" + this.getFamily_name() + ", Middle_name=" + this.getMiddle_name() + ", Degree=" + this.getDegree() + ", Preferred=" + this.isPreferred() + ", Family_name2=" + this.getFamily_name2() + ", Uuid=" + this.getUuid() + ", Voided=" + this.isVoided() + ", Date_changed=" + this.getDate_changed() + ", Date_created=" + this.getDate_created() + ", Date_voided=" + this.getDate_voided() + ", Void_reason=" + this.getVoid_reason() + ", Creator=" + this.getCreator() + ")";
    }



//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;

}
