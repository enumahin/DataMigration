package org.ccfng.datamigration.provider;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfProvider")
@XmlAccessorType(XmlAccessType.FIELD)
public class Providers {

    @XmlElement(name = "Provider")
    private List<Provider> providers = null;

    public Providers() {
    }


    public List<Provider> getProviders() {
        return this.providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Providers)) return false;
        final Providers other = (Providers) o;
        if (!other.canEqual(this)) return false;
        final Object this$providers = this.getProviders();
        final Object other$providers = other.getProviders();
	    return this$providers == null ? other$providers == null : this$providers.equals(other$providers);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $providers = this.getProviders();
        result = result * PRIME + ($providers == null ? 43 : $providers.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Providers;
    }

    public String toString() {
        return "Providers(providers=" + this.getProviders() + ")";
    }
}
