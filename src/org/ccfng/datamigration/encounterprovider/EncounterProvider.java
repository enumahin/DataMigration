package org.ccfng.datamigration.encounterprovider;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "EncounterProvider")
@EqualsAndHashCode(exclude = {"Voided"})
@Entity
public class EncounterProvider {

    @Id
    @XmlElement(name = "Encounter_provider_id")
    private Integer Encounter_provider_id;

    @XmlElement(name = "Provider_id")
    private Integer Provider_id;

    @XmlElement(name = "Encounter_id")
    private Integer Encounter_id;

    @XmlElement(name = "Encounter_role_id")
    private Integer Encounter_role_id;

    @XmlElement(name = "Voided")
    private boolean Voided;

    @XmlElement(name = "Voided_by")
    private Integer Voided_by;

    @XmlElement(name = "Changed_by")
    private Integer Changed_by;

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

    @XmlElement(name = "Uuid")
    @Column(columnDefinition = "CHAR(38)")
    @Type(type = "uuid-char")
    private UUID Uuid;

    public EncounterProvider() {
    }

    public Integer getEncounter_provider_id() {
        return Encounter_provider_id;
    }

    public void setEncounter_provider_id(Integer encounter_provider_id) {
        Encounter_provider_id = encounter_provider_id;
    }

    public Integer getProvider_id() {
        return Provider_id;
    }

    public void setProvider_id(Integer provider_id) {
        Provider_id = provider_id;
    }

    public Integer getEncounter_id() {
        return Encounter_id;
    }

    public void setEncounter_id(Integer encounter_id) {
        Encounter_id = encounter_id;
    }

    public Integer getEncounter_role_id() {
        return Encounter_role_id;
    }

    public void setEncounter_role_id(Integer encounter_role_id) {
        Encounter_role_id = encounter_role_id;
    }

    public boolean isVoided() {
        return Voided;
    }

    public void setVoided(boolean voided) {
        Voided = voided;
    }

    public Integer getVoided_by() {
        return Voided_by;
    }

    public void setVoided_by(Integer voided_by) {
        Voided_by = voided_by;
    }

    public Integer getChanged_by() {
        return Changed_by;
    }

    public void setChanged_by(Integer changed_by) {
        Changed_by = changed_by;
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

    public Date getDate_voided() {
        return Date_voided;
    }

    public void setDate_voided(Date date_voided) {
        Date_voided = date_voided;
    }

    public String getVoid_reason() {
        return Void_reason;
    }

    public void setVoid_reason(String void_reason) {
        Void_reason = void_reason;
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
