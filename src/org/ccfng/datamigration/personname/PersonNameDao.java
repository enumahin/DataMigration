package org.ccfng.datamigration.personname;

import com.sun.istack.internal.Nullable;
import org.ccfng.datamigration.session.SessionManager;
import org.hibernate.Session;

import java.util.List;


public class PersonNameDao implements PersonNameDAOInterface<PersonName, String> {

    private SessionManager sessionManager = new SessionManager();

    public void persist(PersonName entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().saveOrUpdate(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<PersonName> entity) {
        for (PersonName personName : entity){
            sessionManager.openCurrentSessionwithTransaction();
                sessionManager.getCurrentSession().save(personName);
            sessionManager.closeCurrentSessionwithTransaction();
        }
    }

    public void update(PersonName entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    @Nullable
    public PersonName findById(int id) {
        PersonName personName = null;
        try {
            sessionManager.openCurrentSessionwithTransaction();
                personName = (PersonName) sessionManager.getCurrentSession().get(PersonName.class, id);
            sessionManager.closeCurrentSessionwithTransaction();
            return personName;
        }catch (Exception ex){
            System.out.println(ex.toString());
            ex.printStackTrace();
            return null;
        }

    }

    public void delete(PersonName entity) {
        sessionManager.openCurrentSessionwithTransaction();
            sessionManager.getCurrentSession().delete(entity);
        sessionManager.closeCurrentSessionwithTransaction();

    }

    @SuppressWarnings("unchecked")
    @Nullable
    public List<PersonName> findAll() {
        sessionManager.openCurrentSessionwithTransaction();
            List<PersonName> personNames = (List<PersonName>) sessionManager.getCurrentSession().createQuery("from PersonName").list();
        sessionManager.closeCurrentSessionwithTransaction();
        return personNames;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
            List<PersonName> entityList = findAll();
            for (PersonName entity : entityList) {
                delete(entity);
            }
        sessionManager.closeCurrentSessionwithTransaction();

    }
}