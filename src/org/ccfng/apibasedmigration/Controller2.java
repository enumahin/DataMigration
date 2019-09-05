//package org.ccfng.apibasedmigration;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.concurrent.Task;
//import javafx.fxml.FXML;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TextField;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.ccfng.apibasedmigration.models.Address;
//import org.ccfng.apibasedmigration.models.Encounter;
//import org.ccfng.apibasedmigration.models.Facility;
//import org.ccfng.apibasedmigration.models.Identifier;
//import org.ccfng.apibasedmigration.models.Patient;
//import org.ccfng.apibasedmigration.models.PatientName;
//import org.ccfng.datamigration.filepaths.ConceptMap;
//import org.ccfng.global.ConnectionClass;
//import org.ccfng.global.Location;
//import org.json.JSONObject;
//
//public class Controller2 {
//
//	private final String USER_AGENT = "Mozilla/5.0";
//
//	ConnectionClass cc;
//
//	private HashMap<Integer, Integer> concepts =  new HashMap<>();
//
//	@FXML
//	private ComboBox<Location> stateCombo;
//
//	@FXML
//	private ComboBox<Location> lgaCombo;
//
//	@FXML
//	private ComboBox<Location> facilityCombo;
//
//	@FXML
//	private TextField datimCode;
//
//	private Location defaultLocation;
//
//	private Task<ObservableList<Patient>> patientTask;
//
//	private Task<Set<Address>> addressTask;
//
//	private Task<Set<PatientName>> patientNameTask;
//
//	private Task<Set<Identifier>> patientIdentifierTask;
//
//	private Task<Set<Encounter>> encounterTask;
//
////	@FXML
//	private ObservableList<Patient> allPatient = FXCollections.observableArrayList();
//
//	public void initialize(){
//
//		cc = new ConnectionClass();
//
//		Path pathToFile = Paths.get("conceptMapping.csv");
//		try (BufferedReader br = Files.newBufferedReader(pathToFile,
//				StandardCharsets.US_ASCII)) {
//
//			// read the first line from the text file
//			String line = br.readLine();
//
//			// loop until all lines are read
//			while (line != null) {
//
//				// use string.split to load a string array with the values from
//				// each line of
//				// the file, using a comma as the delimiter
//				String[] attributes = line.split(",");
//
//				ConceptMap concept = createConceptMap(attributes);
//
//				// adding book into ArrayList
//				concepts.put(concept.getOpenmrs(), concept.getNmrs());
//
//				// read next line before looping
//				// if end of file reached, line would be null
//				line = br.readLine();
//			}
//
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
//
//		buildLocation("states",133452);
//
//	}
//
//	@FXML
//	private void buildData(){
//				buildPatient();
//				new Thread(patientTask).start();
//	}
//
//	private static ConceptMap createConceptMap(String[] metadata) {
//		Integer openmrs =  Integer.parseInt(metadata[0]);
//		Integer nmrs = Integer.parseInt(metadata[1]);
//
//		// create and return book of this metadata
//		return new ConceptMap(openmrs, nmrs);
//
//	}
//
//	private void buildPatient() {
//		patientTask = new Task<ObservableList<Patient>>() {
//			@Override
//			protected ObservableList<Patient> call() throws Exception {
//				Connection conn = null;
//				Statement stmt = null;
//				Connection conn2 = null;
//				Statement stmt2 = null;
//
//					try {
//						//STEP 2: Register JDBC driver
//						Class.forName("com.mysql.jdbc.Driver");
//
//						//STEP 3: Open a connection
//						conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//						//logToConsole("\n Connected to database successfully...");
//
//						//STEP 4: Execute a query
//
//						stmt = conn.createStatement();
//
//						String sql = "SELECT "
//								+ "patient.patient_id AS patientID, person.creator AS creator, person.date_created, person.gender,"
//								+ " person.birthdate, birthdate_estimated, person.dead, person.death_date, person.cause_of_death, "
//								+ "(SELECT value_text from obs where concept_id = 7778430 && person_id = patient.patient_id Limit 1) AS phone "
//								+ " FROM patient LEFT JOIN person on patient.patient_id = person.person_id "
//								+ " where person.voided = 0";
//						//logToConsole("\n Creating Select statement...");
//						ResultSet rs = stmt.executeQuery(sql);
//						//STEP 5: Extract data from result set
//						Facility facility = new Facility();
//						facility.setState(stateCombo.getSelectionModel().getSelectedItem().getLocation());
//						facility.setLga(lgaCombo.getSelectionModel().getSelectedItem().getLocation());
//						facility.setFacilityName(facilityCombo.getSelectionModel().getSelectedItem().getLocation());
//						facility.setDatimCode(datimCode.getText());
//						while (rs.next()) {
//
//							Patient patient = new Patient();
//							patient.setPatientID(rs.getInt("patientID"));
//							patient.setPhone(rs.getString("phone"));
//							patient.setDateCreated(rs.getDate("date_created"));
//							patient.setBirthDate(rs.getDate("birthdate"));
//							patient.setBirthdateEstimated(rs.getBoolean("birthdate_estimated"));
//							patient.setGender(rs.getString("gender"));
//							patient.setDead(rs.getBoolean("dead"));
//							patient.setCreator(rs.getInt("creator"));
//							patient.setCauseOfDeath(rs.getString("cause_of_death"));
//							patient.setDeathDate(rs.getDate("death_date"));
//							patient.setFacility(facility);
//							allPatient.add(patient);
//						}
//					}
//					catch (SQLException se) {
//						//Handle errors for JDBC
//						se.printStackTrace();
//						System.out.println("\n SQLException Error: " + se.getMessage());
//					}
//					catch (Exception e) {
//						//Handle errors for Class.forName
//						e.printStackTrace();
//						System.out.println("\n Exception Error: " + e.getMessage());
//					}
//					finally {
//						//finally block used to close resources
//						try {
//							if (stmt != null)
//								conn.close();
//						}
//						catch (SQLException se) {
//						}// do nothing
//						closeConnection(conn);
//					}//end try
//				Platform.runLater(() -> {
////					System.out.println(allPatient);
//					buildPatientAddresses();
//				});
//
//
//					return allPatient;
//			}
//		 };
//		}
//
//	private void buildPatientAddresses() {
//		Set<Address> addresses = new HashSet<>();
//		addressTask = new Task<Set<Address>>() {
//
//			@Override
//			protected Set<Address> call() throws Exception {
//
//			for (Patient pat : allPatient) {
//				Connection conn = null;
//				Statement stmt = null;
//				Connection conn2 = null;
//				Statement stmt2 = null;
//
//				try {
//					//STEP 2: Register JDBC driver
//					Class.forName("com.mysql.jdbc.Driver");
//
//					//STEP 3: Open a connection
//					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//					//logToConsole("\n Connected to database successfully...");
//
//					//STEP 4: Execute a query
//					stmt = conn.createStatement();
//
//
////						String sql = "SELECT * FROM person_address where person_id = "+pat.getPatientID();
//						String sql = "SELECT * FROM person_address where person_id = "+pat.getPatientID() +" ORDER BY person_address_id Limit 1";
//						//logToConsole("\n Creating Select statement...");
//						ResultSet rs = stmt.executeQuery(sql);
//						//STEP 5: Extract data from result set
//
//						while (rs.next()) {
//	                        Address address = new Address();
//							address.setCountry(rs.getString("country"));
//							address.setLatitude(rs.getString("latitude"));
//							address.setLongitude(rs.getString("longitude"));
//							address.setAddress1(rs.getString("address1"));
//							address.setAddress2(rs.getString("address2"));
//							address.setAddress3(rs.getString("address3"));
//							address.setCityVillage(rs.getString("city_village"));
//							address.setStateProvince(rs.getString("state_province"));
//							address.setPostalCode(rs.getString("postal_code"));
//							address.setPreferred(rs.getBoolean("preferred"));
//							addresses.add(address);
//							pat.setAddress(address);
//							break;
//						}
////						pat.setAddress(addresses);
//					}
//				catch(SQLException se){
//						//Handle errors for JDBC
//						se.printStackTrace();
//						System.out.println("\n SQLException Error: " + se.getMessage());
//					}
//				catch(Exception e){
//						//Handle errors for Class.forName
//						e.printStackTrace();
//						System.out.println("\n Exception Error: " + e.getMessage());
//					}
//				finally{
//						//finally block used to close resources
//						try {
//							if (stmt != null)
//								conn.close();
//						}
//						catch (SQLException se) {
//						}// do nothing
//						closeConnection(conn);
//					}//end try
//				break;
//			}
//			   Platform.runLater(()->{
//				   buildPatientName();
////				   System.out.println(allPatient);
//			   });
//			  return addresses;
//			}
//		};
//	}
//
//	private void buildPatientName() {
//
//		Set<PatientName> patientNames = new HashSet<>();
//		patientNameTask = new Task<Set<PatientName>>() {
//
//			@Override
//			protected Set<PatientName> call() throws Exception {
//
//		for (Patient pat : allPatient) {
//			Connection conn = null;
//			Statement stmt = null;
//			Connection conn2 = null;
//			Statement stmt2 = null;
//
//			try {
//				//STEP 2: Register JDBC driver
//				Class.forName("com.mysql.jdbc.Driver");
//
//				//STEP 3: Open a connection
//				conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//				//logToConsole("\n Connected to database successfully...");
//
//				//STEP 4: Execute a query
//				stmt = conn.createStatement();
//
//
////				String sql = "SELECT * FROM person_name where person_id = "+pat.getPatientID();
//				String sql = "SELECT person_name.*, "
//						+ "(SELECT identifier FROM patient_identifier where patient_id = "+pat.getPatientID()+" AND preferred = 1 ) AS hospitalNo "
//						+ "FROM person_name where person_id = "+pat.getPatientID()+ " ORDER BY person_name_id DESC LIMIT 1";
//				//logToConsole("\n Creating Select statement...");
//				ResultSet rs = stmt.executeQuery(sql);
//				//STEP 5: Extract data from result set
//
//				while (rs.next()) {
//					PatientName patientName = new PatientName();
//					patientName.setFirstName(rs.getString("given_name"));
//					patientName.setLastName(rs.getString("family_name"));
//					patientName.setMiddleName(rs.getString("middle_name"));
//					patientName.setPrefix(rs.getString("prefix"));
//					patientName.setPreferred(rs.getBoolean("preferred"));
//					patientNames.add(patientName);
//					//###############################################################
//						pat.setGivenName(rs.getString("given_name"));
//						pat.setSurname(rs.getString("family_name"));
//						pat.setMiddleName(rs.getString("middle_name"));
//						pat.setHospitalNo(rs.getString("hospitalNo"));
//						pat.setPreferred(true);
//						pat.setPrefix(rs.getString("prefix"));
//					//###############################################################
//
//					break;
//				}
////				pat.setPatientname(patientNames);
//			}
//			catch(SQLException se){
//				//Handle errors for JDBC
//				se.printStackTrace();
//				System.out.println("\n SQLException Error: " + se.getMessage());
//			}
//			catch(Exception e){
//				//Handle errors for Class.forName
//				e.printStackTrace();
//				System.out.println("\n Exception Error: " + e.getMessage());
//			}
//			finally{
//				//finally block used to close resources
//				try {
//					if (stmt != null)
//						conn.close();
//				}
//				catch (SQLException se) {
//				}// do nothing
//				closeConnection(conn);
//			}//end try
//			break;
//		}
//			Platform.runLater(() -> {
//				System.out.println(allPatient);
////				buildPatientIdentifiers();
//			});
//
//			return patientNames;
//			}
//		};
//	}
//
//	private void buildLocation(String target, int parentID) {
//
//			Connection conn = null;
//			Statement stmt = null;
//			Connection conn2 = null;
//			Statement stmt2 = null;
//
//			ObservableList<Location> locationMap = FXCollections.observableArrayList();
//
//			try {
//				//STEP 2: Register JDBC driver
//				Class.forName("com.mysql.jdbc.Driver");
//
//				//STEP 3: Open a connection
//				conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//				//logToConsole("\n Connected to database successfully...");
//
//				//STEP 4: Execute a query
//				stmt = conn.createStatement();
//
//
//				String sql = "SELECT * FROM address_hierarchy_entry where parent_id = "+parentID;
//				//logToConsole("\n Creating Select statement...");
//				ResultSet rs = stmt.executeQuery(sql);
//				//STEP 5: Extract data from result set
//
//				while (rs.next()) {
//						locationMap.add(new Location(rs.getInt("address_hierarchy_entry_id"),rs.getString("name")));
//				}
//			}
//			catch(SQLException se){
//				//Handle errors for JDBC
//				se.printStackTrace();
//				System.out.println("\n SQLException Error: " + se.getMessage());
//			}
//			catch(Exception e){
//				//Handle errors for Class.forName
//				e.printStackTrace();
//				System.out.println("\n Exception Error: " + e.getMessage());
//			}
//			finally{
//				//finally block used to close resources
//				try {
//					if (stmt != null)
//						conn.close();
//				}
//				catch (SQLException se) {
//				}// do nothing
//				closeConnection(conn);
//			}//end try
//
//		if(target.equals("states")){
//			stateCombo.setItems(locationMap);
//		}else if(target.equals("lgas")){
//			lgaCombo.setItems(locationMap);
//		}else if(target.equals("facilities")){
//			facilityCombo.setItems(locationMap);
//		}
//	}
//
//	private void buildPatientIdentifiers() {
//
//		Set<Identifier> identifiers = new HashSet<>();
//
//		patientIdentifierTask = new Task<Set<Identifier>>() {
//
//			@Override
//			protected Set<Identifier> call() throws Exception {
//
//			for (Patient pat : allPatient) {
//				Connection conn = null;
//				Statement stmt = null;
//				Connection conn2 = null;
//				Statement stmt2 = null;
//
//				try {
//					//STEP 2: Register JDBC driver
//					Class.forName("com.mysql.jdbc.Driver");
//
//					//STEP 3: Open a connection
//					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//					//logToConsole("\n Connected to database successfully...");
//
//					//STEP 4: Execute a query
//					stmt = conn.createStatement();
//
//
//					String sql = "SELECT * FROM patient_identifier where patient_id = "+pat.getPatientID();
//					//logToConsole("\n Creating Select statement...");
//					ResultSet rs = stmt.executeQuery(sql);
//					//STEP 5: Extract data from result set
//
//					while (rs.next()) {
//						if(rs.getInt("identifier_type") == 2 || rs.getInt("identifier_type") == 3 || rs.getInt("identifier_type") == 4) {
//							Identifier identifier = new Identifier();
//							identifier.setIdentifier(rs.getString("identifier"));
//							switch (rs.getInt("identifier_type")) {
//								case 2:
//									identifier.setIdentifierType(2);
//									break;
//								case 3:
//									identifier.setIdentifierType(5);
//									break;
//								case 4:
//									identifier.setIdentifierType(4);
//									break;
//							}
//							identifier.setLocationId(rs.getInt("location_id"));
//							identifier.setPreferred(rs.getBoolean("preferred"));
//							identifiers.add(identifier);
//						}
//					}
//					pat.setIdentifiers(identifiers);
//				}
//				catch(SQLException se){
//					//Handle errors for JDBC
//					se.printStackTrace();
//					System.out.println("\n SQLException Error: " + se.getMessage());
//				}
//				catch(Exception e){
//					//Handle errors for Class.forName
//					e.printStackTrace();
//					System.out.println("\n Exception Error: " + e.getMessage());
//				}
//				finally{
//					//finally block used to close resources
//					try {
//						if (stmt != null)
//							conn.close();
//					}
//					catch (SQLException se) {
//					}// do nothing
//					closeConnection(conn);
//				}//end try
//			}
//				Platform.runLater(()->{
//					System.out.println(allPatient);
//					buildEncounters();
//				});
//				return identifiers;
//			}
//		};
//	}
//
//	private Boolean isAdult(Patient pat){
//		if(Duration.between(
//				LocalDate.parse(pat.getBirthDate().toString(), cc.getFormatter())
//						.atStartOfDay(),LocalDate.now().atStartOfDay()
//		).toDays() >= 18){
//			System.out.println("IS ADAULT!\n");
//			return true;
//		}else{
//			System.out.println("Pediatric \n");
//			return false;
//		}
//	}
//
//	private void buildEncounters(){
//		Set<Encounter> encounters = new HashSet<>();
//		encounterTask = new Task<Set<Encounter>>() {
//
//			@Override
//			protected Set<Encounter> call() throws Exception {
//
//			for (Patient pat : allPatient) {
//				Connection conn = null;
//				Statement stmt = null;
//
//				try {
//					//STEP 2: Register JDBC driver
//					Class.forName("com.mysql.jdbc.Driver");
//
//					//STEP 3: Open a connection
//					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//					//logToConsole("\n Connected to database successfully...");
//
//					//STEP 4: Execute a query
//					stmt = conn.createStatement();
//
//
//					String sql = "SELECT * FROM encounter where patient_id = "+pat.getPatientID() +" AND voided=0";
//					//logToConsole("\n Creating Select statement...");
//					ResultSet rs = stmt.executeQuery(sql);
//					//STEP 5: Extract data from result set
//
//
//					while (rs.next()) {
//
//						Encounter encounter = new Encounter();
//						encounter.setEncounterDate(rs.getDate("encounter_datetime"));
//							if (rs.getInt("form_id") == 1) {
//								if(isAdult(pat)) {
//									encounter.setFormTypeId(22);
//									encounter.setEncounterTypeId(8);
//								}else{
//									encounter.setFormTypeId(20);
//									encounter.setEncounterTypeId(8);
//								}
//							}
//							else if (rs.getInt("form_id") == 56 ||
//									rs.getInt("form_id") == 70 ||
//									rs.getInt("form_id") == 71 ||
//									rs.getInt("form_id") == 47 ||
//									rs.getInt("form_id") == 72) {
//								encounter.setFormTypeId(14);
//								encounter.setEncounterTypeId(12);
//							}
//							else if (rs.getInt("form_id") == 28 ||  rs.getInt("form_id") == 19
//									||  rs.getInt("form_id") == 65) {
//								encounter.setFormTypeId(23);
//								encounter.setEncounterTypeId(14);
//							}
//							else if (rs.getInt("form_id") == 46 || rs.getInt("form_id") == 53) {
//								encounter.setFormTypeId(27);
//								encounter.setEncounterTypeId(13);
//							}
//							else if (rs.getInt("form_id") == 12) {
//								encounter.setFormTypeId(15);
//								encounter.setEncounterTypeId(16);
//							}
//							else if (rs.getInt("form_id") == 67) {
//								encounter.setFormTypeId(21);
//								encounter.setEncounterTypeId(11);
//							}
//							else {
//								encounter.setFormTypeId(rs.getInt("form_id"));
//								encounter.setEncounterTypeId(rs.getInt("encounter_type"));
//							}
//						    encounter.setEncounterLocationId(facilityCombo.getSelectionModel().getSelectedItem().getLocationID());
//
//						encounters.add(encounter);
//					}
//					pat.setEncounters(encounters);
//				}
//				catch(SQLException se){
//					//Handle errors for JDBC
//					se.printStackTrace();
//					System.out.println("\n SQLException Error: " + se.getMessage());
//				}
//				catch(Exception e){
//					//Handle errors for Class.forName
//					e.printStackTrace();
//					System.out.println("\n Exception Error: " + e.getMessage());
//				}
//				finally{
//					//finally block used to close resources
//					try {
//						if (stmt != null)
//							conn.close();
//					}
//					catch (SQLException se) {
//					}// do nothing
//					closeConnection(conn);
//				}//end try
//			}
//				Platform.runLater(() -> {
//					System.out.println(allPatient);
//				});
//				return encounters;
//			}
//		};
//	}
//
//	@FXML
//	private void getLgas(){
//		buildLocation("lgas", stateCombo.getSelectionModel().getSelectedItem().getLocationID());
//	}
//
//	@FXML
//	private void getFacilities(){
//		getDefaults();
//		getFacility();
//		facilityCombo.getSelectionModel().select(defaultLocation);
//	}
//
//	private void getFacility(){
//
//		Connection conn = null;
//		Statement stmt = null;
//		Connection conn2 = null;
//		Statement stmt2 = null;
//
//		ObservableList<Location> locationMap = FXCollections.observableArrayList();
//
//		try {
//			//STEP 2: Register JDBC driver
//			Class.forName("com.mysql.jdbc.Driver");
//
//			//STEP 3: Open a connection
//			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//			//logToConsole("\n Connected to database successfully...");
//
//			//STEP 4: Execute a query
//			stmt = conn.createStatement();
//
//
//			String sql = "SELECT * FROM location where state_province = '"+stateCombo.getSelectionModel().getSelectedItem().getLocation()+"'";
//			//logToConsole("\n Creating Select statement...");
//			ResultSet rs = stmt.executeQuery(sql);
//			//STEP 5: Extract data from result set
//
//			while (rs.next()) {
//				if(rs.getString("name").equals(defaultLocation.getLocation())){
//					defaultLocation.setLocationID(rs.getInt("location_id"));
//				}
//				locationMap.add(new Location(rs.getInt("location_id"),rs.getString("name")));
//			}
//		}
//		catch(SQLException se){
//			//Handle errors for JDBC
//			se.printStackTrace();
//			System.out.println("\n SQLException Error: " + se.getMessage());
//		}
//		catch(Exception e){
//			//Handle errors for Class.forName
//			e.printStackTrace();
//			System.out.println("\n Exception Error: " + e.getMessage());
//		}
//		finally{
//			//finally block used to close resources
//			try {
//				if (stmt != null)
//					conn.close();
//			}
//			catch (SQLException se) {
//			}// do nothing
//			closeConnection(conn);
//		}//end try
//		Comparator<Location> comparator = Comparator.comparing(Location::getLocation);
//		FXCollections.sort(locationMap,comparator);
//		facilityCombo.setItems(locationMap);
//
//	}
//
//
//	private void getDefaults(){
//
//		Connection conn = null;
//		Statement stmt = null;
//		Connection conn2 = null;
//		Statement stmt2 = null;
//
//		try {
//			//STEP 2: Register JDBC driver
//			Class.forName("com.mysql.jdbc.Driver");
//
//			//STEP 3: Open a connection
//			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
//			//logToConsole("\n Connected to database successfully...");
//
//			//STEP 4: Execute a query
//			stmt = conn.createStatement();
//
//
//			String sql = "SELECT * FROM global_property where property = 'default_location' || property = 'datim_code'";
//			//logToConsole("\n Creating Select statement...");
//			ResultSet rs = stmt.executeQuery(sql);
//			//STEP 5: Extract data from result set
//
//			while (rs.next()) {
//				if(rs.getString("property").equals("default_location")){
//					defaultLocation = new Location();
//					defaultLocation.setLocation(rs.getString("property_value"));
//				}else if(rs.getString("property").equals("datim_code")){
//					datimCode.setText(rs.getString("property_value"));
//				}
//			}
//		}
//		catch(SQLException se){
//			//Handle errors for JDBC
//			se.printStackTrace();
//			System.out.println("\n SQLException Error: " + se.getMessage());
//		}
//		catch(Exception e){
//			//Handle errors for Class.forName
//			e.printStackTrace();
//			System.out.println("\n Exception Error: " + e.getMessage());
//		}
//		finally{
//			//finally block used to close resources
//			try {
//				if (stmt != null)
//					conn.close();
//			}
//			catch (SQLException se) {
//			}// do nothing
//			closeConnection(conn);
//		}//end try
//
//	}
//
//	private void closeConnection(Connection conn) {
//		try {
//			if (conn != null)
//				conn.close();
//		} catch (SQLException se) {
//			se.printStackTrace();
//			System.out.println("\n Error: " + se.getMessage());
//		}//end finally try
//	}
//
//	private void restConsummer(){
//		String restUrl="https://myApp.com/api/v1/json";
//		String username="myusername";
//		String password="mypassword";
//        for(Patient patient : allPatient) {
//	        JSONObject user = new JSONObject(patient);
//
//	        String jsonData = user.toString();
//
//	        HttpPost httpPost = createConnectivity(restUrl, username, password);
//
//	        executeReq(jsonData, httpPost);
//        }
//	}
//
//	HttpPost createConnectivity(String restUrl, String username, String password)
//	{
//		HttpPost post = new HttpPost(restUrl);
//		String auth = new StringBuffer(username).append(":").append(password).toString();
////		byte[] encodedAuth = Base64.getEncoder(auth.getBytes(Charset.forName("US-ASCII")));
//		String authHeader = "Basic " + auth;
//		post.setHeader("AUTHORIZATION", authHeader);
//		post.setHeader("Content-Type", "application/json");
//		post.setHeader("Accept", "application/json");
//		post.setHeader("X-Stream" , "true");
//		return post;
//	}
//
//	void executeReq(String jsonData, HttpPost httpPost)
//	{
//		try{
//			executeHttpRequest(jsonData, httpPost);
//		}
//		catch (UnsupportedEncodingException e){
//			System.out.println("error while encoding api url : "+e);
//		}
//		catch (IOException e){
//			System.out.println("ioException occured while sending http request : "+e);
//		}
//		catch(Exception e){
//			System.out.println("exception occured while sending http request : "+e);
//		}
//		finally{
//			httpPost.releaseConnection();
//		}
//	}
//
//	void executeHttpRequest(String jsonData,  HttpPost httpPost)  throws UnsupportedEncodingException, IOException
//	{
//		HttpResponse response=null;
//		String line = "";
//		StringBuffer result = new StringBuffer();
//		httpPost.setEntity(new StringEntity(jsonData));
//		HttpClient client = HttpClientBuilder.create().build();
//		response = client.execute(httpPost);
//		System.out.println("Post parameters : " + jsonData );
//		System.out.println("Response Code : " +response.getStatusLine().getStatusCode());
//		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//		while ((line = reader.readLine()) != null){ result.append(line); }
//		System.out.println(result.toString());
//	}
//
//
//}
