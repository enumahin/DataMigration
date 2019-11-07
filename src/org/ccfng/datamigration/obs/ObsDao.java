package org.ccfng.datamigration.obs;

import java.util.List;
import org.ccfng.datamigration.session.SessionManager;


public class ObsDao implements ObsDAOInterface<Obs, String> {

    private SessionManager sessionManager = new SessionManager();

    public ObsDao() {
    }

    public void persist(Obs entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<Obs> entity) {
        sessionManager.openCurrentSessionwithTransaction();
            for (Obs obs : entity){
                sessionManager.getCurrentSession().save(obs);
            }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(Obs entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }


    public Obs findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
            Obs obs = sessionManager.getCurrentSession().get(Obs.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return obs;
    }

    public void delete(Obs entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<Obs> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Obs> obss = (List<Obs>) sessionManager.getCurrentSession().createQuery("from Obs").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return obss;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Obs> entityList = findAll();
        for (Obs entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}
