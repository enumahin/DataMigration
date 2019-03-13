package org.ccfng.datamigration.patient;

import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.session.SessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.List;


public class PatientDao implements PatientDAOInterface<Patient, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(Patient entity) {
        //sessionManager.openCurrentSessionwithTransaction();
            //sessionManager.getCurrentSession().saveOrUpdate(entity);
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        cfg.setProperty("hibernate.connection.url", "jdbc:mysql://" + SessionManager.host+":"+SessionManager.port+"/"+
                SessionManager.db+"?useSSl=false");
        cfg.setProperty("hibernate.connection.username", SessionManager.username);
        cfg.setProperty("hibernate.connection.password", SessionManager.password);
        SessionFactory sf = cfg.buildSessionFactory();
        Session session = sf.openSession();
            session.beginTransaction();
                    session.saveOrUpdate(entity);
            session.getTransaction().commit();
            session.close();
        //sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<Patient> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (Patient patient : entity){
            sessionManager.getCurrentSession().save(patient);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(Patient entity) {
        sessionManager.openCurrentSessionwithTransaction();
        sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public Patient findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        Patient patient = (Patient) sessionManager.getCurrentSession().get(Patient.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return patient;
    }

    public void delete(Patient entity) {
        sessionManager.openCurrentSessionwithTransaction();
        sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<Patient> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Patient> patients = (List<Patient>) sessionManager.getCurrentSession().createQuery("from Patient").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return patients;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Patient> entityList = findAll();
        for (Patient entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}