package org.ccfng.apibasedmigration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ccfng.apibasedmigration.models.Address;
import org.ccfng.apibasedmigration.models.Encounter;
import org.ccfng.apibasedmigration.models.Facility;
import org.ccfng.apibasedmigration.models.Identifier;
import org.ccfng.apibasedmigration.models.Obs;
import org.ccfng.apibasedmigration.models.Patient;
import org.ccfng.apibasedmigration.models.PatientName;
import org.ccfng.apibasedmigration.models.Provider;
import org.ccfng.datamigration.filepaths.ConceptMap;
import org.ccfng.global.ConnectionClass;
import org.ccfng.global.Location;
import org.json.JSONObject;

public class Controller {

	private final String USER_AGENT = "Mozilla/5.0";

	ConnectionClass cc;

	private HashMap<Integer, Integer> concepts =  new HashMap<>();

	@FXML
	private ComboBox<Location> stateCombo;

	@FXML
	private ComboBox<Location> lgaCombo;

	@FXML
	private ComboBox<Location> facilityCombo;

	@FXML
	private TextField datimCode;

	@FXML
	private TextArea console;

	private Location defaultLocation;

	private Task<ObservableList<Patient>> patientTask;

	private Task<Set<Address>> addressTask;

	private Task<Set<PatientName>> patientNameTask;

	private Task<Set<Identifier>> patientIdentifierTask;

	private Task<Set<Encounter>> encounterTask;

	private Task<Set<Obs>> obsTask;

	private Task<Void> migrationTask;

	Set<Address> addresses = new HashSet<>();
	Set<PatientName> patientNames = new HashSet<>();
	Set<Encounter> encounters = new HashSet<>();
	Set<Identifier> identifiers = new HashSet<>();
	Set<Obs> obs = new HashSet<>();

//	@FXML
	private ObservableList<Patient> allPatient = FXCollections.observableArrayList();

	@FXML
	private Label lblStatus;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private ProgressIndicator progressIndicator;

	DateFormat df = new SimpleDateFormat("dd/mm/yyyy");

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void initialize(){
		buildPatient();
		buildPatientAddresses();

		buildPatientName();

		buildPatientIdentifiers();

		buildObs();

		cc = new ConnectionClass();

		Path pathToFile = Paths.get("conceptMapping.csv");
		try (BufferedReader br = Files.newBufferedReader(pathToFile,
				StandardCharsets.US_ASCII)) {

			// read the first line from the text file
			String line = br.readLine();

			// loop until all lines are read
			while (line != null) {

				// use string.split to load a string array with the values from
				// each line of
				// the file, using a comma as the delimiter
				String[] attributes = line.split(",");

				ConceptMap concept = createConceptMap(attributes);

				// adding book into ArrayList
				concepts.put(concept.getOpenmrs(), concept.getNmrs());

				// read next line before looping
				// if end of file reached, line would be null
				line = br.readLine();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		buildLocation("states",133452);

	}

	@FXML
	private void buildData(){
		lblStatus.setText("Extraction Started....");
		new Thread(patientTask).start();
		new Thread(patientNameTask).start();
		new Thread(patientIdentifierTask).start();
		new Thread(addressTask).start();
		new Thread(encounterTask).start();
//		if(progressBar.progressProperty().isBound()) {
//			progressBar.progressProperty().unbind();
//			progressIndicator.progressProperty().unbind();
//		}
//
//		progressBar.progressProperty().bind(obsTask.progressProperty());
//		progressIndicator.progressProperty().bind(obsTask.progressProperty());
//		new Thread(obsTask).start();
	}

	private static ConceptMap createConceptMap(String[] metadata) {
		Integer openmrs =  Integer.parseInt(metadata[0]);
		Integer nmrs = Integer.parseInt(metadata[1]);

		// create and return book of this metadata
		return new ConceptMap(openmrs, nmrs);

	}

	private void buildPatient() {
		allPatient = FXCollections.observableArrayList();
		patientTask = new Task<ObservableList<Patient>>() {
			@Override
			protected ObservableList<Patient> call() throws Exception {
				Connection conn = null;
				Statement stmt = null;
				Connection conn2 = null;
				Statement stmt2 = null;

					try {
						//STEP 2: Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");

						//STEP 3: Open a connection
						conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
						//logToConsole("\n Connected to database successfully...");

						//STEP 4: Execute a query

						stmt = conn.createStatement();

						String sql = "SELECT "
								+ "patient.patient_id AS patientID, person.creator AS creator, person.date_created, person.gender,"
								+ " DATE_FORMAT(person.birthdate,'%d/%m/%Y') AS birthdate, birthdate_estimated, person.dead, "
								+ "DATE_FORMAT(person.death_date,'%d/%m/%Y') AS death_date, person.cause_of_death, "
								+ "(SELECT value_text from obs where concept_id = 7778430 && person_id = patient.patient_id Limit 1) AS phone "
								+ " FROM patient LEFT JOIN person on patient.patient_id = person.person_id "
								+ " where person.voided = 0";
						//logToConsole("\n Creating Select statement...");
						ResultSet rs = stmt.executeQuery(sql);
						//STEP 5: Extract data from result set
						Facility facility = new Facility();
						facility.setState(stateCombo.getSelectionModel().getSelectedItem().getLocation());
						facility.setLga(lgaCombo.getSelectionModel().getSelectedItem().getLocation());
						facility.setFacilityName(facilityCombo.getSelectionModel().getSelectedItem().getLocation());
						facility.setDatimCode(datimCode.getText());
						while (rs.next()) {

							Patient patient = new Patient();
							patient.setPatientID(rs.getInt("patientID"));
							patient.setPhone(rs.getString("phone"));
//							patient.setDateCreated(rs.getDate("date_created"));
							patient.setBirthDate(rs.getString("birthdate"));
							patient.setBirthdateEstimated(rs.getBoolean("birthdate_estimated"));
							patient.setGender(rs.getString("gender"));
							patient.setDead(rs.getBoolean("dead"));
							patient.setCauseOfDeath(rs.getString("cause_of_death"));
							if(null != rs.getString("death_date"))
								patient.setDeathDate(rs.getString("death_date"));
							patient.setFacility(facility);
							allPatient.add(patient);
						}
					}
					catch (SQLException se) {
						//Handle errors for JDBC
						se.printStackTrace();
						logToConsole("\n SQLException Error: " + se.getMessage());
					}
					catch (Exception e) {
						//Handle errors for Class.forName
						e.printStackTrace();
						logToConsole("\n Exception Error: " + e.getMessage());
					}
					finally {
						//finally block used to close resources
						try {
							if (stmt != null)
								conn.close();
						}
						catch (SQLException se) {
						}// do nothing
						closeConnection(conn);
					}//end try
				Platform.runLater(() -> {
//					System.out.println(allPatient);
					logToConsole("\n All Patient Fetched! ");
				});

					return allPatient;
			}
		 };
		}

	private void buildPatientAddresses() {
		logToConsole("\n Processing patient Addresses ");
		addressTask = new Task<Set<Address>>() {

			@Override
			protected Set<Address> call() throws Exception {

//			for (Patient pat : allPatient) {
				Connection conn = null;
				Statement stmt = null;
				Connection conn2 = null;
				Statement stmt2 = null;

				try {
					//STEP 2: Register JDBC driver
					Class.forName("com.mysql.jdbc.Driver");

					//STEP 3: Open a connection
					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
					//logToConsole("\n Connected to database successfully...");

					//STEP 4: Execute a query
					stmt = conn.createStatement();


//						String sql = "SELECT * FROM person_address where person_id = "+pat.getPatientID();
						String sql = "SELECT * FROM person_address";
						//logToConsole("\n Creating Select statement...");
						ResultSet rs = stmt.executeQuery(sql);
						//STEP 5: Extract data from result set

						while (rs.next()) {
	                        Address address = new Address();
							address.setCountry(rs.getString("country"));
							address.setLatitude(rs.getString("latitude"));
							address.setLongitude(rs.getString("longitude"));
							address.setAddress1(rs.getString("address1"));
							address.setAddress2(rs.getString("address2"));
							address.setAddress3(rs.getString("address3"));
							address.setCityVillage(rs.getString("city_village"));
							address.setStateProvince(rs.getString("state_province"));
							address.setPostalCode(rs.getString("postal_code"));
//							address.setPreferred(rs.getBoolean("preferred"));
							address.setPatientID(rs.getInt("person_id"));
							addresses.add(address);
//							pat.setAddress(address);
//							break;
						}
//						pat.setAddress(addresses);
					logToConsole("\n Done Processing patient Addresses, awaiting thread runner... ");
					}
				catch(SQLException se){
						//Handle errors for JDBC
						se.printStackTrace();
						logToConsole("\n \n SQLException Error: " + se.getMessage());
					}
				catch(Exception e){
						//Handle errors for Class.forName
						e.printStackTrace();
						logToConsole("\n \n Exception Error: " + e.getMessage());
					}
				finally{
						//finally block used to close resources
						try {
							if (stmt != null)
								conn.close();
						}
						catch (SQLException se) {
						}// do nothing
						closeConnection(conn);
					}//end try
//				break;
//			}
			   Platform.runLater(()->{
			   	new Thread(patientNameTask).start();
//				   System.out.println(addresses);
				   logToConsole("\n Patient Address Fetched! ");
			   });
			  return addresses;
			}
		};
	}

	private void buildPatientName() {

		patientNameTask = new Task<Set<PatientName>>() {

			@Override
			protected Set<PatientName> call() throws Exception {

//		for (Patient pat : allPatient) {
			Connection conn = null;
			Statement stmt = null;
			Connection conn2 = null;
			Statement stmt2 = null;

			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				//STEP 3: Open a connection
				conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
				//logToConsole("\n Connected to database successfully...");

				//STEP 4: Execute a query
				stmt = conn.createStatement();


//				String sql = "SELECT * FROM person_name where person_id = "+pat.getPatientID();
				String sql = "SELECT * FROM person_name ";
				//logToConsole("\n Creating Select statement...");
				ResultSet rs = stmt.executeQuery(sql);
				//STEP 5: Extract data from result set

				while (rs.next()) {
					PatientName patientName = new PatientName();
					patientName.setPatientID(rs.getInt("person_id"));
					patientName.setFirstName(rs.getString("given_name"));
					patientName.setLastName(rs.getString("family_name"));
					patientName.setMiddleName(rs.getString("middle_name"));
					patientName.setPrefix(rs.getString("prefix"));
//					patientName.setPreferred(rs.getBoolean("preferred"));
					patientNames.add(patientName);
					//###############################################################
//						pat.setGivenName(rs.getString("given_name"));
//						pat.setSurname(rs.getString("family_name"));
//						pat.setMiddleName(rs.getString("middle_name"));
//						pat.setHospitalNo(rs.getString("hospitalNo"));
//						pat.setPreferred(true);
//						pat.setPrefix(rs.getString("prefix"));
					//###############################################################

//					break;
				}
//				pat.setPatientname(patientNames);
			}
			catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				logToConsole("\n \n SQLException Error: " + se.getMessage());
			}
			catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
				logToConsole("\n \n Exception Error: " + e.getMessage());
			}
			finally{
				//finally block used to close resources
				try {
					if (stmt != null)
						conn.close();
				}
				catch (SQLException se) {
				}// do nothing
				closeConnection(conn);
			}//end try
//			break;
//		}
			Platform.runLater(() -> {
//				System.out.println(patientNames);
				logToConsole("\n PatientName Fetched! ");
			});

			return patientNames;
			}
		};
	}

	private void buildLocation(String target, int parentID) {

			Connection conn = null;
			Statement stmt = null;
			Connection conn2 = null;
			Statement stmt2 = null;

			ObservableList<Location> locationMap = FXCollections.observableArrayList();

			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				//STEP 3: Open a connection
				conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
				//logToConsole("\n Connected to database successfully...");

				//STEP 4: Execute a query
				stmt = conn.createStatement();


				String sql = "SELECT * FROM address_hierarchy_entry where parent_id = "+parentID;
				//logToConsole("\n Creating Select statement...");
				ResultSet rs = stmt.executeQuery(sql);
				//STEP 5: Extract data from result set

				while (rs.next()) {
						locationMap.add(new Location(rs.getInt("address_hierarchy_entry_id"),rs.getString("name")));
				}
			}
			catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				logToConsole("\n \n SQLException Error: " + se.getMessage());
			}
			catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
				logToConsole("\n \n Exception Error: " + e.getMessage());
			}
			finally{
				//finally block used to close resources
				try {
					if (stmt != null)
						conn.close();
				}
				catch (SQLException se) {
				}// do nothing
				closeConnection(conn);
			}//end try

		if(target.equals("states")){
			stateCombo.setItems(locationMap);
		}else if(target.equals("lgas")){
			lgaCombo.setItems(locationMap);
		}else if(target.equals("facilities")){
			facilityCombo.setItems(locationMap);
		}
	}

	private void buildPatientIdentifiers() {

		patientIdentifierTask = new Task<Set<Identifier>>() {

			@Override
			protected Set<Identifier> call() throws Exception {

//			for (Patient pat : allPatient) {
				Connection conn = null;
				Statement stmt = null;
				Connection conn2 = null;
				Statement stmt2 = null;

				try {
					//STEP 2: Register JDBC driver
					Class.forName("com.mysql.jdbc.Driver");

					//STEP 3: Open a connection
					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
					//logToConsole("\n Connected to database successfully...");

					//STEP 4: Execute a query
					stmt = conn.createStatement();


					String sql = "SELECT * FROM patient_identifier";
					//logToConsole("\n Creating Select statement...");
					ResultSet rs = stmt.executeQuery(sql);
					//STEP 5: Extract data from result set

					while (rs.next()) {
						if(rs.getInt("identifier_type") == 2 || rs.getInt("identifier_type") == 3 || rs.getInt("identifier_type") == 4) {
							Identifier identifier = new Identifier();
							identifier.setPatientID(rs.getInt("patient_id"));
							identifier.setIdentifier(rs.getString("identifier"));
							switch (rs.getInt("identifier_type")) {
								case 2:
									identifier.setIdentifierType(2);
									break;
								case 3:
									identifier.setIdentifierType(5);
									break;
								case 4:
									identifier.setIdentifierType(4);
									break;
							}
							identifier.setLocationId(rs.getInt("location_id"));
							identifier.setPreferred(rs.getBoolean("preferred"));
							identifiers.add(identifier);
						}
					}
//					pat.setIdentifiers(identifiers);
				}
				catch(SQLException se){
					//Handle errors for JDBC
					se.printStackTrace();
					logToConsole("\n \n SQLException Error: " + se.getMessage());
				}
				catch(Exception e){
					//Handle errors for Class.forName
					e.printStackTrace();
					logToConsole("\n \n Exception Error: " + e.getMessage());
				}
				finally{
					//finally block used to close resources
					try {
						if (stmt != null)
							conn.close();
					}
					catch (SQLException se) {
					}// do nothing
					closeConnection(conn);
				}//end try
//			}
				Platform.runLater(()->{
//					System.out.println(identifiers);
					logToConsole("\n Identifier Fetched! ");
				});
				return identifiers;
			}
		};
	}

	private Boolean isAdult(int patInt){
		return true;
//		Patient pat = allPatient.stream()
//				.filter(pa -> pa.getPatientID() == patInt)
//				.findAny().orElse(null);
//		if(pat != null){
//			if (Duration.between(
//					LocalDate.parse(pat.getBirthDate(), formatter)
//							.atStartOfDay(), LocalDate.now().atStartOfDay()
//			).toDays() >= 18) {
//				logToConsole("\n IS ADAULT!");
//				return true;
//			} else {
//				logToConsole("\n Pediatric ");
//				return false;
//			}
//		}else{
//			return true;
//		}
	}

	private void buildEncounters(){

		encounterTask = new Task<Set<Encounter>>() {

			@Override
			protected Set<Encounter> call() throws Exception {

//			for (Patient pat : allPatient) {
				Connection conn = null;
				Statement stmt = null;

				try {
					//STEP 2: Register JDBC driver
					Class.forName("com.mysql.jdbc.Driver");

					//STEP 3: Open a connection
					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
					//logToConsole("\n Connected to database successfully...");

					//STEP 4: Execute a query
					stmt = conn.createStatement();


					String sql = "SELECT patient_id, encounter_id, DATE_FORMAT(encounter_datetime,'%d/%m/%Y') AS encounter_datetime, form_id, encounter_type FROM encounter ";
					//logToConsole("\n Creating Select statement...");
					ResultSet rs = stmt.executeQuery(sql);
					//STEP 5: Extract data from result set


					while (rs.next()) {

						Encounter encounter = new Encounter();

						Provider provider = new Provider("Admin","Admin","Admin");
						encounter.setProvider(provider);
						encounter.setPatientID(rs.getInt("patient_id"));
						encounter.setEncounterID(rs.getInt("encounter_id"));
						encounter.setEncounterDate(rs.getString("encounter_datetime"));
							if (rs.getInt("form_id") == 1) {
								if(isAdult(rs.getInt("patient_id"))) {
									encounter.setFormTypeId(22);
									encounter.setEncounterTypeId(8);
								}else{
									encounter.setFormTypeId(20);
									encounter.setEncounterTypeId(8);
								}
							}
							else if (rs.getInt("form_id") == 56 ||
									rs.getInt("form_id") == 70 ||
									rs.getInt("form_id") == 71 ||
									rs.getInt("form_id") == 47 ||
									rs.getInt("form_id") == 72) {
								encounter.setFormTypeId(14);
								encounter.setEncounterTypeId(12);
							}
							else if (rs.getInt("form_id") == 28 ||  rs.getInt("form_id") == 19
									||  rs.getInt("form_id") == 65) {
								encounter.setFormTypeId(23);
								encounter.setEncounterTypeId(14);
							}
							else if (rs.getInt("form_id") == 46 || rs.getInt("form_id") == 53) {
								encounter.setFormTypeId(27);
								encounter.setEncounterTypeId(13);
							}
							else if (rs.getInt("form_id") == 12) {
								encounter.setFormTypeId(15);
								encounter.setEncounterTypeId(16);
							}
							else if (rs.getInt("form_id") == 67) {
								encounter.setFormTypeId(21);
								encounter.setEncounterTypeId(11);
							}
							else {
								encounter.setFormTypeId(rs.getInt("form_id"));
								encounter.setEncounterTypeId(rs.getInt("encounter_type"));
							}
						    encounter.setEncounterLocationId(facilityCombo.getSelectionModel().getSelectedItem().getLocationID());

						encounters.add(encounter);
					}
//					pat.setEncounters(encounters);
				}
				catch(SQLException se){
					//Handle errors for JDBC
					se.printStackTrace();
					logToConsole("\n \n SQLException Error: " + se.getMessage());
				}
				catch(Exception e){
					//Handle errors for Class.forName
					e.printStackTrace();
					logToConsole("\n \n Exception Error: " + e.getMessage());
				}
				finally{
					//finally block used to close resources
					try {
						if (stmt != null)
							conn.close();
					}
					catch (SQLException se) {
					}// do nothing
					closeConnection(conn);
				}//end try
//			}
				Platform.runLater(() -> {
					logToConsole("\n Encounter Fetched! ");
//					System.out.println(encounters);

				});
				return encounters;
			}
		};
	}

	private void buildObs(){

		obsTask = new Task<Set<Obs>>() {

			@Override
			protected Set<Obs> call() throws Exception {

//			for (Patient pat : allPatient) {
				try (PrintWriter writer = new PrintWriter("missing_concepts.csv", "UTF-8")) {
				Connection conn = null;
				Statement stmt = null;

				try {
					//STEP 2: Register JDBC driver
					Class.forName("com.mysql.jdbc.Driver");

					//STEP 3: Open a connection
					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
					//logToConsole("\n Connected to database successfully...");

					//STEP 4: Execute a query
					stmt = conn.createStatement();

					String sql = "SELECT concept_id, encounter_id, person_id, obs_id, value_coded, value_numeric, DATE_FORMAT(value_datetime,'%d/%m/%Y') AS value_datetime, "
							+ "value_text, obs_group_id, (select count(*) from obs) As num_row FROM obs";
					//logToConsole("\n Creating Select statement...");
					ResultSet rs = stmt.executeQuery(sql);
					//STEP 5: Extract data from result set
					int wDone = 1;
					while (rs.next()) {
						if (concepts.containsKey(rs.getInt("concept_id"))) {
							Obs ob = new Obs();
							ob.setEcounterID(rs.getInt("encounter_id"));
							ob.setPatientID(rs.getInt("person_id"));
							ob.setObsID(rs.getInt("obs_id"));
							ob.setParent(false);
							ob.setConceptId(concepts.get(rs.getInt("concept_id")));

							if(null != rs.getString("value_coded")){
								ob.setValueTypeId("value_coded");
								if(concepts.containsKey(rs.getInt("value_coded"))) {
									ob.setValue(String.valueOf(rs.getInt("value_coded")));
								}else{
									writer.println(rs.getInt("concept_id"));
									ob.setValue(String.valueOf(rs.getInt("value_coded")));
								}
								ob.setParent(false);
							}else if(null != rs.getString("value_numeric")) {
								ob.setValueTypeId("value_numeric");
								ob.setValue(String.valueOf(rs.getInt("value_numeric")));
								ob.setParent(false);
							}else if(null != rs.getString("value_datetime")) {
								ob.setValueTypeId("value_datetime");
								ob.setValue(rs.getString("value_datetime"));
								ob.setParent(false);
							}else if(null != rs.getString("value_text")) {
								ob.setValueTypeId("value_text");
								ob.setValue(rs.getString("value_text"));
								ob.setParent(false);
							}else{
								ob.setValueTypeId("");
								ob.setValue("");
								ob.setParent(true);
							}
							ob.setObsGroupId(rs.getInt("obs_group_id"));
//							System.out.println(obs);
							obs.add(ob);
						}else{
							writer.println(rs.getInt("concept_id"));
						}
						updateProgress(wDone + 1, rs.getInt("num_row"));
						Integer pDone = ((wDone + 1) / rs.getInt("num_row")) * 100;
						wDone++;
						lblStatus.setText("Loading Obs!");
					}
//					pat.setEncounters(encounters);
					lblStatus.setText("Obs Loaded!");
				}
				catch(SQLException se){
					//Handle errors for JDBC
					se.printStackTrace();
					logToConsole("\n \n SQLException Error: " + se.getMessage());
				}
				catch(Exception e){
					//Handle errors for Class.forName
					e.printStackTrace();
					logToConsole("\n \n Exception Error: " + e.getMessage());
				}
				finally{
					//finally block used to close resources
					try {
						if (stmt != null)
							conn.close();
					}
					catch (SQLException se) {
					}// do nothing
					closeConnection(conn);
				}//end try
				} catch (IOException exc) {
					logToConsole("\n Error writing Configs to file: " + exc.getMessage() + "..... ");
				}
//			}
				Platform.runLater(() -> {
//					processor();
					lblStatus.setText("Data Fetched!");
//					System.out.println(obs);
				});
				return obs;
			}
		};
	}

	@FXML
	private void getLgas(){
		buildLocation("lgas", stateCombo.getSelectionModel().getSelectedItem().getLocationID());
	}

	@FXML
	private void getFacilities(){
		getDefaults();
		getFacility();
		facilityCombo.getSelectionModel().select(defaultLocation);
	}

	private void getFacility(){

		Connection conn = null;
		Statement stmt = null;
		Connection conn2 = null;
		Statement stmt2 = null;

		ObservableList<Location> locationMap = FXCollections.observableArrayList();

		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			//logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query
			stmt = conn.createStatement();


			String sql = "SELECT * FROM location where state_province = '"+stateCombo.getSelectionModel().getSelectedItem().getLocation()+"'";
			//logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set

			while (rs.next()) {
				if(rs.getString("name").equals(defaultLocation.getLocation())){
					defaultLocation.setLocationID(rs.getInt("location_id"));
				}
				locationMap.add(new Location(rs.getInt("location_id"),rs.getString("name")));
			}
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n \n SQLException Error: " + se.getMessage());
		}
		catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n \n Exception Error: " + e.getMessage());
		}
		finally{
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			}
			catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		Comparator<Location> comparator = Comparator.comparing(Location::getLocation);
		FXCollections.sort(locationMap,comparator);
		facilityCombo.setItems(locationMap);

	}

	private void getDefaults(){

		Connection conn = null;
		Statement stmt = null;
		Connection conn2 = null;
		Statement stmt2 = null;

		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			//logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query
			stmt = conn.createStatement();


			String sql = "SELECT * FROM global_property where property = 'default_location' || property = 'datim_code'";
			//logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set

			while (rs.next()) {
				if(rs.getString("property").equals("default_location")){
					defaultLocation = new Location();
					defaultLocation.setLocation(rs.getString("property_value"));
				}else if(rs.getString("property").equals("datim_code")){
					datimCode.setText(rs.getString("property_value"));
				}
			}
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n \n SQLException Error: " + se.getMessage());
		}
		catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n \n Exception Error: " + e.getMessage());
		}
		finally{
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			}
			catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try

		buildEncounters();

	}

	private void closeConnection(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
			logToConsole("\n \n Error: " + se.getMessage());
		}//end finally try
	}

//	private void restConsummer( String restUrl, Patient patient){
//
//			String username="admin";
//			String password="Admin123";
//	        JSONObject user = new JSONObject(patient);
//
//	        String jsonData = user.toString();
//
//			HttpPost httpPost = new HttpPost(restUrl);
//			String auth = new StringBuffer(username).append(":").append(password).toString();
//			String encodedAuth = Base64.getEncoder().encodeToString((auth).getBytes(StandardCharsets.UTF_8));
//			String authHeader = "Basic " + encodedAuth;
//			httpPost.setHeader("AUTHORIZATION", authHeader);
//			httpPost.setHeader("Content-Type", "application/json");
//			httpPost.setHeader("Accept", "application/json");
//			httpPost.setHeader("X-Stream" , "true");
//
//		try (PrintWriter writer = new PrintWriter("migration_log.txt", "UTF-8")) {
//			try{
//				HttpResponse response=null;
//				String line = "";
//				StringBuffer result = new StringBuffer();
//				httpPost.setEntity(new StringEntity(jsonData));
//				HttpClient client = HttpClientBuilder.create().build();
//				response = client.execute(httpPost);
//				logToConsole("\n Post parameters : " + jsonData );
//				writer.println("\n Post parameters : " + jsonData );
//				logToConsole("\n Response Code : " +response.getStatusLine().getStatusCode());
//				writer.println("\n Response Code : " +response.getStatusLine().getStatusCode());
//				writer.println("\n Response Reason : " +response.getStatusLine().getReasonPhrase());
//				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//				while ((line = reader.readLine()) != null){ result.append(line); }
//				   System.out.println(result.toString());
//			}
//			catch (UnsupportedEncodingException e){
//				logToConsole("\n error while encoding api url : "+e);
//				writer.println("\nerror while encoding api url : "+e);
//			}
//			catch (IOException e){
//				logToConsole("\n ioException occured while sending http request : "+e);
//				writer.println("\nioException occured while sending http request : "+e);
//			}
//			catch(Exception e){
//				logToConsole("\n exception occured while sending http request : "+e);
//				writer.println("\nexception occured while sending http request : "+e);
//			}
//			finally{
//				httpPost.releaseConnection();
//			}
//
//		} catch (IOException exc) {
//			logToConsole("\n Error writing Configs to file: " + exc.getMessage() + "..... ");
//		}
//
//
//	}

	public void sendPatient(String url, Patient patient){
		try (PrintWriter writer = new PrintWriter("migration_log.txt", "UTF-8")) {
			HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
			JSONObject jsonPatient = new JSONObject(patient);
			HttpPost request = null;
			try {

				request = new HttpPost(url);
				StringEntity params = new StringEntity(jsonPatient.toString());
				String encodedAuth = Base64.getEncoder().encodeToString(("admin:Admin123").getBytes(StandardCharsets.UTF_8));
				String authHeader = "Basic admin:Admin123";
				request.addHeader("AUTHORIZATION", authHeader);
				request.addHeader("content-type", "application/x-www-form-urlencoded");
				request.addHeader("Accept", "application/json");
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);

				logToConsole("\n Response Code : " + response.getStatusLine().getStatusCode());
				writer.println("\n ################################### PATIENT "+patient.getGivenName() +" "+patient.getSurname()+" ###################################");
				writer.println("\n Response Code : " + response.getStatusLine().getStatusCode());
				writer.println("\n Response Reason : " + response.getStatusLine().getReasonPhrase());

			}
			catch (Exception ex) {

				writer.println("\n "+ ex.getMessage());

			}
			finally {
				if(request != null)
					request.releaseConnection();
			}
		}catch (IOException exc) {
			logToConsole("\n Error writing Configs to file: " + exc.getMessage() + "..... ");
		}
	}

	private Task<ObservableList<Patient>> processedPatient;

//	private void processor(){
//			lblStatus.setText("Processing....");
//		AtomicLong count = new AtomicLong();
//		processedPatient = new Task<ObservableList<Patient>>() {
//
//			@Override
//			protected ObservableList<Patient> call() throws Exception {
////				allPatient.forEach(patient -> {
//				allPatient.stream().findFirst().ifPresent(patient -> {
//					patientNames.stream()
//							.filter(p -> p.getPatientID()
//									.equals(patient.getPatientID()))
//							.findFirst()
//							.ifPresent(p -> {
//								patient.setGivenName(p.getFirstName());
//								patient.setMiddleName(p.getMiddleName());
//								patient.setPrefix(p.getPrefix());
//								patient.setSurname(p.getLastName());
//								patient.setPreferred(true);
//							});
//					addresses.stream().filter(ad -> ad.getPatientID().equals(patient.getPatientID()))
//							.findFirst().ifPresent(paad ->
//								patient.setAddress(new Address(paad.getCountry(), paad.getLatitude(),
//										paad.getLongitude(), paad.getAddress1(), paad.getAddress2(), paad.getAddress3(),
//										paad.getCityVillage(), paad.getStateProvince(), paad.getPostalCode()))
//					);
//
//					identifiers.stream()
//							.filter(id -> id.getPatientID().equals(patient.getPatientID()))
//							.forEach(identifier ->
//								patient.getIdentifiers().add(
//								new Identifier(identifier.getIdentifier(),identifier.getIdentifierType(),
//										identifier.getLocationId(),identifier.isPreferred())
//								));
//
//					encounters.stream().filter(en -> en.getPatientID().equals(patient.getPatientID()))
//							.forEach(encounter ->
//								patient.getEncounters().add(
//								 new Encounter(encounter.getEncounterID(), encounter.getEncounterTypeId(), encounter.getEncounterLocationId(),
//										 encounter.getEncounterDate(),encounter.getFormTypeId())
//								)
//							);
//					List<Obs> pObs = obs.stream().filter(obs1 ->
//						obs1.getPatientID().equals(patient.getPatientID())
//					).collect(Collectors.toList());
//
//					patient.getEncounters().forEach(en ->
//									en.setObs(
//							pObs.stream().filter(obs1 -> obs1.getEcounterID()
//									.equals(en.getEncounterID()) && obs1.getObsGroupId().equals(0))
//									.collect(Collectors.toList()))
//
//					);
//
//					patient.getEncounters().forEach(en ->
//							en.getObs().forEach ( obs1 -> obs1.setObsChildren(
//									pObs.stream().filter(ob -> ob.getEcounterID().equals(obs1.getEcounterID()) &&
//											ob.getObsGroupId().equals(obs1.getObsID()))
//											.collect(Collectors.toList())
//									)
//					));
//
//					//################## Remove Unwanted Fields;
//					patient.setPatientID(null);
//					patient.getAddress().setPatientID(null);
//					patient.setCreator(null);
//					patient.setPreferred(null);
//					patient.getEncounters().forEach(encounter -> {
//						encounter.setEncounterID(null);
//						encounter.setPatientID(null);
//						encounter.getObs().forEach(obs1 -> {
//							obs1.setPatientID(null);
//							obs1.setEcounterID(null);
//							obs1.setObsID(null);
//							obs1.setObsGroupId(null);
//							obs1.getObsChildren().forEach(ch -> {
//								ch.setPatientID(null);
//								ch.setEcounterID(null);
//								ch.setObsID(null);
//								ch.setObsGroupId(null);
//								ch.setObsChildren(null);
//								ch.setParent(null);
//							});
//						});
//					});
//					logToConsole("\n Step is "+count);
//				});
//
//				Platform.runLater(()->{
//					Patient pT = allPatient.stream().findFirst().orElse(null);
//						if(pT != null) {
//							lblStatus.setText("Data Processed!");
//							logToConsole(new JSONObject(pT).toString());
//
//						}
//					});
//				return allPatient;
//			}
//		};
//	}

	public void logToConsole(String text) {
		console.setWrapText(true);
		Platform.runLater(() -> {
			if (text != null)
				console.appendText(text);
		});
	}

		@FXML
	private void processJSON(){

			lblStatus.setText("Processing....");
			AtomicLong count = new AtomicLong();
			Patient patient = allPatient.stream().findFirst().orElse(null);
			if(patient != null) {
				getEncObs(patient.getPatientID());
			}
			processedPatient = new Task<ObservableList<Patient>>() {

				@Override
				protected ObservableList<Patient> call() throws Exception {
					//				allPatient.forEach(patient -> {
//					Patient patient = allPatient.stream().findFirst().orElse(null);
					if(patient != null) {
//						getEncObs(patient.getPatientID());
						//					}
						//					allPatient.stream().findFirst().ifPresent(patient -> {
						patientNames.stream()
								.filter(p -> p.getPatientID()
										.equals(patient.getPatientID()))
								.findFirst()
								.ifPresent(p -> {
									patient.setGivenName(p.getFirstName());
									patient.setMiddleName(p.getMiddleName());
									patient.setPrefix(p.getPrefix());
									patient.setSurname(p.getLastName());
								});
						addresses.stream().filter(ad -> ad.getPatientID().equals(patient.getPatientID()))
								.findFirst().ifPresent(paad ->
								patient.setAddress(new Address(paad.getCountry(), paad.getLatitude(),
										paad.getLongitude(), paad.getAddress1(), paad.getAddress2(), paad.getAddress3(),
										paad.getCityVillage(), paad.getStateProvince(), paad.getPostalCode()))
						);

						identifiers.stream()
								.filter(id -> id.getPatientID().equals(patient.getPatientID()))
								.forEach(identifier ->
										patient.getIdentifiers().add(
												new Identifier(identifier.getIdentifier(), identifier.getIdentifierType(),
														identifier.getLocationId(), identifier.isPreferred())
										));

						List<Obs> pObs = new ArrayList<>();

						patient.setEncounters(
								encounters.stream().filter(en -> en.getPatientID().equals(patient.getPatientID()))
										.collect(Collectors.toSet())
						);

						//						List<Obs> pObs = obs.stream().filter(obs1 ->
						//								obs1.getPatientID().equals(patient.getPatientID())
						//						).collect(Collectors.toList());

						//						patient.getEncounters().forEach(en ->
						//								en.setObs(
						//										pObs.stream().filter(obs1 -> obs1.getEcounterID()
						//												.equals(en.getEncounterID()) && obs1.getObsGroupId().equals(0))
						//												.collect(Collectors.toList()))
						//
						//						);
						patient.getEncounters().forEach(en -> {
									en.setObs(
											obs.stream().filter(obs1 -> obs1.getEcounterID()
													.equals(en.getEncounterID()))
													.collect(Collectors.toList()));
								}

						);

						//						patient.getEncounters().forEach(en ->
						//								en.getObs().forEach ( obs1 -> obs1.setObsChildren(
						//										pObs.stream().filter(ob -> ob.getEcounterID().equals(obs1.getEcounterID()) &&
						//												ob.getObsGroupId().equals(obs1.getObsID()))
						//												.collect(Collectors.toList())
						//										)
						//								));

						patient.getEncounters().forEach(en ->
								en.getObs().forEach(obs1 -> obs1.setObsChildren(
										cObs.stream().filter(ob -> ob.getObsGroupId().equals(obs1.getObsID()))
												.collect(Collectors.toList())
										)
								));

						//################## Remove Unwanted Fields;
												patient.setPatientID(null);
												patient.getAddress().setPatientID(null);
												patient.getEncounters().forEach(encounter -> {
													encounter.getObs().forEach(obs1 -> {
														obs1.setPatientID(null);
														obs1.setEcounterID(null);
														obs1.setObsID(null);
														obs1.setObsGroupId(null);
						//								obs1.setParent("true");
														obs1.getObsChildren().forEach(ch -> {
//															ch.setPatientID(null);
//															ch.setEcounterID(null);
//															ch.setObsID(null);
															ch.setObsGroupId(null);
//															ch.setObsChildren(null);
						//									ch.setParent(null);
														});
													});
													encounter.setEncounterID(null);
													encounter.setPatientID(null);
												});
						logToConsole("\n Step is " + count);
					}
//					});

					Platform.runLater(()->{
						Patient pT = allPatient.stream().findFirst().orElse(null);
						if(pT != null) {
							lblStatus.setText("Data Processed!");
							logToConsole(new JSONObject(pT).toString());

						}
					});
					return allPatient;
				}
			};
			new Thread(processedPatient).start();
	}

	public void getMigrationTask() {

	}

	Thread thread;

	@FXML
	private void migrate(){
		migrationTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				int wDone = 1;
				allPatient.stream().findFirst().ifPresent(patient -> {
					sendPatient("http://localhost:8081/openmrs-standalone/ws/rest/v1/migration", patient);
				});
//				for(Patient patient: allPatient){
////					restConsummer("http://41.76.248.132:8080/openmrs/ws/rest/v1/migration", patient);
//					restConsummer("http://localhost:8081/openmrs-standalone/ws/rest/v1/migration", patient);
//					updateProgress(wDone + 1, allPatient.size());
//					Integer pDone = ((wDone + 1) / allPatient.size()) * 100;
//					wDone++;
//				}
				return null;
			}
		};
		progressBar.progressProperty().unbind();
		progressIndicator.progressProperty().unbind();
		progressBar.progressProperty().bind(migrationTask.progressProperty());
		progressIndicator.progressProperty().bind(migrationTask.progressProperty());
		//getMigrationTask();
		thread = new Thread(migrationTask);
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	private void stopMigration(){
		thread.stop();
		migrationTask.cancel();
		migrationTask = null;
	}

	List<org.ccfng.apibasedmigration.models.external.Obs> cObs;

	private void getEncObs( int enc){
		        cObs = new ArrayList<>();
		        obs = new HashSet<>();
				List<Obs> thisObs = new ArrayList<>();
				try (PrintWriter writer = new PrintWriter("missing_concepts.csv", "UTF-8")) {
					Connection conn = null;
					Statement stmt = null;

					try {
						//STEP 2: Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");

						//STEP 3: Open a connection
						conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
						//logToConsole("\n Connected to database successfully...");

						//STEP 4: Execute a query
						stmt = conn.createStatement();

						String sql = "SELECT concept_id, encounter_id, person_id, obs_id, value_coded, value_numeric, DATE_FORMAT(value_datetime,'%d/%m/%Y') AS value_datetime, "
								+ "value_text, obs_group_id FROM obs where person_id ="+enc;
						//logToConsole("\n Creating Select statement...");
						ResultSet rs = stmt.executeQuery(sql);
						//STEP 5: Extract data from result set
						int wDone = 1;
						while (rs.next()) {
							if (concepts.containsKey(rs.getInt("concept_id"))) {
								if(rs.getInt("obs_group_id") > 0){
									org.ccfng.apibasedmigration.models.external.Obs ob = new org.ccfng.apibasedmigration.models.external.Obs();
									ob.setConceptId(concepts.get(rs.getInt("concept_id")));

									if (null != rs.getString("value_coded")) {
										ob.setValueTypeId("value_coded");
										if (concepts.containsKey(rs.getInt("value_coded"))) {
											ob.setValue(String.valueOf(rs.getInt("value_coded")));
										} else {
											writer.println(rs.getInt("concept_id"));
											ob.setValue(String.valueOf(rs.getInt("value_coded")));
										}
									} else if (null != rs.getString("value_numeric")) {
										ob.setValueTypeId("value_numeric");
										ob.setValue(String.valueOf(rs.getInt("value_numeric")));
									} else if (null != rs.getString("value_datetime")) {
										ob.setValueTypeId("value_datetime");
										ob.setValue(rs.getString("value_datetime"));
									} else if (null != rs.getString("value_text")) {
										ob.setValueTypeId("value_text");
										ob.setValue(rs.getString("value_text"));
									} else {
										ob.setValueTypeId("");
										ob.setValue("");
										
									}
									ob.setObsGroupId(rs.getInt("obs_group_id"));
									System.out.println(ob);
									cObs.add(ob);
									
								}else {

									Obs ob = new Obs();
									ob.setEcounterID(rs.getInt("encounter_id"));
									ob.setPatientID(rs.getInt("person_id"));
									ob.setObsID(rs.getInt("obs_id"));
									ob.setParent(true);
									ob.setConceptId(concepts.get(rs.getInt("concept_id")));
									if (null != rs.getString("value_coded")) {
										ob.setValueTypeId("value_coded");
										if (concepts.containsKey(rs.getInt("value_coded"))) {
											ob.setValue(String.valueOf(rs.getInt("value_coded")));
										} else {
											writer.println(rs.getInt("concept_id"));
											ob.setValue(String.valueOf(rs.getInt("value_coded")));
										}
										ob.setParent(false);
									} else if (null != rs.getString("value_numeric")) {
										ob.setValueTypeId("value_numeric");
										ob.setValue(String.valueOf(rs.getInt("value_numeric")));
										ob.setParent(false);
									} else if (null != rs.getString("value_datetime")) {
										ob.setValueTypeId("value_datetime");
										ob.setValue(rs.getString("value_datetime"));
										ob.setParent(false);
									} else if (null != rs.getString("value_text")) {
										ob.setValueTypeId("value_text");
										ob.setValue(rs.getString("value_text"));
										ob.setParent(false);
									} else {
										ob.setValueTypeId("");
										ob.setValue("");
										ob.setParent(true);
									}
									ob.setObsGroupId(rs.getInt("obs_group_id"));
									System.out.println(ob);
									obs.add(ob);
								}
							}else{
								writer.println(rs.getInt("concept_id"));
							}
						}
						//					pat.setEncounters(encounters);
						lblStatus.setText("Obs Loaded!");
					}
					catch(SQLException se){
						//Handle errors for JDBC
						se.printStackTrace();
						logToConsole("\n \n SQLException Error: " + se.getMessage());
					}
					catch(Exception e){
						//Handle errors for Class.forName
						e.printStackTrace();
						logToConsole("\n \n Exception Error: " + e.getMessage());
					}
					finally{
						//finally block used to close resources
						try {
							if (stmt != null)
								conn.close();
						}
						catch (SQLException se) {
						}// do nothing
						closeConnection(conn);
					}//end try
				} catch (IOException exc) {
					logToConsole("\n Error writing Configs to file: " + exc.getMessage() + "..... ");
				}
				System.out.println(thisObs.toString());

			}

}
