package org.ccfng.datamigration.encounter;

import javax.transaction.Transactional;
import java.util.List;

public class EncounterService {
    private static EncounterDao encounterDao;

    public EncounterService() {
        encounterDao = new EncounterDao();
    }

    @Transactional
    public void persist(Encounter entity) {
        encounterDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<Encounter> entity) {
        encounterDao.saveAll(entity);
    }

    @Transactional
    public void update(Encounter entity) {
        encounterDao.update(entity);
    }

    public Encounter findById(String id) {
        Encounter encounter = encounterDao.findById(id);
        return encounter;
    }

    public void delete(String id) {
        Encounter encounter = encounterDao.findById(id);
        encounterDao.delete(encounter);
    }

    public List<Encounter> findAll() {
        List<Encounter> encounters = encounterDao.findAll();
        return encounters;
    }

    public void deleteAll() {
        encounterDao.deleteAll();
    }

    public EncounterDao encounterDao() {
        return encounterDao;
    }
}
