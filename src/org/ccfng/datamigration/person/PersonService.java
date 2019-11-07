package org.ccfng.datamigration.person;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.ObservableList;
import org.ccfng.datamigration.session.SessionManager;

public class PersonService {

    private static PersonDao personDao;

    public PersonService() {
        personDao = new PersonDao();
    }

    @Transactional
    public void persist(Person entity) {
        personDao.persist(entity);
    }

    @Transactional
    public void saveAll(List<Person> entity) {
        personDao.saveAll(entity);
    }

    @Transactional
    public void update(Person entity) {
        personDao.update(entity);
    }

    public Person findById(String id) {
        Person person = personDao.findById(id);
        return person;
    }

    public void delete(String id) {
        Person person = personDao.findById(id);
        personDao.delete(person);
    }

    public List<Person> findAll() {
        List<Person> persons = personDao.findAll();
        return persons;
    }

    public void deleteAll() {
        personDao.deleteAll();
    }

    public PersonDao personDao() {
        return personDao;
    }

    public void batchInsert(ObservableList<Person> persons){

         String INSERT_SQL = "INSERT INTO person"
                + "(person_id, gender, birthdate, birthdate_estimated, dead, death_date, creator, date_created, " +
                 "date_changed, voided, date_voided, void_reason, uuid, deathdate_estimated, birthtime) " +
                 "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String jdbcUrl = "jdbc:mysql://"+SessionManager.host+":"+SessionManager.port+"/"+SessionManager.db;
        String username = SessionManager.username;
        String password = SessionManager.password;
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {

            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

                // Insert sample records
                for (Person person : persons) {
                    stmt.setInt(1, person.getPerson_id());
                    stmt.setString(2, person.getGender());
                    stmt.setDate(3, new java.sql.Date(person.getBirthdate().getTime()));
                    stmt.setBoolean(4, person.getBirthdate_estimated());
                    stmt.setBoolean(5, person.getDead());
                    stmt.setDate(6, new java.sql.Date(person.getDeath_date().getTime()));
                    stmt.setInt(7, person.getCreator());
                    stmt.setDate(8, new java.sql.Date(person.getDate_created().getTime()));
                    stmt.setDate(9, new java.sql.Date(person.getDate_changed().getTime()));
                    stmt.setBoolean(10, person.getVoided());
                    stmt.setDate(11, new java.sql.Date(person.getDate_voided().getTime()));
                    stmt.setString(12, person.getVoid_reason());
                    stmt.setString(13, person.getUuid().toString());
                    stmt.setBoolean(14, person.getDeathdate_estimated());
                    stmt.setDate(15, new java.sql.Date(person.getBirthtime().getTime()));

                    //Add statement to batch
                    stmt.addBatch();
                }
                //execute batch
                stmt.executeBatch();
                conn.commit();
                System.out.println("Transaction is committed successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                if (conn != null) {
                    try {
                        System.out.println("Transaction is being rolled back.");
                        conn.rollback();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
