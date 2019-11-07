package org.ccfng.datamigration.filepaths;

public enum tables {
    person, person_address, person_attribute, person_name, users, provider, patient, patient_identifier, patient_program, visit, encounter, encounter_provider, obs, user_role ;

    tables(){}

    public String value(){ return name(); }

    public static tables fromvalue(String v){ return valueOf(v);}
}
