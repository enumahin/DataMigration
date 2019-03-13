package org.ccfng.datamigration.personattribute;

import org.ccfng.datamigration.session.SessionManager;

import javax.transaction.Transactional;
import java.util.List;

public class PersonAttributeService {

    private static PersonAttributeDao personAttributeDao;

    public PersonAttributeService() {
        personAttributeDao = new PersonAttributeDao();
    }

    @Transactional
    public void persist(PersonAttribute entity) {
        personAttributeDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<PersonAttribute> entity) {
        personAttributeDao.saveAll(entity);
    }

    @Transactional
    public void update(PersonAttribute entity) {
        personAttributeDao.update(entity);
    }

    public PersonAttribute findById(String id) {
        PersonAttribute personAttribute = personAttributeDao.findById(id);
        return personAttribute;
    }

    public void delete(String id) {
        PersonAttribute personAttribute = personAttributeDao.findById(id);
        personAttributeDao.delete(personAttribute);
    }

    public List<PersonAttribute> findAll() {
        List<PersonAttribute> personAttributes = personAttributeDao.findAll();
        return personAttributes;
    }

    public void deleteAll() {
        personAttributeDao.deleteAll();
    }

    public PersonAttributeDao personAttributeDao() {
        return personAttributeDao;
    }
}
