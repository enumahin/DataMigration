package org.ccfng.datamigration.obs;

import javax.transaction.Transactional;
import java.util.List;

public class ObsService {
    private static ObsDao obsDao;

    public ObsService() {
        obsDao = new ObsDao();
    }

    @Transactional
    public void persist(Obs entity) {
        obsDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<Obs> entity) {
        obsDao.saveAll(entity);
    }

    @Transactional
    public void update(Obs entity) {
        obsDao.update(entity);
    }

    public Obs findById(String id) {
        return obsDao.findById(id);
    }

    public void delete(String id) {
        Obs obs = obsDao.findById(id);
        if(obs != null)
            obsDao.delete(obs);
    }

    public List<Obs> findAll() {
        return obsDao.findAll();

    }
//    @Transactional
//    public void deleteAll() {
//        obsDao.deleteAll();
//    }

    public ObsDao obsDao() {
        return obsDao;
    }
}
