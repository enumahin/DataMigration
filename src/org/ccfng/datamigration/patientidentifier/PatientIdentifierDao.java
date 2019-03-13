package org.ccfng.datamigration.patientidentifier;

import org.ccfng.datamigration.patientidentifier.PatientIdentifier;
import org.ccfng.datamigration.patientidentifier.PatientIdentifierDAOInterface;
import org.ccfng.datamigration.session.SessionManager;

import java.util.List;


public class PatientIdentifierDao implements PatientIdentifierDAOInterface<PatientIdentifier, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(PatientIdentifier entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<PatientIdentifier> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (PatientIdentifier patientidentifier : entity){
            sessionManager.getCurrentSession().save(patientidentifier);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(PatientIdentifier entity) {
        sessionManager.openCurrentSessionwithTransaction();
        sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public PatientIdentifier findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        PatientIdentifier patientidentifier = (PatientIdentifier) sessionManager.getCurrentSession().get(PatientIdentifier.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return patientidentifier;
    }

    public void delete(PatientIdentifier entity) {
        sessionManager.openCurrentSessionwithTransaction();
        sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<PatientIdentifier> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PatientIdentifier> patientidentifiers = (List<PatientIdentifier>) sessionManager.getCurrentSession().createQuery("from PatientIdentifier").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return patientidentifiers;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PatientIdentifier> entityList = findAll();
        for (PatientIdentifier entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}