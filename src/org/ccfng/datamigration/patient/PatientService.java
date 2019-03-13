package org.ccfng.datamigration.patient;

import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.session.SessionManager;

import javax.transaction.Transactional;
import java.util.List;

public class PatientService {
    
    private static PatientDao patientDao;

    private SessionManager sessionManager = new SessionManager();

    public PatientService() {
        patientDao = new PatientDao();
    }

    //@Transactional
    public void persist(Patient entity) {
        
        patientDao.persist(entity);
        
    }

    @Transactional
    public void saveAll(List<Patient> entity) {
        
        patientDao.saveAll(entity);
        
    }

    @Transactional
    public void update(Patient entity) {
        
        patientDao.update(entity);
        
    }

    public Patient findById(String id) {
        
        Patient patient = patientDao.findById(id);
        
        return patient;
    }

    public void delete(String id) {
        
        Patient patient = patientDao.findById(id);
        patientDao.delete(patient);
        
    }

    public List<Patient> findAll() {
        
        List<Patient> patients = patientDao.findAll();
        
        return patients;
    }

    public void deleteAll() {
        
        patientDao.deleteAll();
        
    }

    public PatientDao patientDao() {
        return patientDao;
    }
}
