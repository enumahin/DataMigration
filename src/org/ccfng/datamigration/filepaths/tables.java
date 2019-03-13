package org.ccfng.datamigration.filepaths;

public enum tables {
    encounter, obs, patient, patientidentifier, patientprogram, person, personaddress, personattribute, personname, visit;

    private tables(){}

    public String value(){ return name(); }

    public static tables fromvalue(String v){ return valueOf(v);}
}
