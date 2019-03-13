package org.ccfng.datamigration.person;

import org.ccfng.datamigration.session.SessionManager;

import java.util.List;


public class PersonDao implements PersonDAOInterface<Person, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(Person entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<Person> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        for (Person person : entity){
            sessionManager.getCurrentSession().save(person);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(Person entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public Person findById(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        Person person = (Person) sessionManager.getCurrentSession().get(Person.class, id);
        sessionManager.closeCurrentSessionwithTransaction();
        return person;
    }

    public void delete(Person entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @SuppressWarnings("unchecked")
    public List<Person> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Person> persons = (List<Person>) sessionManager.getCurrentSession().createQuery("from Person").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return persons;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        List<Person> entityList = findAll();
        for (Person entity : entityList) {
            delete(entity);
        }
        sessionManager.closeCurrentSessionwithTransaction();
    }
}