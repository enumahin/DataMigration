package org.ccfng.datamigration.obs;

import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfObs")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(exclude = {"obses"})
public class Obses {

    @XmlElement(name = "Obs")
    private List<Obs> obses = null;

    public List<Obs> getObses() {
        return this.obses;
    }

    public void setObses(List<Obs> obses) {
        this.obses = obses;
    }
}
