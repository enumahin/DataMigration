package org.ccfng.datamigration.visit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfVisit")
@XmlAccessorType(XmlAccessType.FIELD)
public class Visits {

    @XmlElement(name = "Visit")
    private List<Visit> visits = null;

    public Visits() {
    }

    public List<Visit> getVisits() {
        return this.visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Visits)) return false;
        final Visits other = (Visits) o;
        if (!other.canEqual(this)) return false;
        final Object this$visits = this.getVisits();
        final Object other$visits = other.getVisits();
	    return this$visits == null ? other$visits == null : this$visits.equals(other$visits);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $visits = this.getVisits();
        result = result * PRIME + ($visits == null ? 43 : $visits.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Visits;
    }

    public String toString() {
        return "Visits(visits=" + this.getVisits() + ")";
    }
}
