package org.ccfng.datamigration.filepaths;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConceptMap {

    private Integer openmrs;

    private Integer nmrs;

    public ConceptMap(Integer openmrs, Integer nmrs) {
        this.openmrs = openmrs;
        this.nmrs = nmrs;
    }

    public Integer getOpenmrs() {
        return openmrs;
    }

    public void setOpenmrs(Integer openmrs) {
        this.openmrs = openmrs;
    }

    public Integer getNmrs() {
        return nmrs;
    }

    public void setNmrs(Integer nmrs) {
        this.nmrs = nmrs;
    }

    @Override
    public String toString() {
        return "ConceptMap{" +
                "openmrs=" + openmrs +
                ", nmrs=" + nmrs +
                '}';
    }
}
