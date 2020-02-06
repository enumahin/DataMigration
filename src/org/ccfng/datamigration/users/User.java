package org.ccfng.datamigration.users;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Usersss")
@EqualsAndHashCode(exclude = {"Retired"})
@Entity
public class User {

    @Id
    @XmlElement(name = "User_id")
    private Integer User_id;

    @XmlElement(name = "System_id")
    private String System_id;

    @XmlElement(name = "Username")
    private String Username;

    @XmlElement(name = "Password")
    private String Password;

    @XmlElement(name = "Salt")
    private String Salt;

    @XmlElement(name = "Secret_question")
    private String Secret_question;

    @XmlElement(name = "Secret_answer")
    private String Secret_answer;

    @XmlElement(name = "Retired")
    private Boolean Retired;

    @XmlElement(name = "Retired_by")
    private Integer Retired_by;

    @XmlElement(name = "Changed_by")
    private Integer Changed_by;

    @XmlElement(name = "Person_id")
    private Integer Person_id;

    @XmlElement(name = "Date_changed")
    private Date Date_changed;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @XmlElement(name = "Date_retired")
    private Date Date_retired;

    @XmlElement(name = "Retire_reason")
    private String Retire_reason;

    @XmlElement(name = "Creator")
    private Integer Creator;

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID Uuid;

    public User() {
    }

    public Integer getUser_id() {
        return User_id;
    }

    public void setUser_id(Integer user_id) {
        User_id = user_id;
    }

    public String getSystem_id() {
        return System_id;
    }

    public void setSystem_id(String system_id) {
        System_id = system_id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getSalt() {
        return Salt;
    }

    public void setSalt(String salt) {
        Salt = salt;
    }

    public String getSecret_question() {
        return Secret_question;
    }

    public void setSecret_question(String secret_question) {
        Secret_question = secret_question;
    }

    public String getSecret_answer() {
        return Secret_answer;
    }

    public void setSecret_answer(String secret_answer) {
        Secret_answer = secret_answer;
    }

    public Boolean getRetired() {
        return Retired;
    }

    public void setRetired(Boolean retired) {
        Retired = retired;
    }

    public Integer getRetired_by() {
        return Retired_by;
    }

    public void setRetired_by(Integer retired_by) {
        Retired_by = retired_by;
    }

    public Integer getChanged_by() {
        return Changed_by;
    }

    public void setChanged_by(Integer changed_by) {
        Changed_by = changed_by;
    }

    public Integer getPerson_id() {
        return Person_id;
    }

    public void setPerson_id(Integer person_id) {
        Person_id = person_id;
    }

    public Date getDate_changed() {
        return Date_changed;
    }

    public void setDate_changed(Date date_changed) {
        Date_changed = date_changed;
    }

    public Date getDate_created() {
        return Date_created;
    }

    public void setDate_created(Date date_created) {
        Date_created = date_created;
    }

    public Date getDate_retired() {
        return Date_retired;
    }

    public void setDate_retired(Date date_retired) {
        Date_retired = date_retired;
    }

    public String getRetire_reason() {
        return Retire_reason;
    }

    public void setRetire_reason(String retire_reason) {
        Retire_reason = retire_reason;
    }

    public Integer getCreator() {
        return Creator;
    }

    public void setCreator(Integer creator) {
        Creator = creator;
    }

    public UUID getUuid() {
        return Uuid;
    }

    public void setUuid(UUID uuid) {
        Uuid = uuid;
    }
}