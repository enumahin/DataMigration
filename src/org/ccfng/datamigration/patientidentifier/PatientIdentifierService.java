package org.ccfng.datamigration.patientidentifier;

import org.ccfng.datamigration.session.SessionManager;

import javax.transaction.Transactional;
import java.util.List;

public class PatientIdentifierService {
    
    private static PatientIdentifierDao patientidentifierDao;

    private SessionManager sessionManager = new SessionManager();

    public PatientIdentifierService() {
        patientidentifierDao = new PatientIdentifierDao();
    }

    @Transactional
    public void persist(PatientIdentifier entity) {
        
        patientidentifierDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<PatientIdentifier> entity) {
        
        patientidentifierDao.saveAll(entity);
    }

    @Transactional
    public void update(PatientIdentifier entity) {
        patientidentifierDao.update(entity);
    }

    public PatientIdentifier findById(String id) {
        PatientIdentifier patient = patientidentifierDao.findById(id);
        return patient;
    }

    public void delete(String id) {
        PatientIdentifier patient = patientidentifierDao.findById(id);
        patientidentifierDao.delete(patient);
    }

    public List<PatientIdentifier> findAll() {
        List<PatientIdentifier> patients = patientidentifierDao.findAll();
        return patients;
    }

    public void deleteAll() {
        patientidentifierDao.deleteAll();
    }

    public PatientIdentifierDao patientidentifierDao() {
        return patientidentifierDao;
    }
}
