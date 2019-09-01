package org.ccfng.apibasedmigration;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.ccfng.apibasedmigration.models.Address;
import org.ccfng.apibasedmigration.models.Patient;
import org.ccfng.apibasedmigration.models.PatientName;
import org.ccfng.datamigration.filepaths.ConceptMap;
import org.ccfng.global.ConnectionClass;
import org.ccfng.global.Location;

public class Controller {

	ConnectionClass cc;

	private HashMap<Integer, Integer> concepts =  new HashMap<>();

	@FXML
	private ComboBox<Location> stateCombo;

	@FXML
	private ComboBox<Location> lgaCombo;

	@FXML
	private ComboBox<Location> facilityCombo;

	@FXML
	private TextField datimCode;

//	@FXML
	private ObservableList<Patient> allPatient = FXCollections.observableArrayList();


	public void initialize(){

		cc = new ConnectionClass();

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

		buildLocation("states",133452);

	}

	@FXML
	private void buildData(){

				buildPatient();

				buildPatientAddresses();

				buildPatientName();
	}

	private static ConceptMap createConceptMap(String[] metadata) {
		Integer openmrs =  Integer.parseInt(metadata[0]);
		Integer nmrs = Integer.parseInt(metadata[1]);

		// create and return book of this metadata
		return new ConceptMap(openmrs, nmrs);

	}

	private void buildPatient() {

				Connection conn = null;
				Statement stmt = null;
				Connection conn2 = null;
				Statement stmt2 = null;

					try {
						//STEP 2: Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");

						//STEP 3: Open a connection
						conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
						//logToConsole("\n Connected to database successfully...");

						//STEP 4: Execute a query

						stmt = conn.createStatement();

						String sql = "SELECT "
								+ "patient.patient_id AS patientID, person.creator AS creator, person.date_created, person.gender,"
								+ " person.birthdate, birthdate_estimated, person.dead, person.death_date, person.cause_of_death, "
								+ "(SELECT value_text from obs where concept_id = 7778430 && person_id = patient.patient_id Limit 1) AS phone "
								+ " FROM patient LEFT JOIN person on patient.patient_id = person.person_id "
								+ " where person.voided = 0";
						//logToConsole("\n Creating Select statement...");
						ResultSet rs = stmt.executeQuery(sql);
						//STEP 5: Extract data from result set

						while (rs.next()) {

							Patient patient = new Patient();
							patient.setPatientID(rs.getInt("patientID"));
							patient.setPhone(rs.getString("phone"));
							patient.setDateCreated(rs.getDate("date_created"));
							patient.setBirthDate(rs.getDate("birthdate"));
							patient.setBirthdateEstimated(rs.getBoolean("birthdate_estimated"));
							patient.setGender(rs.getString("gender"));
							patient.setDead(rs.getBoolean("dead"));
							patient.setCreator(rs.getInt("creator"));
							patient.setCauseOfDeath(rs.getString("cause_of_death"));
							patient.setDeathDate(rs.getDate("death_date"));

							allPatient.add(patient);
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
				System.out.println(allPatient);
		}

	private void buildPatientAddresses() {
		for (Patient pat : allPatient) {
			Connection conn = null;
			Statement stmt = null;
			Connection conn2 = null;
			Statement stmt2 = null;

			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				//STEP 3: Open a connection
				conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
				//logToConsole("\n Connected to database successfully...");

				//STEP 4: Execute a query
				stmt = conn.createStatement();


					String sql = "SELECT * FROM person_address where person_id = "+pat.getPatientID();
					//logToConsole("\n Creating Select statement...");
					ResultSet rs = stmt.executeQuery(sql);
					//STEP 5: Extract data from result set
				    Set<Address> addresses = new HashSet<>();
					while (rs.next()) {
                        Address address = new Address();
						address.setCountry(rs.getString("country"));
						address.setLatitude(rs.getString("latitude"));
						address.setLongitude(rs.getString("longitude"));
						address.setAddress1(rs.getString("address1"));
						address.setAddress2(rs.getString("address2"));
						address.setAddress3(rs.getString("address3"));
						address.setCityVillage(rs.getString("city_village"));
						address.setStateProvince(rs.getString("state_province"));
						address.setPostalCode(rs.getString("postal_code"));
						address.setPreferred(rs.getBoolean("preferred"));
						addresses.add(address);
					}
					pat.setAddress(addresses);
				}
			catch(SQLException se){
					//Handle errors for JDBC
					se.printStackTrace();
					System.out.println("\n SQLException Error: " + se.getMessage());
				}
			catch(Exception e){
					//Handle errors for Class.forName
					e.printStackTrace();
					System.out.println("\n Exception Error: " + e.getMessage());
				}
			finally{
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
		System.out.println(allPatient);
	}

	private void buildPatientName() {
		for (Patient pat : allPatient) {
			Connection conn = null;
			Statement stmt = null;
			Connection conn2 = null;
			Statement stmt2 = null;

			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				//STEP 3: Open a connection
				conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
				//logToConsole("\n Connected to database successfully...");

				//STEP 4: Execute a query
				stmt = conn.createStatement();


				String sql = "SELECT * FROM person_name where person_id = "+pat.getPatientID();
				//logToConsole("\n Creating Select statement...");
				ResultSet rs = stmt.executeQuery(sql);
				//STEP 5: Extract data from result set
				Set<PatientName> patientNames = new HashSet<>();
				while (rs.next()) {
					PatientName patientName = new PatientName();
					patientName.setFirstName(rs.getString("given_name"));
					patientName.setLastName(rs.getString("family_name"));
					patientName.setMiddleName(rs.getString("middle_name"));
					patientName.setPrefix(rs.getString("prefix"));
					patientName.setPreferred(rs.getBoolean("preferred"));
					patientNames.add(patientName);
				}
				pat.setPatientname(patientNames);
			}
			catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				System.out.println("\n SQLException Error: " + se.getMessage());
			}
			catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
				System.out.println("\n Exception Error: " + e.getMessage());
			}
			finally{
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
		System.out.println(allPatient);
	}

	private void buildLocation(String target, int parentID) {

			Connection conn = null;
			Statement stmt = null;
			Connection conn2 = null;
			Statement stmt2 = null;

			ObservableList<Location> locationMap = FXCollections.observableArrayList();

			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				//STEP 3: Open a connection
				conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
				//logToConsole("\n Connected to database successfully...");

				//STEP 4: Execute a query
				stmt = conn.createStatement();


				String sql = "SELECT * FROM address_hierarchy_entry where parent_id = "+parentID;
				//logToConsole("\n Creating Select statement...");
				ResultSet rs = stmt.executeQuery(sql);
				//STEP 5: Extract data from result set

				while (rs.next()) {
						locationMap.add(new Location(rs.getInt("address_hierarchy_entry_id"),rs.getString("name")));
				}
			}
			catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				System.out.println("\n SQLException Error: " + se.getMessage());
			}
			catch(Exception e){
				//Handle errors for Class.forName
				e.printStackTrace();
				System.out.println("\n Exception Error: " + e.getMessage());
			}
			finally{
				//finally block used to close resources
				try {
					if (stmt != null)
						conn.close();
				}
				catch (SQLException se) {
				}// do nothing
				closeConnection(conn);
			}//end try

		if(target.equals("states")){
			stateCombo.setItems(locationMap);
		}else if(target.equals("lgas")){
			lgaCombo.setItems(locationMap);
		}else if(target.equals("facilities")){
			facilityCombo.setItems(locationMap);
		}
	}

	@FXML
	private void getLgas(){
		buildLocation("lgas", stateCombo.getSelectionModel().getSelectedItem().getLocationID());
	}

	@FXML
	private void getFacilities(){
		getFacility();
		getDefaults();
	}

	private void getFacility(){

		Connection conn = null;
		Statement stmt = null;
		Connection conn2 = null;
		Statement stmt2 = null;

		ObservableList<Location> locationMap = FXCollections.observableArrayList();

		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			//logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query
			stmt = conn.createStatement();


			String sql = "SELECT * FROM location where state_province = '"+stateCombo.getSelectionModel().getSelectedItem().getLocation()+"'";
			//logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set

			while (rs.next()) {
				locationMap.add(new Location(rs.getInt("location_id"),rs.getString("name")));
			}
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			System.out.println("\n SQLException Error: " + se.getMessage());
		}
		catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			System.out.println("\n Exception Error: " + e.getMessage());
		}
		finally{
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			}
			catch (SQLException se) {
			}// do nothing
			closeConnection(conn);
		}//end try
		Comparator<Location> comparator = Comparator.comparing(Location::getLocation);
		FXCollections.sort(locationMap,comparator);
		facilityCombo.setItems(locationMap);

	}


	private void getDefaults(){

		Connection conn = null;
		Statement stmt = null;
		Connection conn2 = null;
		Statement stmt2 = null;

		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());
			//logToConsole("\n Connected to database successfully...");

			//STEP 4: Execute a query
			stmt = conn.createStatement();


			String sql = "SELECT * FROM global_property where property = 'default_location' || property = 'datim_code'";
			//logToConsole("\n Creating Select statement...");
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set

			while (rs.next()) {
				if(rs.getString("property").equals("default_location")){
//					facilityCombo.setValue();
				}else if(rs.getString("property").equals("datim_code")){
					datimCode.setText(rs.getString("property_value"));
				}
			}
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			System.out.println("\n SQLException Error: " + se.getMessage());
		}
		catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			System.out.println("\n Exception Error: " + e.getMessage());
		}
		finally{
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
