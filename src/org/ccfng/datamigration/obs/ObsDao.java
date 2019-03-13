package org.ccfng.datamigration.obs;

import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.session.SessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.List;


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
            Obs obs = (Obs) sessionManager.getCurrentSession().get(Obs.class, id);
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