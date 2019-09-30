package org.ccfng.datamigration.datamapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.ccfng.datamigration.filepaths.ConceptMap;
import org.ccfng.global.ConnectionClass;
import org.ccfng.global.DestinationConnectionClass;

public class ConceptmapController {

	// Create a HashMap
	HashMap<Integer, Integer> concepts = new HashMap<>();

	@FXML
	TableView<Mapper> tableView;

	@FXML
	TableView<Mapper> tableView1;

	Task<ObservableList<Mapper>> mapperTask;
	Task<ObservableList<Mapper>> unmapperTask;

	ConnectionClass cc ;
	DestinationConnectionClass dd ;


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

		getOpenMrsConcept();
		getUnmappedConcept();
      new Thread(mapperTask).start();

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

	private void getOpenMrsConcept() {

		mapperTask = new Task<ObservableList<Mapper>>() {

			@Override
			protected ObservableList<Mapper> call() throws Exception {


		Connection conn = null;
		Statement stmt = null;
		Connection conn2 = null;
		Statement stmt2 = null;
		ObservableList<Mapper> conceptMaps = FXCollections.observableArrayList();
		for (Map.Entry<Integer, Integer> entry : concepts.entrySet()){

				try {
					//STEP 2: Register JDBC driver
					Class.forName("com.mysql.jdbc.Driver");

					//STEP 3: Open a connection
					//logToConsole("\n Connecting to Source Database!!");
					conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
					//logToConsole("\n Connected to database successfully...");

					//STEP 4: Execute a query

					stmt = conn.createStatement();
					String sql = "SELECT \n"
							+ "name \n"
							+ " FROM concept_name \n"
							+ " where concept_id = "+entry.getKey()+" AND locale='en' AND locale_preferred=1 AND voided=0";
					//logToConsole("\n Creating Select statement...");
					ResultSet rs = stmt.executeQuery(sql);
					//STEP 5: Extract data from result set
					Mapper conceptMap = new Mapper();
					while (rs.next()) {
						conceptMap.setOpenmrsId(entry.getKey());
						conceptMap.setOpenmrsName(rs.getString("name"));
						//			rs.close();
						//			try (PrintWriter writer = new PrintWriter("initial_clinical_evaluation.csv", "UTF-8")) {
						//				for(Integer id : patientIds) {
						sql = "SELECT \n"
								+ "name \n"
								+ " FROM concept_name \n"
								+ " where concept_id = "+entry.getValue()+" AND locale='en' AND locale_preferred=1 AND voided=0";
						try {
//							System.out.println("Destination Connection: " + dd.getDestination_jdbcUrl() + " \n");
							conn2 = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(),
									dd.getDestinationPassword());
							stmt2 = conn2.createStatement();
							ResultSet rs2 = stmt2.executeQuery(sql);
							Integer encID = 0;
							while (rs2.next()) {
								conceptMap.setNmrsId(entry.getValue());
								conceptMap.setNmrsName(rs2.getString("name"));
							}
							conceptMaps.add(conceptMap);
							rs2.close();
						}
						catch (SQLException se) {
							//Handle errors for JDBC
							se.printStackTrace();
							System.out.println("\n SQLException Error: " + se.getMessage());
						}
						catch (Exception e) {
							//Handle errors for Class.forName
							e.printStackTrace();
							System.out.println("\n Exception Error: " + e.getMessage());
						}
						finally {
							//finally block used to close resources
							try {
								if (stmt2 != null)
									conn2.close();
							}
							catch (SQLException se) {
							}// do nothing
							closeConnection(conn2);
						}//end try
						//				}
						//logToConsole("\n Data Successfully Fetched!");
						//			} catch (IOException exc) {
						//				System.out.println("Error writing Configs to file: " + exc.getMessage() + "..... \n");
						//			}
					}
				}
				catch (SQLException se) {
					//Handle errors for JDBC
					se.printStackTrace();
					System.out.println("\n SQLException Error: " + se.getMessage());
				}
				catch (Exception e) {
					//Handle errors for Class.forName
					e.printStackTrace();
					System.out.println("\n Exception Error: " + e.getMessage());
				}
				finally {
					//finally block used to close resources
					try {
						if (stmt != null)
							conn.close();
					}
					catch (SQLException se) {
					}// do nothing
					closeConnection(conn);
				}//end try

	    }
				Platform.runLater(() ->{
					tableView.setItems(conceptMaps);
				});

				System.out.println("Done!");
				return conceptMaps;
			}
		};
	}

	private void getUnmappedConcept() {
		Set<Integer> unMappedConcepts = new HashSet<>();
		try (BufferedReader br = Files.newBufferedReader(Paths.get("missing_concepts.csv"),
				StandardCharsets.US_ASCII)) {
			// read the first line from the text file
			String line = br.readLine();
			// loop until all lines are read

			while (line != null) {
				String[] attributes = line.split(",");
				ConceptMap concept = createConceptMap(attributes);
				unMappedConcepts.add(concept.getOpenmrs());
				line = br.readLine();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		unmapperTask = new Task<ObservableList<Mapper>>() {

			@Override
			protected ObservableList<Mapper> call() throws Exception {


				Connection conn = null;
				Statement stmt = null;
				ObservableList<Mapper> conceptMaps = FXCollections.observableArrayList();
				for (Integer inte : unMappedConcepts){

					try {
						//STEP 2: Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");

						//STEP 3: Open a connection
						//logToConsole("\n Connecting to Source Database!!");
						conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
						//logToConsole("\n Connected to database successfully...");

						//STEP 4: Execute a query

						stmt = conn.createStatement();
						String sql = "SELECT \n"
								+ "name \n"
								+ " FROM concept_name \n"
								+ " where concept_id = "+inte+" AND locale='en' AND locale_preferred=1 AND voided=0";
						//logToConsole("\n Creating Select statement...");
						ResultSet rs = stmt.executeQuery(sql);
						//STEP 5: Extract data from result set
						Mapper conceptMap = new Mapper();
						while (rs.next()) {
							conceptMap.setOpenmrsId(inte);
							conceptMap.setOpenmrsName(rs.getString("name"));

						}
					}
					catch (SQLException se) {
						//Handle errors for JDBC
						se.printStackTrace();
						System.out.println("\n SQLException Error: " + se.getMessage());
					}
					catch (Exception e) {
						//Handle errors for Class.forName
						e.printStackTrace();
						System.out.println("\n Exception Error: " + e.getMessage());
					}
					finally {
						//finally block used to close resources
						try {
							if (stmt != null)
								conn.close();
						}
						catch (SQLException se) {
						}// do nothing
						closeConnection(conn);
					}//end try

				}
				Platform.runLater(() ->{
					tableView1.setItems(conceptMaps);
				});

				System.out.println("Done!");
				return conceptMaps;
			}
		};

		new Thread(unmapperTask).start();
	}

	private void closeConnection(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
			System.out.println("\n Error: " + se.getMessage());
		}//end finally try
	}

}
