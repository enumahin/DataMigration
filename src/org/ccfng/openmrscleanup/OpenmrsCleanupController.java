package org.ccfng.openmrscleanup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ccfng.datamigration.Controller;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.session.SessionManager;
import org.ccfng.openmrscleanup.models.Address;
import org.ccfng.openmrscleanup.models.DateFix;
import org.ccfng.openmrscleanup.models.LastCarePharmacy;
import org.ccfng.openmrscleanup.models.PatientStatus;
import org.ccfng.openmrscleanup.models.PharmacyEncounter;
import org.ccfng.openmrscleanup.models.Regimen;

public class OpenmrsCleanupController {

	@FXML
	public TableView<PharmacyEncounter> pharmacyEncounterTable;

	@FXML
	public TableView<LastCarePharmacy> lastCarePharmacyEncounterTable;

	@FXML
	public TableView<LastCarePharmacy> lastPharmacyEncounterTable;

	@FXML
	public TableView<LastCarePharmacy> nextAppointmentTable;

	@FXML
	public TableView<DateFix> dateFixTableView;


	@FXML
	private TextArea appConsole;

	@FXML
	private TextField limit;


	@FXML
	private ComboBox<Regimen> allRegimenComboBox;

	@FXML
	private ComboBox<Regimen> newRegimenComboBox;

	@FXML
	private DatePicker startDate;

	@FXML
	private Button createDTG;

	private Task<ObservableList<LastCarePharmacy>> lastPharmacyTask;

	private Task<ObservableList<PharmacyEncounter>> weightHeightTask;

	@FXML
	private TableView<PharmacyEncounter> weightHeightTable;

	private Task<ObservableList<PatientStatus>> patientStatusTask;

	@FXML
	private TableView<PatientStatus> noCommencementTableView;

	@FXML
	private TableView<Address> addressTableView;

	private Task<ObservableList<Address>> addressTask;

	@FXML
	private ComboBox<String> observationCombo;

	@FXML
	private ComboBox<String> criteriaCombo;

	@FXML
	private TextField valueToSearch;

	@FXML
	private TextField valueToAdd;

	public String sourceHost;

	public String sourcePort;

	public String sourceUsername;

	public String sourcePassword;

	public String sourceDb;

	public String tableSuffix;

	@FXML
	public Label weightHeightStatus;

	String source_jdbcUrl = null;

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

		observationCombo.getItems().add("Weight");
		observationCombo.getItems().add("Height");
		observationCombo.getItems().add("Drug Duration Number");
		criteriaCombo.getItems().add("Greater Than");
		criteriaCombo.getItems().add("Less Than");
		limit.setText("0");
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

		lastCarePharmacyEncounterTable.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE
		);

		dateFixTableView.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE
		);

		nextAppointmentTable.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE
		);

		weightHeightTable.getSelectionModel().setSelectionMode(
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

					stmt.execute("INSERT INTO drug (drug_id, concept_id, name, combination,dose_strength, creator,units, uuid,date_created) "
							+ "VALUES(NULL,"+dtg_id+",'Dolutegravir (DTG) 50mg',0,1,1,'mg',UUID(),NOW())");
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

					stmt.execute("INSERT INTO drug (drug_id,concept_id, name, combination,dose_strength, creator,units, uuid,date_created) "
							+ "VALUES(NULL,"+t3d_drug_id+",'Tenofovir/Lamivudine/Dolutegravir (TDF/3TC/DTG)',1,1,1,'mg',UUID(),NOW())");
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
//					logToConsole(processed3);


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

		String sql = "";

		if(allRegimenComboBox.getSelectionModel().getSelectedItem() == null){
			logToConsole("\n Searching for First Lines without Regimen!\n");
			sql = "SELECT ob.person_id as patientID, "
					+ "(select value_coded from obs where concept_id=7778108 AND person_id = ob.person_id "
					+ "AND encounter_id=ob.encounter_id AND voided = 0 order by obs_id ASC LIMIT 1) AS valueText,"
//					+ "null AS valueText, "
						+ "encounter.encounter_id as encounterID, "
						+ "ob.obs_id AS obsID, "
					+ "(Select identifier FROM patient_identifier where patient_id = ob.person_id && identifier_type = 4 limit 1) AS pepfarID,"
					+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName "
						+ "FROM person_name where person_id = ob.person_id && preferred = 1 limit 1) As patientName,"
					+ "encounter.encounter_datetime AS EncounterDateTime, "

					+ "(Select name from concept_name where concept_name.concept_id = ob.concept_id && concept_name.locale ='en' && concept_name.locale_preferred = 1) AS Question,"
					+ "(Select name from concept_name where concept_name.concept_id = ob.value_coded && concept_name.locale ='en' && concept_name.locale_preferred = 1) AS Answer"
					+ "  FROM obs AS ob LEFt JOIN encounter "
					+ "on ob.encounter_id = encounter.encounter_id where "
					+ " ob.value_coded = 7778108 "
					+  " AND encounter.encounter_datetime >='"+ LocalDate.parse(startDate.getValue().toString() , formatter).toString()  //+ " || "
					+ "' AND NOT Exists( select value_coded from obs where concept_id=7778108 AND person_id = ob.person_id "
							+ "AND encounter_id=ob.encounter_id AND voided = 0) "
					// + "' HAVING regimenSel = null || regimenSel = '' || regimenSel = 0"
					+ " order by ob.person_id";
		}else{
			sql = "SELECT ob.person_id as patientID, "
					+ "(Select value_text from obs where encounter_id = ob.encounter_id "
							+ "AND concept_id = 1596 AND person_id = ob.person_id group by value_text Limit 1) "
							+ "AS valueText, encounter.encounter_id as encounterID, "
							+ "ob.obs_id AS obsID, "
					+ "(Select identifier FROM patient_identifier where patient_id = ob.person_id && identifier_type = 4 group by identifier) AS pepfarID,"
					+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName "
					+ "FROM person_name where person_id = ob.person_id && preferred = 1) As patientName,"
							+ "encounter.encounter_datetime AS EncounterDateTime, "
					+ "(Select name from concept_name where concept_name.concept_id = ob.concept_id && "
							+ "concept_name.locale ='en' && concept_name.locale_preferred = 1 group by name) AS Question,"
					+ "(Select name from concept_name where concept_name.concept_id = ob.value_coded && "
						+ "concept_name.locale ='en' && concept_name.locale_preferred = 1 group by name) AS Answer"
						+ "  FROM obs AS ob LEFt JOIN encounter "
					+ "on ob.encounter_id = encounter.encounter_id where "
					+ "ob.value_coded = "+allRegimenComboBox.getSelectionModel().getSelectedItem().getConceptID()
					+  " AND encounter.encounter_datetime >='"+ LocalDate.parse(startDate.getValue().toString() , formatter).toString()  //+ " || "
					//+ "ob.obs_group_id in (SELECT obs_id from obs as os where os.concept_id = 7778408 && os.encounter_id = ob.encounter_id group by os.obs_id))"
					+ "' order by ob.person_id";
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
				if(rs.getString("valueText") != null)
					pharmacyEncounter.setAnswer(rs.getString("Answer") + " - " + rs.getString("valueText"));
				else
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
	private void getLastEncounter(){

//		if(lastCarePharmacyEncounterTable.getItems() != null) {
//			lastCarePharmacyEncounterTable.getItems().removeAll();
//		}
		//new Thread(lastPharmacyTask).start();

		partialGetLCE();
	}

	@FXML
	private void createPharmForm() {
      createForm(lastCarePharmacyEncounterTable.getSelectionModel().getSelectedItems(), "Pharm");
	}

	@FXML
	private void createCareForm() {
		createForm(lastPharmacyEncounterTable.getSelectionModel().getSelectedItems(), "Care");
	}

	private void createForm( ObservableList<LastCarePharmacy> lastCarePharmacys ,String loc){

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
					+ "date_changed, voided, date_voided, void_reason, uuid,provider_id) "
					+ "VALUES ( NULL,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
						if(loc.equals("Pharm"))
							stmt1.setInt(4, loadCF.getPatientAge() >= 15 ? 46 : 53);
						else
							stmt1.setInt(4, 56);
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


	private LastCarePharmacy getLastPharmacyEncounter( LastCarePharmacy ls, String loc){

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
							+ " encounter.encounter_datetime > '"+ls.getLastCareEncounterDate()+"' AND ";
			if(loc.equals("pharm"))
				sql += "encounter.form_id IN (46,53) order by encounter.encounter_datetime ASC Limit 1 ";
			else
				sql += "encounter.form_id = 56 order by encounter.encounter_datetime ASC Limit 1 ";
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
						+ " encounter.encounter_datetime < '"+ls.getLastCareEncounterDate()+"' AND ";
				if(loc.equals("pharm"))
					sql += "encounter.form_id IN (46,53) order by encounter.encounter_datetime DESC Limit 1 ";
				else
					sql += "encounter.form_id = 56 order by encounter.encounter_datetime DESC Limit 1 ";
				ResultSet rs2 = stmt.executeQuery(sql);
				 rs2 = stmt.executeQuery(sql);

				logToConsole("\nLast Pharmacy Date: "+ls.getLastCareEncounterDate());

				//STEP 5: Extract data from result set
				//int count = 0;
				while (rs2.next()) {
					//++count;
					lastCarePharmacy.setLastPharmacyEncounterDate(rs2.getDate("LastPharmacyEncounterDate"));
					lastCarePharmacy.setLastPharmEncounterID(rs2.getInt("encounter_id"));
					logToConsole("\n Fetched Date: "+rs2.getDate("LastPharmacyEncounterDate"));
				}
				rs2.close();
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
						if(allRegimenComboBox.getSelectionModel().getSelectedItem() != null) {
							logToConsole("\n Loading Selected Regimens...!!\n");
							stmt.setInt(1, 7778108);
							stmt.setInt(2, regimen.getConceptID());
							stmt.setInt(3, pE.getObsID());
							stmt.addBatch();
							stmt.addBatch("UPDATE obs SET value_coded=7778108 where concept_id=7778111 && encounter_id=" + pE
									.getEncounterID());
						}else{
							stmt.addBatch("INSERT INTO obs"
									+ "(person_id, concept_id, encounter_id, order_id, obs_datetime, location_id," +
									"accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, " +
									"value_numeric, value_modifier, value_text, value_complex, comments," +
									"creator, date_created, voided, date_voided, void_reason, uuid, obs_group_id) " +
									"VALUES ("
									+ pE.getPatientID()+","+
									7778108+","+
									pE.getEncounterID()+",NULL,"+
									pE.getEncounterDateTime()+",42,NULL,NULL,"+regimen.getConceptID()+",NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,"+
									pE.getEncounterDateTime()+",0,NULL,NULL,"+UUID.randomUUID().toString()+",NULL)");

							logToConsole("\n Loading Selected Data...!!\n");
							stmt.addBatch();
						}
					}
					//execute batch
					logToConsole("\n Updating Data....!\n");
					stmt.executeBatch();

					logToConsole("Regimens Updated successfully.");
				} catch (SQLException e) {
					e.printStackTrace();
//					rollbackTransaction(conn, e);
				}catch (Exception e){
					e.printStackTrace();
//					rollbackTransaction(conn, e);
				}

//				Create New Regimen

//				try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
//
//					// Insert sample records
//					for (PharmacyEncounter pE : pharmacyEncounterTable.getSelectionModel().getSelectedItems()) {
//						logToConsole("\n Loading Selected Regimens...!!\n");
//						stmt.setInt(1, 7778108);
//						stmt.setInt(2, regimen.getConceptID());
//						stmt.setInt(3, pE.getObsID());
//						stmt.addBatch();
//						stmt.addBatch("UPDATE obs SET value_coded=7778108 where concept_id=7778111 && encounter_id="+pE.getEncounterID());
//					}
//					//execute batch
//					logToConsole("\n Updating Data....!\n");
//					stmt.executeBatch();
//
//					logToConsole("Regimens Updated successfully.");
//				} catch (SQLException e) {
//					e.printStackTrace();
//					//					rollbackTransaction(conn, e);
//				}catch (Exception e){
//					e.printStackTrace();
//					//					rollbackTransaction(conn, e);
//				}

				try{
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

	private void partialGetLCE(){
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

			String sql = "SELECT pa.patient_id as patientID, enc.encounter_id as encounterID, DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 AS birthdate, "
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
					+ " AND enc.voided = 0"
					+ " AND pa.patient_id NOT IN (select person_id FROM obs WHERE person_id = pa.patient_id AND concept_id=1737 AND voided = 0) "
					+ " AND pa.patient_id NOT IN (select person_id FROM obs WHERE person_id = pa.patient_id AND concept_id=977 AND voided = 0) "
					+ " AND enc.encounter_datetime NOT IN (select encounter_datetime from encounter where patient_id = pa.patient_id AND encounter.form_id IN (46,53))"
					//+  " AND encounter.encounter_datetime >='"+ LocalDate.parse(startDate.getValue().toString() , formatter).toString()  //+ " || "
					//+ "ob.obs_group_id in (SELECT obs_id from obs as os where os.concept_id = 7778408 && os.encounter_id = ob.encounter_id group by os.obs_id))"
					+ " order by enc.encounter_datetime DESC Limit 15";
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

				lastCarePharmacy.setPatientAge(rs.getInt("birthdate"));
				logToConsole("\n Patient Age is: "+rs.getInt("birthdate"));

				logToConsole("\nPatient Age: "+lastCarePharmacy.getPatientAge());
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

				LastCarePharmacy lastCP  = getLastPharmacyEncounter(lastCarePharmacy,"pharm");

				lastCarePharmacy.setLastPharmacyEncounterDate(lastCP.getLastPharmacyEncounterDate());

				lastCarePharmacy.setLastPharmEncounterID(lastCP.getLastPharmEncounterID());

				lastCarePharmacyEncounters.add(lastCarePharmacy);

				//logToConsole(lastCarePharmacy.toString()+"\n");
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
			}
		}catch (Exception ex){
			logToConsole("\n Error: " + ex.getMessage());
			ex.printStackTrace();
		}
			lastCarePharmacyEncounterTable.setItems(lastCarePharmacyEncounters);

	}

	@FXML
	private void getNextAppointment(){
		ObservableList<LastCarePharmacy> lastCarePharmacyEncounters = FXCollections.observableArrayList();
		appConsole.clear();
		int lim = Integer.parseInt(limit.getText());
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

			String sql = "SELECT pa.patient_id as patientID, enc.encounter_id as encounterID, "
					+ "DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 AS birthdate, "
					//+ "ob.obs_id AS obsID, "
					+ "(Select identifier FROM patient_identifier where patient_id = pa.patient_id && identifier_type = 4) AS pepfarID,"
					+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName "
					+ "FROM person_name where person_id = pa.patient_id && preferred = 1) As patientName,"
					+ "enc.encounter_datetime AS LastCareEncounterDate, "
					+ " ob.value_coded AS RegimenID , "
					+ " (SELECT value_coded from obs where (obs.encounter_id = enc.encounter_id AND obs.concept_id=7777821 AND voided = 0) Limit 1) AS NextAppointment,"
					+ " (SELECT value_datetime from obs where (obs.encounter_id = enc.encounter_id "
					+ "AND obs.concept_id=7777822 AND voided = 0) Limit 1) AS NextAppointmentDATE,"
//					+ "concept_name.locale ='en' && concept_name.locale_preferred = 1 Limit 1) AS RegimenLine, "
					+ "ob.obs_id AS obsID"
					+ "  FROM encounter AS enc LEFt JOIN patient as pa "
					+ "on pa.patient_id = enc.patient_id "
					+ " LEFT JOIN obs as ob on ob.encounter_id = enc.encounter_id AND ob.voided = 0 AND ob.concept_id = 7777822"
					+ " LEFT JOIN person on person.person_id = enc.patient_id "
					+ " where "
					+ " enc.form_id = 56 "
//					+ " AND enc.voided = 0"
					+ " AND EXISTS (select encounter_id FROM obs WHERE encounter_id=enc.encounter_id AND person_id = pa.patient_id AND concept_id=7778111 AND voided = 0) "
					+ " AND NOT EXISTS (select person_id FROM obs WHERE person_id = pa.patient_id AND concept_id=1737 AND voided = 0) "
					+ " AND NOT EXISTS (select person_id FROM obs WHERE person_id = pa.patient_id AND concept_id=977 AND voided = 0) "
					+ " HAVING LastCareEncounterDate >= NextAppointmentDATE" //" OR (DATEDIFF(NextAppointmentDATE, STR_TO_DATE(LastCareEncounterDate, '%Y-%m-%d')) > 180)"
					+ " order by enc.encounter_datetime DESC Limit "+lim;

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			int tSum = 0;
			while (rs.next()) {
				++tSum;
				LastCarePharmacy lastCarePharmacy = new LastCarePharmacy();
				lastCarePharmacy.setPatientID(rs.getInt("patientID"));
				lastCarePharmacy.setEncounterID(rs.getInt("encounterID"));
				lastCarePharmacy.setObsID(rs.getInt("obsID"));
				lastCarePharmacy.setPepfarID(rs.getString("pepfarID"));
				lastCarePharmacy.setPatientName(rs.getString("patientName"));
				lastCarePharmacy.setLastCareEncounterDate(rs.getDate("LastCareEncounterDate"));

				lastCarePharmacy.setNextAppointmentDays(
						Duration.between(
								LocalDate.parse(rs.getDate("LastCareEncounterDate").toString(),formatter).atStartOfDay(),
								LocalDate.parse(rs.getDate("NextAppointmentDATE").toString(),formatter).atStartOfDay()
								).toDays());

				lastCarePharmacy.setNextAppointmentDate(rs.getDate("NextAppointmentDATE"));

				lastCarePharmacy.setPatientAge(rs.getInt("birthdate"));
				logToConsole("\n Patient Age is: "+rs.getInt("birthdate"));


				lastCarePharmacyEncounters.add(lastCarePharmacy);

				//logToConsole(lastCarePharmacy.toString()+"\n");
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
			}
		}catch (Exception ex){
			logToConsole("\n Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		nextAppointmentTable.setItems(lastCarePharmacyEncounters);

	}

	@FXML
	private void getDateDetails(){
		ObservableList<DateFix> dateFixes = FXCollections.observableArrayList();
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

						String sql = "SELECT pa.patient_id as patientID, "
//								+ "DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 AS birthdate, "
								+ "patient_identifier.identifier AS pepfarID,"
								+ "CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName ,"
								+ "(SELECT value_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where encounter.form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 "
									+ "AND obs.voided = 0 order by obs.obs_id ASC Limit 1) AS ArtStartDate, "
								+ "(SELECT encounter.encounter_id from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 AND obs.voided = 0 Limit 1) AS ArtStartEncounterID, "
								+ "(SELECT obs.concept_id from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 AND obs.voided = 0 Limit 1) AS ArtStartConceptID, "
								+ "(SELECT value_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where form_id=65 AND encounter.patient_id=pa.patient_id AND obs.concept_id=859 AND obs.voided = 0 Limit 1) AS DateConfirmed, "
								+ "(SELECT encounter.encounter_id from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where form_id=65 AND encounter.patient_id=pa.patient_id AND obs.concept_id=859 AND obs.voided = 0 Limit 1) AS DateConfirmedEncounterID, "
								+ "(SELECT obs.concept_id from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where form_id=65 AND encounter.patient_id=pa.patient_id AND obs.concept_id=859 AND obs.voided = 0 Limit 1) AS DateConfirmedConceptID, "
								+ "(SELECT encounter.encounter_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where (form_id=46 || form_id=53 ) AND encounter.patient_id=pa.patient_id AND obs.concept_id=7778111 AND obs.voided = 0 "
									+ "order by encounter.encounter_id ASC Limit 1) AS FirstPharmacy, "
								+ "(SELECT encounter.encounter_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
									+ "where form_id=65 AND encounter.patient_id=pa.patient_id AND obs.voided = 0 Limit 1) AS DateEnrolled "
								+ " FROM patient as pa "
			//					+ " LEFT JOIN person on person.person_id = pa.patient_id "
								+ " LEFT JOIN patient_identifier on patient_identifier.patient_id = pa.patient_id && identifier_type = 4"
								+ " LEFT JOIN person_name on person_name.person_id = pa.patient_id && person_name.preferred = 1"
								+ " WHERE "
								+ " pa.voided = 0 "
								+ " AND pa.patient_id NOT IN (select person_id FROM obs WHERE person_id = pa.patient_id AND concept_id=1737 AND voided = 0) "
								+ " AND pa.patient_id NOT IN (select person_id FROM obs WHERE person_id = pa.patient_id AND concept_id=977 AND voided = 0) "
								+ " HAVING (ArtStartDate IS NULL || DateConfirmed IS NULL || DateConfirmed > ArtStartDate) "
								+ " order by pa.patient_id DESC";
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			int tSum = 0;
			while (rs.next()) {
//				if(
//						(rs.getDate("ArtStartDate") == null || rs.getDate("DateConfirmed") == null)
//						||
//							(rs.getDate("DateConfirmed") > rs.getDate("ArtStartDate"))
//						) {
					++tSum;
					DateFix dateFix = new DateFix();
					dateFix.setPatientID(rs.getInt("patientID"));
//					dateFix.setPatientAge(rs.getInt("birthdate"));
					dateFix.setPepfarID(rs.getString("pepfarID"));
					dateFix.setPatientName(rs.getString("patientName"));
					dateFix.setArtStartDate(rs.getDate("ArtStartDate"));
					dateFix.setArtStartEncounterId(rs.getInt("ArtStartEncounterID"));
					dateFix.setHivConfirmedDate(rs.getDate("DateConfirmed"));
					dateFix.setHivConfirmedEncounterId(rs.getInt("DateConfirmedEncounterID"));
					dateFix.setHivConfirmedConceptId(rs.getInt("DateConfirmedConceptID"));
					dateFix.setFirstARVDate(rs.getDate("FirstPharmacy"));
					dateFix.setDateEnrolled(rs.getDate("DateEnrolled"));
					dateFix.setArtStartConceptId(rs.getInt("ArtStartConceptID"));

					logToConsole("\n Patient : " + dateFix.toString());

					dateFixes.add(dateFix);

//				}
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
			if (dateFixes.isEmpty()) {
				logToConsole("\n No Record Found..");
			}
		}catch (Exception ex){
			logToConsole("\n Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		dateFixTableView.setItems(dateFixes);

	}

	@FXML
	private void updateHivConfirmedDate(){

		logToConsole("\n Initializing.!\n");

		String INSERT_SQL = "UPDATE obs SET value_datetime=DATE_SUB(?, INTERVAL 21 DAY) WHERE person_id=? AND concept_id=?";

		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
			Class.forName("com.mysql.jdbc.Driver");
			logToConsole("\n Connecting..!\n");
			conn.setAutoCommit(false);

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {

				for (DateFix dF : dateFixTableView.getSelectionModel().getSelectedItems()) {
					if(dF.getArtStartDate().toString() != null) {
						logToConsole("\n Loading Selected Data...!!\n");
						logToConsole("\n Loading Selected Data...: " + dF.getArtStartDate().toString());
						logToConsole("\n Loading Selected Data...: " + dF.getPatientID());
						logToConsole("\n Loading Selected Data...: " + dF.getHivConfirmedConceptId());
						stmt.setString(1, dF.getArtStartDate().toString());
						stmt.setInt(2, dF.getPatientID());
						stmt.setInt(3, dF.getHivConfirmedConceptId());
						stmt.addBatch();
					}else{

						stmt.addBatch("INSERT INTO obs"
								+ "(person_id, concept_id, encounter_id, order_id, obs_datetime, location_id," +
								"accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, " +
								"value_numeric, value_modifier, value_text, value_complex, comments," +
								"creator, date_created, voided, date_voided, void_reason, uuid, obs_group_id) " +
								"VALUES ("
								+ dF.getPatientID()+","+
								dF.getArtStartConceptId()+","+
								dF.getArtStartEncounterId()+",NULL,"+
								dF.getFirstARVDate()+",42,NULL,NULL,NULL,NULL,NULL,"+
								dF.getFirstARVDate()+",NULL,NULL,NULL,NULL,NULL,1,"+
								dF.getFirstARVDate()+",0,NULL,NULL,"+UUID.randomUUID().toString()+",NULL)");

						logToConsole("\n Loading Selected Data...!!\n");
						logToConsole("\n Loading Selected Data...: " + dF.getArtStartDate().toString());
						logToConsole("\n Loading Selected Data...: " + dF.getPatientID());
						logToConsole("\n Loading Selected Data...: " + dF.getHivConfirmedConceptId());
						stmt.setString(1, dF.getArtStartDate().toString());
						stmt.setInt(2, dF.getPatientID());
						stmt.setInt(3, dF.getHivConfirmedConceptId());
						stmt.addBatch();
					}
				}
				//execute batch
				logToConsole("\n Updating Data....!\n");
				stmt.executeBatch();
				conn.commit();
				logToConsole("Date Updated successfully.");
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

	@FXML
	private void updateNextAppointmentDate(){

		int plus = Integer.parseInt(limit.getText());

		logToConsole("\n Initializing.!\n");

		String INSERT_SQL = "UPDATE obs SET value_datetime=DATE_ADD(?, INTERVAL "+plus+" DAY) WHERE obs_id=?";

		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
			Class.forName("com.mysql.jdbc.Driver");
			logToConsole("\n Connecting..!\n");
			conn.setAutoCommit(false);

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {

				for (LastCarePharmacy dF : nextAppointmentTable.getSelectionModel().getSelectedItems()) {
					logToConsole("\n Loading Selected Data...!!\n");
					logToConsole("\n Loading Selected Data...: "+dF.getPatientID());
					stmt.setString(1, dF.getLastCareEncounterDate().toString());
					stmt.setInt(2, dF.getObsID());
					stmt.addBatch();
				}
				//execute batch
				logToConsole("\n Updating Data....!\n");
				stmt.executeBatch();
				conn.commit();
				logToConsole("Date Updated successfully.");
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

	@FXML
	private void exportToExcel() throws IOException{
		exportCarePharm(lastCarePharmacyEncounterTable);
	}

	@FXML
	private void exportPharmToExcel() throws IOException{
		exportCarePharm(lastPharmacyEncounterTable);
	}

	private void exportCarePharm(TableView<LastCarePharmacy> table) throws IOException {

		Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("Export");

		Row row = spreadsheet.createRow(0);

		for (int j = 0; j < table.getColumns().size(); j++) {
			row.createCell(j).setCellValue(table.getColumns().get(j).getText());
		}

		for (int i = 0; i < table.getItems().size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < table.getColumns().size(); j++) {
				if(table.getColumns().get(j).getCellData(i) != null) {
					row.createCell(j).setCellValue(table.getColumns().get(j).getCellData(i).toString());
				}
				else {
					row.createCell(j).setCellValue("");
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream("export_"+LocalDateTime.now().toString()+".xls");
		workbook.write(fileOut);
		fileOut.close();

//		Platform.exit();

	}

	@FXML
	private void exportNextToExcel() throws IOException {

		TableView<LastCarePharmacy> table = nextAppointmentTable;

		Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("Export");

		Row row = spreadsheet.createRow(0);

		for (int j = 0; j < table.getColumns().size(); j++) {
			row.createCell(j).setCellValue(table.getColumns().get(j).getText());
		}

		for (int i = 0; i < table.getItems().size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < table.getColumns().size(); j++) {
				if(table.getColumns().get(j).getCellData(i) != null) {
					row.createCell(j).setCellValue(table.getColumns().get(j).getCellData(i).toString());
				}
				else {
					row.createCell(j).setCellValue("");
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream("next_appointment_"+LocalDateTime.now().toString()+".xls");
		workbook.write(fileOut);
		fileOut.close();

		//		Platform.exit();

	}

	@FXML
	public void exportDateFxToExcel() throws IOException {

		TableView<DateFix> table = dateFixTableView;

		Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("Export");

		Row row = spreadsheet.createRow(0);

		for (int j = 0; j < table.getColumns().size(); j++) {
			row.createCell(j).setCellValue(table.getColumns().get(j).getText());
		}

		for (int i = 0; i < table.getItems().size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < table.getColumns().size(); j++) {
				if(table.getColumns().get(j).getCellData(i) != null) {
					row.createCell(j).setCellValue("dates_"+table.getColumns().get(j).getCellData(i).toString());
				}
				else {
					row.createCell(j).setCellValue("");
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(LocalDateTime.now().toString()+".xls");
		workbook.write(fileOut);
		fileOut.close();

//		Platform.exit();

	}

	@FXML
	private void pharmToExcel() throws IOException {

		TableView<PharmacyEncounter> table = pharmacyEncounterTable;

		Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("Export");

		Row row = spreadsheet.createRow(0);

		for (int j = 0; j < table.getColumns().size(); j++) {
			row.createCell(j).setCellValue(table.getColumns().get(j).getText());
		}

		for (int i = 0; i < table.getItems().size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < table.getColumns().size(); j++) {
				if(table.getColumns().get(j).getCellData(i) != null) {
					row.createCell(j).setCellValue("pharm_"+table.getColumns().get(j).getCellData(i).toString());
				}
				else {
					row.createCell(j).setCellValue("");
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(LocalDateTime.now().toString()+".xls");
		workbook.write(fileOut);
		fileOut.close();

//		Platform.exit();

	}


	@FXML
	private void coCommencementToExcel() throws IOException {

		TableView<PatientStatus> table = noCommencementTableView;

		Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("Export");

		Row row = spreadsheet.createRow(0);

		for (int j = 0; j < table.getColumns().size(); j++) {
			row.createCell(j).setCellValue(table.getColumns().get(j).getText());
		}

		for (int i = 0; i < table.getItems().size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < table.getColumns().size(); j++) {
				if(table.getColumns().get(j).getCellData(i) != null) {
					row.createCell(j).setCellValue(table.getColumns().get(j).getCellData(i).toString());
				}
				else {
					row.createCell(j).setCellValue("");
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream("No_Commencement_"+LocalDateTime.now().toString()+".xls");
		workbook.write(fileOut);
		fileOut.close();

		//		Platform.exit();

	}

	private void fetchPatientStatus(){
		appConsole.clear();
		patientStatusTask = new Task<ObservableList<PatientStatus>>(){

			@Override
			protected ObservableList<PatientStatus> call() {

				String sql = "SELECT pa.patient_id as patientID, "
						+ "patient_identifier.identifier AS pepfarID,"
						+ "CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName ,"
						+ "(SELECT value_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
						+ "where encounter.form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 "
						+ "AND obs.voided = 0 order by obs.obs_id ASC Limit 1) AS ArtStartDate, "
						+ "(SELECT value_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
						+ "obs.concept_id=7777822 AND encounter.form_id=56 AND obs.voided = 0 AND encounter.voided=0 "
						+ "AND encounter.patient_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS NextAppointmentDATE, "
						+ "(SELECT value_text from obs where "
						+ "(obs.concept_id=7778430 || obs.concept_id=891) AND voided = 0 AND obs.person_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS PhoneNumber, "
						+ "(select concept_name.name FROM obs LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
						+ "AND (obs.concept_id=1737 || obs.concept_id=977) AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
						+ "AND concept_name.locale_preferred = 1 order by value_coded DESC Limit 1) AS Exited, "
						+ "(SELECT encounter_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
						+ "obs.concept_id=7777822 AND encounter.form_id=56 AND obs.voided = 0 AND encounter.voided=0 "
						+ "AND encounter.patient_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS LastDrugPickUpDate "
						+ " FROM patient as pa "
						+ " LEFT JOIN patient_identifier on patient_identifier.patient_id = pa.patient_id && identifier_type = 4"
						+ " LEFT JOIN person_name on person_name.person_id = pa.patient_id && person_name.preferred = 1 "
						+ " LEFT JOIN patient_program on patient_program.patient_id = pa.patient_id"
						+ " WHERE "
						+ " program_id = 3 AND patient_program.voided=0"
						+ " AND NOT EXISTS (SELECT * from encounter where form_id = 1 and patient_id = pa.patient_id)"
						+ " order by pa.patient_id DESC ";

//				Controller ctrl = new Controller();
				ObservableList<PatientStatus> patientStatuses = FXCollections.observableArrayList();
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

					logToConsole("\n Fetching Appointment List Please wait...");

					stmt = conn.createStatement();

					//logToConsole("\n Fetching Obs starting from .."+LocalDate.parse(startDate.getValue().toString() , formatter));
					ResultSet rs = stmt.executeQuery(sql);
					//STEP 5: Extract data from result set
					while (rs.next()) {
						PatientStatus patientStatus = new PatientStatus();
						patientStatus.setPatientID(rs.getInt("patientID"));
						//						patientStatus.setEncounterID(rs.getInt("encounterID"));
						//						patientStatus.setObsID(rs.getInt("obsID"));
						patientStatus.setPepfarID(rs.getString("pepfarID"));
						patientStatus.setPatientName(rs.getString("patientName"));
						if(rs.getDate("NextAppointmentDATE") != null) {
							patientStatus.setNextAppointmentDate(rs.getDate("NextAppointmentDATE"));
							patientStatus.setDrugDuration(Duration.between(
									LocalDate.now().atStartOfDay(),
									LocalDate.parse(rs.getDate("NextAppointmentDATE").toString(), formatter).atStartOfDay()
							).toDays());
						}
						if(rs.getDate("LastDrugPickUpDate") != null) {
							patientStatus.setLastARVDate(rs.getDate("LastDrugPickUpDate"));
						}
						if(rs.getString("Exited") != null) {
							patientStatus.setStatus(rs.getString("Exited"));
						}else{
							if(patientStatus.getDrugDuration() != null) {
								if (patientStatus.getDrugDuration() >= 0) {
									patientStatus.setStatus("Active");
								} else if (patientStatus.getDrugDuration() >= -28) {
									patientStatus.setStatus("Missed Appointment");
								} else {
									patientStatus.setStatus("Lost to Follow Up");
								}
							}
						}
						patientStatus.setPatientPhoneNumber(rs.getString("PhoneNumber"));

						patientStatuses.add(patientStatus);
						logToConsole(patientStatus.toString()+"\n");
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
					if (patientStatuses.isEmpty()) {
						logToConsole("\n No Record Found..");
					} else {
						Platform.runLater(() -> {
								noCommencementTableView.setItems(patientStatuses);
						});
					}
				}catch (Exception ex){
					logToConsole("\n Error: " + ex.getMessage());
					ex.printStackTrace();
				}
				return patientStatuses;
			}
		};
	}

	private void getNoAddress(){
		appConsole.clear();
		addressTask = new Task<ObservableList<Address>>(){

			@Override
			protected ObservableList<Address> call() {

				addressTableView.getItems().removeAll();

				String sql = "SELECT pa.patient_id as patientID, "
						+ "(SELECT state_province From person_address where person_address.person_id = pa.patient_id && person_address.preferred = 1) AS stateProvince, "
						+ "(SELECT city_village From person_address where person_address.person_id = pa.patient_id && person_address.preferred = 1) AS cityVillage, "
						+ "(SELECT address1 From person_address where person_address.person_id = pa.patient_id && person_address.preferred = 1) AS Address1, "
						+ "(SELECT address2 From person_address where person_address.person_id = pa.patient_id && person_address.preferred = 1) AS Address2, "						+ "patient_identifier.identifier AS pepfarID,"
						+ "CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName ,"
						+ "(SELECT value_text from obs where "
						+ "(obs.concept_id=7778430 || obs.concept_id=891) AND voided = 0 AND obs.person_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS PhoneNumber "
						+ " FROM patient as pa "
						+ " LEFT JOIN patient_identifier on patient_identifier.patient_id = pa.patient_id && identifier_type = 4"
						+ " LEFT JOIN person_name on person_name.person_id = pa.patient_id && person_name.preferred = 1 "
//						+ " LEFT JOIN person_address on person_address.person_id = pa.patient_id && person_address.preferred = 1 "
//						+ " WHERE NOT EXISTS (select * from person_address where person_id=pa.patient_id) "
//						+ " HAVING stateProvince = ''"
						+ " order by pa.patient_id DESC ";

				Controller ctrl = new Controller();
				ObservableList<Address> patientStatuses = FXCollections.observableArrayList();
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

					logToConsole("\n Fetching Appointment List Please wait...");

					stmt = conn.createStatement();

					//logToConsole("\n Fetching Obs starting from .."+LocalDate.parse(startDate.getValue().toString() , formatter));
					ResultSet rs = stmt.executeQuery(sql);
					//STEP 5: Extract data from result set
					while (rs.next()) {
						if(rs.getString("cityVillage") == null || rs.getString("stateProvince") == null) {
							Address patientStatus = new Address();
							patientStatus.setPatientID(rs.getInt("patientID"));
							//						patientStatus.setEncounterID(rs.getInt("encounterID"));
							//						patientStatus.setObsID(rs.getInt("obsID"));
							patientStatus.setPepfarID(rs.getString("pepfarID"));
							patientStatus.setPatientName(rs.getString("patientName"));

							patientStatus.setState(rs.getString("stateProvince"));
							patientStatus.setTown(rs.getString("cityVillage"));
							patientStatus.setAddress1(rs.getString("Address1"));
							patientStatus.setAddress2(rs.getString("Address2"));

							patientStatuses.add(patientStatus);
							logToConsole(patientStatus.toString() + "\n");
						}
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
					if (patientStatuses.isEmpty()) {
						logToConsole("\n No Record Found..");
					} else {
						Platform.runLater(() -> {
							addressTableView.setItems(patientStatuses);
						});
					}
				}catch (Exception ex){
					logToConsole("\n Error: " + ex.getMessage());
					ex.printStackTrace();
				}
				return patientStatuses;
			}
		};
	}

	@FXML
	private void getPatientAddress(){
		getNoAddress();
		new Thread(addressTask).start();
	}
//
//	@FXML
//	private void getPatientStatus(){
//		fetchPatientStatus(true);
//		new Thread(patientStatusTask).start();
//	}

	private void getExcess(){

		if(observationCombo.getSelectionModel().getSelectedItem() == null){
			logToConsole("\n Select Observation");
			return;
		}
		if(criteriaCombo.getSelectionModel().getSelectedItem() == null){
			logToConsole("\n Select Criteria");
			return;
		}
		weightHeightStatus.setText("Fetching Data ...");
		weightHeightStatus.setStyle("-fx-text-fill: brown");

		weightHeightTask = new Task<ObservableList<PharmacyEncounter>>() {

			@Override
			protected ObservableList<PharmacyEncounter> call() throws Exception {

				ObservableList<PharmacyEncounter> lastCarePharmacyEncounters = FXCollections.observableArrayList();
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

					String sql = "SELECT pa.patient_id as patientID, enc.encounter_id as encounterID, "
							+ "DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 AS birthdate, "
							+ "(Select identifier FROM patient_identifier where patient_id = pa.patient_id && identifier_type = 4) AS pepfarID,"
							+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName "
							+ "FROM person_name where person_id = pa.patient_id && preferred = 1) As patientName,"
							+ "enc.encounter_datetime AS LastCareEncounterDate, "
							+ " ob.value_numeric AS Answer , "
							+ "ob.obs_id As ObsID ,"
//							+ " (SELECT value_coded from obs where (obs.encounter_id = enc.encounter_id AND obs.concept_id=7777821) Limit 1) AS NextAppointment,"
							+ "(Select name from concept_name where concept_name.concept_id = ob.concept_id "
							+ "&& concept_name.locale ='en' && concept_name.locale_preferred = 1 AND voided = 0) AS Question"
							+ "  FROM encounter AS enc LEFt JOIN patient as pa "
							+ "on pa.patient_id = enc.patient_id "
							+ " LEFT JOIN obs as ob on ob.encounter_id = enc.encounter_id"
							+ " LEFT JOIN person on person.person_id = enc.patient_id "
							+ " where ";
//							+ " enc.form_id = 56 "

					if(observationCombo.getSelectionModel().getSelectedItem() == "Weight"){
						sql += " ob.concept_id IN (1734,7778168,1087,1022,1017,951,729,715,640,426,198,193,85,30)";
					}else if(observationCombo.getSelectionModel().getSelectedItem() == "Height"){
						sql+= " ob.concept_id IN (442,571,1735)";
					}else if(observationCombo.getSelectionModel().getSelectedItem() == "Drug Duration Number"){
						sql+= " ob.concept_id IN (7778370)";
					}
					sql += " AND enc.voided = 0";
					if(criteriaCombo.getSelectionModel().getSelectedItem() == "Greater Than"){
						sql += " AND ob.value_numeric >  "+Integer.parseInt(valueToSearch.getText());
					}else{
						sql += " AND ob.value_numeric <  "+Integer.parseInt(valueToSearch.getText());
					}
					sql += " order by enc.encounter_datetime DESC";
					ResultSet rs = stmt.executeQuery(sql);

					int tSum = 0;
					while (rs.next()) {
						++tSum;
						PharmacyEncounter lastCarePharmacy = new PharmacyEncounter();
						lastCarePharmacy.setPatientID(rs.getInt("patientID"));
						lastCarePharmacy.setEncounterID(rs.getInt("encounterID"));
						lastCarePharmacy.setPepfarID(rs.getString("pepfarID"));
						lastCarePharmacy.setPatientName(rs.getString("patientName"));
						lastCarePharmacy.setEncounterDateTime(rs.getDate("LastCareEncounterDate"));
						lastCarePharmacy.setValueNumeric(rs.getInt("Answer"));
						lastCarePharmacy.setQuestion(rs.getString("Question"));
						lastCarePharmacy.setObsID(rs.getInt("ObsID"));
						logToConsole("\n Patient Age is: "+rs.getInt("birthdate"));
						lastCarePharmacyEncounters.add(lastCarePharmacy);
					}
					logToConsole("\n Total Number: "+tSum);
					rs.close();
					logToConsole("\n Done..");
				} catch (SQLException e) {
					weightHeightStatus.setText("Error! ...");
					weightHeightStatus.setStyle("-fx-text-fill: brown");
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
						weightHeightStatus.setText("No Record Found ...");
						weightHeightStatus.setStyle("-fx-text-fill: brown");
					}
				}catch (Exception ex){
					logToConsole("\n Error: " + ex.getMessage());
					ex.printStackTrace();
				}
				Platform.runLater(()->{
					weightHeightTable.setItems(lastCarePharmacyEncounters);
					weightHeightStatus.setText("Data Fetched ...");
					weightHeightStatus.setStyle("-fx-text-fill: darkgreen");
				});

				return lastCarePharmacyEncounters;
			}
		};
	}

	@FXML
	private void getWeightHeight(){
		getExcess();

		new Thread(weightHeightTask).start();
	}

	@FXML
	private void updateWeightHeight(){

		if(valueToAdd.getText() == null) {
			logToConsole("\n Can not Add Empty Value");
			return;
		}else if(Integer.parseInt(valueToAdd.getText()) < 1 || Integer.parseInt(valueToAdd.getText()) > 200){
			logToConsole("\n Value is out of Range");
			return;
		}

		logToConsole("\n Initializing.!\n");

		String INSERT_SQL = "UPDATE obs SET value_numeric=? WHERE obs_id=?";

		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
			Class.forName("com.mysql.jdbc.Driver");
			logToConsole("\n Connecting..!\n");
			conn.setAutoCommit(false);

			try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {

				for (PharmacyEncounter dF : weightHeightTable.getSelectionModel().getSelectedItems()) {

						logToConsole("\n Loading Selected Data...!!\n");
						stmt.setInt(1, Integer.parseInt(valueToAdd.getText()));
						stmt.setInt(2, dF.getObsID());
						stmt.addBatch();

				}
				//execute batch
				logToConsole("\n Updating Data....!\n");
				stmt.executeBatch();
				conn.commit();
				logToConsole("Value Updated successfully.");
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

		getExcess();
		new Thread(weightHeightTask).start();
	}

	@FXML
	private void fetchNoArtCommencement(){
		fetchPatientStatus();
		new Thread(patientStatusTask).start();
	}

	private void fetchCareWOPharmacy(){

		appConsole.clear();
		logToConsole("#################### CHECKING DESTINATION DATABASE! \n");

//		lastPharmacyTask = new Task<ObservableList<LastCarePharmacy>>() {
//
//			@Override
//			protected ObservableList<LastCarePharmacy> call() throws Exception {
				ObservableList<LastCarePharmacy> lastCarePharmacyEncounters = FXCollections.observableArrayList();

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

					String sql = "SELECT pa.patient_id as patientID, enc.encounter_id as encounterID, "
							+ "DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 AS birthdate, "
							//+ "ob.obs_id AS obsID, "
							+ "(Select identifier FROM patient_identifier where patient_id = pa.patient_id && identifier_type = 4) AS pepfarID,"
							+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName FROM "
							+ "person_name where person_id = pa.patient_id && preferred = 1) As patientName,"
							+ "enc.encounter_datetime AS LastCareEncounterDate, "
							+ " ob.value_coded AS RegimenID , "
							+ " (SELECT value_coded from obs where (obs.encounter_id = enc.encounter_id AND obs.concept_id=7777821) Limit 1) AS NextAppointment,"
							+ "(Select name from concept_name where concept_name.concept_id = ob.value_coded && concept_name.locale ='en' "
							+ "&& concept_name.locale_preferred = 1) AS RegimenLine"
							+ "  FROM encounter AS enc LEFt JOIN patient as pa "
							+ "on pa.patient_id = enc.patient_id "
							+ " LEFT JOIN obs as ob on ob.encounter_id = enc.encounter_id"
							+ " LEFT JOIN person on person.person_id = enc.patient_id "
							+ " where "
							+ " enc.form_id IN (46,53) "
							+ " AND ob.concept_id = 7778111"
							+ " AND enc.voided = 0"
							+ " AND NOT EXISTS (select * FROM obs WHERE person_id = pa.patient_id AND concept_id=1737 AND voided = 0) "
							+ " AND NOT EXISTS (select * FROM obs WHERE person_id = pa.patient_id AND concept_id=977 AND voided = 0) "
							+ " AND NOT EXISTS (select encounter_datetime from encounter where encounter_datetime= enc.encounter_datetime AND patient_id = pa.patient_id AND encounter.form_id = 56)"
							+ " order by enc.encounter_datetime DESC Limit 15";
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

						lastCarePharmacy.setPatientAge(rs.getInt("birthdate"));
						logToConsole("\n Patient Age is: "+rs.getInt("birthdate"));

						logToConsole("\nPatient Age: "+lastCarePharmacy.getPatientAge());
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

						LastCarePharmacy lastCP  = getLastPharmacyEncounter(lastCarePharmacy,"care");
						if(lastCP != null) {
							lastCarePharmacy.setLastPharmacyEncounterDate(lastCP.getLastPharmacyEncounterDate());

							lastCarePharmacy.setLastPharmEncounterID(lastCP.getLastPharmEncounterID());
						}
						lastCarePharmacyEncounters.add(lastCarePharmacy);

						//logToConsole(lastCarePharmacy.toString()+"\n");
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
					}
				}catch (Exception ex){
					logToConsole("\n Error: " + ex.getMessage());
					ex.printStackTrace();
				}

//				Platform.runLater(()->{
					lastPharmacyEncounterTable.setItems(lastCarePharmacyEncounters);
//				});

//				return lastCarePharmacyEncounters;
//
//			}
//		};


	}

	@FXML
	private void getCareWOPharmacy(){
		fetchCareWOPharmacy();
//		new Thread(lastPharmacyTask).start();
	}
}
