package org.ccfng.datamigration.provider;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "Provider")
@EqualsAndHashCode(exclude = {"Retired"})
@Entity
public class Provider {
    @Id
    @XmlElement(name = "Provider_id")
    private Integer Provider_id;

    @XmlElement(name = "Person_id")
    private Integer Person_id;

    @XmlElement(name = "Retired")
    private boolean Retired;

    @XmlElement(name = "Name")
    private String Name;

    @XmlElement(name = "Identifier")
    private String Identifier;

    @XmlElement(name = "Provider_role_id")
    private Integer Provider_role_id;

    @XmlElement(name = "Changed_by")
    private Integer Changed_by;

    @XmlElement(name = "Retired_by")
    private Integer Retired_by;

    @XmlElement(name = "Date_changed")
    private Date Date_changed;

    @XmlElement(name = "Date_created")
    private Date Date_created;

    @XmlElement(name = "Date_retired")
    private Date Date_retired;

    @XmlElement(name = "Retire_reason")
    private String Retire_reason;

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID Uuid;

    @XmlElement(name = "Creator")
    private Integer Creator;

    public Provider() {
    }

    public Integer getProvider_id() {
        return Provider_id;
    }

    public void setProvider_id(Integer provider_id) {
        Provider_id = provider_id;
    }

    public Integer getPerson_id() {
        return Person_id;
    }

    public void setPerson_id(Integer person_id) {
        Person_id = person_id;
    }

    public boolean isRetired() {
        return Retired;
    }

    public void setRetired(boolean retired) {
        Retired = retired;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        Identifier = identifier;
    }

    public Integer getProvider_role_id() {
        return Provider_role_id;
    }

    public void setProvider_role_id(Integer provider_role_id) {
        Provider_role_id = provider_role_id;
    }

    public Integer getRetired_by() {
        return Retired_by;
    }

    public void setRetired_by(Integer retired_by) {
        Retired_by = retired_by;
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

    public UUID getUuid() {
        return Uuid;
    }

    public Integer getChanged_by() {
        return Changed_by;
    }

    public void setChanged_by(Integer changed_by) {
        Changed_by = changed_by;
    }

    public void setUuid(UUID uuid) {
        Uuid = uuid;
    }

    public Integer getCreator() {
        return Creator;
    }

    public void setCreator(Integer creator) {
        Creator = creator;
    }
}
