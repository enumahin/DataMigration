package org.ccfng.datamigration.encounter;

import org.ccfng.datamigration.session.SessionManager;

import java.util.List;

public class EncounterDao implements EncounterDAOInterface<Encounter, String> {

    private SessionManager sessionManager = new SessionManager();
    
    public EncounterDao() {
    }
    
    public void persist(Encounter entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
}

    public void saveAll(List<Encounter> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (Encounter encounter : entity){
            sessionManager.getCurrentSession().save(encounter);
        }
        sessionManager.closeCurrentSessionwithTransaction();
}

    public void update(Encounter entity) {
        sessionManager.openCurrentSessionwithTransaction();
        sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
}

    public Encounter findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        Encounter encounter = (Encounter) sessionManager.getCurrentSession().get(Encounter.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return encounter;
    }

    public void delete(Encounter entity) {
        sessionManager.openCurrentSessionwithTransaction();
        sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
}

    @SuppressWarnings("unchecked")
    public List<Encounter> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Encounter> encounters = (List<Encounter>) sessionManager.getCurrentSession().createQuery("from Encounter").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return encounters;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Encounter> entityList = findAll();
        for (Encounter entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
}
}