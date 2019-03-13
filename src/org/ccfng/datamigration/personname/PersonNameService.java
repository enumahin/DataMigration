package org.ccfng.datamigration.personname;

import org.ccfng.datamigration.Controller;
import org.ccfng.datamigration.session.SessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.transaction.Transactional;
import java.util.List;

public class PersonNameService {

    private static PersonNameDao personNameDao;

    public PersonNameService() {
        personNameDao = new PersonNameDao();
    }

    @Transactional
    public void persist(PersonName entity) {
        personNameDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<PersonName> entity) {
        personNameDao.saveAll(entity);
    }

    @Transactional
    public void update(PersonName entity) {
        personNameDao.update(entity);
    }

    public PersonName findById(int id) {
        PersonName personName = personNameDao.findById(id);
        return personName;
    }

    public void delete(int id) {
        PersonName personName = personNameDao.findById(id);
        if(personName != null)
        personNameDao.delete(personName);
    }

    @Transactional
    public List<PersonName> findAll() {

        //Controller controller = new Controller();
        //controller.logToConsole("Querying for Person Names....\n");
        List<PersonName> personNames = personNameDao.findAll();

        return personNames;
    }

//    public void deleteAll() {
//        personNameDao.deleteAll();
//    }

    public PersonNameDao personNameDao() {
        return personNameDao;
    }
}
