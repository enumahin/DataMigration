package org.ccfng.datamigration.person;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfPerson")
@XmlAccessorType(XmlAccessType.FIELD)
public class Persons {

    @XmlElement(name = "Person")
    private List<Person> persons = null;

    public Persons() {
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Persons)) return false;
        final Persons other = (Persons) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$persons = this.getPersons();
        final Object other$persons = other.getPersons();
        if (this$persons == null ? other$persons != null : !this$persons.equals(other$persons)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $persons = this.getPersons();
        result = result * PRIME + ($persons == null ? 43 : $persons.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Persons;
    }

    public String toString() {
        return "Persons(persons=" + this.getPersons() + ")";
    }
}
