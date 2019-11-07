package org.ccfng.datamigration.patientprogram;

import java.util.List;
import org.ccfng.datamigration.session.SessionManager;


public class PatientProgramDao implements PatientProgramDAOInterface<PatientProgram, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(PatientProgram entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<PatientProgram> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (PatientProgram patientProgram : entity){
            sessionManager.getCurrentSession().save(patientProgram);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(PatientProgram entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public PatientProgram findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        PatientProgram patientProgram = sessionManager.getCurrentSession().get(PatientProgram.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return patientProgram;
    }

    public void delete(PatientProgram entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<PatientProgram> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PatientProgram> patientPrograms = (List<PatientProgram>)
                sessionManager.getCurrentSession().createQuery("from PatientProgram").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return patientPrograms;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PatientProgram> entityList = findAll();
        for (PatientProgram entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}
