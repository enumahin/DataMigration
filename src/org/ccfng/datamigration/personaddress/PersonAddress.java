package org.ccfng.datamigration.personaddress;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "PersonAddress")
@EqualsAndHashCode(exclude = {"Date_changed","Uuid",
        "Date_created","Creator","Start_date","Postal_code",
        "Voided_by","Address15","Void_reason","Address3","Address2",
        "Address1","Address4","Voided","Date_voided","Address5","Address6",
        "Address7","Address8","Address9","Date_created","Person_id","Creator","Address10",
        "Address11","Address12","Address13","Address14","City_village",
        "Country"})
@Table(name = "person_address")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class PersonAddress
{
    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type="uuid-char")
    private UUID Uuid;

    @XmlElement(name = "Date_changed")
    private Date Date_changed;

    @XmlElement(name = "Start_date")
    private Date Start_date;

    @XmlElement(name = "Postal_code")
    private String Postal_code;

//    @XmlElement(name = "Changed_by")
//    private Integer Changed_by;

//    @XmlElement(name = "Voided_by")
//    private Integer Voided_by;

    @XmlElement(name = "Address15")
    private String Address15;

    @XmlElement(name = "Void_reason")
    private String Void_reason;

    @XmlElement(name = "Address3")
    private String Address3;

    @XmlElement(name = "Address2")
    private String Address2;

    @XmlElement(name = "Address1")
    private String Address1;

    @XmlElement(name = "Latitude")
    private String Latitude;

    @XmlElement(name = "Address10")
    private String Address10;

    @XmlElement(name = "Address11")
    private String Address11;

    @XmlElement(name = "Address12")
    private String Address12;

    @XmlElement(name = "Address13")
    private String Address13;

    @XmlElement(name = "Address14")
    private String Address14;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @XmlElement(name = "Person_id")
    private Integer Person_id;

    @XmlElement(name = "Creator")
    private Integer Creator;

    @XmlElement(name = "Address4")
    private String Address4;

    @XmlElement(name = "End_date")
    private Date End_date;

    @XmlElement(name = "Address5")
    private String Address5;

    @XmlElement(name = "Address6")
    private String Address6;

    @XmlElement(name = "Address7")
    private String Address7;

    @XmlElement(name = "Country")
    private String Country;

    @XmlElement(name = "Address8")
    private String Address8;

    @XmlElement(name = "Address9")
    private String Address9;

    @XmlElement(name = "City_village")
    private String City_village;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_address_id_seq_gen")
    @SequenceGenerator(name = "person_address_id_seq_gen", sequenceName = "person_address_id_seq", allocationSize = 800)
    @XmlElement(name = "Person_address_id")
    private Integer Person_address_id;

    @XmlElement(name = "Voided")
    private boolean Voided;

    @XmlElement(name = "State_province")
    private String State_province;

    @XmlElement(name = "Longitude")
    private String Longitude;

    @XmlElement(name = "Preferred")
    private boolean Preferred;

    @XmlElement(name = "Date_voided")
    private Date Date_voided;

    public PersonAddress() {
    }

    public UUID getUuid() {
        return this.Uuid;
    }

    public Date getDate_changed() {
        return this.Date_changed;
    }

    public Date getStart_date() {
        return this.Start_date;
    }

    public String getPostal_code() {
        return this.Postal_code;
    }

    public String getAddress15() {
        return this.Address15;
    }

    public String getVoid_reason() {
        return this.Void_reason;
    }

    public String getAddress3() {
        return this.Address3;
    }

    public String getAddress2() {
        return this.Address2;
    }

    public String getAddress1() {
        return this.Address1;
    }

    public String getLatitude() {
        return this.Latitude;
    }

    public String getAddress10() {
        return this.Address10;
    }

    public String getAddress11() {
        return this.Address11;
    }

    public String getAddress12() {
        return this.Address12;
    }

    public String getAddress13() {
        return this.Address13;
    }

    public String getAddress14() {
        return this.Address14;
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

    public String getAddress4() {
        return this.Address4;
    }

    public Date getEnd_date() {
        return this.End_date;
    }

    public String getAddress5() {
        return this.Address5;
    }

    public String getAddress6() {
        return this.Address6;
    }

    public String getAddress7() {
        return this.Address7;
    }

    public String getCountry() {
        return this.Country;
    }

    public String getAddress8() {
        return this.Address8;
    }

    public String getAddress9() {
        return this.Address9;
    }

    public String getCity_village() {
        return this.City_village;
    }

    public Integer getPerson_address_id() {
        return this.Person_address_id;
    }

    public boolean isVoided() {
        return this.Voided;
    }

    public String getState_province() {
        return this.State_province;
    }

    public String getLongitude() {
        return this.Longitude;
    }

    public boolean isPreferred() {
        return this.Preferred;
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

    public void setStart_date(Date Start_date) {
        this.Start_date = Start_date;
    }

    public void setPostal_code(String Postal_code) {
        this.Postal_code = Postal_code;
    }

    public void setAddress15(String Address15) {
        this.Address15 = Address15;
    }

    public void setVoid_reason(String Void_reason) {
        this.Void_reason = Void_reason;
    }

    public void setAddress3(String Address3) {
        this.Address3 = Address3;
    }

    public void setAddress2(String Address2) {
        this.Address2 = Address2;
    }

    public void setAddress1(String Address1) {
        this.Address1 = Address1;
    }

    public void setLatitude(String Latitude) {
        this.Latitude = Latitude;
    }

    public void setAddress10(String Address10) {
        this.Address10 = Address10;
    }

    public void setAddress11(String Address11) {
        this.Address11 = Address11;
    }

    public void setAddress12(String Address12) {
        this.Address12 = Address12;
    }

    public void setAddress13(String Address13) {
        this.Address13 = Address13;
    }

    public void setAddress14(String Address14) {
        this.Address14 = Address14;
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

    public void setAddress4(String Address4) {
        this.Address4 = Address4;
    }

    public void setEnd_date(Date End_date) {
        this.End_date = End_date;
    }

    public void setAddress5(String Address5) {
        this.Address5 = Address5;
    }

    public void setAddress6(String Address6) {
        this.Address6 = Address6;
    }

    public void setAddress7(String Address7) {
        this.Address7 = Address7;
    }

    public void setCountry(String Country) {
        this.Country = Country;
    }

    public void setAddress8(String Address8) {
        this.Address8 = Address8;
    }

    public void setAddress9(String Address9) {
        this.Address9 = Address9;
    }

    public void setCity_village(String City_village) {
        this.City_village = City_village;
    }

    public void setPerson_address_id(Integer Person_address_id) {
        this.Person_address_id = Person_address_id;
    }

    public void setVoided(boolean Voided) {
        this.Voided = Voided;
    }

    public void setState_province(String State_province) {
        this.State_province = State_province;
    }

    public void setLongitude(String Longitude) {
        this.Longitude = Longitude;
    }

    public void setPreferred(boolean Preferred) {
        this.Preferred = Preferred;
    }

    public void setDate_voided(Date Date_voided) {
        this.Date_voided = Date_voided;
    }

    public String toString() {
        return "PersonAddress(Uuid=" + this.getUuid() + ", Date_changed=" + this.getDate_changed() + ", Start_date=" + this.getStart_date() + ", Postal_code=" + this.getPostal_code() + ", Address15=" + this.getAddress15() + ", Void_reason=" + this.getVoid_reason() + ", Address3=" + this.getAddress3() + ", Address2=" + this.getAddress2() + ", Address1=" + this.getAddress1() + ", Latitude=" + this.getLatitude() + ", Address10=" + this.getAddress10() + ", Address11=" + this.getAddress11() + ", Address12=" + this.getAddress12() + ", Address13=" + this.getAddress13() + ", Address14=" + this.getAddress14() + ", Date_created=" + this.getDate_created() + ", Person_id=" + this.getPerson_id() + ", Creator=" + this.getCreator() + ", Address4=" + this.getAddress4() + ", End_date=" + this.getEnd_date() + ", Address5=" + this.getAddress5() + ", Address6=" + this.getAddress6() + ", Address7=" + this.getAddress7() + ", Country=" + this.getCountry() + ", Address8=" + this.getAddress8() + ", Address9=" + this.getAddress9() + ", City_village=" + this.getCity_village() + ", Person_address_id=" + this.getPerson_address_id() + ", Voided=" + this.isVoided() + ", State_province=" + this.getState_province() + ", Longitude=" + this.getLongitude() + ", Preferred=" + this.isPreferred() + ", Date_voided=" + this.getDate_voided() + ")";
    }
}
