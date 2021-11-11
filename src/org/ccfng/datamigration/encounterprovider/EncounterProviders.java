package org.ccfng.datamigration.encounterprovider;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfEncounterProvider")
@XmlAccessorType(XmlAccessType.FIELD)
public class EncounterProviders {

    @XmlElement(name = "EncounterProvider")
    private List<EncounterProvider> encounterproviders = null;

    public EncounterProviders() {
    }


    public List<EncounterProvider> getEncounterProviders() {
        return this.encounterproviders;
    }

    public void setEncounterProvider(List<EncounterProvider> encounterproviders) {
        this.encounterproviders = encounterproviders;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof EncounterProviders)) return false;
        final EncounterProviders other = (EncounterProviders) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$encounterproviders = this.getEncounterProviders();
        final Object other$encounterproviders = other.getEncounterProviders();
        if (this$encounterproviders == null ? other$encounterproviders != null : !this$encounterproviders.equals(other$encounterproviders))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $encounterproviders = this.getEncounterProviders();
        result = result * PRIME + ($encounterproviders == null ? 43 : $encounterproviders.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EncounterProvider;
    }

    public String toString() {
        return "EncounterProvider(encounterproviders=" + this.getEncounterProviders() + ")";
    }
}
