package org.ccfng.datamigration.personaddress;

import org.ccfng.datamigration.session.SessionManager;

import java.util.List;


public class PersonAddressDao implements PersonAddressDAOInterface<PersonAddress, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(PersonAddress entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<PersonAddress> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (PersonAddress personAddress : entity){
            sessionManager.getCurrentSession().save(personAddress);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(PersonAddress entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public PersonAddress findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        PersonAddress personAddress = (PersonAddress) sessionManager.getCurrentSession().get(PersonAddress.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return personAddress;
    }

    public void delete(PersonAddress entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<PersonAddress> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PersonAddress> personAddresss = (List<PersonAddress>) sessionManager.getCurrentSession().createQuery("from PersonAddress").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return personAddresss;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PersonAddress> entityList = findAll();
        for (PersonAddress entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}