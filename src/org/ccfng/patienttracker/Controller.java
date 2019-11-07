package org.ccfng.patienttracker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ccfng.openmrscleanup.models.PatientStatus;


public class Controller {


	@FXML
	private TextField gThan;

	@FXML
	private TextField lThan;

	@FXML
	private TextField tRecords;

	private String source_jdbcUrl = null;

	private String sourceHost;

	private String sourcePort;

	private String sourceUsername;

	private String sourcePassword;

	private String sourceDb;

	@FXML
	private TextArea appConsole;

	private Task<ObservableList<PatientStatus>> patientStatusTask;

	@FXML
	public TableView<PatientStatus> patientStatusTableView;

	DateTimeFormatter formatter;

	public Controller() {
	}

	public void initialize(){
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
				source_jdbcUrl = "jdbc:mysql://" +this.sourceHost + ":" + this.sourcePort + "/" + sourceDb +
						"?useServerPrepStmts=false&rewriteBatchedStatements=true";
			}
			logToConsole("\n Database Config Fetched!!");
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
			e.printStackTrace();
		}
	}

	public void logToConsole(String text) {
		Platform.runLater(() -> {
			if (text != null)
				appConsole.appendText(text);
		});
	}

	private void fetchPatientStatus(){
		appConsole.clear();
		patientStatusTask = new Task<ObservableList<PatientStatus>>(){

			@Override
			protected ObservableList<PatientStatus> call() {

				patientStatusTableView.getItems().removeAll();

				String sql = "SELECT pa.patient_id as patientID, "
						+ "patient_identifier.identifier AS pepfarID,"
						+ "CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName ,"
						+ "(SELECT value_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
						+ "where encounter.form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 "
						+ "AND obs.voided = 0 order by encounter.encounter_datetime ASC Limit 1) AS ArtStartDate, "

						+ "(SELECT value_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
						+ "obs.concept_id=7777822 AND encounter.form_id=56 AND obs.voided = 0 AND encounter.voided=0 "
						+ "AND encounter.patient_id=pa.patient_id order by encounter.encounter_datetime DESC Limit 1) AS NextAppointmentDATE, "

						+ "(SELECT value_text from obs where "
						+ "(obs.concept_id=7778430 || obs.concept_id=891) AND voided = 0 AND obs.person_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS PhoneNumber, "

						+ "(select concept_name.name FROM obs LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
						+ "AND (obs.concept_id=1737 || obs.concept_id=977) AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
						+ "AND concept_name.locale_preferred = 1 order by value_coded DESC Limit 1) AS Exited, "

						+ "(SELECT encounter_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
						+ "obs.concept_id=7777822 AND encounter.form_id=56 AND obs.voided = 0 AND encounter.voided=0 "
						+ "AND encounter.patient_id=pa.patient_id order by encounter.encounter_datetime DESC Limit 1) AS LastDrugPickUpDate "
						+ " FROM patient as pa "
						+ " LEFT JOIN patient_identifier on patient_identifier.patient_id = pa.patient_id && identifier_type = 4"
						+ " LEFT JOIN person_name on person_name.person_id = pa.patient_id && person_name.preferred = "
						+ "( select MAX(preferred) from person_name where person_name.person_id = pa.patient_id)"
						+ " LEFT JOIN patient_program on patient_program.patient_id = pa.patient_id"
						+ " WHERE "
						+ " program_id = 3 AND patient_program.voided=0 AND"
						+ " pa.voided = 0 "
						+ " HAVING (DATEDIFF(NextAppointmentDATE, STR_TO_DATE(CURRENT_DATE, '%Y-%m-%d'))  < "+Integer.parseInt(lThan.getText())
						+ " AND DATEDIFF(NextAppointmentDATE, STR_TO_DATE(CURRENT_DATE, '%Y-%m-%d')) > "+Integer.parseInt(gThan.getText())
						+ ") "
						+ " Limit "+Integer.parseInt(tRecords.getText());

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
				try (Connection conn = DriverManager.getConnection(source_jdbcUrl, sourceUsername, sourcePassword)) {
					logToConsole("\n Source Database connection successful..");

					logToConsole("\n Fetching Appointment List Please wait...");

					stmt = conn.createStatement();
					logToConsole("\n Query Created, Please wait...");
					logToConsole("\n Fetching Data Please wait...");
					ResultSet rs = stmt.executeQuery(sql);
					logToConsole("\n Raw Data Fetched...");
					logToConsole("\n Processing data, Please wait...");
					//STEP 5: Extract data from result set
					while (rs.next()) {

							PatientStatus patientStatus = new PatientStatus();
							patientStatus.setPatientID(rs.getInt("patientID"));
							//						patientStatus.setEncounterID(rs.getInt("encounterID"));
							//						patientStatus.setObsID(rs.getInt("obsID"));
							patientStatus.setPepfarID(rs.getString("pepfarID"));
							patientStatus.setPatientName(rs.getString("patientName"));
							if (rs.getDate("NextAppointmentDATE") != null) {
								patientStatus.setNextAppointmentDate(rs.getDate("NextAppointmentDATE"));
								patientStatus.setDrugDuration(Duration.between(
										LocalDate.now().atStartOfDay(),
										LocalDate.parse(rs.getDate("NextAppointmentDATE").toString(), formatter)
												.atStartOfDay()
								).toDays());
							}
							if (rs.getDate("LastDrugPickUpDate") != null) {
								patientStatus.setLastARVDate(rs.getDate("LastDrugPickUpDate"));
							}
							if (rs.getString("Exited") != null) {
								patientStatus.setStatus(rs.getString("Exited"));
							} else {
								if (patientStatus.getDrugDuration() != null) {
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
							//logToConsole(patientStatus.toString() + "\n");

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
								patientStatusTableView.setItems(patientStatuses);
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
	private void getPatientStatus(){
		fetchPatientStatus();
		new Thread(patientStatusTask).start();
	}

	@FXML
	private void appointmentToExcel(){
		try{
			TableView<PatientStatus> table = patientStatusTableView;

			Workbook workbook = new HSSFWorkbook();
			Sheet spreadsheet = workbook.createSheet("Export");

			Row row = spreadsheet.createRow(0);

			for (int j = 0; j < (table.getColumns().size() - 1); j++) {
				row.createCell(j).setCellValue(table.getColumns().get(j + 1).getText());
			}

			for (int i = 0; i < table.getItems().size(); i++) {
				row = spreadsheet.createRow(i + 1);
				for (int j = 0; j < (table.getColumns().size() - 1); j++) {
					if (table.getColumns().get(j + 1).getCellData(i) != null) {
						row.createCell(j).setCellValue(table.getColumns().get(j + 1).getCellData(i).toString());
					} else {
						row.createCell(j).setCellValue("");
					}
				}
			}

			FileOutputStream fileOut = new FileOutputStream("missed_appointment_" + LocalDateTime.now().toString() + ".xls");
			workbook.write(fileOut);
			fileOut.close();
		}catch(IOException ex){
			logToConsole("\n Error: "+ex.getMessage());
			ex.printStackTrace();
		}
		//		Platform.exit();

	}
}
