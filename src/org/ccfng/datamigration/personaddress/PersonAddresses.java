package org.ccfng.datamigration.personaddress;

import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfPerson_address")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(exclude = {"person_addresses"})
public class PersonAddresses {

    @XmlElement(name = "Person_address")
    private List<PersonAddress> person_addresses = null;

    public PersonAddresses() {
    }

    public List<PersonAddress> getPerson_addresses() {
        return this.person_addresses;
    }

    public void setPerson_addresses(List<PersonAddress> person_addresses) {
        this.person_addresses = person_addresses;
    }

    public String toString() {
        return "PersonAddresses(person_addresses=" + this.getPerson_addresses() + ")";
    }
}
