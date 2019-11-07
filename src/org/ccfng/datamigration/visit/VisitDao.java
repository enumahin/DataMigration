package org.ccfng.datamigration.visit;

import java.util.List;
import org.ccfng.datamigration.session.SessionManager;


public class VisitDao implements VisitDAOInterface<Visit, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(Visit entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<Visit> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (Visit visit : entity){
            sessionManager.getCurrentSession().save(visit);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(Visit entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public Visit findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        Visit visit = sessionManager.getCurrentSession().get(Visit.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return visit;
    }

    public void delete(Visit entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<Visit> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Visit> visits = (List<Visit>) sessionManager.getCurrentSession().createQuery("from Visit").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return visits;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Visit> entityList = findAll();
        for (Visit entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}
