package org.ccfng.datamigration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.ccfng.datamigration.encounter.Encounter;
import org.ccfng.datamigration.filepaths.ConceptMap;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.visit.Visit;
import org.ccfng.global.ConnectionClass;
import org.ccfng.global.DestinationConnectionClass;
import org.ccfng.global.KeyValueClass;

public class EncounterObsController {

	HashMap<Integer, Integer> concepts = new HashMap<>();

	ConnectionClass cc ;

	DestinationConnectionClass dd ;

	@FXML
	private Label lblCount;

	@FXML
	private Label lblTotal;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private ComboBox<KeyValueClass> cboEncounter;

	@FXML
	private Button btnMigrate;

	@FXML
	private TextArea txtConsole;

	private Task<ObservableList<Encounter>> encounterTask;

	@FXML
	private void migrate(){
		lblTotal.setText(""+1000);
		Task<ObservableList<Integer>> task  = new Task<ObservableList<Integer>>() {
			int wDone = 0;
			@Override
			protected ObservableList<Integer> call() throws Exception {
				Platform.runLater(()->{
					logToConsole("Starting Loop!");
				});
				for(Integer i = 0; i <= 1000; i++){
					Thread.sleep(1000);
					updateProgress(wDone + 1, 1000);
					wDone++;
					Integer j = wDone;
					Platform.runLater(()->{
						lblCount.setText(String.valueOf(j));
					});

				}
				Platform.runLater(()->{
					logToConsole("Done Looping!");
				});
				return null;
			};
		};
		progressBar.progressProperty().bind(task.progressProperty());
		progressIndicator.progressProperty().bind(task.progressProperty());

		new Thread(task).start();
	}

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

		ObservableList<KeyValueClass> migrationType = FXCollections.observableArrayList();

		migrationType.add(new KeyValueClass(1, "Hiv Enrollment"));
		migrationType.add(new KeyValueClass(2, "Adult Initial Clinical Evaluation"));
		migrationType.add(new KeyValueClass(3, "Pediatric Initial Clinical Evaluation"));
		migrationType.add(new KeyValueClass(4, "Care Card"));
		migrationType.add(new KeyValueClass(5, "Pharmacy"));
		migrationType.add(new KeyValueClass(6, "Client Tracking and Termination"));
		migrationType.add(new KeyValueClass(7, "Lab Order and Result"));
		migrationType.add(new KeyValueClass(8, "Client Intake"));

		cboEncounter.setItems(migrationType);

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

	private Integer createVisit(Integer patientID, Date encounterDate){
		Integer t3d_id = null;
		txtConsole.clear();
		logToConsole("#################### CHECKING DESTINATION DATABASE! \n");
		
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
		}
		String INSERT_SQL = "INSERT INTO visit"
				+ "(visit_id, patient_id, visit_type_id, date_started, date_stopped, location_id," +
				" creator, date_created, uuid) " +
				"VALUES ( ?,?,?,?,?,?,?,?,?)";

		try (Connection conn = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword());) {

			conn.setAutoCommit(false);
			try (PreparedStatement stmt1 = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {
				// Insert sample records
				try {
					stmt1.setString(1, null);
					stmt1.setInt(2, patientID);
					stmt1.setInt(3, 1);
					stmt1.setDate(4, encounterDate);
					stmt1.setDate(5, encounterDate);
					stmt1.setInt(6, 8);
					stmt1.setInt(7, 1);
					stmt1.setDate(8, encounterDate);
					stmt1.setString(9, UUID.randomUUID().toString());

					//Add statement to batch
					stmt1.execute();
					ResultSet rs = stmt1.getGeneratedKeys();
					if (rs.next()){
						t3d_id =rs.getInt(1);
					}
					rs.close();
				}
				catch (Exception ex) {
					logToConsole("\n Error: " + ex.getMessage());
					ex.printStackTrace();
				}
				conn.commit();
				logToConsole("\n Transaction is committed successfully.");
			}
			catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return t3d_id;
	}

	private Integer getVisit(Integer patientID, Date encounterDate) {
		Visit visit = null;
		txtConsole.clear();
		logToConsole("#################### CHECKING DESTINATION DATABASE! \n");
		
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
		}
		try (Connection conn = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword());) {
			logToConsole("\n Destination Database connection successful..");

			stmt = conn.createStatement();
			String sql = "SELECT * FROM visit where patient_id="+patientID +" and "+"date_started='"+encounterDate+"'";
			logToConsole("\n Fetching Visits..");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				visit = new Visit();
				visit.setVisit_id(rs.getInt("visit_id"));
				visit.setPatient_id(rs.getInt("patient_id"));
				visit.setVisit_type_id(1);
				visit.setDate_started(rs.getDate("date_started"));
				visit.setDate_stopped(rs.getDate("date_stopped"));
				visit.setLocation_id(2);
				visit.setUuid(UUID.fromString(rs.getString("uuid")));
				visit.setCreator(1);
				visit.setDate_changed(rs.getDate("date_changed"));
				visit.setDate_created(rs.getDate("date_created"));
				visit.setDate_voided(rs.getDate("date_voided"));
				visit.setVoid_reason(rs.getString("void_reason"));
				visit.setVoided(rs.getBoolean("voided"));

			}
			rs.close();
		} catch (SQLException e) {
			logToConsole("\n Error: " + e.getMessage());
			e.printStackTrace();
		}finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception se) {
			}// do nothing
		}//end try
		
		if(visit != null){
			return visit.getVisit_id();
		}else{
			return createVisit(patientID, encounterDate);
		}
	}

	private List<Obs> getObs(String sql){
		List<Obs> getObs = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery(sql);
			while (rs.next()) {
				try (PrintWriter writer = new PrintWriter("missing_concepts.csv", "UTF-8")) {
					if (concepts.containsKey(rs.getInt("concept_id"))) {
						Obs permObs = new Obs();
						permObs.setUuid(UUID.fromString(rs.getString("uuid")));
						if (rs.getString("accession_number") != null)
							permObs.setAccession_number(rs.getString("accession_number"));

						if (rs.getString("comments") != null)
							permObs.setComments(rs.getString("comments"));

						// DO Concept Mapping here

						if (concepts.containsKey(rs.getInt("concept_id"))) {
							permObs.setConcept_id(concepts.get(rs.getInt("concept_id")));
						} else {
							writer.println(rs.getInt("concept_id"));
							permObs.setConcept_id(rs.getInt("concept_id"));
						}
						permObs.setUuid(UUID.randomUUID());
						permObs.setCreator(1);
						permObs.setDate_created(rs.getDate("date_created"));

						permObs.setEncounter_id(rs.getInt("encounter_id"));
						permObs.setLocation_id(8);
						permObs.setObs_datetime(rs.getDate("obs_datetime"));

						if (rs.getInt("obs_group_id") > 0)
							permObs.setObs_group_id(rs.getInt("obs_group_id"));

						if (rs.getInt("obs_id") > 0)
							permObs.setObs_id(rs.getInt("obs_id"));
						if (rs.getInt("order_id") > 0)
							permObs.setOrder_id(rs.getInt("order_id"));

						permObs.setPerson_id(rs.getInt("person_id"));
						if (null != rs.getString("value_coded")) {
							if (concepts.containsKey(rs.getInt("value_coded"))) {
								permObs.setValue_coded(concepts.get(rs.getInt("value_coded")));
							} else {
								writer.println(rs.getInt("value_coded"));
								permObs.setValue_coded(concepts.get(rs.getInt("value_coded")));
							}
						}
						//permObs.setValue_coded(rs.getInt("value_coded"));
						if (rs.getInt("value_coded_name_id") > 0)
							permObs.setValue_coded_name_id(rs.getInt("value_coded_name_id"));
						if (rs.getString("value_complex") != null)
							permObs.setValue_complex(rs.getString("value_complex"));
						if (rs.getDate("value_datetime") != null)
							permObs.setValue_datetime(rs.getDate("value_datetime"));
						if (rs.getInt("value_drug") > 0)
							permObs.setValue_drug(rs.getInt("value_drug"));
						if (rs.getInt("value_group_id") > 0)
							permObs.setValue_group_id(rs.getInt("value_group_id"));
						if (rs.getString("value_modifier") != null)
							permObs.setValue_modifier(rs.getString("value_modifier"));
						if (rs.getDouble("value_numeric") != 0)
							permObs.setValue_numeric(rs.getDouble("value_numeric"));
						if (rs.getString("value_text") != null)
							permObs.setValue_text(rs.getString("value_text"));
						if (rs.getInt("voided_by") > 0)
							permObs.setVoided_by(rs.getInt("voided_by"));
						getObs.add(permObs);
					}else{
						writer.println(rs.getInt("concept_id"));
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
			logToConsole("\n SQLException Error: " + se.getMessage());
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
			logToConsole("\n Exception Error: " + e.getMessage());
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try

		return getObs;
	}

	private void verseEncounter(String sql, int form_id, int encounter_type, String obsSQL){

		ObservableList<Encounter> allEncounters = FXCollections.observableArrayList();
		//
		Connection conn1 = null;
		Statement stmt1 = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn1 = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			logToConsole("\n Connected to database successfully...");

			stmt1 = conn1.createStatement();


			logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt1.executeQuery(sql);
			//STEP 5: Extract data from result set
			logToConsole("\n Fetching Encounters....");
			while (rs.next()) {
				//Retrieve by column name
				Integer vID = getVisit(rs.getInt("patient_id"), rs.getDate("encounter_datetime"));
				Encounter encounter = new Encounter();
				encounter.setEncounter_id(rs.getInt("encounter_id"));
				encounter.setEncounter_datetime(rs.getDate("encounter_datetime"));
				encounter.setForm_id(form_id);
				encounter.setEncounter_type(encounter_type);
				encounter.setUuid(UUID.randomUUID());
				encounter.setCreator(1);
				encounter.setDate_changed(rs.getDate("date_changed"));
				encounter.setDate_created(rs.getDate("date_created"));
				encounter.setLocation_id(8);
				encounter.setPatient_id(rs.getInt("patient_id"));
				encounter.setVisit_id(vID);
				allEncounters.add(encounter);
			}
			rs.close();
			logToConsole("\n Encounters Successfully Fetched!");
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
				if (stmt1 != null)
					conn1.close();
			} catch (SQLException se) {
			}// do nothing
			closeConnection(conn1);
		}//end try
		logToConsole("\n Building Observations!");
//		String obsSQL = ;
		List<Obs> encOb = getObs(obsSQL);
		System.out.println(encOb);
		if(encOb.size() > 0){
			logToConsole("\n OBS Size: "+encOb.size());
			Platform.runLater(()->{
				lblTotal.setText(""+allEncounters.size());
			});
			ObservableList<Encounter> listEnc = FXCollections.observableArrayList();
			List<Obs> collectedObs = new ArrayList<>();
			encounterTask = new Task<ObservableList<Encounter>>() {

				@Override
				protected ObservableList<Encounter> call() throws Exception {

					String INSERT_SQL = "INSERT INTO encounter"
							+ "(encounter_id, encounter_type, patient_id, location_id, form_id, encounter_datetime, creator, date_created, " +
							"date_changed, voided, date_voided, void_reason, uuid, visit_id) " +
							"VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					logToConsole("\n Connecting to destination DB! \n");
					try (Connection conn = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword());) {
						conn.setAutoCommit(false);
						try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
							Integer wDone = 1;
							// Insert sample records

							for (Encounter module : allEncounters) {
								encOb.stream().filter(ob-> module.getPatient_id() == ob.getPerson_id()).forEach(ob -> {
									ob.setEncounter_id(module.getEncounter_id());
									collectedObs.add(ob);
								});

								//								logToConsole("Total number of Obs"+encOb);
								//								logToConsole("Total number of Enc Obs"+encObs);
								//								try {
								stmt.setInt(1, module.getEncounter_id());
								stmt.setInt(2, module.getEncounter_type());
								stmt.setInt(3, module.getPatient_id());
								stmt.setInt(4, module.getLocation_id());
								stmt.setInt(5, module.getForm_id());
								stmt.setDate(6, new java.sql.Date(module.getEncounter_datetime().getTime()));
								stmt.setInt(7, module.getCreator());
								if (module.getDate_created() != null)
									stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
								else
									stmt.setDate(8, null);
								if (module.getDate_changed() != null)
									stmt.setDate(9, new java.sql.Date(module.getDate_changed().getTime()));
								else
									stmt.setDate(9, null);
								stmt.setBoolean(10, module.isVoided());
								if (module.getDate_voided() != null)
									stmt.setDate(11, new java.sql.Date(module.getDate_voided().getTime()));
								else
									stmt.setDate(11, null);
								stmt.setString(12, module.getVoid_reason());
								stmt.setString(13, module.getUuid().toString());
								stmt.setInt(14, module.getVisit_id());

//																	new Thread(new Task<String>(){
//																		@Override
//																		protected String call() throws Exception {
								Platform.runLater(()->{
									lblCount.setText(wDone.toString());
								});

//																			return null;
//																		}
//																	}).start();
								//Add statement to batch
								stmt.addBatch();
								updateProgress(wDone + 1, allEncounters.size());
								Integer pDone = ((wDone + 1) / allEncounters.size()) * 100;

								listEnc.add(module);
							}
							//execute batch()
							//							try {
							stmt.executeBatch();
							logToConsole("Transaction is committed successfully.");
							//							} catch (Exception ex) {
							//								logToConsole("Error in batch insert: " + ex.getMessage());
							//								closeConnection(conn);
							//							}

						} catch (SQLException e) {
							logToConsole("Error running Insert statement: " + e.getMessage());
							e.printStackTrace();
							rollbackTransaction(conn, e);
						}catch (Exception e) {
							logToConsole("Error running Insert statement: " + e.getMessage());
							e.printStackTrace();
							rollbackTransaction(conn, e);
						}
						conn.commit();
					} catch (SQLException e) {
						logToConsole("Error Establishing connection: " + e.getMessage());
						e.printStackTrace();

					}
					return listEnc;
				}
			};

			Platform.runLater(() -> {
				createObs(encOb);
			});

			if(progressBar.progressProperty().isBound()){
				progressBar.progressProperty().unbind();
				progressIndicator.progressProperty().unbind();
			}
			progressBar.progressProperty().bind(encounterTask.progressProperty());
			progressIndicator.progressProperty().bind(encounterTask.progressProperty());

			new Thread(encounterTask).start();
		}else{
			logToConsole("\n No Observation found!");
		}
	}

	public void createHivEnrollmentEncounter(){

		new Thread(()->{
			verseEncounter("SELECT * FROM encounter WHERE encounter_type = 15 and voided = 0 "
				+ "group by patient_id", 23, 14,
				"Select obs.* from obs LEFT JOIN encounter on obs.encounter_id=encounter.encounter_id where encounter.encounter_type=15");
		}).start();


	}
	public void createAdultInitialEncounter(){
		String encSQL = "Select encounter.* from encounter left join person on encounter.patient_id=person.person_id where form_id = 1 and "
				+ "DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 >= 18 and encounter.voided = 0 "
				+ "group by patient_id";
		int form_id= 22;
		int encTYPE= 26;
		String obsSQL = "Select obs.* from obs LEFT JOIN encounter on obs.encounter_id=encounter.encounter_id where (encounter.form_id=1 OR encounter.form_id=24)";
		verseEncounter(encSQL,form_id,encTYPE,obsSQL);
	}
	public void createPedInitialEncounter(){
		String encSQL = "Select encounter.* from encounter left join person on encounter.patient_id=person.person_id where form_id = 1 and "
				+ "DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 < 18 and encounter.voided = 0 "
				+ "group by patient_id";
		int form_id= 20;
		int encTYPE= 24;
		String obsSQL = "Select obs.* from obs LEFT JOIN encounter on obs.encounter_id=encounter.encounter_id where (encounter.form_id=1 OR encounter.form_id=27)";
		verseEncounter(encSQL,form_id,encTYPE,obsSQL);
	}
	public void createCareCardEncounter(){
		String encSQL = "Select * from encounter where (form_id = 56) and voided = 0 "
				+ "group by patient_id";
		int form_id= 14;
		int encTYPE= 12;
		String obsSQL = "Select obs.* from obs LEFT JOIN encounter on obs.encounter_id=encounter.encounter_id where (encounter.form_id=56)";
		verseEncounter(encSQL,form_id,encTYPE,obsSQL);
	}
	public void createPharmacyEncounter(){
		String encSQL = "Select * from encounter where (form_id = 46 || form_id = 53) and voided = 0 "
				+ "group by patient_id";
		int form_id= 27;
		int encTYPE= 13;
		String obsSQL = "Select obs.* from obs LEFT JOIN encounter on obs.encounter_id=encounter.encounter_id where (form_id = 46 || form_id = 53)";
		verseEncounter(encSQL,form_id,encTYPE,obsSQL);
	}
	public void createClientTrackingEncounter(){

	}
	public void createLabEncounter(){
		String encSQL = "Select * from encounter where encounter_type=5 and voided = 0 "
				+ "group by patient_id";
		int form_id= 21;
		int encTYPE= 11;
		String obsSQL = "Select obs.* from obs LEFT JOIN encounter on obs.encounter_id=encounter.encounter_id where encounter_type=5";
		verseEncounter(encSQL,form_id,encTYPE,obsSQL);
	}
	public void createClientIntakeEncounter(){

	}

//	public void createEncounter(List<Encounter> encounters, List<Obs> obs){
//		lblTotal.setText(""+encounters.size());
//		ObservableList<Encounter> listEnc = FXCollections.observableArrayList();
//		encounterTask = new Task<ObservableList<Encounter>>() {
//
//			@Override
//			protected ObservableList<Encounter> call() throws Exception {
//
//					String INSERT_SQL = "INSERT INTO encounter"
//							+ "(encounter_id, encounter_type, patient_id, location_id, form_id, encounter_datetime, creator, date_created, " +
//							"date_changed, voided, date_voided, void_reason, uuid, visit_id) " +
//							"VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//					logToConsole("\n Connecting to destination DB! \n");
//					try (Connection conn = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword());) {
//						conn.setAutoCommit(false);
//						try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
//							int wDone = 0;
//							// Insert sample records
//							for (Encounter module : encounters) {
////								List<Obs> encObs = obs.stream().filter(ob-> module.getPatient_id() == ob.getPerson_id()).collect(Collectors.toList());
//								try {
//									stmt.setInt(1, module.getEncounter_id());
//									stmt.setInt(2, module.getEncounter_type());
//									stmt.setInt(3, module.getPatient_id());
//									stmt.setInt(4, module.getLocation_id());
//									stmt.setInt(5, module.getForm_id());
//									stmt.setDate(6, new java.sql.Date(module.getEncounter_datetime().getTime()));
//									stmt.setInt(7, module.getCreator());
//									if (module.getDate_created() != null)
//										stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
//									else
//										stmt.setDate(8, null);
//									if (module.getDate_changed() != null)
//										stmt.setDate(9, new java.sql.Date(module.getDate_changed().getTime()));
//									else
//										stmt.setDate(9, null);
//									stmt.setBoolean(10, module.isVoided());
//									if (module.getDate_voided() != null)
//										stmt.setDate(11, new java.sql.Date(module.getDate_voided().getTime()));
//									else
//										stmt.setDate(11, null);
//									stmt.setString(12, module.getVoid_reason());
//									stmt.setString(13, module.getUuid().toString());
//									stmt.setInt(14, module.getVisit_id());
////									lblCount.setText(""+wDone);
//									//Add statement to batch
//									stmt.addBatch();
//									updateProgress(wDone + 1, encounters.size());
//									Integer pDone = ((wDone + 1) / encounters.size()) * 100;
//									wDone++;
//									//##################################### INSERT OBSERVATIONS
//
////									Obs currentObs = new Obs();
////
////									String OBS_INSERT_SQL = "INSERT INTO obs"
////											+ "(person_id, concept_id, encounter_id, order_id, obs_datetime, location_id," +
////											"accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, " +
////											"value_numeric, value_modifier, value_text, value_complex, comments," +
////											"creator, date_created, voided, date_voided, void_reason, uuid, form_namespace_and_path, obs_group_id,obs_id) " +
////											"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
////
////									try (Connection conn_obs = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword());) {
////
////										conn_obs.setAutoCommit(false);
////										try (PreparedStatement stmt_obs = conn_obs.prepareStatement(OBS_INSERT_SQL);) {
////
////											for (Obs ob : encObs) {
////												///try {
//////												currentObs = module;
////												stmt_obs.setInt(26, ob.getObs_id());
////												if(ob.getObs_group_id() != null)
////													stmt_obs.setInt(25, ob.getObs_group_id());
////												else
////													stmt_obs.setString(25, null);
////												stmt_obs.setInt(1, ob.getPerson_id());
////												stmt_obs.setInt(2, ob.getConcept_id());
////												stmt_obs.setInt(3, ob.getEncounter_id());
////												if(ob.getOrder_id() != null)
////													stmt_obs.setInt(4, ob.getOrder_id());
////												else
////													stmt_obs.setString(4, null);
////												if (ob.getObs_datetime() != null)
////													stmt_obs.setDate(5, new java.sql.Date(ob.getObs_datetime().getTime()));
////												else
////													stmt_obs.setDate(5, null);
////												if (ob.getLocation_id() >= 0)
////													stmt_obs.setInt(6, ob.getLocation_id());
////												else
////													stmt_obs.setInt(6, 0);
////												if(ob.getAccession_number() != null)
////													stmt_obs.setString(7, ob.getAccession_number());
////												else
////													stmt_obs.setString(7, null);
////												if(ob.getValue_group_id() != null)
////													stmt_obs.setInt(8, ob.getValue_group_id());
////												else
////													stmt_obs.setString(8, null);
////												if(ob.getValue_coded() != null)
////													stmt_obs.setInt(9, ob.getValue_coded());
////												else
////													stmt_obs.setString(9, null);
////												if(ob.getValue_coded_name_id() != null)
////													stmt_obs.setInt(10, ob.getValue_coded_name_id());
////												else
////													stmt_obs.setString(10, null);
////												if(ob.getValue_drug() != null)
////													stmt_obs.setInt(11, ob.getValue_drug());
////												else
////													stmt_obs.setString(11, null);
////												if (ob.getValue_datetime() != null)
////													stmt_obs.setDate(12, new java.sql.Date(ob.getValue_datetime().getTime()));
////												else
////													stmt_obs.setDate(12, null);
////												if(ob.getValue_numeric() != null)
////													stmt_obs.setDouble(13, ob.getValue_numeric());
////												else
////													stmt_obs.setString(13, null);
////												if(ob.getValue_modifier() != null)
////													stmt_obs.setString(14, ob.getValue_modifier());
////												else
////													stmt_obs.setString(14, null);
////												if(ob.getValue_text() != null)
////													stmt_obs.setString(15, ob.getValue_text());
////												else
////													stmt_obs.setString(15, null);
////												if(ob.getValue_complex() != null)
////													stmt_obs.setString(16, ob.getValue_complex());
////												else
////													stmt_obs.setString(16, null);
////												if(ob.getComments() != null)
////													stmt_obs.setString(17, ob.getComments());
////												else
////													stmt_obs.setString(17, null);
////												stmt_obs.setInt(18, ob.getCreator());
////												if (ob.getDate_created() != null)
////													stmt_obs.setDate(19, new java.sql.Date(ob.getDate_created().getTime()));
////												else
////													stmt_obs.setDate(19, null);
////												stmt_obs.setBoolean(20, ob.isVoided());
////												if (ob.getDate_voided() != null)
////													stmt_obs.setDate(21, new java.sql.Date(ob.getDate_voided().getTime()));
////												else
////													stmt_obs.setDate(21, null);
////												if(ob.getVoid_reason() != null)
////													stmt_obs.setString(22, ob.getVoid_reason());
////												else
////													stmt_obs.setString(22, null);
////												stmt_obs.setString(23, ob.getUuid().toString());
////												//stmt_obs.setInt(24, ob.getPrevious_version());
////												if(ob.getForm_namespace_and_path() != null)
////													stmt_obs.setString(24, ob.getForm_namespace_and_path());
////												else
////													stmt_obs.setString(24, null);
////												//Add statement to batch
////												stmt_obs.addBatch();
////											}
////											//execute batch
////											stmt_obs.executeBatch();
////											conn_obs.commit();
////											logToConsole("\nObs Inserted.");
////										} catch (SQLException e) {
////											logToConsole("\n"+e.getMessage());
////											e.printStackTrace();
////											rollbackTransaction(conn, e);
////										}
////									} catch (SQLException e) {
////										logToConsole("\n"+e.getMessage());
////										e.printStackTrace();
////									}
//								} catch (Exception ex) {
//									StringWriter errors = new StringWriter();
//									ex.printStackTrace(new PrintWriter(errors));
//									logToConsole("\n"+errors.toString());
//									ex.printStackTrace();
////									return null;
//								}
//								listEnc.add(module);
//							}
//							//execute batch
//							try {
//								stmt.executeBatch();
//								conn.commit();
//								logToConsole("Transaction is committed successfully.");
//							} catch (Exception ex) {
//								logToConsole("Error in batch insert: " + ex.getMessage());
//								closeConnection(conn);
//							}
//						} catch (SQLException e) {
//							logToConsole("Error running Insert statement: " + e.getMessage());
//							e.printStackTrace();
//							rollbackTransaction(conn, e);
//						}
//					} catch (SQLException e) {
//						logToConsole("Error Establishing connection: " + e.getMessage());
//						e.printStackTrace();
//
//					}
//				return listEnc;
//			}
//		};
//
//		Platform.runLater(() -> {
//
//		});
//
//		progressBar.progressProperty().bind(encounterTask.progressProperty());
//		progressIndicator.progressProperty().bind(encounterTask.progressProperty());
//	}

	public void createObs(List<Obs> encOb){

		Obs currentObs = new Obs();

		String OBS_INSERT_SQL = "INSERT INTO obs"
				+ "(person_id, concept_id, encounter_id, order_id, obs_datetime, location_id," +
				"accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, " +
				"value_numeric, value_modifier, value_text, value_complex, comments," +
				"creator, date_created, voided, date_voided, void_reason, uuid, form_namespace_and_path, obs_group_id,obs_id) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	try (Connection conn = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword());) {

		conn.setAutoCommit(false);
		try (PreparedStatement stmt_obs = conn.prepareStatement(OBS_INSERT_SQL);) {

			for (Obs ob : encOb) {
					///try {
					//												currentObs = module;
					stmt_obs.setInt(26, ob.getObs_id());
					if (ob.getObs_group_id() != null)
						stmt_obs.setInt(25,
								ob.getObs_group_id());
					else
						stmt_obs.setString(25, null);
					stmt_obs.setInt(1, ob.getPerson_id());
					stmt_obs.setInt(2, ob.getConcept_id());
					stmt_obs.setInt(3,
							ob.getEncounter_id());
					if (ob.getOrder_id() != null)
						stmt_obs.setInt(4, ob.getOrder_id());
					else
						stmt_obs.setString(4, null);
					if (ob.getObs_datetime() != null)
						stmt_obs.setDate(5,
								new java.sql.Date(
										ob.getObs_datetime()
												.getTime()));
					else
						stmt_obs.setDate(5, null);
					if (ob.getLocation_id() >= 0)
						stmt_obs.setInt(6,
								ob.getLocation_id());
					else
						stmt_obs.setInt(6, 0);
					if (ob.getAccession_number() != null)
						stmt_obs.setString(7,
								ob.getAccession_number());
					else
						stmt_obs.setString(7, null);
					if (ob.getValue_group_id() != null)
						stmt_obs.setInt(8,
								ob.getValue_group_id());
					else
						stmt_obs.setString(8, null);
					if (ob.getValue_coded() != null)
						stmt_obs.setInt(9,
								ob.getValue_coded());
					else
						stmt_obs.setString(9, null);
					if (ob.getValue_coded_name_id() != null)
						stmt_obs.setInt(10,
								ob.getValue_coded_name_id());
					else
						stmt_obs.setString(10, null);
					if (ob.getValue_drug() != null)
						stmt_obs.setInt(11,
								ob.getValue_drug());
					else
						stmt_obs.setString(11, null);
					if (ob.getValue_datetime() != null)
						stmt_obs.setDate(12,
								new java.sql.Date(
										ob.getValue_datetime()
												.getTime()));
					else
						stmt_obs.setDate(12, null);
					if (ob.getValue_numeric() != null)
						stmt_obs.setDouble(13,
								ob.getValue_numeric());
					else
						stmt_obs.setString(13, null);
					if (ob.getValue_modifier() != null)
						stmt_obs.setString(14,
								ob.getValue_modifier());
					else
						stmt_obs.setString(14, null);
					if (ob.getValue_text() != null)
						stmt_obs.setString(15,
								ob.getValue_text());
					else
						stmt_obs.setString(15, null);
					if (ob.getValue_complex() != null)
						stmt_obs.setString(16,
								ob.getValue_complex());
					else
						stmt_obs.setString(16, null);
					if (ob.getComments() != null)
						stmt_obs.setString(17,
								ob.getComments());
					else
						stmt_obs.setString(17, null);
					stmt_obs.setInt(18, ob.getCreator());
					if (ob.getDate_created() != null)
						stmt_obs.setDate(19,
								new java.sql.Date(
										ob.getDate_created()
												.getTime()));
					else
						stmt_obs.setDate(19, null);
					stmt_obs.setBoolean(20, ob.isVoided());
					if (ob.getDate_voided() != null)
						stmt_obs.setDate(21,
								new java.sql.Date(
										ob.getDate_voided()
												.getTime()));
					else
						stmt_obs.setDate(21, null);
					if (ob.getVoid_reason() != null)
						stmt_obs.setString(22,
								ob.getVoid_reason());
					else
						stmt_obs.setString(22, null);
					stmt_obs.setString(23,
							ob.getUuid().toString());
					//stmt_obs.setInt(24, ob.getPrevious_version());
					if (ob.getForm_namespace_and_path()
							!= null)
						stmt_obs.setString(24,
								ob.getForm_namespace_and_path());
					else
						stmt_obs.setString(24, null);
					//Add statement to batch
					stmt_obs.addBatch();
//					wDone++;


			}
			//execute batch
			stmt_obs.executeBatch();
			//																				conn_obs.commit();
			logToConsole("\nObs Inserted.");
		} catch (SQLException e) {
			logToConsole("\n"+e.getMessage());
			e.printStackTrace();
			rollbackTransaction(conn, e);
		}catch (Exception xe){
			rollbackTransaction(conn, xe);
			xe.printStackTrace();
			//																				return null;
		}
		conn.commit();
		logToConsole("\nAll Obs successfully Inserted!");
	} catch (SQLException e) {
		logToConsole("Error Establishing connection: " + e.getMessage());
		e.printStackTrace();

	}
	}

	@FXML
	private void migrate1(){
		txtConsole.setText("########################## OpenMRS Patient Data Migration ########################");
			Integer typeID = cboEncounter.getSelectionModel().getSelectedItem().getKey();

			if(typeID != null){
				if(typeID == 1){
					createHivEnrollmentEncounter();
				}else if(typeID == 2){
					createAdultInitialEncounter();
				}else if(typeID == 3){
					createPedInitialEncounter();
				}else if(typeID == 4){
					createCareCardEncounter();
				}else if(typeID == 5){
					createPharmacyEncounter();
				}else if(typeID == 6){
					createClientTrackingEncounter();
				}else if(typeID == 7){
					createLabEncounter();
				}else if(typeID == 8){
					createClientIntakeEncounter();
				}
			}else {
				logToConsole("Please Select Migration Type First!");
			}
	}

	private void rollbackTransaction(Connection conn, Exception e) {
		if (conn != null) {
			try {
				logToConsole("\nTransaction is being rolled back: " + e.getMessage());
				conn.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
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
		Platform.runLater(() -> {
			if (text != null)
				txtConsole.appendText(text);
		});

	}
}
