package org.ccfng.global;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.ccfng.datamigration.encounter.Encounter;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.patientidentifier.PatientIdentifier;
import org.ccfng.datamigration.patientprogram.PatientProgram;
import org.ccfng.datamigration.person.Person;
import org.ccfng.datamigration.personaddress.PersonAddress;
import org.ccfng.datamigration.personattribute.PersonAttribute;
import org.ccfng.datamigration.personname.PersonName;

import java.sql.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DBMiddleMan {

	

	public static final String DRIVER = "com.mysql.jdbc.Driver";

	static final DestinationConnectionClass cc = new DestinationConnectionClass();
	static final DestinationConnectionClass dd = new DestinationConnectionClass();

	public static ObservableList<Obs> allObs;

	public static ObservableList<Patient> allPatients;

	public static Set<Patient> allPatientsOnArt;

	public static ObservableList<PatientIdentifier> allPatientIdentifiers;
	public static ObservableList<PatientProgram> allPatientPrograms;
	public static ObservableList<Person> allPeople;
	public static ObservableList<PersonName> allPeopleNames;
	public static ObservableList<PersonAttribute> allPeopleAttributes;
	public static ObservableList<PersonAddress> allPeopleAddresses;
	public static ObservableList<Encounter> allEncounters;
	public static ObservableList<org.ccfng.global.KeyValueClass> allLocations;
	public static ObservableList<org.ccfng.global.KeyValueClass> allForms;
	public static Integer presentLocation = 0;


	public static class KeyValueClass{
		private Integer id;

		private String value;

		public KeyValueClass(Integer id, String value) {
			this.id = id;
			this.value = value;
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

	public static void getLocations() {
//		ObservableList<KeyValueClass> locations = FXCollections.observableArrayList();
		allLocations = FXCollections.observableArrayList();
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (Exception exc) {

		}
		try (Connection conn = DriverManager
				.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword())) {

			stmt = conn.createStatement();
			String sql = "SELECT * FROM location ";
//			Platform.runLater(() -> {
//				logToConsole("\n Fetching Locations..");
//			});
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				org.ccfng.global.KeyValueClass loc = new org.ccfng.global.KeyValueClass();
				loc.setKey(rs.getInt("location_id"));
				loc.setValue(rs.getString("name"));
				allLocations.add(loc);
			}
			rs.close();
//			Platform.runLater(() -> {
//				logToConsole("\n Done..");
//			});
		}
		catch (SQLException e) {
//			Platform.runLater(() -> {
//				logToConsole("\n Error: " + e.getMessage());
//			});
			e.printStackTrace();
		}
		finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			}
			catch (Exception se) {
			}// do nothing
		}//end try
	}

	public static void getForms() {
//		ObservableList<KeyValueClass> locations = FXCollections.observableArrayList();
		allForms = FXCollections.observableArrayList();
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (Exception exc) {

		}
		try (Connection conn = DriverManager
				.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword())) {

			stmt = conn.createStatement();
			String sql = "SELECT * FROM form ";
//			Platform.runLater(() -> {
//				logToConsole("\n Fetching Locations..");
//			});
			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				org.ccfng.global.KeyValueClass loc = new org.ccfng.global.KeyValueClass();
				loc.setKey(rs.getInt("form_id"));
				loc.setValue(rs.getString("name"));
				allForms.add(loc);
			}
			rs.close();
//			Platform.runLater(() -> {
//				logToConsole("\n Done..");
//			});
		}
		catch (SQLException e) {
//			Platform.runLater(() -> {
//				logToConsole("\n Error: " + e.getMessage());
//			});
			e.printStackTrace();
		}
		finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			}
			catch (Exception se) {
			}// do nothing
		}//end try
	}

	public static void getObs(Integer loc) {

		allObs = FXCollections.observableArrayList();
//		Thread obsThread = new Thread(() -> {

			String sql = "select obs.*, encounter.encounter_datetime from obs"
					+ " left join encounter on "
					+ "obs.encounter_id = encounter.encounter_id "
					+ " where encounter.location_id = "+loc+" AND  obs.voided=0 AND encounter.voided = 0  ORDER BY encounter_datetime ASC";
			PreparedStatement stmt = null;
			try {
				//STEP 2: Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");
			}
			catch (Exception exc) {
			}
//			System.out.println(cc.getSource_jdbcUrl()+ " "+ cc.getSourceDb()+" "+cc.getSourceUsername()+" "+cc.getSourcePassword());
			try (Connection conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword())) {
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
					obs.setLocation_id(loc);
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
//		Comparator<Obs> comparator = Comparator.comparing(Obs::getObs_id);
//		FXCollections.sort(allObs,comparator);

	}

	public static void getPatients(Integer loc){
		allPatients = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection

			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT patient.*, patient_program.program_id, patient_program.location_id from patient_program left join patient on patient_program.patient_id = patient.patient_id " +
					"  where patient.voided = 0 AND patient_program.voided = 0 AND patient_program.location_id = "+loc;

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

			allPatientsOnArt = new HashSet<>();

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

		Comparator<Patient> comparator = Comparator.comparing(Patient::getPatient_id);
		FXCollections.sort(allPatients,comparator);
//
//		Comparator<Patient> comparator2 = Comparator.comparing(Patient::getPatient_id);
//		FXCollections.sort(allPatientsOnArt,comparator2);
	}

	public static void getPatientIdentifiers(Integer loc){
		allPatientIdentifiers = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * from patient_identifier where voided =0 AND location_id="+loc;

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

		Comparator<PatientIdentifier> comparator = Comparator.comparing(PatientIdentifier::getPatient_id);
		FXCollections.sort(allPatientIdentifiers,comparator);
	}

	public static void getPatientProgram(Integer loc){
		allPatientPrograms = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM patient_program where voided=0 AND location_id = "+loc;

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PatientProgram patientProgram = new PatientProgram();
				patientProgram.setPatient_program_id(rs.getInt("patient_program_id"));

				patientProgram.setProgram_id(rs.getInt("program_id"));

				//patientProgram.setOutcome_concept_id(rs.getInt("outcome_concept_id"));
				patientProgram.setLocation_id(loc);
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

				if(patientProgram.getProgram_id() == 1){
					allPatients.stream()
							.filter(patient -> patient.getPatient_id().equals(patientProgram.getPatient_id()))
							.findFirst().ifPresent(p -> allPatientsOnArt.add(p));
				}
			}
			System.out.println("All Patient Programs: "+allPatientPrograms.size());
			System.out.println("All Patient on ART: "+allPatientsOnArt.size());

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

		Comparator<PatientProgram> comparator = Comparator.comparing(PatientProgram::getPatient_id);
		FXCollections.sort(allPatientPrograms,comparator);
	}

	public static void getConceptNames(){
		conceptNames = new HashSet<>();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

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
            conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

            //STEP 4: Execute a query

            stmt = conn.createStatement();
            String sql = "SELECT * FROM person where voided=0";

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

        Comparator<Person> comparator = Comparator.comparing(Person::getPerson_id);
        FXCollections.sort(allPeople,comparator);
    }

	public static void getPeopleAttributes(){

		allPeopleAttributes = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM person_attribute where voided=0";

			ResultSet rs = stmt.executeQuery(sql);
			//STEP 5: Extract data from result set
			while (rs.next()) {
				//Retrieve by column name
				PersonAttribute personArt = new PersonAttribute();
				personArt.setPerson_attribute_id(rs.getInt("person_attribute_id"));
				personArt.setPerson_id(rs.getInt("person_id"));
				personArt.setValue(rs.getString("value"));
				personArt.setPerson_attribute_type_id(rs.getInt("person_attribute_type_id"));

				allPeopleAttributes.add(personArt);
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

		Comparator<PersonAttribute> comparator = Comparator.comparing(PersonAttribute::getPerson_id);
		FXCollections.sort(allPeopleAttributes,comparator);
	}

	public static void getPeopleNames(){

		allPeopleNames = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM person_name where voided=0";

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

		Comparator<PersonName> comparator = Comparator.comparing(PersonName::getPerson_id);
		FXCollections.sort(allPeopleNames,comparator);
	}

	public static void getEncounters(Integer loc){
		allEncounters = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT encounter.* FROM encounter left join patient on encounter.patient_id = patient.patient_id "
					+ "where encounter.voided=0 AND patient.voided=0 AND location_id = "+loc+" ORDER BY encounter_datetime ASC";

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

		Comparator<Encounter> comparator = Comparator.comparing(Encounter::getEncounter_datetime);
		FXCollections.sort(allEncounters,comparator);
	}

	public static void getAllPeopleAddresses(){
		allPeopleAddresses = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		try {
			//STEP 2: Register JDBC driver
			Class.forName(DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(cc.getDestination_jdbcUrl(), cc.getDestinationUsername(), cc.getDestinationPassword());

			//STEP 4: Execute a query

			stmt = conn.createStatement();
			String sql = "SELECT * FROM person_address where voided=0";

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

		Comparator<PersonAddress> comparator = Comparator.comparing(PersonAddress::getPerson_id);
		FXCollections.sort(allPeopleAddresses,comparator);
	}
}
