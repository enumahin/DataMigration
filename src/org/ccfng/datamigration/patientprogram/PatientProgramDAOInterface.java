package org.ccfng.datamigration.patientprogram;

import java.io.Serializable;
import java.util.List;

public interface PatientProgramDAOInterface<T, Id extends Serializable> {

    public void persist(T entity);

    public void saveAll(List<T> entity);

    public void update(T entity);

    public T findById(Id id);

    public void delete(T entity);

    public List<T> findAll();

    public void deleteAll();

}