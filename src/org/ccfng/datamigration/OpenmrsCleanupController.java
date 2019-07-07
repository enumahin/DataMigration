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
import org.ccfng.datamigration.openmrscleanup.PharmacyEncounter;
import org.ccfng.datamigration.openmrscleanup.Regimen;
import org.ccfng.datamigration.session.SessionManager;

public class OpenmrsCleanupController {

	@FXML
	public TableView<PharmacyEncounter> pharmacyEncounterTable;

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

		pharmacyEncounterTable.getSelectionModel().setSelectionMode(
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
			conn.commit();
		} catch (SQLException e) {
			logToConsole("Error: "+e.getMessage());
			e.printStackTrace();
		}catch (Exception ex){
			logToConsole("Error: "+ex.getMessage());
			ex.printStackTrace();
		}

		//### ADD THE 3 DTG REGIMENS TO FIRST LINE CONCEPT ANSWER


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


			String sql = "SELECT ob.person_id as patientID, encounter.encounter_id as encounterID, "
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
								+ "' order by ob.person_id Limit 50";
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
				pharmacyEncounter.setAnswer(rs.getString("Answer"));
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
	private void updateRegimens(){
		//Set<PharmacyEncounter> selectedEncounters = new HashSet<>();
		logToConsole("\n Initializing.!\n");

			String INSERT_SQL = "UPDATE obs SET value_coded=? WHERE obs_id=?";

			try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
				Class.forName("com.mysql.jdbc.Driver");
				logToConsole("\n Connecting..!\n");
				conn.setAutoCommit(false);
				Regimen regimen = newRegimenComboBox.getSelectionModel().getSelectedItem();
				try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {

					// Insert sample records
					for (PharmacyEncounter pE : pharmacyEncounterTable.getSelectionModel().getSelectedItems()) {
						logToConsole("\n Loading Selected Regimens...!!\n");
						stmt.setInt(1, regimen.getConceptID());
						stmt.setInt(2, pE.getObsID());
						stmt.addBatch();
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
				logToConsole("Transaction is being rolled back: " + e.getMessage());
				conn.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
