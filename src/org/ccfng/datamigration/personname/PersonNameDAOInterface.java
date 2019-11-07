package org.ccfng.datamigration.personname;

import java.io.Serializable;
import java.util.List;

public interface PersonNameDAOInterface<T, Integer extends Serializable> {

    void persist(T entity);

    void saveAll(List<T> entity);

    void update(T entity);

    T findById(int id);

    void delete(T entity);

    List<T> findAll();

    void deleteAll();

}
