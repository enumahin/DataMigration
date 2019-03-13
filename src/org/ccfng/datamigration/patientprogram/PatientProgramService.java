package org.ccfng.datamigration.patientprogram;

import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.patient.PatientDao;
import org.ccfng.datamigration.session.SessionManager;

import javax.transaction.Transactional;
import java.util.List;

public class PatientProgramService {
    
    private static PatientProgramDao patientprogramDao;


    public PatientProgramService() {
        patientprogramDao = new PatientProgramDao();
    }

    @Transactional
    public void persist(PatientProgram entity) {
        patientprogramDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<PatientProgram> entity) {
        patientprogramDao.saveAll(entity);
    }

    @Transactional
    public void update(PatientProgram entity) {
        patientprogramDao.update(entity);
    }

    public PatientProgram findById(String id) {
        PatientProgram patientProgram = patientprogramDao.findById(id);
        return patientProgram;
    }

    public void delete(String id) {
        PatientProgram patientProgram = patientprogramDao.findById(id);
        patientprogramDao.delete(patientProgram);
    }

    public List<PatientProgram> findAll() {
        List<PatientProgram> patients = patientprogramDao.findAll();
        return patients;
    }

    public void deleteAll() {
        patientprogramDao.deleteAll();
    }

    public PatientProgramDao patientprogramDao() {
        return patientprogramDao;
    }
}
