package org.ccfng.datamigration.personname;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfPerson_name")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonNames {

    @XmlElement(name = "Person_name")
    private List<PersonName> person_names = null;

    public PersonNames() {
    }


    public List<PersonName> getPerson_names() {
        return this.person_names;
    }

    public void setPerson_names(List<PersonName> person_names) {
        this.person_names = person_names;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PersonNames)) return false;
        final PersonNames other = (PersonNames) o;
        if (!other.canEqual(this)) return false;
        final Object this$person_names = this.getPerson_names();
        final Object other$person_names = other.getPerson_names();
	    return this$person_names == null ? other$person_names == null : this$person_names.equals(other$person_names);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $person_names = this.getPerson_names();
        result = result * PRIME + ($person_names == null ? 43 : $person_names.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PersonNames;
    }

    public String toString() {
        return "PersonNames(person_names=" + this.getPerson_names() + ")";
    }
}
