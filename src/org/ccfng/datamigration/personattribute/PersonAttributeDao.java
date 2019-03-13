package org.ccfng.datamigration.personattribute;

import org.ccfng.datamigration.session.SessionManager;

import java.util.List;


public class PersonAttributeDao implements PersonAttributeDAOInterface<PersonAttribute, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(PersonAttribute entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<PersonAttribute> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (PersonAttribute personAttribute : entity){
            sessionManager.getCurrentSession().save(personAttribute);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(PersonAttribute entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public PersonAttribute findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        PersonAttribute personAttribute = (PersonAttribute) sessionManager.getCurrentSession().get(PersonAttribute.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return personAttribute;
    }

    public void delete(PersonAttribute entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<PersonAttribute> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PersonAttribute> personAttributes = (List<PersonAttribute>) sessionManager.getCurrentSession().createQuery("from PersonAttribute").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return personAttributes;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<PersonAttribute> entityList = findAll();
        for (PersonAttribute entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}