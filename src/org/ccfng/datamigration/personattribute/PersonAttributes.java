package org.ccfng.datamigration.personattribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfPerson_attribute")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonAttributes {

    @XmlElement(name = "Person_attribute")
    private List<PersonAttribute> personAttributes = null;

    public PersonAttributes() {
    }


    public List<PersonAttribute> getPersonAttributes() {
        return this.personAttributes;
    }

    public void setPersonAttributes(List<PersonAttribute> personAttributes) {
        this.personAttributes = personAttributes;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PersonAttributes)) return false;
        final PersonAttributes other = (PersonAttributes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$personAttributes = this.getPersonAttributes();
        final Object other$personAttributes = other.getPersonAttributes();
        if (this$personAttributes == null ? other$personAttributes != null : !this$personAttributes.equals(other$personAttributes))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $personAttributes = this.getPersonAttributes();
        result = result * PRIME + ($personAttributes == null ? 43 : $personAttributes.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PersonAttributes;
    }

    public String toString() {
        return "PersonAttributes(personAttributes=" + this.getPersonAttributes() + ")";
    }
}
