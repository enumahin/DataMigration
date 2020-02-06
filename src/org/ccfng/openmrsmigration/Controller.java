package org.ccfng.openmrsmigration;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.ccfng.datamigration.filepaths.ConceptMap;
import org.ccfng.datamigration.patientidentifier.PatientIdentifier;
import org.ccfng.datamigration.patientprogram.PatientProgram;
import org.ccfng.datamigration.person.Person;
import org.ccfng.datamigration.personaddress.PersonAddress;
import org.ccfng.datamigration.personattribute.PersonAttribute;
import org.ccfng.datamigration.personname.PersonName;
import org.ccfng.global.ConnectionClass;
import org.ccfng.global.DestinationConnectionClass;
import org.ccfng.global.KeyValueClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

	HashMap<Integer, Integer> concepts = new HashMap<>();

	ConnectionClass cc ;

	DestinationConnectionClass dd ;

	@FXML
	private TextArea console;

	@FXML
	private Button btnMigrate;

	@FXML
	private Label currentCount;

	@FXML
	private Label totalCount;

	@FXML
	private Label totalPatients;
	@FXML
	private Label totalMigrated;
	@FXML
	private Label totalEncounters;
	@FXML
	private Label totalPharmacyEncounters;
	@FXML
	private Label totalCareCardEncounters;
	@FXML
	private Label totalLabEncounters;
	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private ImageView imageView;

	@FXML
	ComboBox<KeyValueClass> cboLocation;

	private DateFormat formatter = new SimpleDateFormat("d-m-yyyy");

	final Integer[] init = { 1 };

	ObservableList<Patient> allPatients = FXCollections.observableArrayList();

	String IDs = "";

	public Controller(){}

	public void initialize(){
		cc = new ConnectionClass();
		dd = new DestinationConnectionClass();
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

		new Thread(this::getLocations).start();

		allPatients = getPatients();
	}

	@FXML
	private void migrate(){

		//######################## Patient Identifier
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Integer total = allPatients.size();
				Platform.runLater(()->{
					currentCount.setText(0+"");
					totalCount.setText(total.toString());
					totalPatients.setText(total.toString());
				});
				Platform.runLater(()->{
					logToConsole("\nBuilding Patient Records...\n");
				});
				//########################### Get All Patients from source EMR
				List<PatientIdentifier> patientIdentifiers = getPatientIdentiFier();
				List<PatientProgram> patientPrograms = getPatientPrograms();
				List<Person> people = getPeople();
				List<PersonAttribute> allPersonAttributes = getPersonAttributes();
				List<PersonName> allPersonNames = getPersonNames();
				List<PersonAddress> allPersonAddress = getPersonAddresses();
				List<Visit> allVisits = getVisits();

				System.out.println("Total Identifier: "+patientIdentifiers.size());

				for(Patient patient : allPatients) {
					patient.setPatientIdentifiers(
							patientIdentifiers.stream().filter(patientIdentifier -> patientIdentifier.getPatient_id().equals(patient.getPatient_id()))
							.collect(Collectors.toList())
					);
					patient.setPatientPrograms(
							patientPrograms.stream().filter(patientProgram -> patientProgram.getPatient_id().equals(patient.getPatient_id()))
									.collect(Collectors.toList())
					);
					patient.setPerson(
							people.stream().filter(person -> person.getPerson_id().equals(patient.getPatient_id()))
									.findFirst().orElse(null)
					);
					patient.setPersonAddresses(
							allPersonAddress.stream().filter(personAddress -> personAddress.getPerson_id().equals(patient.getPatient_id()))
								.collect(Collectors.toList())
							);
					patient.setPersonAttributes(
							allPersonAttributes.stream().filter(personAttribute -> personAttribute.getPerson_id().equals(patient.getPatient_id()))
									.collect(Collectors.toList())
					);
					patient.setPersonNames(
							allPersonNames.stream().filter(personName -> personName.getPerson_id().equals(patient.getPatient_id()))
									.collect(Collectors.toList())
					);
					patient.setVisits(
							allVisits.stream().filter(visit -> visit.getPatient_id().equals(patient.getPatient_id()))
									.collect(Collectors.toList())
					);

					Platform.runLater(()->{
						currentCount.setText(init[0].toString());
					});

					Thread.sleep(100);
					logToConsole(init[0].toString()+"\n");
					updateProgress(init[0], total);
					init[0]++;
					System.out.println(patient.toString());
				}

				return null;
			}
		};

		if(progressBar.progressProperty().isBound()){
			progressBar.progressProperty().unbind();
			progressIndicator.progressProperty().unbind();
		}
		progressBar.progressProperty().bind(task.progressProperty());
		progressIndicator.progressProperty().bind(task.progressProperty());

		new Thread(task).start();

	}

	private List<PatientProgram> getPatientPrograms() {

		List<PatientProgram> allPatientPrograms = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql =  "SELECT * FROM patient_program where patient_id IN ("+IDs+")";
			logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				PatientProgram patientProgram = new PatientProgram();
				patientProgram.setPatient_program_id(rs.getInt("patient_program_id"));
					if(rs.getInt("program_id") == 3){
						patientProgram.setProgram_id(1);
					}else if(rs.getInt("program_id") == 1){
						patientProgram.setProgram_id(2);
					}else if(rs.getInt("program_id") == 9){
						patientProgram.setProgram_id(3);
					}else{
						patientProgram.setProgram_id(rs.getInt("program_id"));
					}
				//patientProgram.setOutcome_concept_id(rs.getInt("outcome_concept_id"));
				patientProgram.setLocation_id(2);
				patientProgram.setPatient_id(rs.getInt("patient_id"));
				patientProgram.setUuid(UUID.randomUUID());
				patientProgram.setCreator(1);
				patientProgram.setDate_changed(rs.getDate("date_changed"));
				patientProgram.setDate_created(rs.getDate("date_created"));
				patientProgram.setDate_voided(rs.getDate("date_voided"));
				patientProgram.setVoid_reason(rs.getString("void_reason"));
				patientProgram.setVoided(rs.getBoolean("voided"));
				patientProgram.setDate_completed(rs.getDate("date_completed"));
				patientProgram.setDate_enrolled(rs.getDate("date_enrolled"));
				allPatientPrograms.add(patientProgram);
			}
			rs.close();
			logToConsole("\n Data Successfully Fetched!\n");
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		return allPatientPrograms;
	}

	private List<PersonAddress> getPersonAddresses() {

		List<PersonAddress> allPersonAddresses = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql =  "SELECT * FROM person_address where person_id IN ("+IDs+")";
			logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PersonAddress personAddress = new PersonAddress();
				personAddress.setPerson_address_id(rs.getInt("person_address_id"));
				personAddress.setCity_village(rs.getString("city_village"));
				personAddress.setCountry(rs.getString("country"));
				//personAddress.setStart_date(rs.getDate("start_date"));
				//personAddress.setEnd_date(rs.getDate("end_date"));
				personAddress.setLatitude(rs.getString("latitude"));
				personAddress.setLongitude(rs.getString("longitude"));
				personAddress.setPerson_id(rs.getInt("person_id"));
				personAddress.setPostal_code(rs.getString("postal_code"));
				personAddress.setPreferred(rs.getBoolean("preferred"));
				personAddress.setState_province(rs.getString("state_province"));
				personAddress.setAddress1(rs.getString("address1"));
				personAddress.setAddress2(rs.getString("address2"));
				personAddress.setAddress3(rs.getString("address3"));
				personAddress.setAddress4(rs.getString("address4"));
				personAddress.setAddress5(rs.getString("address5"));
				personAddress.setAddress6(rs.getString("address6"));
                                /*personAddress.setAddress7(rs.getString("address7"));
                                personAddress.setAddress8(rs.getString("address8"));
                                personAddress.setAddress9(rs.getString("address9"));
                                personAddress.setAddress10(rs.getString("address10"));
                                personAddress.setAddress11(rs.getString("address11"));
                                personAddress.setAddress12(rs.getString("address12"));
                                personAddress.setAddress13(rs.getString("address13"));
                                personAddress.setAddress14(rs.getString("address14"));
                                personAddress.setAddress15(rs.getString("address15"));*/
				personAddress.setUuid(UUID.randomUUID());
				personAddress.setCreator(1);
				//personAddress.setDate_changed(rs.getDate("date_changed"));
				personAddress.setDate_created(rs.getDate("date_created"));
				personAddress.setDate_voided(rs.getDate("date_voided"));
				personAddress.setVoid_reason(rs.getString("void_reason"));
				personAddress.setVoided(rs.getBoolean("voided"));

				allPersonAddresses.add(personAddress);
			}
			rs.close();
			logToConsole("\n Data Successfully Fetched!\n");
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		return allPersonAddresses;
	}
	private List<PersonAttribute> getPersonAttributes() {

		List<PersonAttribute> allPersonAttributes = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql =  "SELECT * FROM person_attribute where person_id IN ("+IDs+")";
			logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PersonAttribute personAttribute = new PersonAttribute();
				personAttribute.setPerson_attribute_id(rs.getInt("person_attribute_id"));
				personAttribute.setPerson_id(rs.getInt("person_id"));
				personAttribute.setValue(rs.getString("value"));
				personAttribute.setPerson_attribute_type_id(rs.getInt("person_attribute_type_id"));
				personAttribute.setUuid(UUID.randomUUID());
				personAttribute.setCreator(1);
				personAttribute.setDate_created(rs.getDate("date_created"));
				personAttribute.setDate_voided(rs.getDate("date_voided"));
				personAttribute.setVoid_reason(rs.getString("void_reason"));
				personAttribute.setVoided(rs.getBoolean("voided"));

				allPersonAttributes.add(personAttribute);
			}
			rs.close();
			logToConsole("\n Data Successfully Fetched!\n");
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		return allPersonAttributes;
	}
	private List<PersonName> getPersonNames() {

		List<PersonName> allPersonNames = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql =  "SELECT * FROM person_name where person_id IN ("+IDs+")";
			logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PersonName personName = new PersonName();
				personName.setPerson_name_id(rs.getInt("person_name_id"));
				personName.setPreferred(rs.getBoolean("preferred"));
				personName.setPerson_id(rs.getInt("person_id"));
				personName.setPrefix(rs.getString("prefix"));
				personName.setGiven_name(rs.getString("given_name"));
				personName.setMiddle_name(rs.getString("middle_name"));
				personName.setFamily_name_prefix(rs.getString("family_name_prefix"));
				personName.setFamily_name(rs.getString("family_name"));
				personName.setFamily_name2(rs.getString("family_name2"));
				personName.setFamily_name_suffix(rs.getString("family_name_suffix"));
				personName.setDegree(rs.getString("degree"));
				personName.setUuid(UUID.randomUUID());
				personName.setCreator(1);
				personName.setDate_changed(rs.getDate("date_changed"));
				personName.setDate_created(rs.getDate("date_created"));
				personName.setDate_voided(rs.getDate("date_voided"));
				personName.setVoid_reason(rs.getString("void_reason"));
				personName.setVoided(rs.getBoolean("voided"));

				allPersonNames.add(personName);
			}
			rs.close();
			logToConsole("\n Data Successfully Fetched!\n");
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		return allPersonNames;
	}

	private List<Person> getPeople() {

		List<Person> people = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql =  "SELECT * FROM person where person_id IN ("+IDs+")";
			logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				Person person = new Person();
				person.setPerson_id(rs.getInt("person_id"));
				person.setGender(rs.getString("gender"));
				if (rs.getDate("birthdate") != null)
					person.setBirthdate(rs.getDate("birthdate"));
				person.setBirthdate_estimated(rs.getBoolean("birthdate_estimated"));
				person.setDead(rs.getBoolean("dead"));
				//person.setDeath_date(rs.getDate("death_date"));
				//if(rs.findColumn("deathdate_estimated") <= 0)
				//person.setDeathdate_estimated(rs.getBoolean("deathdate_estimated"));
				//if(rs.findColumn("birthtime") <= 0)
				//person.setBirthtime(rs.getTime("birthtime"));
				//person.setUuid(UUID.randomUUID());
				person.setUuid(UUID.randomUUID());
				person.setCreator(1);
				person.setDate_changed(rs.getDate("date_changed"));
				person.setDate_created(rs.getDate("date_created"));
				//person.setDate_voided(rs.getDate("date_voided"));
				person.setDate_voided(null);
				person.setVoid_reason(rs.getString("void_reason"));
				person.setVoided(rs.getBoolean("voided"));

				people.add(person);
			}
			rs.close();
			logToConsole("\n Data Successfully Fetched!\n");
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		return people;
	}

	private List<Visit> getVisits() {

		List<Visit> allVisits = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql =  "Select patient_id, encounter_datetime from encounter where patient_id IN ("+IDs+") group by encounter_datetime, patient_id";
			logToConsole("\n Creating Select statement...");
			ResultSet rs1 = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			logToConsole("\nCreating Visits");
			while (rs1.next()) {
				//#####################


					try {
						//STEP 2: Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
					}
					catch (Exception exc) {
//						logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
					}
					String INSERT_SQL = "INSERT INTO visit"
							+ "(visit_id, patient_id, visit_type_id, date_started, date_stopped, location_id," +
							" creator, date_created, uuid) " +
							"VALUES ( ?,?,?,?,?,?,?,?,?)";

					try (Connection conn1 = DriverManager
							.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword())) {
						logToConsole("\nConnected");
						conn1.setAutoCommit(false);
						try (PreparedStatement stmt1 = conn1.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
							// Insert sample records
							Visit vs = new Visit();
//							logToConsole("\nQuery Executed");
							vs.setPatient_id(rs1.getInt("patient_id"));
//							logToConsole("\nQuery Executed1");
							vs.setVisit_type_id(1);
//							logToConsole("\nQuery Executed2");
							vs.setDate_started(rs1.getDate("encounter_datetime"));
//							logToConsole("\nQuery Executed3");
							vs.setDate_stopped(rs1.getDate("encounter_datetime"));
//							logToConsole("\nQuery Executed4");
							vs.setLocation_id(cboLocation.getSelectionModel().getSelectedItem().getKey());
//							logToConsole("\nQuery Executed5");
							vs.setCreator(1);
//							logToConsole("\nQuery Executed6");
							vs.setDate_created(rs1.getDate("encounter_datetime"));
//							logToConsole("\nQuery Executed7");
							vs.setUuid(UUID.randomUUID());
//							logToConsole("\nQuery Executed8");
//							logToConsole("\nVS Structured");
							try {
								stmt1.setString(1, null);
								stmt1.setInt(2, vs.getPatient_id());
								stmt1.setInt(3, vs.getVisit_type_id());
								stmt1.setDate(4, java.sql.Date.valueOf(vs.getDate_started().toString()));
								stmt1.setDate(5, java.sql.Date.valueOf(vs.getDate_stopped().toString()));
								stmt1.setInt(6, vs.getLocation_id());
								stmt1.setInt(7, vs.getCreator());
								stmt1.setDate(8, java.sql.Date.valueOf(vs.getDate_created().toString()));
								stmt1.setString(9, vs.getUuid().toString());
//								logToConsole("\nPrepared");
								//Add statement to batch
								stmt1.execute();
//								logToConsole("\nSaved Visit");
								ResultSet rs = stmt1.getGeneratedKeys();
								if (rs.next()) {
									vs.setVisit_id(rs.getInt(1));
								}
								rs.close();
								allVisits.add(vs);
							}
							catch (Exception ex) {
//								logToConsole("\n Error: " + ex.getMessage());
								ex.printStackTrace();
							}
							conn1.commit();
//							logToConsole("\n Created fetched successfully.");
						}
						catch (SQLException e) {
							e.printStackTrace();
							rollbackTransaction(conn1, e);
						}catch (Exception ex){
							ex.printStackTrace();
						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					}

				//#####################
			}
			rs1.close();
			logToConsole("\n Visits Successfully Fetched!\n");
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		return allVisits;
	}

	private List<PatientIdentifier> getPatientIdentiFier() {

		List<PatientIdentifier> allPatientIdentifiers = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql =  "SELECT * FROM patient_identifier where patient_id IN ("+IDs+")";
			logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				PatientIdentifier patientIdentifier = new PatientIdentifier();
				patientIdentifier.setPatient_identifier_id(rs.getInt("patient_identifier_id"));
				patientIdentifier.setPreferred(rs.getBoolean("preferred"));
				if (rs.getInt("identifier_type") == 3 )
					patientIdentifier.setIdentifier_type(5);
				else
					patientIdentifier.setIdentifier_type(rs.getInt("identifier_type"));
				patientIdentifier.setIdentifier(rs.getString("identifier"));
				patientIdentifier.setUuid(UUID.randomUUID());
				patientIdentifier.setCreator(1);
				patientIdentifier.setDate_created(rs.getDate("date_created"));
				patientIdentifier.setDate_voided(rs.getDate("date_voided"));
				patientIdentifier.setLocation_id(cboLocation.getSelectionModel().getSelectedItem().getKey());
				patientIdentifier.setPatient_id(rs.getInt("patient_id"));
				patientIdentifier.setVoid_reason(rs.getString("void_reason"));
				patientIdentifier.setVoided(rs.getBoolean("voided"));

				allPatientIdentifiers.add(patientIdentifier);
			}
			rs.close();
			logToConsole("\n Data Successfully Fetched!\n");
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		return allPatientIdentifiers;
	}

	private void getLocations() {
		ObservableList<KeyValueClass> locations = FXCollections.observableArrayList();

		Platform.runLater(()->{
			logToConsole("#################### CHECKING DESTINATION DATABASE! \n");
		});

		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			Platform.runLater(()->{
				logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
			});
		}
		try (Connection conn = DriverManager
				.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword())) {
			Platform.runLater(()->{
				logToConsole("\n Destination Database connection successful..");
			});

			stmt = conn.createStatement();
			String sql = "SELECT * FROM location ";
			Platform.runLater(()->{
				logToConsole("\n Fetching Locations..");
			});
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				KeyValueClass loc = new KeyValueClass();
				loc.setKey(rs.getInt("location_id"));
				loc.setValue(rs.getString("name"));
				locations.add(loc);
			}
			rs.close();
			Platform.runLater(()->{
				logToConsole("\n Done..");
			});
		} catch (SQLException e) {
			Platform.runLater(()->{
				logToConsole("\n Error: " + e.getMessage());
			});
			e.printStackTrace();
		}finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception se) {
			}// do nothing
		}//end try
		cboLocation.setItems(locations);
	}

	private ObservableList<Patient> getPatients() {
		ObservableList<Patient> patients = FXCollections.observableArrayList();

		Platform.runLater(()->{
			logToConsole("#################### CHECKING DESTINATION DATABASE! \n");
		});

		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			Platform.runLater(()->{
				logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
			});
		}
		try (Connection conn = DriverManager
				.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword())) {
			Platform.runLater(()->{
				logToConsole("\n Destination Database connection successful..");
			});

			stmt = conn.createStatement();
			String sql = "SELECT * FROM patient LEFT JOIN person on patient.patient_id = person.person_id WHERE patient.voided = 0 AND person.voided = 0";
			Platform.runLater(()->{
				logToConsole("\n Fetching Patients..");
			});
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			String result = "";
			while (rs.next()) {
				result += rs.getInt("patient_id")+",";
				Patient patient = new Patient();
				patient.setPatient_id(rs.getInt("patient_id"));
				//patient.setAllergy_status(rs.getString("allergy_status"));
				patient.setCreator(1);
				patient.setDate_changed(rs.getDate("date_changed"));
				patient.setDate_created(rs.getDate("date_created"));
				patient.setDate_voided(rs.getDate("date_voided"));
				patient.setVoid_reason(rs.getString("void_reason"));
				patient.setVoided(rs.getBoolean("voided"));

				patients.add(patient);
			}
			rs.close();
			IDs = Optional.ofNullable(result)
					.filter(sStr -> sStr.length() != 0)
					.map(sStr -> sStr.substring(0, sStr.length() - 1))
					.orElse(result);
			Platform.runLater(()->{
				logToConsole("\n Done..");
			});
		} catch (SQLException e) {
			Platform.runLater(()->{
				logToConsole("\n Error: " + e.getMessage());
			});
			e.printStackTrace();
		}finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception se) {
			}// do nothing
		}//end try
		return patients;
	}

	private static ConceptMap createConceptMap(String[] metadata) {
		Integer openmrs =  Integer.parseInt(metadata[0]);
		Integer nmrs = 0;
		try {
			nmrs = Integer.parseInt(metadata[1]);
		}catch (Exception ex){
		}

		// create and return book of this metadata
		return new ConceptMap(openmrs, nmrs);

	}

	private void closeConnection(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
			logToConsole("\n Error: " + se.getMessage());
		}//end finally try
	}

	public void logToConsole(String text) {
				Task<String> task = new Task<String>() {
					@Override
					protected String call() {
						Platform.runLater(() -> {
							if (text != null)
								console.appendText(text);
						});
						return null;
					}
				};

				new Thread(task).start();
	}

	private void rollbackTransaction(Connection conn, Exception e) {
		if (conn != null) {
			try {
				Platform.runLater(()->{
					logToConsole("\nTransaction is being rolled back: " + e.getMessage());
				});
				conn.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
