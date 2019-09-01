package org.ccfng.datamigration;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.DesignDocument;

public class MobileDataController {

	public MobileDataController() {
	}

	public void initialize(){
		try {
			//--------------- Creating Connection--------------------------//
			HttpClient httpClient = new StdHttpClient.Builder()
					.url("http://localhost:5984")
					.build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
			//--------------- Creating database----------------------------//
			CouchDbConnector db = new StdCouchDbConnector("openmrs", dbInstance);
			db.createDatabaseIfNotExists();
			//--------------- Creating Document----------------------------//

			//--------------- Creating Document----------------------------//
			DesignDocument dd = new DesignDocument("patient");
			db.create(dd);
		}catch (Exception ex){
			ex.printStackTrace();
		}

	}

	public void connector(){

	}

	private  void startCouchbase() {
//
//		Session dbSession = new Session(host, port, username, password);
//
//		dbSession.createDatabase(name);
//
//		try {
//			dbSession.getDatabase(name);
//		} catch (Exception e) {
//			return false;
//		}
//
//		Context context;
//
//		Manager manager = null;
//	    try{
//				manager = new Manager(, Manager.DEFAULT_OPTIONS);
//    }catch(IOException e) {
//			e.printStackTrace();
//		}
//		Database database = null;
//    try {
//				database = manager.getDatabase("myapp");
//    } catch (CouchbaseLiteException e) {
//			e.printStackTrace();
//		}
//		Map<String, Object> properties = new HashMap<String, Object>();
//		properties.put("session", "Couchbase Mobile");
//		properties.put("conference", "JavaOne");
//		Document document = database.createDocument();
//    try {
//				document.putProperties(properties);
//    }catch (CouchbaseLiteException e) {
//			e.printStackTrace();
//		}
	}
}
