package org.ccfng.datamigration.person;

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
import java.sql.Time;
import java.util.Date;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.personaddress.PersonAddress;
import org.ccfng.datamigration.personname.PersonName;
import org.ccfng.global.DBMiddleMan;
import org.hibernate.annotations.Type;

@XmlRootElement(name = "Person")
@EqualsAndHashCode(exclude = {"Date_changed","Uuid",
        "Date_created","Person_id","Creator","Death_date","Dead",
        "Void_reason","Birthtime","Deathdate_estimated","Gender",
        "Birthdate_estimated","Birthdate","Voided","Date_voided"})
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "person")
public class Person {

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

    @XmlElement(name = "Date_changed")
    private Date Date_changed;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "Person_id_seq_gen")
    @SequenceGenerator(name = "Person_id_seq_gen", sequenceName = "Person_id_seq", allocationSize = 800)
    @XmlElement(name = "Person_id")
    private Integer Person_id;

    @XmlElement(name = "Creator")
    private Integer Creator;

    @XmlElement(name = "Death_date")
    private Date Death_date;

//    @XmlElement(name = "Changed_by")
//    private String Changed_by;

    @XmlElement(name = "Dead")
    private boolean Dead;

//    @XmlElement(name = "Voided_by")
//    private String Voided_by;

//    @XmlElement(name = "Cause_of_death")
//    private String Cause_of_death;

    @XmlElement(name = "Void_reason")
    private String Void_reason;

    @XmlElement(name = "Birthtime")
    private Date Birthtime;

    @XmlElement(name = "Deathdate_estimated")
    private boolean Deathdate_estimated;

    @XmlElement(name = "Gender")
    private String Gender;

    @XmlElement(name = "Birthdate_estimated")
    private boolean Birthdate_estimated;

    @XmlElement(name = "Birthdate")
    private Date Birthdate;

    @XmlElement(name = "Voided")
    private boolean Voided;

    @XmlElement(name = "Date_voided")
    private Date Date_voided;

    public Person() {
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public Date getDate_changed() {
        return this.Date_changed;
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

    public Date getDeath_date() {
        return this.Death_date;
    }

    public boolean getDead() {
        return this.Dead;
    }

    public String getVoid_reason() {
        return this.Void_reason;
    }

    public Date getBirthtime() {
        return this.Birthtime;
    }

    public boolean getDeathdate_estimated() {
        return this.Deathdate_estimated;
    }

    public String getGender() {
        return this.Gender;
    }

    public boolean getBirthdate_estimated() {
        return this.Birthdate_estimated;
    }

    public Date getBirthdate() {
        return this.Birthdate;
    }

    public boolean getVoided() {
        return this.Voided;
    }

    public Date getDate_voided() {
        return this.Date_voided;
    }

    public void setUuid(UUID Uuid) {
        this.Uuid = Uuid;
    }

    public void setDate_changed(Date Date_changed) {
        this.Date_changed = Date_changed;
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

    public void setDeath_date(Date Death_date) {
        this.Death_date = Death_date;
    }

    public void setDead(boolean Dead) {
        this.Dead = Dead;
    }

    public void setVoid_reason(String Void_reason) {
        this.Void_reason = Void_reason;
    }

    public void setBirthtime(Time Birthtime) {
        this.Birthtime = Birthtime;
    }

    public void setDeathdate_estimated(boolean Deathdate_estimated) {
        this.Deathdate_estimated = Deathdate_estimated;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public void setBirthdate_estimated(boolean Birthdate_estimated) {
        this.Birthdate_estimated = Birthdate_estimated;
    }

    public void setBirthdate(Date Birthdate) {
        this.Birthdate = Birthdate;
    }

    public void setVoided(boolean Voided) {
        this.Voided = Voided;
    }

    public void setDate_voided(Date Date_voided) {
        this.Date_voided = Date_voided;
    }

    public String toString() {
        return "Person(Uuid=" + this.getUuid() + ", Date_changed=" + this.getDate_changed() + ", Date_created=" + this.getDate_created() + ", Person_id=" + this.getPerson_id() + ", Creator=" + this.getCreator() + ", Death_date=" + this.getDeath_date() + ", Dead=" + this.getDead() + ", Void_reason=" + this.getVoid_reason() + ", Birthtime=" + this.getBirthtime() + ", Deathdate_estimated=" + this.getDeathdate_estimated() + ", Gender=" + this.getGender() + ", Birthdate_estimated=" + this.getBirthdate_estimated() + ", Birthdate=" + this.getBirthdate() + ", Voided=" + this.getVoided() + ", Date_voided=" + this.getDate_voided() + ")";
    }

    public PersonName getPersonName(){
       return DBMiddleMan.allPeopleNames.stream()
                .filter(personName ->
                        personName.getPerson_id().equals(this.getPerson_id())
                )
                .reduce((first, second) -> second)
               .orElse(null);
    }

    public PersonAddress getPersonAddress(){
        return DBMiddleMan.allPeopleAddresses.stream()
                .filter(personAdd ->
                        personAdd.getPerson_id().equals(this.getPerson_id())
                )
                .reduce((first, second) -> second)
                .orElse(null);
    }

//    public PersonAttribute getPersonAttribute(){
//        return DBMiddleMan.allPeo.stream()
//                .filter(personName ->
//                        personName.getPerson_id().equals(this.getPerson_id())
//                )
//                .reduce((first, second) -> second)
//                .get();
//    }

    public Patient getPatient(){
       return DBMiddleMan.allPatients.stream().filter(patient -> patient.getPatient_id().equals(this.getPerson_id()))
                .findFirst().orElse(null);
    }
}
