package org.ccfng.datamigration.patient;

import java.io.Serializable;
import java.util.List;

public interface PatientDAOInterface<T, Id extends Serializable> {

    void persist(T entity);

    void saveAll(List<T> entity);

    void update(T entity);

    T findById(Id id);

    void delete(T entity);

    List<T> findAll();

    void deleteAll();

}
