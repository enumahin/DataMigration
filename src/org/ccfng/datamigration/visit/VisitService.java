package org.ccfng.datamigration.visit;

import org.ccfng.datamigration.session.SessionManager;

import javax.transaction.Transactional;
import java.util.List;

public class VisitService {

    private static VisitDao visitDao;


    public VisitService() {
        visitDao = new VisitDao();
    }

    @Transactional
    public void persist(Visit entity) {
        visitDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<Visit> entity) {
        visitDao.saveAll(entity);
    }

    @Transactional
    public void update(Visit entity) {
        visitDao.update(entity);
    }

    public Visit findById(String id) {
        Visit visit = visitDao.findById(id);
        return visit;
    }

    public void delete(String id) {
        Visit visit = visitDao.findById(id);
        visitDao.delete(visit);
    }

    public List<Visit> findAll() {
        List<Visit> visits = visitDao.findAll();
        return visits;
    }

    public void deleteAll() {
        visitDao.deleteAll();
    }

    public VisitDao visitDao() {
        return visitDao;
    }
}
