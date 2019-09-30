package org.ccfng.linelist;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
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
import org.ccfng.datamigration.obs.Obs;
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

	@FXML
	private Label lblActiveBy28;

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

	private void processVLoad(){
		ObservableList<LineList> lineLists = FXCollections.observableArrayList();
		final int[] wDone = { 0 };
		Integer total = DBMiddleMan.allPatientsOnArt.size();
		final int[] activeBy28 = { 0 };

		lblTotal.setText(total.toString());
		lineListTask = new Task<ObservableList<LineList>>() {

			@Override
			protected ObservableList<LineList> call() throws Exception {

				for(Patient patient : DBMiddleMan.allPatientsOnArt
//						.stream()
//						.filter(patient -> patient.getPatient_id() == 19752
//								|| patient.getPatient_id() == 22348
//								|| patient.getPatient_id() == 22566
//								|| patient.getPatient_id() == 21380
//								|| patient.getPatient_id() == 20952).collect(Collectors
//						.toList())
						) {
					final int[] ddUnit = { 1 };
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
								).reduce((first, second)->second).ifPresent(pAdd ->
								lineList.setAddress(pAdd.getAddress1() != null ? pAdd.getAddress1() : pAdd.getAddress2()));
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

					DBMiddleMan.allObs.stream().filter(obs7 ->
						obs7.getPerson_id().equals(patient.getPatient_id()) && (obs7.getValue_coded() == 47 || obs7.getValue_coded() == 1259))
							.reduce((first, second) ->second).ifPresent(o -> {
								if(ChronoUnit.DAYS.between(LocalDate.parse(o.getEncounter().getEncounter_datetime().toString()),LocalDate.now()) < 270){
									lineList.setPregnancyStatus("Yes");
								}else{
									lineList.setPregnancyStatus("No");
								}
					});

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778111 && obs.getPerson_id().equals(patient.getPatient_id())
									&& obs.getEncounter().getEncounter_type() == 7)
							.reduce((first, second) -> second).ifPresent(obs -> {

						lineList.setLastPickUpDate(
								java.sql.Date.valueOf(obs.getEncounter().getEncounter_datetime().toString()));


						Obs unit = DBMiddleMan.allObs.stream()
								.filter(ob -> ob.getConcept_id() == 7778371 &&
										obs.getEncounter_id().equals(ob.getEncounter_id()))
								.findFirst().orElse(null);

						Obs drugDuration = DBMiddleMan.allObs.stream()
								.filter(ob -> ob.getConcept_id() == 7778370 &&
										obs.getEncounter_id().equals(ob.getEncounter_id()))
								.findFirst().orElse(null);

						Obs careObs = DBMiddleMan.allObs.stream()
								.filter(ob -> ob.getConcept_id() == 7778111 && ob.getPerson_id()
										.equals(obs.getPerson_id())
										&& ob.getEncounter().getForm_id() == 56 &&
										obs.getEncounter().getEncounter_datetime().equals(ob.getEncounter().getEncounter_datetime()))
								.findFirst().orElse(null);

							if(drugDuration != null){

								if(unit != null){
									if(unit.getValue_coded() == 524){
										ddUnit[0] = 30;
										System.out.println(drugDuration.getValue_numeric() * ddUnit[0]);
									}else if(unit.getValue_coded() == 520){
										ddUnit[0] = 7;
										System.out.println(drugDuration.getValue_numeric()  * ddUnit[0]);
									}
								}

								Date nextAppointmentDate = java.sql.Date.valueOf(LocalDate.parse(obs.getEncounter().getEncounter_datetime().toString())
										.plusDays(drugDuration.getValue_numeric().intValue() * ddUnit[0]));

								lineList.setNextAppointmentDate(nextAppointmentDate);

								lineList.setDaysOfArtRefill(drugDuration.getValue_numeric().longValue() * ddUnit[0]);

								Long numMissed = ChronoUnit.DAYS
										.between(LocalDate.now(), nextAppointmentDate.toLocalDate());

								lineList.setNumberOfDaysMissedAppointment(numMissed);

								Obs exited = patient.exited();

								if(exited != null) {
											DBMiddleMan.conceptNames.stream().filter(concept -> concept.getId().equals(exited.getValue_coded()))
													.findFirst().ifPresent(concept -> {
														lineList.setCurrentArtStatus(concept.getValue());
														lineList.setActiveBy28(concept.getValue());
														lineList.setActiveBy90(concept.getValue());
											});

								}else if (numMissed >= 0) {
									lineList.setCurrentArtStatus("Active");
									lineList.setActiveBy28("Active");
									lineList.setActiveBy90("Active");
									activeBy28[0]++;
									Platform.runLater(() -> {
										lblActiveBy28.setText(String.valueOf(activeBy28[0]));
									});
								} else if (numMissed >= -28) {
									lineList.setCurrentArtStatus("Missed Appointment");
									lineList.setActiveBy28("Active");
									lineList.setActiveBy90("Active");
									activeBy28[0]++;
									Platform.runLater(() -> {
										lblActiveBy28.setText(String.valueOf(activeBy28[0]));
									});
								} else if (numMissed >= -90) {
									lineList.setCurrentArtStatus("LTFU");
									lineList.setActiveBy28("InActive");
									lineList.setActiveBy90("Active");
								} else {
									lineList.setCurrentArtStatus("LTFU");
									lineList.setActiveBy28("InActive");
									lineList.setActiveBy90("InActive");
								}

							}else if(careObs != null){
								Obs nextAppointmentObs = careObs.getEncounter().getOb(7777822);

								if (nextAppointmentObs != null) {

									lineList.setNextAppointmentDate(nextAppointmentObs.getValue_datetime());

									lineList.setDaysOfArtRefill(
											ChronoUnit.DAYS
													.between(LocalDate
																	.parse(obs.getEncounter().getEncounter_datetime().toString()),
															LocalDate.parse(nextAppointmentObs.getValue_datetime().toString()))
									);

									Long numMissed = ChronoUnit.DAYS
											.between(LocalDate.now(), nextAppointmentObs.getValue_datetime().toLocalDate());
									lineList.setNumberOfDaysMissedAppointment(numMissed);

									Obs exited = patient.exited();

									if(exited != null) {
										DBMiddleMan.conceptNames.stream().filter(concept -> concept.getId().equals(exited.getValue_coded()))
												.findFirst().ifPresent(concept -> {
											lineList.setCurrentArtStatus(concept.getValue());
											lineList.setActiveBy28(concept.getValue());
											lineList.setActiveBy90(concept.getValue());
										});

									}else if (numMissed >= 0) {
										lineList.setCurrentArtStatus("Active");
										lineList.setActiveBy28("Active");
										lineList.setActiveBy90("Active");
										activeBy28[0]++;
										Platform.runLater(() -> {
											lblActiveBy28.setText(String.valueOf(activeBy28[0]));
										});
									} else if (numMissed >= -28) {
										lineList.setCurrentArtStatus("Missed Appointment");
										lineList.setActiveBy28("Active");
										lineList.setActiveBy90("Active");
										activeBy28[0]++;
										Platform.runLater(() -> {
											lblActiveBy28.setText(String.valueOf(activeBy28[0]));
										});
									} else if (numMissed >= -90) {
										lineList.setCurrentArtStatus("LTFU");
										lineList.setActiveBy28("InActive");
										lineList.setActiveBy90("Active");
									} else {
										lineList.setCurrentArtStatus("LTFU");
										lineList.setActiveBy28("InActive");
										lineList.setActiveBy90("InActive");
									}
								}
						}
					});

					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778531 && obs.getPerson_id().equals(patient.getPatient_id()))
							.findFirst().ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
							.ifPresent(concept -> lineList.setRegimenLineAtStart(concept.getValue()))
					);

					DBMiddleMan.allObs.stream()
							.filter(obs -> (obs.getConcept_id() == 7778532 || obs.getConcept_id() == 7778533 || obs.getConcept_id() == 7778534) && obs.getPerson_id().equals(patient.getPatient_id()))
							.findFirst().ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
									.ifPresent(concept -> lineList.setRegimenAtStart(concept.getValue()))
					);


					DBMiddleMan.allObs.stream()
							.filter(obs -> obs.getConcept_id() == 7778111 && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs ->
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
									.ifPresent(concept ->
											lineList.setCurrentRegimenLine(concept.getValue())
									)
					);

					DBMiddleMan.allObs.stream()
							.filter(obs -> (obs.getConcept_id() == 7778108 || obs.getConcept_id() == 7778109 || obs.getConcept_id() == 7778410) && obs.getPerson_id().equals(patient.getPatient_id()))
							.reduce((first, second) -> second).ifPresent(obs -> {
						if (obs.getConcept_id().equals(7778410) && obs.getValue_coded().equals(33)) {
							DBMiddleMan.allObs.stream().filter(ob -> ob.getEncounter_id().equals(obs.getEncounter_id()) &&
							   ob.getConcept_id().equals(1596)).findFirst().ifPresent( o -> lineList.setCurrentRegimen(o.getValue_text()));

						} else {
							DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded()))
									.findFirst()
									.ifPresent(concept -> lineList.setCurrentRegimen(concept.getValue()));
						}
							}
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
