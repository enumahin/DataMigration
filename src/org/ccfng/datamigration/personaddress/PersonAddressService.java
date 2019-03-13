package org.ccfng.datamigration.personaddress;
import org.ccfng.datamigration.session.SessionManager;

import java.util.List;

public class PersonAddressService {

    private static PersonAddressDao personAddressDao;

    private SessionManager sessionManager = new SessionManager();

    public PersonAddressService() {
        personAddressDao = new PersonAddressDao();
    }

    public void persist(PersonAddress entity) {
        sessionManager.openCurrentSessionwithTransaction();
        personAddressDao.persist(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void saveAll(List<PersonAddress> entity) {
        sessionManager.openCurrentSessionwithTransaction();
        personAddressDao.saveAll(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public void update(PersonAddress entity) {
        sessionManager.openCurrentSessionwithTransaction();
        personAddressDao.update(entity);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public PersonAddress findById(String id) {
        sessionManager.openCurrentSession();
        PersonAddress patient = personAddressDao.findById(id);
        sessionManager.closeCurrentSession();
        return patient;
    }

    public void delete(String id) {
        sessionManager.openCurrentSessionwithTransaction();
        PersonAddress patient = personAddressDao.findById(id);
        personAddressDao.delete(patient);
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public List<PersonAddress> findAll() {
        sessionManager.openCurrentSession();
        List<PersonAddress> patients = personAddressDao.findAll();
        sessionManager.closeCurrentSession();
        return patients;
    }

    public void deleteAll() {
        sessionManager.openCurrentSessionwithTransaction();
        personAddressDao.deleteAll();
        sessionManager.closeCurrentSessionwithTransaction();
    }

    public PersonAddressDao personAddressDao() {
        return personAddressDao;
    }
}
