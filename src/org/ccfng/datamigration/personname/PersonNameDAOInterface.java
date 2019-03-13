package org.ccfng.datamigration.personname;

import java.io.Serializable;
import java.util.List;

public interface PersonNameDAOInterface<T, Integer extends Serializable> {

    public void persist(T entity);

    public void saveAll(List<T> entity);

    public void update(T entity);

    public T findById(int id);

    public void delete(T entity);

    public List<T> findAll();

    public void deleteAll();

}