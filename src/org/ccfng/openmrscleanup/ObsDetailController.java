package org.ccfng.openmrscleanup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.ccfng.datamigration.Controller;
import org.ccfng.datamigration.session.SessionManager;
import org.ccfng.openmrscleanup.models.PharmacyEncounter;
import org.ccfng.openmrscleanup.models.Regimen;

public class ObsDetailController {
	@FXML
	private TableView<PharmacyEncounter> pharmacyEncounterTable;

	@FXML
	private TextArea appConsole;

	@FXML
	private ComboBox<Regimen> allRegimenComboBox;

	@FXML
	private ComboBox<Regimen> newRegimenComboBox;


	public String sourceHost;

	public String sourcePort;

	public String sourceUsername;

	public String sourcePassword;

	public String sourceDb;

	public String tableSuffix;

	public PharmacyEncounter pharmacyEncounterPub;

	String source_jdbcUrl = null;
	String driver = null;
	String source_username = null;
	String source_password = null;
	String dbTYPE = null;
	String suffix = null;
	File file = null;

	public ObsDetailController(){
	}

	public void logToConsole(String text) {
		Platform.runLater(() -> {
			if (text != null)
				appConsole.appendText(text);
		});

	}


	public void initVariable(PharmacyEncounter id_usuario){
		this.pharmacyEncounterPub = id_usuario;

	}

	public void initialize(){

		pharmacyEncounterTable.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE
		);

		logToConsole("Selected Regimen is "+SessionManager.activeObs.toString());
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
				logToConsole(row.getItem().toString());
			}
		});


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

		getDispensed();

	}

	@FXML
	private void getEncounterDetails(){

	}

	@FXML
	private void getDispensed(){
		Controller ctrl = new Controller();
		ObservableList<PharmacyEncounter> pharmacyEncounters = FXCollections.observableArrayList();
		appConsole.clear();
		logToConsole("#################### CHECKING DESTINATION DATABASE! \n");

		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception exc) {
			logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "");
		}
		try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword);) {
			logToConsole("\n Source Database connection successful..");

			stmt = conn.createStatement();

			String sql = "SELECT ob.person_id as patientID, encounter.encounter_id as encounterID, "
					+ "ob.obs_id AS obsID, "
					+ "(Select identifier FROM patient_identifier where patient_id = ob.person_id && identifier_type = 4 Limit 1) AS pepfarID,"
					+ "(Select CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName FROM person_name where person_id = ob.person_id && preferred = 1 Limit 1) As patientName,"
					+ "encounter.encounter_datetime AS EncounterDateTime, "
					+ "(Select name from concept_name where concept_name.concept_id = ob.concept_id && concept_name.locale ='en' && concept_name.locale_preferred = 1 Limit 1) AS Question,"
					+ "(Select name from concept_name where concept_name.concept_id = ob.value_coded && concept_name.locale ='en' && concept_name.locale_preferred = 1 Limit 1) AS Answer,"
					+ "ob.value_numeric AS ValueNumber,"
					+ "ob.obs_group_id AS ObsGroupId"
					+ " FROM obs AS ob LEFt JOIN encounter "
					+ "on ob.encounter_id = encounter.encounter_id where "
					+ " ob.encounter_id = "+SessionManager.activeObs.getEncounterID();
					//+ "ob.obs_group_id in (SELECT obs_id from obs as os where os.concept_id = 7778408 && os.encounter_id = "+SessionManager.activeObs.getEncounterID()+" group by os.obs_id)"
					//+ " order by ob.person_id";
			logToConsole("\n Fetching Obs..");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				PharmacyEncounter pharmacyEncounter = new PharmacyEncounter();
				pharmacyEncounter.setPatientID(rs.getInt("patientID"));
				pharmacyEncounter.setEncounterID(rs.getInt("encounterID"));
				pharmacyEncounter.setObsID(rs.getInt("obsID"));
				pharmacyEncounter.setObsGroupID(rs.getInt("ObsGroupID"));
				pharmacyEncounter.setPepfarID(rs.getString("pepfarID"));
				pharmacyEncounter.setPatientName(rs.getString("patientName"));
				pharmacyEncounter.setEncounterDateTime(rs.getDate("EncounterDateTime"));
				pharmacyEncounter.setQuestion(rs.getString("Question"));
				if(rs.getString("Answer") == null && rs.getString("ValueNumber") != null)
					pharmacyEncounter.setAnswer(rs.getString("ValueNumber"));
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

}
