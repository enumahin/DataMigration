package org.ccfng.viralload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ccfng.global.ConnectionClass;

public class Controller {

	@FXML
	private TextArea appConsole;

	ConnectionClass cc ;

	private Task<ObservableList<VL>> vlTask;

	@FXML
	TableView<VL> vlTableView;

	public Controller(){}

	public void initialize(){
		cc = new ConnectionClass();
	}

	public void logToConsole(String text) {
		Task<String> task = new Task<String>() {

			@Override
			protected String call() throws Exception {
				Platform.runLater(() -> {
					if (text != null)
						appConsole.appendText(text);
				});
				return null;
			}
		};

		new Thread(task).start();
	}

	public void processVLoad(){
		vlTask = new Task<ObservableList<VL>>() {

			@Override
			protected ObservableList<VL> call() throws Exception {

				String sql = "SELECT pa.patient_id as patientID, "
						+ "patient_identifier.identifier AS pepfarID,"
						+ "CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName ,"
						+ "(SELECT value_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
						+ "where encounter.form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 "
						+ "AND obs.voided = 0 order by obs.obs_id ASC Limit 1) AS ArtStartDate, "
						+ "(SELECT value_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
						+ "obs.concept_id=7777822 AND encounter.form_id=56 AND obs.voided = 0 AND encounter.voided=0 "
						+ "AND encounter.patient_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS NextAppointmentDATE, "
						+ "(SELECT encounter_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
						+ "obs.concept_id=315 AND obs.person_id=pa.patient_id AND obs.voided = 0 AND encounter.voided=0 "
						+ " order by obs.obs_id DESC Limit 1) AS LastVLDATE, "
						+ "(SELECT value_numeric FROM obs where "
						+ "obs.concept_id=315 AND obs.voided = 0 "
						+ "AND obs.person_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS LastVLCount, "
						+ "(SELECT value_text from obs where "
						+ "(obs.concept_id=7778430 || obs.concept_id=891) AND voided = 0 AND obs.person_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS PhoneNumber, "
						+ "(select concept_name.name FROM obs LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
						+ "AND (obs.concept_id=1737 || obs.concept_id=977) AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
						+ "AND concept_name.locale_preferred = 1 order by value_coded DESC Limit 1) AS Exited "
						+ " FROM patient as pa "
						+ " LEFT JOIN patient_identifier on patient_identifier.patient_id = pa.patient_id && identifier_type = 4"
						+ " LEFT JOIN person_name on person_name.person_id = pa.patient_id && person_name.preferred = 1 "
						+ " LEFT JOIN patient_program on patient_program.patient_id = pa.patient_id"
						+ " WHERE "
						+ " program_id = 3 AND patient_program.voided=0 AND"
						+ " pa.voided = 0 "
						+ " HAVING "
						+ "  (DATEDIFF(NextAppointmentDATE, STR_TO_DATE(CURRENT_DATE, '%Y-%m-%d')) > -28 AND Exited IS NULL)"
						+ " order by pa.patient_id DESC";

				ObservableList<VL> vls = FXCollections.observableArrayList();
				appConsole.clear();
				logToConsole("#################### CHECKING DESTINATION DATABASE! \n");

				Statement stmt = null;
				try {
					//STEP 2: Register JDBC driver
					Class.forName("com.mysql.jdbc.Driver");
				} catch (Exception exc) {
					logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
				}
				try (Connection conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());) {
					logToConsole("\n Source Database connection successful..");

					logToConsole("\n Fetching Eligibility List Please wait...");

					stmt = conn.createStatement();
					logToConsole("\n Query Created, Please wait...");
					logToConsole("\n Fetching Data Please wait...");
					ResultSet rs = stmt.executeQuery(sql);
					logToConsole("\n Raw Data Fetched...");
					logToConsole("\n Processing data, Please wait...");
					//STEP 5: Extract data from result set
					while (rs.next()) {
						if(rs.getDate("ArtStartDate") != null) {
							if(Duration.between(
									LocalDate.parse(rs.getDate("ArtStartDate").toString(),
											cc.getFormatter())
											.atStartOfDay(),
									LocalDate.now().atStartOfDay()
							).toDays() >= 180) {
								VL vl = new VL();
								if ( rs.getDate("LastVLDATE") == null || rs.getInt("LastVLCount") == 0){
//									logToConsole( "\n 1-"+rs.getDate("LastVLDATE"));
//									logToConsole( "\n 1-"+rs.getDate("ArtStartDate"));

									vl.setPatientID(rs.getInt("patientID"));
									vl.setPepfarID(rs.getString("pepfarID"));
									vl.setPatientName(rs.getString("patientName"));
									vl.setPatientPhoneNumber(rs.getString("PhoneNumber"));
									vl.setArtStartDate(rs.getDate("ArtStartDate"));
									vl.setLastVLDate(rs.getDate("LastVLDATE"));
									vl.setVlCount(rs.getInt("LastVLCount"));
									vl.setVlduedate( java.sql.Date.valueOf(rs.getDate("ArtStartDate").toLocalDate().plusDays(180)));									vls.add(vl);
								}
								 else if(

														Duration.between(
																LocalDate.parse(rs.getDate("LastVLDATE").toString(),
																		cc.getFormatter()).atStartOfDay(),
																LocalDate.now().atStartOfDay()
														).toDays() >= 90 && rs.getInt("LastVLCount") > 999
										)
								{

									vl.setPatientID(rs.getInt("patientID"));
									vl.setPepfarID(rs.getString("pepfarID"));
									vl.setPatientName(rs.getString("patientName"));
									vl.setPatientPhoneNumber(rs.getString("PhoneNumber"));
									vl.setArtStartDate(rs.getDate("ArtStartDate"));
									vl.setLastVLDate(rs.getDate("LastVLDATE"));
									vl.setVlCount(rs.getInt("LastVLCount"));
									vl.setVlduedate( java.sql.Date.valueOf(rs.getDate("LastVLDATE").toLocalDate().plusDays(90)));
									vls.add(vl);

								}else if(
														Duration.between(
																LocalDate.parse(rs.getDate("LastVLDATE").toString(),
																		cc.getFormatter()).atStartOfDay(),
																LocalDate.now().atStartOfDay()
														).toDays() >= 365 && rs.getInt("LastVLCount") < 1000

										) {

									vl.setPatientID(rs.getInt("patientID"));
									vl.setPepfarID(rs.getString("pepfarID"));
									vl.setPatientName(rs.getString("patientName"));
									vl.setPatientPhoneNumber(rs.getString("PhoneNumber"));
									vl.setArtStartDate(rs.getDate("ArtStartDate"));
									vl.setLastVLDate(rs.getDate("LastVLDATE"));
									vl.setVlCount(rs.getInt("LastVLCount"));
									vl.setVlduedate( java.sql.Date.valueOf(rs.getDate("LastVLDATE").toLocalDate().plusDays(365)));
									vls.add(vl);
									//logToConsole(patientStatus.toString() + "\n");
								}
//								logToConsole("\nVl Due Date: "+vl.getVlduedate());
							}
//							logToConsole( "\n"+rs.getDate("LastVLDATE"));
//							logToConsole( "\n"+rs.getDate("ArtStartDate"));
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
					if (vls.isEmpty()) {
						logToConsole("\n No Record Found..");
					} else {
						Platform.runLater(() -> {
							vlTableView.setItems(vls);
						});
					}
				}catch (Exception ex){
					logToConsole("\n Error: " + ex.getMessage());
					ex.printStackTrace();
				}
				return vls;
			}
		};
	}

	@FXML
	private void getVlEligibility(){
		processVLoad();
		new Thread(vlTask).start();
	}

	@FXML
	private void vlToExcel(){
		try{
			TableView<VL> table = vlTableView;

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

			FileOutputStream fileOut = new FileOutputStream("viral_load_" + LocalDateTime.now().toString() + ".xls");
			workbook.write(fileOut);
			fileOut.close();
		}catch(IOException ex){
			logToConsole("\n Error: "+ex.getMessage());
			ex.printStackTrace();
		}
		//		Platform.exit();

	}

}
