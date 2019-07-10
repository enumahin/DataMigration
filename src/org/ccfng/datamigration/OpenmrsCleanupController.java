package org.ccfng.datamigration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.openmrscleanup.LastCarePharmacy;
import org.ccfng.datamigration.openmrscleanup.PharmacyEncounter;
import org.ccfng.datamigration.openmrscleanup.Regimen;
import org.ccfng.datamigration.session.SessionManager;
import org.joda.time.DateTime;
import org.joda.time.Days;

public class OpenmrsCleanupController {

	@FXML
	public TableView<PharmacyEncounter> pharmacyEncounterTable;

	@FXML
	public TableView<LastCarePharmacy> lastCarePharmacyEncounterTable;

	@FXML
	private TextArea appConsole;

	@FXML
	private ComboBox<Regimen> allRegimenComboBox;

	@FXML
	private ComboBox<Regimen> newRegimenComboBox;

	@FXML
	private DatePicker startDate;

	@FXML
	private Button createDTG;

	private Task<ObservableList<LastCarePharmacy>> lastPharmacyTask;


	public String sourceHost;

	public String sourcePort;

	public String sourceUsername;

	public String sourcePassword;

	public String sourceDb;

	public String tableSuffix;

	String source_jdbcUrl = null;
	String driver = null;
	String source_username = null;
	String source_password = null;
	String dbTYPE = null;
	String suffix = null;
	File file = null;

	DateTimeFormatter formatter;

	public void connectionSettings() {
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	public OpenmrsCleanupController(){
	}

	public void logToConsole(String text) {
		Platform.runLater(() -> {
			if (text != null)
				appConsole.appendText(text);
		});
	}

	public void initialize(){

		pharmacyEncounterTable.setOnMousePressed(event -> {
			if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
				Node node = ((Node) event.getTarget()).getParent();
				TableRow row;
				if (node instanceof TableRow) {
					row = (TableRow) node;
				} else {
					// clicking on text part
					row = (TableRow) node.getParent();
				}
				SessionManager.activeObs = (PharmacyEncounter) row.getItem();
				try {
					Stage stage = new Stage();

					FXMLLoader fxmlLoader = new FXMLLoader();

					Pane root = (Pane) fxmlLoader.load(getClass().getResource("obs_detail.fxml").openStream());

					ObsDetailController obsDetailController = (ObsDetailController) fxmlLoader.getController();

					Scene scene = new Scene(root);

					stage.setScene(scene);
					stage.setTitle("Obs Details");
					stage.setResizable(false);
					stage.alwaysOnTopProperty();
					stage.showAndWait();
					logToConsole("\n"+row.getItem().toString());
				}catch (Exception ex){
					logToConsole(ex.getMessage());
					ex.printStackTrace();
				}
			}
		});

//		lastCarePharmacyEncounterTable.setOnMousePressed(event -> {
//
//			logToConsole("\n Event Captured.");
//			if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
//				Node node = ((Node) event.getTarget()).getParent();
//				TableRow row;
//				if (node instanceof TableRow) {
//					logToConsole("\n Row Captured.");
//					row = (TableRow) node;
//				} else {
//					// clicking on text part
//					logToConsole("\n Column Captured.");
//					row = (TableRow) node.getParent();
//				}
//
//				java.sql.Date fD = getLastPharmacyEncounter((LastCarePharmacy) row.getItem());
//
//				lastCarePharmacyEncounterTable.getSelectionModel()
//						.getSelectedItem()
//						.setLastPharmacyEncounterDate(fD);
//
//				logToConsole("\nLast Pharm Date: "+fD);
//			}
//
//		});

		pharmacyEncounterTable.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE
		);

		lastCarePharmacyEncounterTable.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE
		);

		logToConsole("\n Fetching Database Config!!");
		try (Stream<String> stream = Files.lines(Paths.get("source-db-config.txt"))) {
			List<String> db_files = FXCollections.observableArrayList();
			stream.forEach(db_files::add);
			if (!db_files.isEmpty()) {
				//FilePath.xsdDir = db_files.get(0);
				sourceHost = db_files.get(0);
				sourcePort = db_files.get(1);
				sourceUsername = db_files.get(2);
				sourcePassword = db_files.get(3);
				sourceDb = db_files.get(4);
				source_jdbcUrl = "jdbc:mysql://" + sourceHost + ":" + sourcePort + "/" + sourceDb +
						"?useServerPrepStmts=false&rewriteBatchedStatements=true";
			}
			logToConsole("\n Database Config Fetched!!");
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
			e.printStackTrace();
		}

		//Platform.runLater(() -> {
			getRegimens();
			getLastEncounter();
		//});
	}

	@FXML
	private void getEncounterDetails(){

	}

	@FXML
	private void createDTG(){
		int[] conceptIDS;
		//#### CREATE 3 DTG REGIMENS AND DTG DRUG COMBINATION CONCEPTS
		logToConsole("\n Initializing.!\n");
		int t3d_id = 0;
		int A3d_id = 0;
		int a3d_id = 0;
		int dtg_id = 0;
		int t3d_drug_id = 0;
		int t3d_strenght_id = 0;

		String INSERT_SQL = "INSERT INTO concept"
				+ "(concept_id,datatype_id, class_id, creator, uuid,date_created)" +
				"VALUES (null,?,?,?,?,?)";

		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
			Class.forName("com.mysql.jdbc.Driver");
			logToConsole("\n Connecting..!\n");
			conn.setAutoCommit(false);
			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {

				// Insert sample records
					logToConsole("\n Loading Concept for Regimen TDF-3TC-DTG...!!\n");
					stmt.setInt(1, 4);
					stmt.setInt(2, 11);
					stmt.setInt(3, 1);
					stmt.setString(4, UUID.randomUUID().toString());
					stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
					stmt.execute();
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()){
					t3d_id=rs.getInt(1);
					stmt.execute("INSERT INTO concept_name (concept_id, name, locale, creator, uuid,locale_preferred,concept_name_type,date_created) "
							+ "VALUES("+t3d_id+",'TDF-3TC-DTG','en',1,UUID(),1,'FULLY_SPECIFIED',NOW())");
				}
				rs.close();
				logToConsole("\n TDF-3TC-DTG Concept_Id: "+t3d_id);
			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {
				logToConsole("\n Loading Concept for Regimen ABC-3TC-DTG...!!\n");
				stmt.setInt(1, 4);
				stmt.setInt(2, 11);
				stmt.setInt(3, 1);
				stmt.setString(4, UUID.randomUUID().toString());
				stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
				stmt.execute();
				ResultSet rs1 = stmt.getGeneratedKeys();
				if (rs1.next()){
					A3d_id=rs1.getInt(1);
					stmt.execute("INSERT INTO concept_name (concept_id, name, locale, creator, uuid,locale_preferred,concept_name_type,date_created) "
							+ "VALUES("+A3d_id+",'ABC-3TC-DTG','en',1,UUID(),1,'FULLY_SPECIFIED',NOW())");
				}
				rs1.close();

			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {
				logToConsole("\n Loading Concept for Regimen AZT-3TC-DTG...!!\n");
				stmt.setInt(1, 4);
				stmt.setInt(2, 11);
				stmt.setInt(3, 1);
				stmt.setString(4, UUID.randomUUID().toString());
				stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
				stmt.execute();
				ResultSet rs2 = stmt.getGeneratedKeys();
				if (rs2.next()){
					a3d_id=rs2.getInt(1);
					stmt.execute("INSERT INTO concept_name (concept_id, name, locale, creator, uuid, locale_preferred,concept_name_type,date_created) "
							+ "VALUES("+a3d_id+",'AZT-3TC-DTG','en',1,UUID(),1,'FULLY_SPECIFIED',NOW())");
				}
				rs2.close();

			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {
				logToConsole("\n Loading Concept for Drug DTG...!!\n");
				stmt.setInt(1, 4);
				stmt.setInt(2, 3);
				stmt.setInt(3, 1);
				stmt.setString(4, UUID.randomUUID().toString());
				stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
//				stmt.setInt(5, 1);
				stmt.execute();
				ResultSet rs3 = stmt.getGeneratedKeys();
				if (rs3.next()){
					dtg_id=rs3.getInt(1);
					stmt.execute("INSERT INTO concept_name (concept_id, name, locale, creator, uuid, locale_preferred,concept_name_type,date_created) "
							+ "VALUES("+dtg_id+",'Dolutegravir (DTG)','en',1,UUID(),1,'FULLY_SPECIFIED',NOW())");

//					stmt.execute("INSERT INTO drug (concept_id, name, combination,dose_strength, creator,units, uuid,date_created) "
//							+ "VALUES("+dtg_id+",'Dolutegravir (DTG) 50mg',0,1,1,'mg',UUID(),NOW())");
				}
				rs3.close();
			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {
				logToConsole("\n Loading Concept for Drug TDF/3TC/DTG...!!\n");
				stmt.setInt(1, 4);
				stmt.setInt(2, 3);
				stmt.setInt(3, 1);
				stmt.setString(4, UUID.randomUUID().toString());
				stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
				stmt.execute();
				ResultSet rs4 = stmt.getGeneratedKeys();
				if (rs4.next()){
					t3d_drug_id=rs4.getInt(1);
					stmt.execute("INSERT INTO concept_name (concept_id, name, locale, creator, uuid, locale_preferred,concept_name_type,date_created) "
							+ "VALUES("+t3d_drug_id+",'Tenofovir/Lamivudine/Dolutegravir (TDF/3TC/DTG)','en',1,UUID(),1,'FULLY_SPECIFIED',NOW())");

//					stmt.execute("INSERT INTO drug (concept_id, name, combination,dose_strength, creator,units, uuid,date_created) "
//							+ "VALUES("+dtg_id+",'Tenofovir/Lamivudine/Dolutegravir (TDF/3TC/DTG) 300mg/300mg/50mg',1,1,1,'mg',UUID(),NOW())");
				}
				rs4.close();
			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {
				logToConsole("\n Loading Concept for Strength 300mg/300mg/50mg...!!\n");
				stmt.setInt(1, 10);
				stmt.setInt(2, 5);
				stmt.setInt(3, 1);
				stmt.setString(4, UUID.randomUUID().toString());
				stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
				stmt.execute();
				ResultSet rs5 = stmt.getGeneratedKeys();
				if (rs5.next()){
					t3d_strenght_id=rs5.getInt(1);
					stmt.execute("INSERT INTO concept_name (concept_id, name, locale, creator, uuid, locale_preferred,concept_name_type,date_created) "
							+ "VALUES("+t3d_strenght_id+",'300mg/300mg/50mg','en',1,UUID(),1,'FULLY_SPECIFIED',NOW())");
				}
				rs5.close();
			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS);) {
					stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
							+ "VALUES("+7778108+","+t3d_id+",1,UUID(), NOW())");
					stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
							+ "VALUES("+7778108+","+A3d_id+",1,UUID(), NOW())");
					stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
							+ "VALUES("+7778108+","+a3d_id+",1,UUID(), NOW())");

				stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
						+ "VALUES("+7778472+","+t3d_id+",1,UUID(), NOW())");
				stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
						+ "VALUES("+7778472+","+A3d_id+",1,UUID(), NOW())");
				stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
						+ "VALUES("+7778472+","+a3d_id+",1,UUID(), NOW())");

				stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
						+ "VALUES("+7778355+","+t3d_id+",1,UUID(), NOW())");
				stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
						+ "VALUES("+7778355+","+A3d_id+",1,UUID(), NOW())");
				stmt.execute("INSERT INTO concept_answer (concept_id, answer_concept, creator, uuid, date_created) "
						+ "VALUES("+7778355+","+a3d_id+",1,UUID(), NOW())");

			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}
			String upD = "UPDATE htmlformentry_html_form set xml_data=? where form_id=?";
			try (PreparedStatement stmt = conn.prepareStatement(upD,Statement.RETURN_GENERATED_KEYS);) {
				String text = "";
				try {
					logToConsole("\n Fetching Pharmacy Form..");
					text = new String(Files.readAllBytes(Paths.get("pharmacy_form.txt")));
					logToConsole("\n Replacing Placeholders...");
					String master = text;
					String target1 = "DTGCOMBINATIONCONCEPT";
					String target2 = "DTGCOMBINATIONSTRNGHT";
					String target3 = "DTGCONCEPT";
					String replacement1 = Integer.toString(t3d_drug_id);
					String replacement2 = Integer.toString(t3d_strenght_id);
					String replacement3 = Integer.toString(dtg_id);
					String processed = master.replace(target1, replacement1);
					String processed2 = processed.replace(target2, replacement2);
					String processed3 = processed2.replace(target3, replacement3);

//					text.replaceAll("(?i)(" + target1 + ")(?!([^<]+)?>)", Integer.toString(t3d_drug_id));
//					text.replaceAll("(?i)(" + target2 + ")(?!([^<]+)?>)", Integer.toString(t3d_strenght_id));
//					text.replaceAll("(?i)(" + target3 + ")(?!([^<]+)?>)", Integer.toString(dtg_id));

					logToConsole("\n Updating Pharmacy Form..");
//					stmt.execute("UPDATE htmlformentry_html_form set xml_data='"+processed3+"' where form_id=46");
					stmt.setString(1, processed3);
					stmt.setInt(2,46);
					stmt.execute();


					logToConsole("Concepts Created successfully.");
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}
			try (PreparedStatement stmt = conn.prepareStatement(upD,Statement.RETURN_GENERATED_KEYS);) {
				String text = "";
				try {
					logToConsole("\n Fetching Pediatrics Pharmacy Form..");
					text = new String(Files.readAllBytes(Paths.get("p_pharmacy_form.txt")));
					logToConsole("\n Replacing Placeholders...");
					String master = text;
					String target1 = "DTGCOMBINATIONCONCEPT";
					String target2 = "DTGCOMBINATIONSTRNGHT";
					String target3 = "DTGCONCEPT";
					String replacement1 = Integer.toString(t3d_drug_id);
					String replacement2 = Integer.toString(t3d_strenght_id);
					String replacement3 = Integer.toString(dtg_id);
					String processed = master.replace(target1, replacement1);
					String processed2 = processed.replace(target2, replacement2);
					String processed3 = processed2.replace(target3, replacement3);

					//					text.replaceAll("(?i)(" + target1 + ")(?!([^<]+)?>)", Integer.toString(t3d_drug_id));
					//					text.replaceAll("(?i)(" + target2 + ")(?!([^<]+)?>)", Integer.toString(t3d_strenght_id));
					//					text.replaceAll("(?i)(" + target3 + ")(?!([^<]+)?>)", Integer.toString(dtg_id));

					logToConsole("\n Updating Pharmacy Form..");
					//					stmt.execute("UPDATE htmlformentry_html_form set xml_data='"+processed3+"' where form_id=46");
					stmt.setString(1, processed3);
					stmt.setInt(2,53);
					stmt.execute();


					logToConsole("Pharmacy Form Updated successfully.");
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(upD,Statement.RETURN_GENERATED_KEYS);) {
				String text = "";
				try {
					logToConsole("\n Fetching Care Card Form..");
					text = new String(Files.readAllBytes(Paths.get("care_card_form.txt")));
					logToConsole("\n Replacing Placeholders...");
					String master = text;
					String target1 = "DTG1";
					String target2 = "DTG2";
					String target3 = "DTG3";
					String replacement1 = Integer.toString(t3d_id);
					String replacement2 = Integer.toString(a3d_id);
					String replacement3 = Integer.toString(A3d_id);
					String processed = master.replace(target1, replacement1);
					String processed2 = processed.replace(target2, replacement2);
					String processed3 = processed2.replace(target3, replacement3);

					logToConsole("\n Updating Care Card Form..");
					stmt.setString(1, processed3);
					stmt.setInt(2,1);
					stmt.execute();
					logToConsole("Care Card Form Updated successfully.");
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}

			try (PreparedStatement stmt = conn.prepareStatement(upD,Statement.RETURN_GENERATED_KEYS);) {
				String text = "";
				try {
					logToConsole("\n Fetching Care Card Follow Up Form..");
					text = new String(Files.readAllBytes(Paths.get("care_card_follow_form.txt")));
					logToConsole("\n Replacing Placeholders in Care Card Follow Up Form...");
					String master = text;
					String target1 = "DTG1";
					String target2 = "DTG2";
					String target3 = "DTG3";
					logToConsole("\n Loader.. "+Integer.toString(t3d_id));
					logToConsole("\n Loader.. "+Integer.toString(a3d_id));
					logToConsole("\n Loader.. "+Integer.toString(A3d_id));
					logToConsole("\n Fetching Care Card Follow Up Form..");
					logToConsole("\n Fetching Care Card Follow Up Form..");
					String replacement1 = Integer.toString(t3d_id);
					String replacement2 = Integer.toString(a3d_id);
					String replacement3 = Integer.toString(A3d_id);
					String processed = master.replace(target1, replacement1);
					String processed2 = processed.replace(target2, replacement2);
					String processed3 = processed2.replace(target3, replacement3);
					logToConsole(processed3);


					logToConsole("\n Updating Care Card Follow Up Form..");
					stmt.setString(1, processed3);
					stmt.setInt(2,56);
					stmt.execute();
					int dee = -1000;
					ResultSet rs = stmt.getGeneratedKeys();
					if (rs.next()){
						dee=rs.getInt(1);
					}
					logToConsole("Care Card Follow Up Form Updated successfully. with return message of: "+dee);
				} catch (IOException e) {
					e.printStackTrace();
					logToConsole("Error: "+e.getMessage());
				}catch (Exception ex){
					logToConsole("Error: "+ex.getMessage());
					ex.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}catch (Exception e){
				e.printStackTrace();
				rollbackTransaction(conn, e);
			}
			conn.commit();
		} catch (SQLException e) {
			logToConsole("Error: "+e.getMessage());
			e.printStackTrace();
		}catch (Exception ex){
			logToConsole("Error: "+ex.getMessage());
			ex.printStackTrace();
		}

		//### ADD THE 3 DTG REGIMENS TO FIRST LINE CONCEPT ANSWER

		allRegimenComboBox.getItems().removeAll();
		newRegimenComboBox.getItems().removeAll();
		getRegimens();
		//### UPDATE PHARMACY FORM
	}

	private void getRegimens(){
		allRegimenComboBox.getItems().removeAll();
		newRegimenComboBox.getItems().removeAll();
		//Controller ctrl = new Controller();
		Set<Regimen> regimenSet = new HashSet<>();
		connectionSettings();

		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			logToConsole("\n Connecting to Source Database!!");
			conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);
			logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT concept_id, name from concept_name where concept_id IN "
					+ "(select answer_concept from concept_answer where concept_id IN (7778108,7778109,7778410)) order by name ASC";

			logToConsole("\n Creating Select statement...");

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				Regimen regimen = new Regimen();
				regimen.setConceptID(rs.getInt("concept_id"));
				regimen.setRegimenName(rs.getString("name"));
				regimenSet.add(regimen);
				if(rs.getString("name").indexOf("DTG") != -1){
					createDTG.setDisable(true);
				}
			}
			rs.close();
			logToConsole("\n Data Successfully Fetched!\n");

		} catch (SQLException e) {
			logToConsole("\n Error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logToConsole("\n Error: " + e.getMessage());
			e.printStackTrace();
		}finally {
			//finally block used to close resources
			try {
				if (stmt != null);
			} catch (Exception se) {
			}// do nothing
		}//end try
		allRegimenComboBox.getItems().addAll(regimenSet);
		newRegimenComboBox.getItems().addAll(regimenSet);
	}

	@FXML
	private void getDispensed(){
		pharmacyEncounterTable.getItems().removeAll();
		if(allRegimenComboBox.getSelectionModel().getSelectedItem() == null){
			return;
		}
		Controller ctrl = new Controller();
		ObservableList<PharmacyEncounter> pharmacyEncounters = FXCollections.observableArrayList();
		appConsole.clear();
		logToConsole("#################### CHECKING DESTINATION DATABASE! \n");

		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
		}
		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
			logToConsole("\n Destination Database connection successful..");

			stmt = conn.createStatement();
//			String sql = "SELECT obs.person_id as patientID, encounter.encounter_id as encounterID, "
//					+ "obs.obs_id AS obsID, "
//					+ "(Select identifier FROM patient_identifier where patient_id = obs.person_id && identifier_type = 4) AS pepfarID,"
//					+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName FROM person_name where person_id = obs.person_id) As patientName,"
//					+ "encounter.encounter_datetime AS EncounterDateTime,"
//					+ "(Select name from concept_name where concept_id = obs.concept_id) AS Question"
//					+ "(Select name from concept_name where concept_id = obs.value_coded) AS Answer"
//					+ "  FROM obs LEFt JOIN encounter "
//					+ "on obs.encounter_id = encounter.encounter_id where "
//					+ "(encounter.encounter_type = 7 && obs.concept_id IN (7778111)) ||"
//					+ "(encounter.encounter_type = 7 && obs.concept_id IN (7778108,7778109,7778410)";


			String sql = "SELECT ob.person_id as patientID, "
					+ "(Select value_text from obs where encounter_id = ob.encounter_id AND concept_id = 1596 AND person_id = ob.person_id Limit 1) "
					+ "AS valueText, encounter.encounter_id as encounterID, "
								+ "ob.obs_id AS obsID, "
								+ "(Select identifier FROM patient_identifier where patient_id = ob.person_id && identifier_type = 4) AS pepfarID,"
								+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName FROM person_name where person_id = ob.person_id && preferred = 1) As patientName,"
								+ "encounter.encounter_datetime AS EncounterDateTime, "
								+ "(Select name from concept_name where concept_name.concept_id = ob.concept_id && concept_name.locale ='en' && concept_name.locale_preferred = 1) AS Question,"
								+ "(Select name from concept_name where concept_name.concept_id = ob.value_coded && concept_name.locale ='en' && concept_name.locale_preferred = 1) AS Answer"
								+ "  FROM obs AS ob LEFt JOIN encounter "
								+ "on ob.encounter_id = encounter.encounter_id where "
								+ "ob.value_coded = "+allRegimenComboBox.getSelectionModel().getSelectedItem().getConceptID()
								+  " AND encounter.encounter_datetime >='"+ LocalDate.parse(startDate.getValue().toString() , formatter).toString()  //+ " || "
								//+ "ob.obs_group_id in (SELECT obs_id from obs as os where os.concept_id = 7778408 && os.encounter_id = ob.encounter_id group by os.obs_id))"
								+ "' order by ob.person_id";
			logToConsole("\n Fetching Obs starting from .."+LocalDate.parse(startDate.getValue().toString() , formatter));
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				PharmacyEncounter pharmacyEncounter = new PharmacyEncounter();
				pharmacyEncounter.setPatientID(rs.getInt("patientID"));
				pharmacyEncounter.setEncounterID(rs.getInt("encounterID"));
				pharmacyEncounter.setObsID(rs.getInt("obsID"));
				pharmacyEncounter.setPepfarID(rs.getString("pepfarID"));
				pharmacyEncounter.setPatientName(rs.getString("patientName"));
				pharmacyEncounter.setEncounterDateTime(rs.getDate("EncounterDateTime"));
				pharmacyEncounter.setQuestion(rs.getString("Question"));
				pharmacyEncounter.setAnswer(rs.getString("Answer") + " - " + rs.getString("valueText"));
				pharmacyEncounters.add(pharmacyEncounter);
				logToConsole(pharmacyEncounter.toString()+"\n");
			}
			rs.close();
			logToConsole("\n Done..");
		} catch (SQLException e) {
			logToConsole("\n Error: " + e.getMessage());
			e.printStackTrace();
		}finally {
			//finally block used to close resources
			try {
				if (stmt != null);
			} catch (Exception se) {
			}// do nothing
		}//end try
		try {
			if (pharmacyEncounters.isEmpty()) {
				logToConsole("\n No Record Found..");
			} else {
				pharmacyEncounterTable.setItems(pharmacyEncounters);
			}
		}catch (Exception ex){
			logToConsole("\n Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}


	@FXML
	private void getLastEncounter(){

//		if(lastCarePharmacyEncounterTable.getItems() != null) {
//			lastCarePharmacyEncounterTable.getItems().removeAll();
//		}

		ObservableList<LastCarePharmacy> lastCarePharmacyEncounters = FXCollections.observableArrayList();
		appConsole.clear();
		logToConsole("#################### CHECKING DESTINATION DATABASE! \n");

		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
		}
		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
			logToConsole("\n Source Database connection successful..");

			stmt = conn.createStatement();

			String sql = "SELECT pa.patient_id as patientID, enc.encounter_id as encounterID, person.birthdate, "
								//+ "ob.obs_id AS obsID, "
								+ "(Select identifier FROM patient_identifier where patient_id = pa.patient_id && identifier_type = 4) AS pepfarID,"
								+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName FROM person_name where person_id = pa.patient_id && preferred = 1) As patientName,"
								+ "enc.encounter_datetime AS LastCareEncounterDate, "
								+ " ob.value_coded AS RegimenID , "
								+ " (SELECT value_coded from obs where (obs.encounter_id = enc.encounter_id AND obs.concept_id=7777821) Limit 1) AS NextAppointment,"
//								+ " (SELECT encounter_datetime from encounter where encounter.patient_id = enc.patient_id AND "
//								+ " encounter.encounter_datetime > enc.encounter_datetime AND "
//								+ "encounter.form_id IN (46,53) group by encounter.patient_id Limit 1) AS LastPharmacyEncounterDate,"
								+ "(Select name from concept_name where concept_name.concept_id = ob.value_coded && concept_name.locale ='en' && concept_name.locale_preferred = 1) AS RegimenLine"
//								+ "(Select name from concept_name where concept_name.concept_id = ob.value_coded && concept_name.locale ='en' && concept_name.locale_preferred = 1) AS Answer"
								+ "  FROM encounter AS enc LEFt JOIN patient as pa "
								+ "on pa.patient_id = enc.patient_id "
								+ " LEFT JOIN obs as ob on ob.encounter_id = enc.encounter_id"
								+ " LEFT JOIN person on person.person_id = enc.patient_id "
								+ " where "
								+ " enc.form_id = 56 "
								+ " AND ob.concept_id = 7778111"
								+ " AND enc.encounter_datetime NOT IN (select encounter_datetime from encounter where patient_id = pa.patient_id AND encounter.form_id IN (46,53))"
								//+  " AND encounter.encounter_datetime >='"+ LocalDate.parse(startDate.getValue().toString() , formatter).toString()  //+ " || "
								//+ "ob.obs_group_id in (SELECT obs_id from obs as os where os.concept_id = 7778408 && os.encounter_id = ob.encounter_id group by os.obs_id))"
								+ " order by enc.encounter_datetime DESC ";
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			int tSum = 0;
			while (rs.next()) {
				++tSum;
				LastCarePharmacy lastCarePharmacy = new LastCarePharmacy();
				lastCarePharmacy.setPatientID(rs.getInt("patientID"));
				lastCarePharmacy.setEncounterID(rs.getInt("encounterID"));
				//pharmacyEncounter.setObsID(rs.getInt("obsID"));
				lastCarePharmacy.setPepfarID(rs.getString("pepfarID"));
				lastCarePharmacy.setPatientName(rs.getString("patientName"));
				lastCarePharmacy.setLastCareEncounterDate(rs.getDate("LastCareEncounterDate"));

				lastCarePharmacy.setLastCareRegimenLineId(rs.getInt("RegimenID"));
				lastCarePharmacy.setLastCareRegimenLine(rs.getString("RegimenLine"));
				if(rs.getDate("birthdate") == null){
					lastCarePharmacy.setPatientAge(18);
				}else {
					lastCarePharmacy.setPatientAge(Days.daysBetween(new DateTime(rs.getDate("birthdate")),new DateTime(new java.util.Date())).getDays() / 365);
				}
				logToConsole("Patient Age: "+lastCarePharmacy.getPatientAge());
				if(rs.getInt("NextAppointment") == 1570)
					lastCarePharmacy.setNextAppointment(7);
				else if(rs.getInt("NextAppointment") == 1571)
					lastCarePharmacy.setNextAppointment(15);
				else if(rs.getInt("NextAppointment") == 1628)
					lastCarePharmacy.setNextAppointment(30);
				else if(rs.getInt("NextAppointment") == 1574)
					lastCarePharmacy.setNextAppointment(60);
				else if(rs.getInt("NextAppointment") == 1575)
					lastCarePharmacy.setNextAppointment(90);

				LastCarePharmacy lastCP  = getLastPharmacyEncounter(lastCarePharmacy);

				lastCarePharmacy.setLastPharmacyEncounterDate(lastCP.getLastPharmacyEncounterDate());

				lastCarePharmacy.setLastPharmEncounterID(lastCP.getLastPharmEncounterID());

				lastCarePharmacyEncounters.add(lastCarePharmacy);

				logToConsole(lastCarePharmacy.toString()+"\n");
			}
			logToConsole("\n Total Number: "+tSum);
			rs.close();
			logToConsole("\n Done..");
		} catch (SQLException e) {
			logToConsole("\n Error: " + e.getMessage());
			e.printStackTrace();
		}finally {
			//finally block used to close resources
			try {
				if (stmt != null);
			} catch (Exception se) {
			}// do nothing
		}//end try
		try {
			if (lastCarePharmacyEncounters.isEmpty()) {
				logToConsole("\n No Record Found..");
			} else {
				lastCarePharmacyEncounterTable.setItems(lastCarePharmacyEncounters);
			}
		}catch (Exception ex){
			logToConsole("\n Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	@FXML
	private void createPharmForm(){

		ObservableList<LastCarePharmacy> lastCarePharmacys = lastCarePharmacyEncounterTable.getSelectionModel().getSelectedItems();

		ObservableList<Obs> obses = FXCollections.observableArrayList();
		for(LastCarePharmacy loadCF : lastCarePharmacys) {
			logToConsole("\n No data merging!!!");
			connectionSettings();

			Connection conn = null;
			Statement stmt = null;
			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				//STEP 3: Open a connection
				logToConsole("\n Connecting to Source Database!!");
				conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);
				logToConsole("\n Connected to database successfully...");

				//STEP 4: Execute a query

				stmt = conn.createStatement();
				String sql = "";

				logToConsole("\n Fetching Obs...");

				sql = "SELECT * FROM obs where encounter_id=" + loadCF.getLastPharmEncounterID();

				logToConsole("USING: " + sql);
				logToConsole("\n Creating Select statement...");
				ResultSet rs = stmt.executeQuery(sql);
				//STEP 5: Extract data from result set
				while (rs.next()) {
					//Retrieve by column name
					Obs obs = new Obs();
					if (rs.getString("accession_number") != null)
						obs.setAccession_number(rs.getString("accession_number"));

					if (rs.getString("comments") != null)
						obs.setComments(rs.getString("comments"));

					obs.setConcept_id(rs.getInt("concept_id"));

					obs.setUuid(UUID.randomUUID());
					obs.setCreator(1);
					obs.setDate_created(rs.getDate("date_created"));

					if (rs.getDate("date_voided") != null)
						obs.setDate_voided(rs.getDate("date_voided"));

					obs.setEncounter_id(rs.getInt("encounter_id"));
					obs.setLocation_id(2);
					if (rs.getString("void_reason") != null)
						obs.setVoid_reason(rs.getString("void_reason"));

					obs.setVoided(rs.getBoolean("voided"));
					obs.setObs_datetime(rs.getDate("obs_datetime"));

					if (rs.getInt("obs_group_id") > 0)
						obs.setObs_group_id(rs.getInt("obs_group_id"));

					if (rs.getInt("obs_id") > 0)
						obs.setObs_id(rs.getInt("obs_id"));
					if (rs.getInt("order_id") > 0)
						obs.setOrder_id(rs.getInt("order_id"));

					obs.setPerson_id(rs.getInt("person_id"));
					if (rs.getInt("value_coded") > 0)
						obs.setValue_coded(rs.getInt("value_coded"));
					else
						obs.setValue_coded(null);

					if (rs.getInt("value_coded_name_id") > 0)
						obs.setValue_coded_name_id(rs.getInt("value_coded_name_id"));

					if (rs.getString("value_complex") != null)
						obs.setValue_complex(rs.getString("value_complex"));
					if (rs.getDate("value_datetime") != null)
						obs.setValue_datetime(rs.getDate("value_datetime"));
					if (rs.getInt("value_drug") > 0)
						obs.setValue_drug(rs.getInt("value_drug"));
					if (rs.getInt("value_group_id") > 0)
						obs.setValue_group_id(rs.getInt("value_group_id"));
					if (rs.getString("value_modifier") != null)
						obs.setValue_modifier(rs.getString("value_modifier"));
					if (rs.getDouble("value_numeric") != 0)
						obs.setValue_numeric(rs.getDouble("value_numeric"));
					if (rs.getString("value_text") != null)
						obs.setValue_text(rs.getString("value_text"));
					if (rs.getInt("voided_by") > 0)
						obs.setVoided_by(rs.getInt("voided_by"));
					obses.add(obs);

				}
				rs.close();
				logToConsole("\n Data Successfully Fetched!\n");
			}
			catch (SQLException se) {
				//Handle errors for JDBC
				se.printStackTrace();
				logToConsole("\n Error: " + se.getMessage());
			}
			catch (Exception e) {
				//Handle errors for Class.forName
				e.printStackTrace();
				logToConsole("\n Error: " + e.getMessage());
			}
			finally {
				//finally block used to close resources
				try {
					if (stmt != null)
						conn.close();
				}
				catch (SQLException se) {
				}// do nothing

			}//end try

			String INSERT_SQL = "INSERT INTO encounter"
					+ "(encounter_id, encounter_type, patient_id, location_id, form_id, encounter_datetime, creator, date_created, "
					+
					"date_changed, voided, date_voided, void_reason, uuid,provider_id) " +
					"VALUES ( NULL,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			logToConsole("\n Connecting Encounter! \n");
			int encounterID = 0;
			try (Connection conn1 = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
				conn1.setAutoCommit(false);
				try (PreparedStatement stmt1 = conn1.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);) {
					logToConsole("\n Creating Encounter! \n");
					try {

						stmt1.setInt(1, 7);
						stmt1.setInt(2, loadCF.getPatientID());
						stmt1.setInt(3, 42);
						stmt1.setInt(4, loadCF.getPatientAge() >= 18.0 ? 46 : 52);
						stmt1.setDate(5, loadCF.getLastCareEncounterDate());
						stmt1.setInt(6, 1);
						stmt1.setDate(7, loadCF.getLastCareEncounterDate());
						stmt1.setDate(8, null);
						stmt1.setBoolean(9, false);
						stmt1.setDate(10, null);
						stmt1.setString(11, null);
						stmt1.setString(12, UUID.randomUUID().toString());
						stmt1.setInt(13, 1);

						//Add statement to batch
						stmt1.addBatch();

						stmt1.executeBatch();
						ResultSet rs1 = stmt1.getGeneratedKeys();
						while (rs1.next()){
							encounterID = rs1.getInt(1);
						}


						logToConsole("\n Connected Encounter with ID: "+encounterID);

						//######################

						//######################
					}
					catch (Exception ex) {
						logToConsole(ex.getMessage());
						ex.printStackTrace();
					}

				}
				catch (SQLException e) {
					logToConsole("\nError running Insert statement: " + e.getMessage());
					e.printStackTrace();
					rollbackTransaction(conn1, e);
				}


				String INSERT_SQL2 = "INSERT INTO obs"
						+ "(person_id, concept_id, encounter_id, order_id, obs_datetime, location_id," +
						"accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, " +
						"value_numeric, value_modifier, value_text, value_complex, comments," +
						"creator, date_created, voided, date_voided, void_reason, uuid, obs_group_id) " +
						"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					try (PreparedStatement stmt1 = conn1.prepareStatement(INSERT_SQL2);) {
						logToConsole("\n Connecting Obs! \n");
						for (Obs module : obses) {
							///try {
//							stmt1.setInt(26, module.getObs_id());
							if(module.getObs_group_id() != null)
								stmt1.setInt(24, module.getObs_group_id());
							else
								stmt1.setString(24, null);
							stmt1.setInt(1, module.getPerson_id());
							stmt1.setInt(2, module.getConcept_id());
							stmt1.setInt(3, encounterID);
							if(module.getOrder_id() != null)
								stmt1.setInt(4, module.getOrder_id());
							else
								stmt1.setString(4, null);
							if (module.getObs_datetime() != null)
								stmt1.setDate(5, new java.sql.Date(loadCF.getLastCareEncounterDate().getTime()));
							else
								stmt1.setDate(5, null);
							if (module.getLocation_id() >= 0)
								stmt1.setInt(6, module.getLocation_id());
							else
								stmt1.setInt(6, 0);
							if(module.getAccession_number() != null)
								stmt1.setString(7, module.getAccession_number());
							else
								stmt1.setString(7, null);
							if(module.getValue_group_id() != null)
								stmt1.setInt(8, module.getValue_group_id());
							else
								stmt1.setString(8, null);
							//logToConsole("\nValue Coded: "+module.getValue_coded());
							if(module.getValue_coded() != null)
								stmt1.setInt(9, module.getValue_coded());
							else
								stmt1.setString(9, null);
							if(module.getValue_coded_name_id() != null)
								stmt1.setInt(10, module.getValue_coded_name_id());
							else
								stmt1.setString(10, null);
							if(module.getValue_drug() != null)
								stmt1.setInt(11, module.getValue_drug());
							else
								stmt1.setString(11, null);
							if (module.getValue_datetime() != null)
								stmt1.setDate(12, new java.sql.Date(module.getValue_datetime().getTime()));
							else
								stmt1.setDate(12, null);
							if(module.getValue_numeric() != null)
								stmt1.setDouble(13, module.getValue_numeric());
							else
								stmt1.setString(13, null);
							if(module.getValue_modifier() != null)
								stmt1.setString(14, module.getValue_modifier());
							else
								stmt1.setString(14, null);
							if(module.getValue_text() != null)
								stmt1.setString(15, module.getValue_text());
							else
								stmt1.setString(15, null);
							if(module.getValue_complex() != null)
								stmt1.setString(16, module.getValue_complex());
							else
								stmt1.setString(16, null);
							if(module.getComments() != null)
								stmt1.setString(17, module.getComments());
							else
								stmt1.setString(17, null);
							stmt1.setInt(18, module.getCreator());
							if (module.getDate_created() != null)
								stmt1.setDate(19, new java.sql.Date(loadCF.getLastCareEncounterDate().getTime()));
							else
								stmt1.setDate(19, null);
							stmt1.setBoolean(20, module.isVoided());
							if (module.getDate_voided() != null)
								stmt1.setDate(21, new java.sql.Date(loadCF.getLastCareEncounterDate().getTime()));
							else
								stmt1.setDate(21, null);
							if(module.getVoid_reason() != null)
								stmt1.setString(22, module.getVoid_reason());
							else
								stmt1.setString(22, null);
							stmt1.setString(23, UUID.randomUUID().toString());
							//stmt1.setInt(24, module.getPrevious_version());
//							if(module.getForm_namespace_and_path() != null)
//								stmt1.setString(24, module.getForm_namespace_and_path());
//							else
//								stmt1.setString(24, null);
							//Add statement to batch
							stmt1.addBatch();

						}
						//execute batch
						stmt1.executeBatch();
						logToConsole("\nTransaction is committed successfully.");
					} catch (SQLException e) {
						logToConsole(e.getMessage());
						e.printStackTrace();
						rollbackTransaction(conn1, e);
					}

				//execute batch
				try {

					conn1.commit();
					getLastEncounter();
				}
				catch (Exception ex) {
					logToConsole("\nError in batch insert: " + ex.getMessage());

				}
			}
			catch (SQLException e) {
				logToConsole("\nError Establishing connection: " + e.getMessage());
				e.printStackTrace();

			}
		}
	}

	@FXML
	private LastCarePharmacy getLastPharmacyEncounter( LastCarePharmacy ls){

		LastCarePharmacy lastCarePharmacy = new LastCarePharmacy();

		appConsole.clear();

		logToConsole("#################### CHECKING DESTINATION DATABASE! \n");

		Statement stmt = null;

		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
		}
		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {

			logToConsole("\n Source Database connection successful..");

			stmt = conn.createStatement();

			logToConsole("\nPatient ID:"+ls.getPatientID());

			String sql = "SELECT encounter_id, encounter_datetime AS LastPharmacyEncounterDate from encounter where encounter.patient_id = "+ls.getPatientID()+" AND "
							+ " encounter.encounter_datetime > '"+ls.getLastCareEncounterDate()+"' AND "
							+ "encounter.form_id IN (46,53) order by encounter.encounter_datetime ASC Limit 1 ";

			ResultSet rs = stmt.executeQuery(sql);

			logToConsole("\nLast Pharmacy Date: "+ls.getLastCareEncounterDate());

			//STEP 5: Extract data from result set
			int count = 0;
			while (rs.next()) {
				++count;
				lastCarePharmacy.setLastPharmacyEncounterDate(rs.getDate("LastPharmacyEncounterDate"));
				lastCarePharmacy.setLastPharmEncounterID(rs.getInt("encounter_id"));
				logToConsole("\n Fetched Date: "+rs.getDate("LastPharmacyEncounterDate"));
			}
			rs.close();
			if(count == 0){
				sql = "SELECT encounter_id, encounter_datetime AS LastPharmacyEncounterDate from encounter where encounter.patient_id = "+ls.getPatientID()+" AND "
						+ " encounter.encounter_datetime < '"+ls.getLastCareEncounterDate()+"' AND "
						+ "encounter.form_id IN (46,53) order by encounter.encounter_datetime DESC Limit 1 ";

				 rs = stmt.executeQuery(sql);

				logToConsole("\nLast Pharmacy Date: "+ls.getLastCareEncounterDate());

				//STEP 5: Extract data from result set
				//int count = 0;
				while (rs.next()) {
					//++count;
					lastCarePharmacy.setLastPharmacyEncounterDate(rs.getDate("LastPharmacyEncounterDate"));
					lastCarePharmacy.setLastPharmEncounterID(rs.getInt("encounter_id"));
					logToConsole("\n Fetched Date: "+rs.getDate("LastPharmacyEncounterDate"));
				}
				rs.close();
			}
			//logToConsole("\n Done..");
		} catch (SQLException e) {
			logToConsole("\n Error: " + e.getMessage());
			e.printStackTrace();
		}catch (Exception e) {
			logToConsole("\n Error: " + e.getMessage());
			e.printStackTrace();
		}finally {
			//finally block used to close resources
			try {
				if (stmt != null);
			} catch (Exception se) {
			}// do nothing
		}//end try
//		try {
//			if (pharDate == null) {
//				logToConsole("\n No Record Found..");
//			} else {
//				return pharDate;
//			}
//		}catch (Exception ex){
//			logToConsole("\n Error: " + ex.getMessage());
//			ex.printStackTrace();
//		}
		return lastCarePharmacy;
	}

	@FXML
	private void updateRegimens(){
		//Set<PharmacyEncounter> selectedEncounters = new HashSet<>();
		logToConsole("\n Initializing.!\n");

			String INSERT_SQL = "UPDATE obs SET concept_id=?, value_coded=? WHERE obs_id=?";

			try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
				Class.forName("com.mysql.jdbc.Driver");
				logToConsole("\n Connecting..!\n");
				conn.setAutoCommit(false);
				Regimen regimen = newRegimenComboBox.getSelectionModel().getSelectedItem();
				try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {

					// Insert sample records
					for (PharmacyEncounter pE : pharmacyEncounterTable.getSelectionModel().getSelectedItems()) {
						logToConsole("\n Loading Selected Regimens...!!\n");
						stmt.setInt(1, 7778108);
						stmt.setInt(2, regimen.getConceptID());
						stmt.setInt(3, pE.getObsID());
						stmt.addBatch();
						stmt.addBatch("UPDATE obs SET value_coded=7778108 where concept_id=7778111 && encounter_id="+pE.getEncounterID());
					}
					//execute batch
					logToConsole("\n Updating Data....!\n");
					stmt.executeBatch();
					conn.commit();
					logToConsole("Regimens Updated successfully.");
				} catch (SQLException e) {
					e.printStackTrace();
					rollbackTransaction(conn, e);
				}catch (Exception e){
					e.printStackTrace();
					rollbackTransaction(conn, e);
				}
			} catch (SQLException e) {
				logToConsole("Error: "+e.getMessage());
				e.printStackTrace();
			}catch (Exception ex){
				logToConsole("Error: "+ex.getMessage());
				ex.printStackTrace();
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
}
