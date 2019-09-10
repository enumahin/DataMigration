package org.ccfng.linelist;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ccfng.datamigration.patient.Patient;
import org.ccfng.global.ConnectionClass;
import org.ccfng.global.DBMiddleMan;

public class Controller extends org.ccfng.datamigration.Controller {

	@FXML
	private TextArea appConsole;

	ConnectionClass cc ;

	private Task<ObservableList<LineList>> lineListTask;

	@FXML
	TableView<LineList> lineListTableView;

	@FXML
	private ProgressBar progrssBar;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private Label lblCount;

	@FXML
	private Label lblTotal;

	public Controller(){}

	public void initialize(){

		cc = new ConnectionClass();
//
//		new Thread(() ->{
//			Platform.runLater(()->{
//				System.out.println(DBMiddleMan.allObs);
//			});
//		}).start();
//

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



//	public void processVLoad(){
//		lineListTask = new Task<ObservableList<LineList>>() {
//
//			@Override
//			protected ObservableList<LineList> call() throws Exception {
//
//				String sql = "SELECT pa.patient_id as patientID, person_address.address1 AS Address1, "
//						+ "DATEDIFF(CURRENT_DATE, STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 AS birthdate,"
//						+ " person_address.address2 AS Address2, "
//						+ "patient_identifier.identifier AS pepfarID, person.gender AS Sex, "
//						+ "pi.identifier AS hospitalNumber,"
//						+ "CONCAT(person_name.given_name,' ',person_name.family_name) AS patientName ,"
//						+ "(SELECT value_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
//						+ "where encounter.form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 "
//						+ "AND obs.voided = 0 order by encounter.encounter_datetime ASC Limit 1) AS ArtStartDate, "
//
//						+ " DATEDIFF((SELECT value_datetime from obs left join encounter on obs.encounter_id = encounter.encounter_id "
//						+ "where encounter.form_id=1 AND encounter.patient_id=pa.patient_id AND obs.concept_id=863 "
//						+ "AND obs.voided = 0 order by encounter.encounter_datetime ASC Limit 1), STR_TO_DATE(person.birthdate, '%Y-%m-%d'))/365 AS ageAtArtStart,"
//
//						+ "(SELECT value_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
//						+ "obs.concept_id=7777822 AND encounter.form_id=56 AND obs.voided = 0 AND encounter.voided=0 "
//						+ "AND encounter.patient_id=pa.patient_id order by encounter.encounter_datetime DESC Limit 1) AS NextAppointmentDATE, "
//
//						+ "(SELECT encounter_datetime from encounter Left join obs on encounter.encounter_id = obs.encounter_id where "
//						+ "obs.concept_id=315 AND obs.person_id=pa.patient_id AND obs.voided = 0 AND encounter.voided=0 "
//						+ " order by encounter.encounter_datetime DESC Limit 1) AS LastVLDATE, "
//
//						+ "(SELECT encounter_datetime from encounter left join obs on encounter.encounter_id=obs.encounter_id where encounter.voided=0 AND encounter.patient_id = pa.patient_id"
//						+ " AND (encounter.form_id = 46 || encounter.form_id=53) AND obs.concept_id=7778111 order by encounter.encounter_datetime DESC Limit 1) AS LastArtPickUp, "
//
//						+ "(SELECT value_numeric FROM obs left join encounter on obs.encounter_id = encounter.encounter_id where "
//						+ "obs.concept_id=315 AND obs.voided = 0 "
//						+ "AND obs.person_id=pa.patient_id order by encounter.encounter_datetime DESC Limit 1) AS LastVLCount, "
//
//						+ "(SELECT value_text from obs where "
//						+ "(obs.concept_id=7778430 || obs.concept_id=891) AND voided = 0 AND obs.person_id=pa.patient_id order by obs.obs_id DESC Limit 1) AS PhoneNumber, "
//
//						+ "(select concept_name.name FROM obs left join encounter on obs.encounter_id = encounter.encounter_id"
//						+ " LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
//						+ "AND obs.concept_id=7778531 AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
//						+ "AND concept_name.locale_preferred = 1 order by encounter.encounter_datetime ASC Limit 1) AS RegimenLineAtStart, "
//
//						+ "(select concept_name.name FROM obs left join encounter on obs.encounter_id = encounter.encounter_id LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
//						+ "AND (obs.concept_id=7778532 || obs.concept_id=7778533) AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
//						+ "AND concept_name.locale_preferred = 1 order by encounter.encounter_datetime ASC Limit 1) AS RegimenAtStart, "
//
//						+ "(select concept_name.name FROM obs left join encounter on obs.encounter_id = encounter.encounter_id LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
//						+ "AND obs.concept_id=7778111 AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
//						+ "AND concept_name.locale_preferred = 1 order by encounter.encounter_datetime DESC Limit 1) AS CurrentRegimenLine, "
//
//						+ "(select concept_name.name FROM obs left join encounter on obs.encounter_id = encounter.encounter_id LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
//						+ "AND (obs.concept_id=7778108 || obs.concept_id=7778109 || obs.concept_id=7778410) AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
//						+ "AND concept_name.locale_preferred = 1 order by encounter.encounter_datetime DESC Limit 1) AS CurrentRegimen, "
//
//						+ "(select concept_name.name FROM obs LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
//						+ "AND obs.value_coded=47 AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
//						+ "AND concept_name.locale_preferred = 1 AND obs.encounter_id = "
//						+ "(select MAX(encounter_id) from encounter where patient_id=pa.patient_id AND form_id = 56 order by encounter_datetime DESC limit 1) order by obs.obs_id ASC Limit 1) AS Pregnant, "
//
//						+ "(select concept_name.name FROM obs LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
//						+ "AND (obs.concept_id=1737 || obs.concept_id=977) AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
//						+ "AND concept_name.locale_preferred = 1 AND NOT EXISTS (select person_id from obs where person_id=pa.patient_id AND concept_id=1726 And voided = 0 limit 1) order by value_coded DESC Limit 1) AS Exited, "
//
//						+ "(select concept_name.name FROM obs left join encounter on obs.encounter_id = encounter.encounter_id LEFT JOIN concept_name on concept_name.concept_id = obs.value_coded WHERE person_id = pa.patient_id "
//						+ "AND obs.concept_id=7778013 AND obs.voided = 0 AND concept_name.voided = 0 AND concept_name.locale = 'en' "
//						+ "AND concept_name.locale_preferred = 1 order by encounter.encounter_datetime ASC Limit 1) AS ViralLoadIndication "
//
//						+ " FROM patient as pa "
//						+ " LEFT JOIN patient_identifier on patient_identifier.patient_id = pa.patient_id && patient_identifier.identifier_type = 4"
//						+ " LEFT JOIN patient_identifier AS pi on pi.patient_id = pa.patient_id && pi.identifier_type = 3"
//						+ " LEFT JOIN person on person.person_id = pa.patient_id "
//
//						+ " LEFT JOIN person_name on person_name.person_id = pa.patient_id && person_name.preferred = "
//							+ "( select MAX(preferred) from person_name where person_name.person_id = pa.patient_id)"
//
//						+ " LEFT JOIN person_address on person_address.person_id = pa.patient_id && person_address.preferred = "
//						+ "( select MAX(preferred) from person_address where person_address.person_id = pa.patient_id)"
//
//						+ " LEFT JOIN patient_program on patient_program.patient_id = pa.patient_id"
//						+ " WHERE "
//						+ " program_id = 3 AND patient_program.voided=0 AND"
//						+ " pa.voided = 0";
//
//				ObservableList<LineList> vls = FXCollections.observableArrayList();
//				appConsole.clear();
//				logToConsole("#################### CHECKING DESTINATION DATABASE! \n");
//
//				PreparedStatement stmt = null;
//				try {
//					//STEP 2: Register JDBC driver
//					Class.forName("com.mysql.jdbc.Driver");
//				} catch (Exception exc) {
//					logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
//				}
//				try (Connection conn = DriverManager
//						.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());) {
//					logToConsole("\n Source Database connection successful..");
//
//					logToConsole("\n Fetching Line List Please wait...");
//
//					stmt = conn.prepareStatement(sql);
//					stmt.setFetchSize(500);
//					logToConsole("\n Query Created, Please wait...");
//					logToConsole("\n Fetching Data Please wait...");
//					ResultSet rs = stmt.executeQuery(sql);
//					rs.setFetchSize(10);
//					logToConsole("\n Raw Data Fetched...");
//					logToConsole("\n Processing data, Please wait...");
//					//STEP 5: Extract data from result set
//					while (rs.next()) {
//						System.out.println(rs.next());
//						LineList lL = new LineList();
//						lL.setPatientID(rs.getInt("patientID"));
//						lL.setPepfarID(rs.getString("pepfarID"));
//						lL.setPatientName(rs.getString("patientName"));
//						lL.setPatientPhoneNumber(rs.getString("PhoneNumber"));
//						lL.setCurrentViralLoad(rs.getInt("LastVLCount"));
//						if (rs.getString("Address1") != null)
//							lL.setAddress(rs.getString("Address1"));
//						else
//							lL.setAddress(rs.getString("Address2"));
//						lL.setHospitalNumber(rs.getString("hospitalNumber"));
//						lL.setSex(rs.getString("Sex"));
//						lL.setPatientAge(rs.getInt("birthdate"));
//
//						if (rs.getDate("ArtStartDate") != null) {
//							lL.setArtStartDate(rs.getDate("ArtStartDate"));
//							lL.setAgeAtArtStart(rs.getLong("ageAtArtStart"));
//						}
//						if (rs.getDate("LastArtPickUp") != null) {
//							lL.setLastPickUpDate(rs.getDate("LastArtPickUp"));
//						}
//
//						if (rs.getDate("NextAppointmentDATE") != null) {
//							lL.setNextAppointmentDate(rs.getDate("NextAppointmentDATE"));
//
//							lL.setNumberOfDaysMissedAppointment(Duration.between(
//									LocalDate.now().atStartOfDay(),
//									LocalDate.parse(rs.getDate("NextAppointmentDATE").toString(), cc.getFormatter())
//											.atStartOfDay()
//							).toDays());
//						}
//
//						if (rs.getDate("LastArtPickUp") != null && rs.getDate("NextAppointmentDATE") != null) {
//							lL.setDaysOfArtRefill(
//									Duration.between(
//											LocalDate.parse(rs.getDate("LastArtPickUp").toString(),
//													cc.getFormatter()).atStartOfDay(),
//											LocalDate.parse(rs.getDate("NextAppointmentDATE").toString(),
//													cc.getFormatter()).atStartOfDay()
//									).toDays()
//							);
//
//						}
//						if (rs.getDate("LastVLDATE") != null) {
//							lL.setDateOfCurrentViralLoad(rs.getDate("LastVLDATE"));
//						}
//						lL.setRegimenLineAtStart(rs.getString("RegimenLineAtStart"));
//						lL.setRegimenAtStart(rs.getString("RegimenAtStart"));
//						lL.setCurrentRegimenLine(rs.getString("CurrentRegimenLine"));
//						lL.setCurrentRegimen(rs.getString("CurrentRegimen"));
//						lL.setPregnancyStatus(rs.getString("Pregnant"));
//						lL.setViralLoadIndication(rs.getString("ViralLoadIndication"));
//						if (rs.getString("Exited") != null) {
//							lL.setCurrentArtStatus(rs.getString("Exited"));
//						} else {
//							if (lL.getNumberOfDaysMissedAppointment() != null) {
//								if (lL.getNumberOfDaysMissedAppointment() >= 0) {
//									lL.setCurrentArtStatus("Active");
//								} else if (lL.getNumberOfDaysMissedAppointment() >= -28) {
//									lL.setCurrentArtStatus("Missed Appointment");
//								} else {
//									lL.setCurrentArtStatus("InActive");
//								}
//
//							}
//						}
//						if (lL.getNumberOfDaysMissedAppointment() != null) {
//							if (lL.getNumberOfDaysMissedAppointment() >= -28) {
//								lL.setActiveBy28("Active");
//							} else {
//								lL.setActiveBy28("InActive");
//							}
//
//							if (lL.getNumberOfDaysMissedAppointment() >= -90) {
//								lL.setActiveBy90("Active");
//							} else {
//								lL.setActiveBy90("InActive");
//							}
//						}
//
//							vls.add(lL);
//							//logToConsole(patientStatus.toString() + "\n");
//						}
//
//					rs.close();
//					logToConsole("\n Done..");
//				} catch (SQLException e) {
//					logToConsole("\n Error: " + e.getMessage());
//					e.printStackTrace();
//				}finally {
//					//finally block used to close resources
//					try {
//						if (stmt != null);
//					} catch (Exception se) {
//					}// do nothing
//				}//end try
//				try {
//					if (vls.isEmpty()) {
//						logToConsole("\n No Record Found..");
//					} else {
//						Platform.runLater(() -> {
//							lineListTableView.setItems(vls);
//						});
//					}
//				}catch (Exception ex){
//					logToConsole("\n Error: " + ex.getMessage());
//					ex.printStackTrace();
//				}
//				return vls;
//			}
//		};
//	}
	private void processVLoad(){
		ObservableList<LineList> lineLists = FXCollections.observableArrayList();
		final int[] wDone = { 0 };
		Integer total = DBMiddleMan.allPatientsOnArt.size();
		Integer count = 0;

		lblTotal.setText(total.toString());
		lineListTask = new Task<ObservableList<LineList>>() {

			@Override
			protected ObservableList<LineList> call() throws Exception {

				for(Patient patient : DBMiddleMan.allPatientsOnArt) {
					updateProgress(wDone[0] + 1, total);
					Platform.runLater(()->{
						lblCount.setText(String.valueOf(wDone[0]));
					});
					wDone[0]++;
					LineList lineList = new LineList();
					lineList.setPatientID(patient.getPatient_id());

					DBMiddleMan.allPatientIdentifiers.stream()
							.filter(patientIdentifier ->
									patientIdentifier.getPatient_id().equals(patient.getPatient_id()) &&
									patientIdentifier.getIdentifier_type() == 4
							).findFirst().ifPresent(patientIdentifier -> lineList.setPepfarID(patientIdentifier.getIdentifier()));

					DBMiddleMan.allPeopleNames.stream()
							.filter(personName ->
									personName.getPerson_id().equals(patient.getPatient_id())
											&& personName.isPreferred()
							).findAny().ifPresent(pName -> lineList.setPatientName(pName.getGiven_name()+ " "+ pName.getFamily_name()));

					if (null == lineList.getPatientName()){
						DBMiddleMan.allPeopleNames.stream()
								.filter(personName ->
										personName.getPerson_id().equals(patient.getPatient_id())
								).findFirst().ifPresent(pName -> lineList.setPatientName(pName.getGiven_name()+ " "+ pName.getFamily_name()));

					}

					DBMiddleMan.allObs.stream()
							.filter(obs ->
									obs.getPerson_id().equals(patient.getPatient_id()) &&
											(obs.getConcept_id() == 7778430 || obs.getConcept_id() == 891)
							).findAny().ifPresent(phone -> lineList.setPatientPhoneNumber(phone.getValue_text()));

					DBMiddleMan.allPeopleAddresses.stream()
							.filter(personAdd ->
									personAdd.getPerson_id().equals(patient.getPatient_id())
											&& personAdd.isPreferred()
							).findAny().ifPresent(pAdd -> lineList.setAddress(pAdd.getAddress1() != null ? pAdd.getAddress1() : pAdd.getAddress2()));

					if (null == lineList.getAddress()){
						DBMiddleMan.allPeopleAddresses.stream()
								.filter(personAdd ->
										personAdd.getPerson_id().equals(patient.getPatient_id())
								).findAny().ifPresent(pAdd -> lineList.setAddress(pAdd.getAddress1() != null ? pAdd.getAddress1() : pAdd.getAddress2()));
					}

					DBMiddleMan.allPatientIdentifiers.stream()
							.filter(patientIdentifier ->
									patientIdentifier.getPatient_id().equals(patient.getPatient_id()) &&
											patientIdentifier.getIdentifier_type() == 3
							).findFirst().ifPresent(patientIdentifier -> lineList.setHospitalNumber(patientIdentifier.getIdentifier()));

					DBMiddleMan.allPeople.stream().filter(person ->
						person.getPerson_id().equals(patient.getPatient_id())
					).findFirst().ifPresent(pers -> {
						lineList.setSex(pers.getGender());
						lineList.setPatientAge(
								ChronoUnit.YEARS.between(LocalDate.parse(pers.getBirthdate().toString()), LocalDate.now())
						);
					});

					DBMiddleMan.allObs.stream()
							.filter(obs ->
									obs.getPerson_id().equals(patient.getPatient_id()) &&
											(obs.getConcept_id() == 863)
							).findAny().ifPresent(art -> {
								lineList.setArtStartDate(art.getValue_datetime());
								if(null != art.getValue_datetime()) {
//									System.out.println(currentPerson.getBirthdate());
									DBMiddleMan.allPeople.stream().filter(pSon -> pSon.getPerson_id().equals(patient.getPatient_id()))
											.findFirst().ifPresent(person -> lineList.setAgeAtArtStart(
											ChronoUnit.YEARS
													.between(LocalDate.parse(person.getBirthdate().toString()),
															LocalDate.parse(art.getValue_datetime().toString()))
									));

								}
					});

					 DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778111 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs -> {
								DBMiddleMan.allEncounters.stream().filter(enc -> enc.getEncounter_id().equals(obs.getEncounter_id()))
										.findFirst().ifPresent(encounter -> {
									lineList.setLastPickUpDate(
											java.sql.Date.valueOf(LocalDate.parse(encounter.getEncounter_datetime().toString())));
								});
					 });

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778111 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs -> {
						DBMiddleMan.allEncounters.stream().filter(enc -> enc.getEncounter_id().equals(obs.getEncounter_id()))
								.findFirst().ifPresent(encounter -> DBMiddleMan.allObs.stream()
										.filter(obs1 -> obs1.getConcept_id() == 7777822 && obs1.getPerson_id().equals(patient.getPatient_id()))
										.reduce((first, second) -> second).ifPresent(ob ->
										lineList.setDaysOfArtRefill(
												ChronoUnit.YEARS
														.between(LocalDate.parse(encounter.getEncounter_datetime().toString()),
																LocalDate.parse(ob.getValue_datetime().toString()))
										)
								));
					});

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7777822 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs ->
						lineList.setNextAppointmentDate(java.sql.Date.valueOf(obs.getValue_datetime().toString()))
					);

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778531 && obs.getPerson_id().equals(patient.getPatient_id()))
							.findFirst().ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
							.ifPresent(concept -> lineList.setRegimenLineAtStart(concept.getValue()))
					);

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778532 || obs.getConcept_id() == 7778533 && obs.getPerson_id().equals(patient.getPatient_id()))
							.findFirst().ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
									.ifPresent(concept -> lineList.setRegimenAtStart(concept.getValue()))
					);


					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778111 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
									.ifPresent(concept -> lineList.setCurrentRegimenLine(concept.getValue()))
					);

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778108 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
									.ifPresent(concept -> lineList.setCurrentRegimen(concept.getValue()))
					);

					DBMiddleMan.allEncounters.stream().filter(encounter -> encounter.getForm_id() == 56 && encounter.getPatient_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(enc ->
					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getValue_coded() == 47 && obs.getEncounter_id().equals(enc.getEncounter_id()))
//							.reduce((first, second) -> second)
							.findAny()
							.ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
									.ifPresent(concept -> lineList.setCurrentRegimen(concept.getValue()))
					)
					);

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 315 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs -> {
						DBMiddleMan.allEncounters.stream().filter(enc -> enc.getEncounter_id().equals(obs.getEncounter_id()))
								.findFirst().ifPresent(encounter -> {
									lineList.setCurrentViralLoad(obs.getValue_numeric().intValue());
							lineList.setDateOfCurrentViralLoad(
									java.sql.Date.valueOf(LocalDate.parse(encounter.getEncounter_datetime().toString())));
						});
					});

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778013 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
									.ifPresent(concept -> lineList.setViralLoadIndication(concept.getValue()))
					);

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7777822 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs -> {
								Long numMissed = ChronoUnit.DAYS.between(LocalDate.now(),obs.getValue_datetime().toLocalDate());
								lineList.setNumberOfDaysMissedAppointment(numMissed);
								if(numMissed >= 0) {
									lineList.setCurrentArtStatus("Active");
									lineList.setActiveBy28("Active");
									lineList.setActiveBy90("Active");
								}
								else if(numMissed >= -28) {
									lineList.setCurrentArtStatus("Missed Appointment");
									lineList.setActiveBy28("Active");
									lineList.setActiveBy90("Active");
								}
								else if(numMissed >= -90) {
									lineList.setCurrentArtStatus("LTFU");
									lineList.setActiveBy28("InActive");
									lineList.setActiveBy90("Active");
								}else{
									lineList.setCurrentArtStatus("LTFU");
									lineList.setActiveBy28("InActive");
									lineList.setActiveBy90("InActive");
								}
							}
					);

					lineLists.add(lineList);
				}

				return lineLists;
			}

		};

		Platform.runLater(() -> {
			lineListTableView.setItems(lineLists);
		});

		progressIndicator.progressProperty().bind(lineListTask.progressProperty());
		progrssBar.progressProperty().bind(lineListTask.progressProperty());

//		new Thread(lineListTask).start();
	}

	private Thread lineListThread = null;
	@FXML
	private void getLineList(){
		processVLoad();
		lineListThread = new Thread(lineListTask);
		lineListThread.setDaemon(true);
		lineListThread.start();

	}

	@FXML
	private void lLToExcel(){
		try{
			TableView<LineList> table = lineListTableView;

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

			FileOutputStream fileOut = new FileOutputStream("line_list_" + LocalDateTime.now().toString() + ".xls");
			workbook.write(fileOut);
			fileOut.close();
		}catch(IOException ex){
			logToConsole("\n Error: "+ex.getMessage());
			ex.printStackTrace();
		}
		//		Platform.exit();

	}

	public void shutdown(){
		if(this.lineListThread != null )
			if(this.lineListThread.isAlive()) {
			 this.lineListThread.stop();
				if (this.lineListTask != null)
					if (this.lineListTask.isRunning())
						this.lineListTask.cancel();
			}
	}

}
