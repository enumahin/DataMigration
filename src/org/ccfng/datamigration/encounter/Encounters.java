package org.ccfng.datamigration.encounter;

import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfEncounter")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(exclude ={"encounters"})
public class Encounters {

    @XmlElement(name = "Encounter")
    private List<Encounter> encounters = null;

    public Encounters() {
    }


    protected boolean canEqual(Object other) {
        return other instanceof Encounters;
    }

    public List<Encounter> getEncounters() {
        return this.encounters;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters;
    }

    public String toString() {
        return "Encounters(encounters=" + this.getEncounters() + ")";
    }
}
