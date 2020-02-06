package org.ccfng.nmrs.linelist;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.patient.Patient;
import org.ccfng.global.DBMiddleMan;
import org.ccfng.global.DestinationConnectionClass;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class Controller extends org.ccfng.datamigration.Controller {

	@FXML
	private TextArea appConsole;

	DestinationConnectionClass dd ;

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
		dd = new DestinationConnectionClass();
	}

	public void logToConsole(String text) {
		Task<String> task = new Task<String>() {

			@Override
			protected String call() {
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
			protected ObservableList<LineList> call() {

				for(Patient patient : DBMiddleMan.allPatientsOnArt
//						.stream()
//						.filter(patient -> patient.getPatient_id() == 19752
//								|| patient.getPatient_id() == 22348
//								|| patient.getPatient_id() == 22566
//								|| patient.getPatient_id() == 21380
//								|| patient.getPatient_id() == 20952).collect(Collectors
//						.toList())
						) {
					LineList lineList = new LineList();
//					final int[] ddUnit = { 1 };
					try {
					    List<Obs> patientObs = DBMiddleMan.allObs.stream().filter(obs -> obs.getPerson_id().equals(patient.getPatient_id())).collect(Collectors.toList());

						updateProgress(wDone[0] + 1, total);
						Platform.runLater(() -> {
//							System.out.println("Processing Patient with ID: " + patient.getPatient_id() + "\n");
							lblCount.setText(String.valueOf(wDone[0]));
						});
						wDone[0]++;

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
								).findAny().ifPresent(pName -> lineList.setPatientName(pName.getGiven_name() + " " + pName.getFamily_name()));

						if (null == lineList.getPatientName()) {
							DBMiddleMan.allPeopleNames.stream()
									.filter(personName ->
											personName.getPerson_id().equals(patient.getPatient_id())
									).findFirst().ifPresent(pName -> lineList.setPatientName(pName.getGiven_name() + " " + pName.getFamily_name()));

						}

						DBMiddleMan.allPeopleAttributes.stream()
								.filter(personAttribute ->
										personAttribute.getPerson_id().equals(patient.getPatient_id()) &&
												personAttribute.getPerson_attribute_type_id() == 8
								).findFirst().ifPresent(personAttribute -> lineList.setPatientPhoneNumber(personAttribute.getValue()));

						DBMiddleMan.allPeopleAddresses.stream()
								.filter(personAdd ->
										personAdd.getPerson_id().equals(patient.getPatient_id())
												&& personAdd.isPreferred()
								).findAny().ifPresent(pAdd -> lineList.setAddress(pAdd.getAddress1() != null ? pAdd.getAddress1() : pAdd.getAddress2()));

						if (null == lineList.getAddress()) {
							DBMiddleMan.allPeopleAddresses.stream()
									.filter(personAdd ->
											personAdd.getPerson_id().equals(patient.getPatient_id())
									).reduce((first, second) -> second).ifPresent(pAdd ->
									lineList.setAddress(pAdd.getAddress1() != null ? pAdd.getAddress1() : pAdd.getAddress2()));
						}


						DBMiddleMan.allPatientIdentifiers.stream()
								.filter(patientIdentifier ->
										patientIdentifier.getPatient_id().equals(patient.getPatient_id()) &&
												patientIdentifier.getIdentifier_type() == 5
								).findFirst().ifPresent(patientIdentifier -> lineList.setHospitalNumber(patientIdentifier.getIdentifier()));


						DBMiddleMan.allPeople.stream().filter(person ->
								person.getPerson_id().equals(patient.getPatient_id())
						).findFirst().ifPresent(pers -> {
							lineList.setSex(pers.getGender());
							lineList.setPatientAge(
									ChronoUnit.YEARS.between(LocalDate.parse(pers.getBirthdate().toString()), LocalDate.now())
							);
						});

                        patientObs.stream()
								.filter(obs -> (obs.getConcept_id() == 159599)
								).findAny().ifPresent(art -> {
							lineList.setArtStartDate(art.getValue_datetime());
							if (null != art.getValue_datetime()) {
//									System.out.println(currentPerson.getBirthdate());
								DBMiddleMan.allPeople.stream().filter(pSon -> pSon.getPerson_id().equals(patient.getPatient_id()))
										.findFirst().ifPresent(person -> lineList.setAgeAtArtStart(
										ChronoUnit.YEARS
												.between(LocalDate.parse(person.getBirthdate().toString()),
														LocalDate.parse(art.getValue_datetime().toString()))
								));

							}
						});

                        patientObs.stream().filter(obs7 -> (obs7.getValue_coded() == 165048 || obs7.getValue_coded() == 165685))
								.reduce((first, second) -> second).ifPresent(o -> {
							if (ChronoUnit.DAYS.between(LocalDate.parse(o.getEncounter().getEncounter_datetime().toString()), LocalDate.now()) < 270) {
								lineList.setPregnancyStatus("Yes");
							} else {
								lineList.setPregnancyStatus("No");
							}
						});

                        patientObs.stream()
								.filter(obs -> obs.getConcept_id() == 165724
										&& obs.getEncounter().getEncounter_type() == 13)
								.reduce((first, second) -> second).ifPresent(obs -> {

//                            patientObs.stream()
//                                    .filter(obss -> obss.getConcept_id() == 165708 && obss.getEncounter_id().equals(obs.getEncounter_id()))
//                                    .findFirst().ifPresent(ob -> {

                                        lineList.setLastPickUpDate(
                                                Date.valueOf(obs.getEncounter().getEncounter_datetime().toString()));
//                                    });
//						Obs unit = DBMiddleMan.allObs.stream()
//								.filter(ob -> ob.getConcept_id() == 1732 &&
//										obs.getEncounter_id().equals(ob.getEncounter_id()))
//								.findFirst().orElse(null);

							Obs arvReg = obs.getEncounter().getObs().stream()
									.filter(ob -> ob.getConcept_id() == 162240)
									.findFirst().orElse(null);

							Obs drugDuration = null;
							try {
								 drugDuration = obs.getEncounter().getObs().stream()
										.filter(obs1 -> obs1.getConcept_id().equals(159368) && obs1.getObs_group_id().equals(arvReg.getObs_id()))
										.findFirst().orElse(null);
							}catch (Exception ex){

							}
//						Obs careObs = DBMiddleMan.allObs.stream()
//								.filter(ob -> ob.getConcept_id() == 165708 && ob.getPerson_id()
//										.equals(obs.getPerson_id())
//										&& ob.getEncounter().getForm_id() == 14 &&
//										obs.getEncounter().getEncounter_datetime().equals(ob.getEncounter().getEncounter_datetime()))
//								.findFirst().orElse(null);

							if (drugDuration != null) {
//
//								if(unit != null){
//									if(unit.getValue_coded() == 524){
//										ddUnit[0] = 30;
//										System.out.println(drugDuration.getValue_numeric() * ddUnit[0]);
//									}else if(unit.getValue_coded() == 520){
//										ddUnit[0] = 7;
//										System.out.println(drugDuration.getValue_numeric()  * ddUnit[0]);
//									}
//								}

								Date nextAppointmentDate = Date.valueOf(LocalDate.parse(obs.getEncounter().getEncounter_datetime().toString())
										.plusDays(drugDuration.getValue_numeric().intValue()));

								lineList.setNextAppointmentDate(nextAppointmentDate);

								patientObs.stream().filter(obs1 -> obs1.getConcept_id().equals(5096)
												&& obs1.getEncounter().getForm_id().equals(14)
//													&& obs1.getEncounter().getVisit_id().equals(obs.getEncounter().getVisit_id())
										).reduce((first, second) -> second).ifPresent(obs1 -> {
											Platform.runLater(()-> {
												System.out.println("Date: "+ obs1.getValue_datetime());
												lineList.setCareCardnextAppointmentDate(obs1.getValue_datetime());
											});

//										if(obs1.getValue_datetime().equals(nextAppointmentDate)){
										if((ChronoUnit.DAYS
												.between(obs1.getValue_datetime().toLocalDate(),nextAppointmentDate.toLocalDate())) < -20){
											lineList.setSameDate("Yes");
										}else{
											lineList.setSameDate("No");
										}
								});


								lineList.setDaysOfArtRefill(drugDuration.getValue_numeric().longValue());

								Long numMissed = ChronoUnit.DAYS
										.between(LocalDate.now(), nextAppointmentDate.toLocalDate());

								lineList.setNumberOfDaysMissedAppointment(numMissed);




								if (numMissed >= 0) {
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

//							else if(careObs != null){
//								Obs nextAppointmentObs = careObs.getEncounter().getOb(5096);
//
//								if (nextAppointmentObs != null) {
//
//									lineList.setNextAppointmentDate(nextAppointmentObs.getValue_datetime());
//
//									lineList.setDaysOfArtRefill(
//											ChronoUnit.DAYS
//													.between(LocalDate
//																	.parse(obs.getEncounter().getEncounter_datetime().toString()),
//															LocalDate.parse(nextAppointmentObs.getValue_datetime().toString()))
//									);
//
//									Long numMissed = ChronoUnit.DAYS
//											.between(LocalDate.now(), nextAppointmentObs.getValue_datetime().toLocalDate());
//									lineList.setNumberOfDaysMissedAppointment(numMissed);
//
//									Obs exited = patient.exited();
//
//									if(exited != null) {
//										DBMiddleMan.conceptNames.stream().filter(concept -> concept.getId().equals(exited.getValue_coded()))
//												.findFirst().ifPresent(concept -> {
//											lineList.setCurrentArtStatus(concept.getValue());
//											lineList.setActiveBy28(concept.getValue());
//											lineList.setActiveBy90(concept.getValue());
//										});
//
//									}else if (numMissed >= 0) {
//										lineList.setCurrentArtStatus("Active");
//										lineList.setActiveBy28("Active");
//										lineList.setActiveBy90("Active");
//										activeBy28[0]++;
//										Platform.runLater(() -> {
//											lblActiveBy28.setText(String.valueOf(activeBy28[0]));
//										});
//									} else if (numMissed >= -28) {
//										lineList.setCurrentArtStatus("Missed Appointment");
//										lineList.setActiveBy28("Active");
//										lineList.setActiveBy90("Active");
//										activeBy28[0]++;
//										Platform.runLater(() -> {
//											lblActiveBy28.setText(String.valueOf(activeBy28[0]));
//										});
//									} else if (numMissed >= -90) {
//										lineList.setCurrentArtStatus("LTFU");
//										lineList.setActiveBy28("InActive");
//										lineList.setActiveBy90("Active");
//									} else {
//										lineList.setCurrentArtStatus("LTFU");
//										lineList.setActiveBy28("InActive");
//										lineList.setActiveBy90("InActive");
//									}
//								}
//						}


						});

						Obs exited = patient.exited();

						if (exited != null) {
							DBMiddleMan.conceptNames.stream().filter(concept -> concept.getId().equals(exited.getValue_coded()))
									.findFirst().ifPresent(concept -> {
								lineList.setCurrentArtStatus(concept.getValue());
							});
						}


                        DBMiddleMan.allPeople.stream().filter(pip -> pip.getDead() && pip.getPerson_id().equals(patient.getPatient_id()))
								.findFirst().ifPresent(dead -> {
									lineList.setCurrentArtStatus("Dead");
						});

                        patientObs.stream()
								.filter(obs -> obs.getConcept_id() == 165708)
								.findFirst().ifPresent(obs ->
								DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
										.ifPresent(concept -> lineList.setRegimenLineAtStart(concept.getValue()))
						);

                        patientObs.stream()
								.filter(obs -> (obs.getConcept_id() == 164506 || obs.getConcept_id() == 164513 || obs.getConcept_id() == 165702))
								.findFirst().ifPresent(obs ->
								DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
										.ifPresent(concept -> lineList.setRegimenAtStart(concept.getValue()))
						);


                        patientObs.stream()
								.filter(obs -> obs.getConcept_id() == 165708 )
								.reduce((first, second) -> second).ifPresent(obs ->
								DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
										.ifPresent(concept ->
												lineList.setCurrentRegimenLine(concept.getValue())
										)
						);

                        patientObs.stream()
								.filter(obs -> (obs.getConcept_id() == 164506 || obs.getConcept_id() == 164513 || obs.getConcept_id() == 165702))
								.reduce((first, second) -> second).ifPresent(obs -> {
									if (obs.getConcept_id().equals(165702) && obs.getValue_coded().equals(165131)) {
                                        patientObs.stream().filter(ob -> ob.getEncounter_id().equals(obs.getEncounter_id()) &&
												ob.getConcept_id().equals(163101)).findFirst().ifPresent(o -> lineList.setCurrentRegimen(o.getValue_text()));

									} else {
										DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded()))
												.findFirst()
												.ifPresent(concept -> lineList.setCurrentRegimen(concept.getValue()));
									}
								}
						);

                        patientObs.stream()
								.filter(obs -> obs.getConcept_id() == 856)
								.reduce((first, second) -> second).ifPresent(obs -> {
							DBMiddleMan.allEncounters.stream().filter(enc -> enc.getEncounter_id().equals(obs.getEncounter_id()))
									.findFirst().ifPresent(encounter -> {
								lineList.setCurrentViralLoad(obs.getValue_numeric().intValue());
								lineList.setDateOfCurrentViralLoad(
										Date.valueOf(LocalDate.parse(encounter.getEncounter_datetime().toString())));
							});
						});

                        patientObs.stream()
								.filter(obs -> obs.getConcept_id() == 164980 )
								.reduce((first, second) -> second).ifPresent(obs ->
								DBMiddleMan.conceptNames.stream().filter(cName -> cName.getId().equals(obs.getValue_coded())).findFirst()
										.ifPresent(concept -> lineList.setViralLoadIndication(concept.getValue()))
						);

						lineLists.add(lineList);
					}catch (Exception ex){
//						lineList.setPatientID(patient.getPatient_id());
//
//						DBMiddleMan.allPatientIdentifiers.stream()
//								.filter(patientIdentifier ->
//										patientIdentifier.getPatient_id().equals(patient.getPatient_id()) &&
//												patientIdentifier.getIdentifier_type() == 4
//								).findFirst().ifPresent(patientIdentifier -> lineList.setPepfarID(patientIdentifier.getIdentifier()));
						lineLists.add(lineList);
						Platform.runLater(()->{
							System.out.println(ex.getMessage());
							System.out.println(patient.getPatient_id());
							logToConsole(patient.getPatient_id().toString()+"\n");
							ex.printStackTrace();
						});
					}
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
