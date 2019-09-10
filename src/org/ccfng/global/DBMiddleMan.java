package org.ccfng.global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javafx.collections.FXCollections;
import org.ccfng.datamigration.encounter.Encounter;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.patientidentifier.PatientIdentifier;
import org.ccfng.datamigration.patientprogram.PatientProgram;
import org.ccfng.datamigration.person.Person;
import org.ccfng.datamigration.personaddress.PersonAddress;
import org.ccfng.datamigration.personname.PersonName;

public class DBMiddleMan {

	private static final String DRIVER = "com.mysql.jdbc.Driver";

	static final ConnectionClass cc = new ConnectionClass();

	public static List<Obs> allObs;

	public static List<Patient> allPatients;

	public static List<Patient> allPatientsOnArt;

	public static List<PatientIdentifier> allPatientIdentifiers;
	public static List<PatientProgram> allPatientPrograms;
	public static List<Person> allPeople;
	public static List<PersonName> allPeopleNames;
	public static List<PersonAddress> allPeopleAddresses;
	public static List<Encounter> allEncounters;


	public static class KeyValueClass{
		private Integer id;

		private String value;

		public KeyValueClass(Integer id, String value) {
			this.id = id;
			this.value = value;
		}

		public KeyValueClass() {
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	public static Set<KeyValueClass> conceptNames;
//
//	public DBMiddleMan(){
//		allObs = FXCollections.observableArrayList();
//	}

	public static void getObs() {

		allObs = FXCollections.observableArrayList();
//		Thread obsThread = new Thread(() -> {

			String sql = "select * from obs";
			PreparedStatement stmt = null;
			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");
			}
			catch (Exception exc) {
			}
//			System.out.println(cc.getSource_jdbcUrl()+ " "+ cc.getSourceDb()+" "+cc.getSourceUsername()+" "+cc.getSourcePassword());
			try (Connection conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());) {
				stmt = conn.prepareStatement(sql);
				stmt.setFetchSize(500);
				ResultSet rs = stmt.executeQuery(sql);
				rs.setFetchSize(10);
				//STEP 5: Extract data from result set
				while (rs.next()) {
					Obs obs = new Obs();
					if (rs.getString("accession_number") != null)
						obs.setAccession_number(rs.getString("accession_number"));

					if (rs.getString("comments") != null)
						obs.setComments(rs.getString("comments"));

					// DO Concept Mapping here
					obs.setConcept_id(rs.getInt("concept_id"));

					obs.setUuid(UUID.randomUUID());
					obs.setCreator(1);
					obs.setDate_created(rs.getDate("date_created"));

					if (rs.getDate("date_voided") != null)
						obs.setDate_voided(rs.getDate("date_voided"));

					obs.setEncounter_id(rs.getInt("encounter_id"));
					obs.setLocation_id(2);
					//obs.setForm_namespace_and_path(rs.getString("form_namespace_and_path"));
					// obs.setVisit_id(rs.getInt("visit_id"));

					if (rs.getString("void_reason") != null)
						obs.setVoid_reason(rs.getString("void_reason"));

					obs.setVoided(rs.getBoolean("voided"));
					obs.setObs_datetime(rs.getDate("obs_datetime"));

					if (rs.getInt("obs_group_id") > 0)
						obs.setObs_group_id(rs.getInt("obs_group_id"));

					if (rs.getInt("obs_id") > 0)
						obs.setObs_id(rs.getInt("obs_id"));
					//				if (rs.getInt("order_id") > 0)
					obs.setOrder_id(rs.getInt("order_id"));

					obs.setPerson_id(rs.getInt("person_id"));
//					obs.setPrevious_version(rs.getInt("previous_version"));
					obs.setValue_coded(rs.getInt("value_coded"));
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
					allObs.add(obs);
				}
				rs.close();
//				return allObs;
			}
			catch (SQLException e) {
				e.printStackTrace();
			}catch (Exception ex){
				ex.printStackTrace();
			}

			finally {
				//finally block used to close resources
				try {
					if (stmt != null)
						;
				}
				catch (Exception se) {
				}// do nothing
			}
//		});

//		 obsThread.start();
//		return allObs;

	}

	public static void getPatients(){
		allPatients = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * from patient";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				Patient patient = new Patient();
				patient.setPatient_id(rs.getInt("patient_id"));
				//patient.setAllergy_status(rs.getString("allergy_status"));
				patient.setCreator(1);
				patient.setDate_changed(rs.getDate("date_changed"));
				patient.setDate_created(rs.getDate("date_created"));
				patient.setDate_voided(rs.getDate("date_voided"));
				patient.setVoid_reason(rs.getString("void_reason"));
				patient.setVoided(rs.getBoolean("voided"));

				allPatients.add(patient);
			}

			allPatientsOnArt = FXCollections.observableArrayList();

			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
//		return allPatients;
	}

	public static void getPatientIdentifiers(){
		allPatientIdentifiers = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * from patient_identifier";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {

				PatientIdentifier patientIdentifier = new PatientIdentifier();
				patientIdentifier.setPatient_identifier_id(rs.getInt("patient_identifier_id"));
				patientIdentifier.setPreferred(rs.getBoolean("preferred"));
				patientIdentifier.setIdentifier_type(rs.getInt("identifier_type"));
				patientIdentifier.setIdentifier(rs.getString("identifier"));
				patientIdentifier.setUuid(UUID.randomUUID());
				patientIdentifier.setCreator(1);
				//patientIdentifier.setDate_changed(rs.getDate("date_changed"));
				patientIdentifier.setDate_created(rs.getDate("date_created"));
				patientIdentifier.setDate_voided(rs.getDate("date_voided"));
				patientIdentifier.setLocation_id(2);
				patientIdentifier.setPatient_id(rs.getInt("patient_id"));
				patientIdentifier.setVoid_reason(rs.getString("void_reason"));
				patientIdentifier.setVoided(rs.getBoolean("voided"));

				allPatientIdentifiers.add(patientIdentifier);
			}
			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
//		return allPatientIdentifiers;
	}

	public static void getPatientProgram(){
		allPatientPrograms = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM patient_program";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PatientProgram patientProgram = new PatientProgram();
				patientProgram.setPatient_program_id(rs.getInt("patient_program_id"));

				patientProgram.setProgram_id(rs.getInt("program_id"));

				//patientProgram.setOutcome_concept_id(rs.getInt("outcome_concept_id"));
				patientProgram.setLocation_id(2);
				patientProgram.setPatient_id(rs.getInt("patient_id"));
				patientProgram.setUuid(UUID.randomUUID());
				patientProgram.setCreator(1);
				patientProgram.setDate_changed(rs.getDate("date_changed"));
				patientProgram.setDate_created(rs.getDate("date_created"));
				patientProgram.setDate_voided(rs.getDate("date_voided"));
				patientProgram.setVoid_reason(rs.getString("void_reason"));
				patientProgram.setVoided(rs.getBoolean("voided"));
				patientProgram.setDate_completed(rs.getDate("date_completed"));
				patientProgram.setDate_enrolled(rs.getDate("date_enrolled"));
				allPatientPrograms.add(patientProgram);
			}

			allPatientPrograms.forEach(pp -> {
				allPatients.stream()
						.filter(patient -> patient.getPatient_id().equals(pp.getPatient_id())).findFirst().ifPresent(p -> allPatientsOnArt.add(p));
			});


			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
//		return allPatientPrograms;
	}

	public static void getConceptNames(){
		conceptNames = new HashSet<>();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM concept_name where locale = 'en' && locale_preferred=1 && voided=0";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				conceptNames.add(new KeyValueClass(rs.getInt("concept_id"), rs.getString("name")));
			}
			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
		//		return allPatientPrograms;
	}


	public static void getPeople(){

		allPeople = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM person";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				Person person = new Person();
				person.setPerson_id(rs.getInt("person_id"));
				person.setGender(rs.getString("gender"));
				if (rs.getDate("birthdate") != null)
					person.setBirthdate(rs.getDate("birthdate"));
				person.setBirthdate_estimated(rs.getBoolean("birthdate_estimated"));
				person.setDead(rs.getBoolean("dead"));
				person.setUuid(UUID.randomUUID());
				person.setCreator(1);
				person.setDate_changed(rs.getDate("date_changed"));
				person.setDate_created(rs.getDate("date_created"));
				//person.setDate_voided(rs.getDate("date_voided"));
				person.setDate_voided(null);
				person.setVoid_reason(rs.getString("void_reason"));
				person.setVoided(rs.getBoolean("voided"));

				allPeople.add(person);
			}
			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
//		return allPeople;
	}

	public static void getPeopleNames(){

		allPeopleNames = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM person_name";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PersonName personName = new PersonName();
				personName.setPerson_name_id(rs.getInt("person_name_id"));
				personName.setPreferred(rs.getBoolean("preferred"));
				personName.setPerson_id(rs.getInt("person_id"));
				personName.setPrefix(rs.getString("prefix"));
				personName.setGiven_name(rs.getString("given_name"));
				personName.setMiddle_name(rs.getString("middle_name"));
				personName.setFamily_name_prefix(rs.getString("family_name_prefix"));
				personName.setFamily_name(rs.getString("family_name"));
				personName.setFamily_name2(rs.getString("family_name2"));
				personName.setFamily_name_suffix(rs.getString("family_name_suffix"));
				personName.setDegree(rs.getString("degree"));
				personName.setUuid(UUID.randomUUID());
				personName.setCreator(1);
				personName.setDate_changed(rs.getDate("date_changed"));
				personName.setDate_created(rs.getDate("date_created"));
				personName.setDate_voided(rs.getDate("date_voided"));
				personName.setVoid_reason(rs.getString("void_reason"));
				personName.setVoided(rs.getBoolean("voided"));

				allPeopleNames.add(personName);
			}
			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
//		return allPeopleNames;
	}

	public static void getEncounters(){
		allEncounters = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM encounter";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				Encounter encounter = new Encounter();
				encounter.setEncounter_id(rs.getInt("encounter_id"));
				encounter.setEncounter_datetime(rs.getDate("encounter_datetime"));
				encounter.setForm_id(rs.getInt("form_id"));
				encounter.setEncounter_type(rs.getInt("encounter_type"));
				encounter.setUuid(UUID.randomUUID());
				encounter.setCreator(rs.getInt("creator"));
				encounter.setDate_changed(rs.getDate("date_changed"));
				encounter.setDate_created(rs.getDate("date_created"));
				encounter.setDate_voided(rs.getDate("date_voided"));
				encounter.setLocation_id(2);
				encounter.setPatient_id(rs.getInt("patient_id"));
				encounter.setVoid_reason(rs.getString("void_reason"));
				encounter.setVoided(rs.getBoolean("voided"));

				allEncounters.add(encounter);
			}
			rs.close();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
//		return allPeopleNames;
	}

	public static void getAllPeopleAddresses(){
		allPeopleAddresses = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getSource_jdbcUrl(), cc.getSourceUsername(), cc.getSourcePassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM person_address";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PersonAddress personAddress = new PersonAddress();
				personAddress.setPerson_address_id(rs.getInt("person_address_id"));
				personAddress.setCity_village(rs.getString("city_village"));
				personAddress.setCountry(rs.getString("country"));
				personAddress.setLatitude(rs.getString("latitude"));
				personAddress.setLongitude(rs.getString("longitude"));
				personAddress.setPerson_id(rs.getInt("person_id"));
				personAddress.setPostal_code(rs.getString("postal_code"));
				personAddress.setPreferred(rs.getBoolean("preferred"));
				personAddress.setState_province(rs.getString("state_province"));
				personAddress.setAddress1(rs.getString("address1"));
				personAddress.setAddress2(rs.getString("address2"));
				personAddress.setAddress3(rs.getString("address3"));
				personAddress.setAddress4(rs.getString("address4"));
				personAddress.setAddress5(rs.getString("address5"));
				personAddress.setAddress6(rs.getString("address6"));
				personAddress.setUuid(UUID.randomUUID());
				personAddress.setCreator(1);
				personAddress.setDate_created(rs.getDate("date_created"));
				personAddress.setDate_voided(rs.getDate("date_voided"));
				personAddress.setVoid_reason(rs.getString("void_reason"));
				personAddress.setVoided(rs.getBoolean("voided"));

				allPeopleAddresses.add(personAddress);
			}
			rs.close();
			getConceptNames();
		} catch (SQLException se) {
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}//end try
//		return allPeopleNames;
	}
}
