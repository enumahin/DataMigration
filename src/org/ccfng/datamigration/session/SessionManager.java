package org.ccfng.datamigration.session;

import org.ccfng.openmrscleanup.models.PharmacyEncounter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class SessionManager {

    private Session currentSession;

    public static String username = "";

    public static String password = "";

    public static String db = "";

    public static String host = "";

    public static String port = "";

    public static PharmacyEncounter activeObs = new PharmacyEncounter();


//
    private Transaction currentTransaction;
//
    public Session openCurrentSession() {
        //currentSession = getSessionFactory().openSession();
        return currentSession;
    }
    public void closeCurrentSession() {
        currentSession.getTransaction().commit();
        currentSession.close();
    }
//
    public Session getCurrentSession() {
        return this.currentSession;
    }
//
    public static String databaseConnect(){
        try {
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
            cfg.setProperty("hibernate.connection.url", "jdbc:mysql://" + host + ":" + port + "/" + db+"?useSSl=false");
            cfg.setProperty("hibernate.connection.username", username);
            cfg.setProperty("hibernate.connection.password", password);
            SessionFactory sf = cfg.buildSessionFactory();
            if(sf.openSession() != null) {
                sf.close();
                return "Success";
            }
            else
                return "Error Connecting to Database";
        }catch (Exception exc){
            return "Error Connecting to Database: "+exc.getMessage();
        }
    }

    public void openCurrentSessionwithTransaction(){
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        cfg.setProperty("hibernate.connection.url", "jdbc:mysql://" + host+":"+port+"/"+db+"?useSSl=false");
        cfg.setProperty("hibernate.connection.username", username);
        cfg.setProperty("hibernate.connection.password", password);
        SessionFactory sf = cfg.buildSessionFactory();
        Session session = sf.openSession();
        session.beginTransaction();
        this.currentSession = session;

    }

    public void closeCurrentSessionwithTransaction() {
        currentSession.getTransaction().commit();
        currentSession.close();
    }

}
