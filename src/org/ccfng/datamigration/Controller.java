package org.ccfng.datamigration;

import com.mysql.jdbc.StringUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.ccfng.datamigration.encounter.Encounter;
import org.ccfng.datamigration.encounter.Encounters;
import org.ccfng.datamigration.filepaths.FilePath;
import org.ccfng.datamigration.filepaths.tables;
import org.ccfng.datamigration.obs.Obs;
import org.ccfng.datamigration.obs.Obses;
import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.patient.Patients;
import org.ccfng.datamigration.patientidentifier.PatientIdentifier;
import org.ccfng.datamigration.patientidentifier.PatientIdentifiers;
import org.ccfng.datamigration.patientprogram.PatientProgram;
import org.ccfng.datamigration.patientprogram.PatientPrograms;
import org.ccfng.datamigration.person.Person;
import org.ccfng.datamigration.person.Persons;
import org.ccfng.datamigration.personaddress.PersonAddress;
import org.ccfng.datamigration.personaddress.PersonAddresses;
import org.ccfng.datamigration.personattribute.PersonAttribute;
import org.ccfng.datamigration.personattribute.PersonAttributes;
import org.ccfng.datamigration.personname.PersonName;
import org.ccfng.datamigration.personname.PersonNames;
import org.ccfng.datamigration.session.SessionManager;
import org.ccfng.datamigration.visit.Visit;
import org.ccfng.datamigration.visit.Visits;
import org.hibernate.HibernateException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;

public class Controller {

    @FXML
    private AnchorPane mainLayer;

    @FXML
    private Label showDirectory;

    //##########################
    @FXML
    private TextField sourceHost;

    @FXML
    private TextField sourcePort;

    @FXML
    private TextField sourceUsername;

    @FXML
    private TextField sourcePassword;

    @FXML
    private TextField sourceDb;

    @FXML
    private TextField tableSuffix;
    //##########################

    @FXML
    private TextField host;

    @FXML
    private TextField port;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField db;

    @FXML
    private TextArea appConsole ;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ComboBox<tables> tablesComboBox;

    @FXML
    private ComboBox<String> fileComboBox;

    @FXML
    private ComboBox<String> targetComboBox;

    @FXML
    private ComboBox<String> actionComboBox;

    @FXML
    private ComboBox<String> sourceDB;

    private ObservableList<Encounter> allEncounters;

    private Task<ObservableList<Encounter>> encounterTask;

    private Task<ObservableList<Obs>> obsTask;

    private ObservableList<Obs> allObses;

    private Task<ObservableList<Patient>> patientTask;

    private ObservableList<Patient> allPatients;

    private Task<ObservableList<PatientIdentifier>> patientIdentifierTask;

    private ObservableList<PatientIdentifier> allPatientIdentifiers;

    private Task<ObservableList<PatientProgram>> patientProgramTask;

    private ObservableList<PatientProgram> allPatientPrograms;

    private Task<ObservableList<Person>> personTask;

    private ObservableList<Person> allPersons;

    private Task<ObservableList<PersonAddress>> personAddressTask;

    private ObservableList<PersonAddress> allPersonAddresses;

    private Task<ObservableList<PersonAttribute>> personAttributeTask;

    private ObservableList<PersonAttribute> allPersonAttributes;

    private Task<ObservableList<PersonName>> personNameTask;

    private ObservableList<PersonName> allPersonNames;

    private Task<ObservableList<Visit>> visitTask;

    private ObservableList<Visit> allVisits;

    private static Encounters ArrayOfEncounter = new Encounters();
    private static Obses ArrayOfObs = new Obses();
    private static Patients ArrayOfPatient = new Patients();
    private static PatientIdentifiers ArrayOfPatientIdentifiers = new PatientIdentifiers();
    private static PatientPrograms ArrayOfPatientPrograms = new PatientPrograms();
    private static Persons ArrayOfPersons = new Persons();
    private static PersonAddresses ArrayOfPersonAddresses = new PersonAddresses();
    private static PersonAttributes ArrayOfPersonAttributes = new PersonAttributes();
    private static PersonNames ArrayOfPersonNames = new PersonNames();
    private static Visits ArrayOfVisits = new Visits();

    private static String tab = null;

//    private ToggleGroup sourceType = new ToggleGroup();

//    @FXML
//    private RadioButton fromDB;

    @FXML
    private CheckBox fromFile;

    @FXML
    private ProgressIndicator progressIndicator;

    public Controller(){}

    public void initialize(){
        ObservableList<String> targets = FXCollections.observableArrayList();
        targets.add("Source");
        targets.add("Destination");
        targetComboBox.setItems(FXCollections.observableList(targets));

        ObservableList<String> action = FXCollections.observableArrayList();
        action.add("Create");
        action.add("Insert");
        action.add("Update");
        action.add("Delete");
        actionComboBox.setItems(FXCollections.observableList(action));
        try {
            File textFile = new File("db-config.txt");
            if(!textFile.exists()) {
                textFile.createNewFile();
                FileOutputStream oFile = new FileOutputStream(textFile, false);
                oFile.close();
            }
        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();

        }
        try {
            File textFile = new File("source-db-config.txt");
            if(!textFile.exists()) {
                textFile.createNewFile();
                FileOutputStream oFile = new FileOutputStream(textFile, false);
                oFile.close();
            }
        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }

        try (Stream<String> stream = Files.lines(Paths.get("db-config.txt"))) {
            List<String> files = FXCollections.observableArrayList();
                stream.forEach(files::add);
            if(! files.isEmpty()) {
                FilePath.xsdDir = files.get(0);
                host.setText(files.get(1));
                port.setText(files.get(2));
                username.setText(files.get(3));
                password.setText(files.get(4));
                db.setText(files.get(5));
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }

        try (Stream<String> stream = Files.lines(Paths.get("source-db-config.txt"))) {
            List<String> db_files = FXCollections.observableArrayList();
                stream.forEach(db_files::add);
            if(! db_files.isEmpty()) {
                //FilePath.xsdDir = db_files.get(0);
                sourceHost.setText(db_files.get(0));
                sourcePort.setText(db_files.get(1));
                sourceUsername.setText(db_files.get(2));
                sourcePassword.setText(db_files.get(3));
                sourceDb.setText(db_files.get(4));
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        }

        if(FilePath.xsdDir != null){
            showDirectory.setText("Source Directory: " + FilePath.xsdDir);
        }
            this.tablesComboBox.setItems(FXCollections.observableArrayList(tables.values()));
        Platform.runLater(() -> {
            this.sourceDB.setItems(FXCollections.observableArrayList("OpenMRS Migration",
                    "SEEDsCare Migration","IQCare Migration", "XML Based Migration"));
        });
    }

    @FXML
    public void getDirectory(){
        fileComboBox.setItems(null);
        if(fromFile.isSelected()) {
            showDirectory.setText("Select Data File Location");

            DirectoryChooser chooser = new DirectoryChooser();
            File file = chooser.showDialog(mainLayer.getScene().getWindow());

            if (file != null) {

                FilePath.xsdDir = file.getPath();

                File folder = new File(file.getPath());
                File[] listOfFiles = folder.listFiles();


                ObservableList<String> fileNames = FXCollections.observableArrayList();

                for (int i = 0; i < listOfFiles.length; i++) {

                    if (listOfFiles[i].isFile()) {
                        fileNames.add(listOfFiles[i].getName());
                        System.out.println("File " + listOfFiles[i].getName());
                    } else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                }
                fileComboBox.setItems(FXCollections.observableArrayList(fileNames));
                showDirectory.setText("Source Directory: " + FilePath.xsdDir);
            } else {
                showDirectory.setText("Chooser was Cancelled");
                fromFile.setSelected(false);
            }
        }
    }


    public void logToConsole(String text){
        Platform.runLater(()-> {
            if(text != null)
                appConsole.appendText(text);
        });

    }

    @FXML
    public void handleUpload(){

        appConsole.clear();
        logToConsole("############################# Data Migration ##############################\n");

        if(sourceDB.getSelectionModel().getSelectedItem() == null){
            logToConsole("Select Source First!");
        }
        else if(FilePath.xsdDir == null && sourceDB.getSelectionModel().getSelectedIndex() == 3) {
            logToConsole("Please select XML Location first!");
        }else {
            try {

                try {
                    tab = tablesComboBox.getSelectionModel().getSelectedItem().value();
                } catch (NullPointerException ex) {
                    logToConsole("Select Table to Migrate and try again.");
                }
                if (tab != null) {
//            appConsole .setText("############################# Data Migration ##############################\n");
                    logToConsole("Setting up Database Connection Properties...\n");
                    SessionManager.host = host.getText();
                    SessionManager.port = port.getText();
                    SessionManager.username = username.getText();
                    SessionManager.password = password.getText();
                    SessionManager.db = db.getText();
                    checkConnection();
                    //logToConsole("Connecting to Database...\n");


                    switch (tab) {
                        case "encounter":
                            loadEncounter();
                            //new Thread(()->loadEncounter());
                            break;
                        case "obs":
                            loadObs();
                            //new Thread(()->loadObs());
                            break;
                        case "patient":
                            loadPatient();
                            //new Thread(()->loadPatient());
                            break;
                        case "patientidentifier":
                            loadPatientIdentifier();
                            //new Thread(()->loadPatientIdentifier());
                            break;
                        case "patientprogram":
                            loadPatientProgram();
                            //new Thread(()->loadPatientProgram());
                            break;
                        case "person":
                            loadPerson();
                            //new Thread(()->loadPerson());
                            break;
                        case "personaddress":
                            loadPersonAddress();
                            //new Thread(()->loadPersonAddress());
                            break;
                        case "personattribute":
                            loadPersonAttribute();
                            //new Thread(()->loadPersonAttribute());
                            break;
                        case "personname":
                            loadPersonName();
                            //new Thread(()->loadPersonName());
                            break;
                        case "visit":
                            loadVisit();
                            //new Thread(()->loadVisit());
                            break;
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (tab) {
                                    case "encounter":
                                        progressBar.progressProperty().bind(encounterTask.progressProperty());
                                        progressIndicator.progressProperty().bind(encounterTask.progressProperty());
                                        new Thread(encounterTask).start();
                                        break;
                                    case "obs":
                                        progressBar.progressProperty().bind(obsTask.progressProperty());
                                        progressIndicator.progressProperty().bind(obsTask.progressProperty());
                                        new Thread(obsTask).start();
                                        break;
                                    case "patient":
                                        progressBar.progressProperty().bind(patientTask.progressProperty());
                                        progressIndicator.progressProperty().bind(patientTask.progressProperty());
                                        new Thread(patientTask).start();
                                        break;
                                    case "patientidentifier":
                                        progressBar.progressProperty().bind(patientIdentifierTask.progressProperty());
                                        progressIndicator.progressProperty().bind(patientIdentifierTask.progressProperty());
                                        new Thread(patientIdentifierTask).start();
                                        break;
                                    case "patientprogram":
                                        progressBar.progressProperty().bind(patientProgramTask.progressProperty());
                                        progressIndicator.progressProperty().bind(patientProgramTask.progressProperty());
                                        new Thread(patientProgramTask).start();
                                        break;
                                    case "person":
                                        progressBar.progressProperty().bind(personTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personTask.progressProperty());
                                        new Thread(personTask).start();
                                        break;
                                    case "personaddress":
                                        progressBar.progressProperty().bind(personAddressTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personAddressTask.progressProperty());
                                        new Thread(personAddressTask).start();
                                        break;
                                    case "personattribute":
                                        progressBar.progressProperty().bind(personAttributeTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personAttributeTask.progressProperty());
                                        new Thread(personAttributeTask).start();
                                        break;
                                    case "personname":
                                        progressBar.progressProperty().bind(personNameTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personNameTask.progressProperty());
                                        new Thread(personNameTask).start();
                                        break;
                                    case "visit":
                                        progressBar.progressProperty().bind(visitTask.progressProperty());
                                        progressIndicator.progressProperty().bind(visitTask.progressProperty());
                                        new Thread(visitTask).start();
                                        break;
                                }

                            } catch (ArrayIndexOutOfBoundsException ex) {
                                logToConsole("Error: " + ex.getMessage());
                            }
                        }
                    });
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                logToConsole("Error: " + ex.getMessage());
            }
        }
    }

    private void checkConnection(){

            try (PrintWriter writer = new PrintWriter("db-config.txt", "UTF-8")) {
                writer.println(FilePath.xsdDir);
                writer.println(host.getText());
                writer.println(port.getText());
                writer.println(username.getText());
                writer.println(password.getText());
                writer.println(db.getText());

            }catch (IOException exc){
                logToConsole("Error writing Configs to file: "+exc.getMessage()+"..... \n");
            }

            try (PrintWriter writer = new PrintWriter("source-db-config.txt", "UTF-8")) {

                writer.println(sourceHost.getText());
                writer.println(sourcePort.getText());
                writer.println(sourceUsername.getText());
                writer.println(sourcePassword.getText());
                writer.println(sourceDb.getText());

            }catch (IOException exc){
                logToConsole("Error writing Configs to source file: "+exc.getMessage()+"..... \n");
            }
        //}
    }

    String source_jdbcUrl = null;
    String driver = null;
    String source_username = null;
    String source_password = null;
    String dbTYPE = null;
    String suffix = null;
    File file = null;
    private void connectionSettings(){

        suffix = tableSuffix.getText();
        source_username = sourceUsername.getText();
        source_password = sourcePassword.getText();


        if (sourceDB.getSelectionModel().getSelectedIndex() == 0) {
            dbTYPE = "MYSQL DB";
            try {
                logToConsole("#################### OpenMRS MIGRATION!");
                driver = "com.mysql.jdbc.Driver";
                source_jdbcUrl = "jdbc:mysql://" + sourceHost.getText() + ":" + sourcePort.getText() + "/" + sourceDb.getText() +
                        "?useServerPrepStmts=false&rewriteBatchedStatements=true";
            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else if (sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                sourceDB.getSelectionModel().getSelectedIndex() == 2) {

            if(sourceDB.getSelectionModel().getSelectedIndex() == 1)
                logToConsole("#################### SEEDsCare MIGRATION!");
            else
                logToConsole("#################### IQCare MIGRATION!");

            dbTYPE = "SQLSERVER";
            try {
                driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                source_jdbcUrl = "jdbc:sqlserver://" + sourceHost.getText() + ";databaseName=" + sourceDb.getText();
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                            Connection conn = DriverManager.getConnection(url, source_username, source_password);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }



    }

    private void closeConnection(Connection conn ){
        try{
            if(conn!=null)
                conn.close();
        }catch(SQLException se){
            se.printStackTrace();
            logToConsole("\n Error: "+se.getMessage());
        }//end finally try
    }

//########################### Encounter ###################################

    public void EncounterToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Encounters.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfEncounter, new File("encounter.xml"));
        marshaller.marshal(ArrayOfEncounter, System.out);

    }

    private void rollbackTransaction(Connection conn, Exception e){
        if (conn != null) {
            try {
                logToConsole("Transaction is being rolled back: " + e.getMessage());
                conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadEncounter(){
        encounterTask = new Task<ObservableList<Encounter>>() {
            @Override
            protected ObservableList<Encounter> call() throws Exception {

                try {
                    allEncounters = FXCollections.observableArrayList();
                    Encounters Encounters = new Encounters();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();
//
                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else {
                                sql = "SELECT * FROM " + suffix + "encounter";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
                                //Retrieve by column name
                                Encounter encounter = new Encounter();
                                encounter.setEncounter_id(rs.getInt("encounter_id"));
                                encounter.setEncounter_datetime(rs.getDate("encounter_datetime"));
                                if(tablesComboBox.getSelectionModel().getSelectedIndex() == 0) {
                                    if (rs.getInt("encounter_type") == 1 || rs.getInt("encounter_type") == 3)
                                        encounter.setEncounter_type(9);
                                    else if (rs.getInt("encounter_type") == 2)
                                        encounter.setEncounter_type(12);
                                    else if (rs.getInt("encounter_type") == 4)
                                        encounter.setEncounter_type(18);
                                    else if (rs.getInt("encounter_type") == 5)
                                        encounter.setEncounter_type(11);
                                    else if (rs.getInt("encounter_type") == 7)
                                        encounter.setEncounter_type(13);
                                    else if (rs.getInt("encounter_type") == 8)
                                        encounter.setEncounter_type(7);
                                    else if (rs.getInt("encounter_type") == 13 || rs.getInt("encounter_type") == 9)
                                        encounter.setEncounter_type(10);
                                    else if (rs.getInt("encounter_type") == 15)
                                        encounter.setEncounter_type(8);
                                    else
                                        encounter.setEncounter_type(rs.getInt("encounter_type"));
                                }else
                                    encounter.setEncounter_type(rs.getInt("encounter_type"));
                                encounter.setUuid(UUID.randomUUID());
                                encounter.setCreator(1);
                                encounter.setDate_changed(rs.getDate("date_changed"));
                                encounter.setDate_created(rs.getDate("date_created"));
                                encounter.setDate_voided(rs.getDate("date_voided"));
                                if(suffix == "") {
                                    if (rs.getInt("form_id") == 1)
                                        encounter.setForm_id(14);
                                    else if (rs.getInt("form_id") == 16)
                                        encounter.setForm_id(44);
                                    else if (rs.getInt("form_id") == 18)
                                        encounter.setForm_id(43);
                                }else
                                    encounter.setForm_id(rs.getInt("form_id"));
                                encounter.setLocation_id(2);
                                encounter.setPatient_id(rs.getInt("patient_id"));
                                encounter.setVisit_id(rs.getInt("encounter_id"));
                                encounter.setVoid_reason(rs.getString("void_reason"));
                                encounter.setVoided(rs.getBoolean("voided"));

                                allEncounters.add(encounter);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else if(sourceDB.getSelectionModel().getSelectedIndex() == 3) {
                        logToConsole("#################### XML BASED MIGRATION!");
                            File file = null;

                            try {
                                logToConsole("Fetching encounter.xml file......\n");
                                file = new File(xsdDir + "/encounter.xml");
                                logToConsole("File fetched......\n");
                            } catch (Exception e) {
                                logToConsole("Error opening file encounter.xml: " + e.getMessage() + "\n");
                            }

                            try {
                                logToConsole("Converting file to a Model......\n");
                                JAXBContext jaxbContext = JAXBContext.newInstance(Encounters.class);
                                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                                Encounters = (Encounters) unmarshaller.unmarshal(file);
                                logToConsole("Conversion Done......\n");
                            } catch (Exception exc) {
                                logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                            }

                    }

                    if(allEncounters.isEmpty()){
                        for (Encounter theEncounter : Encounters.getEncounters()) {
                            theEncounter.setUuid(UUID.randomUUID());
                            if(theEncounter.getPatient_id() < 1){
                                logToConsole("Error: No Patient ID in: "+theEncounter);
                            }else if(theEncounter.getEncounter_id() < 1){
                                logToConsole("Error: No Encounter ID in: "+theEncounter);
                            }else if(theEncounter.getForm_id() < 1){
                                logToConsole("Error: No Form ID in: "+theEncounter);
                            }
                            allEncounters.add(theEncounter);
                        }
                    }
                    if(! allEncounters.isEmpty()) {

                        Encounter currentEncounter = new Encounter();


                        String INSERT_SQL = "INSERT INTO encounter"
                                + "(encounter_id, encounter_type, patient_id, location_id, form_id, encounter_datetime, creator, date_created, " +
                                "date_changed, voided, date_voided, void_reason, uuid, visit_id) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        logToConsole("\n Connecting to destination DB! \n");
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {
                                conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records
                                for (Encounter module : allEncounters) {
                                    try {
                                        stmt.setInt(1, module.getEncounter_id());
                                        stmt.setInt(2, module.getEncounter_type());
                                        stmt.setInt(3, module.getPatient_id());
                                        stmt.setInt(4, module.getLocation_id());
                                        stmt.setInt(5, module.getForm_id());
                                        stmt.setDate(6, new java.sql.Date(module.getEncounter_datetime().getTime()));
                                        stmt.setInt(7, module.getCreator());
                                        if(module.getDate_created() != null)
                                            stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
                                        else
                                            stmt.setDate(8, null);
                                        if(module.getDate_changed() != null)
                                            stmt.setDate(9, new java.sql.Date(module.getDate_changed().getTime()));
                                        else
                                            stmt.setDate(9, null);
                                        stmt.setBoolean(10, module.isVoided());
                                        if(module.getDate_voided() != null)
                                            stmt.setDate(11, new java.sql.Date(module.getDate_voided().getTime()));
                                        else
                                            stmt.setDate(11, null);
                                        stmt.setString(12, module.getVoid_reason());
                                        stmt.setString(13, module.getUuid().toString());
                                        stmt.setInt(14, module.getVisit_id());

                                        //Add statement to batch
                                        stmt.addBatch();
                                        updateProgress(wDone + 1, allEncounters.size());
                                        Integer pDone = ((wDone + 1) / allEncounters.size()) * 100;
                                        wDone++;
                                    }catch (Exception ex){
                                        StringWriter errors = new StringWriter();
                                        ex.printStackTrace(new PrintWriter(errors));
                                        logToConsole(errors.toString());
                                        ex.printStackTrace();
                                    }
                                }
                                //execute batch
                                try {
                                    stmt.executeBatch();
                                    conn.commit();
                                    logToConsole("Transaction is committed successfully.");
                                }catch (Exception ex){
                                    logToConsole("Error in batch insert: "+ ex.getMessage());
                                    closeConnection(conn);
                                }
                            } catch (SQLException e) {
                                logToConsole("Error running Insert statement: "+e.getMessage());
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            logToConsole("Error Establishing connection: "+e.getMessage());
                            e.printStackTrace();

                        }
                    }else {

                    }
                    return FXCollections.observableArrayList(allEncounters);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

                }

        };
    }

    //########################### End of Encounter ###################################

    //########################### Obs ###################################

    public void ObsToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Obses.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfObs, new File("obs.xml"));
        marshaller.marshal(ArrayOfObs, System.out);

    }


    private void loadObs(){
        obsTask = new Task<ObservableList<Obs>>() {
            @Override
            protected ObservableList<Obs> call() throws Exception {
                try {
                    allObses = FXCollections.observableArrayList();
                    Obses obses = new Obses();
                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"obs";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
                                //Retrieve by column name
                                Obs obs = new Obs();
                                obs.setAccession_number(rs.getString("accession_number"));
                                obs.setComments(rs.getString("comments"));
                                obs.setConcept_id(rs.getInt("concept_id"));
                                obs.setUuid(UUID.randomUUID());
                                obs.setCreator(1);
                                obs.setDate_created(rs.getDate("date_created"));
                                obs.setDate_voided(rs.getDate("date_voided"));
                                obs.setEncounter_id(rs.getInt("encounter_id"));
                                obs.setLocation_id(2);
                                obs.setForm_namespace_and_path(rs.getString("form_namespace_and_path"));
                                obs.setVisit_id(rs.getInt("visit_id"));
                                obs.setVoid_reason(rs.getString("void_reason"));
                                obs.setVoided(rs.getBoolean("voided"));
                                obs.setObs_datetime(rs.getDate("obs_datetime"));
                                obs.setObs_group_id(rs.getInt("obs_group_id"));
                                obs.setObs_id(rs.getInt("obs_id"));
                                obs.setOrder_id(rs.getInt("order_id"));
                                obs.setPerson_id(rs.getInt("person_id"));
                                obs.setPrevious_version(rs.getInt("previous_version"));
                                obs.setValue_coded(rs.getInt("value_coded"));
                                obs.setValue_coded_name_id(rs.getInt("value_coded_name_id"));
                                obs.setValue_complex(rs.getString("value_complex"));
                                obs.setValue_datetime(rs.getDate("value_datetime"));
                                obs.setValue_drug(rs.getInt("value_drug"));
                                obs.setValue_group_id(rs.getInt("value_group_id"));
                                obs.setValue_modifier(rs.getString("value_modifier"));
                                obs.setValue_numeric(rs.getDouble("value_numeric"));
                                obs.setValue_text(rs.getString("value_text"));
                                obs.setVoided_by(rs.getInt("voided_by"));
                                allObses.add(obs);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else{
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching obs.xml file......\n");
                            file = new File(xsdDir + "/obs.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file obs.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(Obses.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            obses = (Obses) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if(allObses.isEmpty()) {
                        for (Obs theObs : obses.getObses()) {
                            theObs.setUuid(UUID.randomUUID());
                            allObses.add(theObs);
                        }
                    }

                    if(! allObses.isEmpty()) {
                        Obs currentObs = new Obs();

                        String INSERT_SQL = "INSERT INTO obs"
                                + "(obs_id, person_id, concept_id, encounter_id, order_id, obs_datetime, location_id," +
                                "accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, " +
                                "value_numeric, value_modifier, value_text, value_complex, comments," +
                                "creator, date_created, voided, date_voided, void_reason, uuid, previous_version, form_namespace_and_path) " +
                                "VALUES ( NULL ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {

                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records

                                for (Obs module : allObses) {
                                    try {
                                        currentObs = module;
                                        //stmt.setInt(1, module.getObs_id());
                                        //stmt.setInt(1, module.getVisit_id());
                                        stmt.setInt(1, module.getPerson_id());
                                        stmt.setInt(2, module.getConcept_id());
                                        stmt.setInt(3, module.getEncounter_id());
                                        stmt.setInt(4, module.getOrder_id());
                                        if (module.getObs_datetime() != null)
                                            stmt.setDate(5, new java.sql.Date(module.getObs_datetime().getTime()));
                                        else
                                            stmt.setDate(5, null);
                                        if(module.getLocation_id() >= 0)
                                        stmt.setInt(6, module.getLocation_id());
                                        else
                                        stmt.setInt(6, 0);
                                        stmt.setString(7, module.getAccession_number());
                                            stmt.setInt(8, module.getValue_group_id());
                                        stmt.setInt(9, module.getValue_coded());
                                        stmt.setInt(10, module.getValue_coded_name_id());
                                        stmt.setInt(11, module.getValue_drug());
                                        if (module.getValue_datetime() != null)
                                            stmt.setDate(12, new java.sql.Date(module.getValue_datetime().getTime()));
                                        else
                                            stmt.setDate(12, null);
                                        stmt.setDouble(13, module.getValue_numeric());
                                        stmt.setString(14, module.getValue_modifier());
                                        stmt.setString(15, module.getValue_text());
                                        stmt.setString(16, module.getValue_complex());
                                        stmt.setString(17, module.getComments());
                                        stmt.setInt(18, module.getCreator());
                                        if (module.getDate_created() != null)
                                            stmt.setDate(19, new java.sql.Date(module.getDate_created().getTime()));
                                        else
                                            stmt.setDate(19, null);
                                        stmt.setBoolean(20, module.isVoided());
                                        if (module.getDate_voided() != null)
                                            stmt.setDate(21, new java.sql.Date(module.getDate_voided().getTime()));
                                        else
                                            stmt.setDate(21, null);
                                        stmt.setString(22, module.getVoid_reason());
                                        stmt.setString(23, module.getUuid().toString());
                                        stmt.setInt(24, module.getPrevious_version());
                                        stmt.setString(25, module.getForm_namespace_and_path());

                                        //Add statement to batch
                                        stmt.addBatch();
                                        updateProgress(wDone + 1, allObses.size());
                                        Integer pDone = ((wDone + 1) / allObses.size()) * 100;
                                        wDone++;
                                    }catch(Exception ex){
                                        logToConsole("\n Exception Error: "+ ex.getMessage() + " in Obs with "+currentObs.getEncounter_id()+" Encounter ID!");
                                        ex.printStackTrace();
                                    }
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allObses);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("ArrayIndexOutOfBoundsException Error: "+ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }catch (HibernateException ex){
                    logToConsole("HibernateException Error: "+ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }catch (Exception ex){
                    logToConsole("Exception Error: "+ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }

            }

        };
    }

    //########################### End of Obs ###################################

    //########################### Patient ###################################

    public void PatientToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Patients.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPatient, new File("patient.xml"));
        marshaller.marshal(ArrayOfPatient, System.out);

    }

    private void loadPatient(){
        patientTask = new Task<ObservableList<Patient>>() {
            @Override
            protected ObservableList<Patient> call() throws Exception {
                try {
                    allPatients = FXCollections.observableArrayList();
                    Patients patients = new Patients();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"patient";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
                                //Retrieve by column name
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
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                                closeConnection(conn);
                        }//end try

                    }else{
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching patient.xml file......\n");
                            file = new File(xsdDir + "/patient.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file patient.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(Patients.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            patients = (Patients) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if(allPatients.isEmpty()) {
                        for (Patient thePatient : patients.getPatients()) {
                            //thePatient.setUuid(UUID.randomUUID());
                            allPatients.add(thePatient);
                        }
                    }

                    if(! allPatients.isEmpty()) {
                        Patient currentPatient = new Patient();
                        logToConsole("\n Loading Data.!\n");
                        String INSERT_SQL = "INSERT INTO patient"
                                + "(patient_id, creator, date_created, voided, date_voided, void_reason) " +
                                "VALUES ( ?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {
                            logToConsole("\n Loading Data..!\n");
                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                logToConsole("\n Loading Data...!\n");
                                int wDone = 0;
                                // Insert sample records
                                for (Patient module : allPatients) {
                                    //logToConsole("\n Loading Data...!!\n");
                                    stmt.setInt(1, module.getPatient_id());
                                    stmt.setInt(2, module.getCreator());
                                    if(module.getDate_created() != null)
                                        stmt.setDate(3, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(3, null);
                                    stmt.setBoolean(4, module.isVoided());
                                    if(module.getDate_voided() != null)
                                        stmt.setDate(5, new java.sql.Date(module.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(5, null);
                                    stmt.setString(6, module.getVoid_reason());
                                    //stmt.setString(7, module.getAllergy_status().toString());

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allPatients.size());
                                    Integer pDone = ((wDone + 1) / allPatients.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                logToConsole("\n Loading Data....!\n");
                                stmt.executeBatch();
                                logToConsole("\n Loading Data.....!\n");
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allPatients);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Patient ###################################

    //########################### Patient Identifier ###################################

    public void PatientIdentifierToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PatientIdentifiers.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPatientIdentifiers, new File("patient_identifier.xml"));
        marshaller.marshal(ArrayOfPatientIdentifiers, System.out);

    }

    private void loadPatientIdentifier(){
        patientIdentifierTask = new Task<ObservableList<PatientIdentifier>>() {
            @Override
            protected ObservableList<PatientIdentifier> call() throws Exception {
                try {
                    allPatientIdentifiers = FXCollections.observableArrayList();
                    PatientIdentifiers patientIdentifiers = new PatientIdentifiers();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"patient_identifier";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
                                //Retrieve by column name
                                PatientIdentifier patientIdentifier = new PatientIdentifier();
                                patientIdentifier.setPatient_identifier_id(rs.getInt("patient_identifier_id"));
                                patientIdentifier.setPreferred(rs.getBoolean("preferred"));
                                if(rs.getInt("identifier_type") == 3)
                                    patientIdentifier.setIdentifier_type(5);
                                else
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
                            logToConsole("\n Data Successfully Fetched!\n");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else {
                        logToConsole("#################### XML BASED MIGRATION!");
                        File file = null;

                        try {
                            logToConsole("Fetching patient_identifier.xml file......\n");
                            file = new File(xsdDir + "/patient_identifier.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file patient_identifier.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(PatientIdentifiers.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            patientIdentifiers = (PatientIdentifiers) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    //logToConsole("Done 1......\n");
                    if(allPatientIdentifiers.isEmpty()) {
                        for (PatientIdentifier thePatientIdentifier : patientIdentifiers.getPatient_identifiers()) {
                            thePatientIdentifier.setUuid(UUID.randomUUID());
                            if (thePatientIdentifier.getIdentifier() != null) {
                                //logToConsole("NULL Found in: "+thePatientIdentifier);
                                allPatientIdentifiers.add(thePatientIdentifier);
                            }

                        }
                    }

                    if(! allPatientIdentifiers.isEmpty()) {

                        String INSERT_SQL = "INSERT INTO patient_identifier"
                                + "(patient_identifier_id, patient_id, identifier, identifier_type, preferred, location_id," +
                                " creator, date_created, voided, date_voided, void_reason, uuid) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {

                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records
                                for (PatientIdentifier module : allPatientIdentifiers) {
                                    stmt.setInt(1, module.getPatient_identifier_id());
                                    stmt.setInt(2, module.getPatient_id());
                                    stmt.setString(3, module.getIdentifier());
                                    stmt.setInt(4, module.getIdentifier_type());
                                    stmt.setBoolean(5, module.isPreferred());
                                    stmt.setInt(6, module.getLocation_id());
                                    stmt.setInt(7, module.getCreator());
                                    if(module.getDate_created() != null)
                                        stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(8, null);
                                    stmt.setBoolean(9, module.isVoided());
                                    if(module.getDate_voided() != null)
                                        stmt.setDate(10, new java.sql.Date(module.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(10, null);
                                    stmt.setString(11, module.getVoid_reason());
                                    stmt.setString(12, module.getUuid().toString());

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allPatientIdentifiers.size());
                                    Integer pDone = ((wDone + 1) / allPatientIdentifiers.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allPatientIdentifiers);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Patient Identifiers ###################################

    //########################### Patient Program ###################################

    public void PatientProgramToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PatientPrograms.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPatientPrograms, new File("patient_program.xml"));
        marshaller.marshal(ArrayOfPatientPrograms, System.out);

    }

    private void loadPatientProgram(){
        patientProgramTask = new Task<ObservableList<PatientProgram>>() {
            @Override
            protected ObservableList<PatientProgram> call() throws Exception {
                try {
                    allPatientPrograms = FXCollections.observableArrayList();
                    PatientPrograms patientPrograms = new PatientPrograms();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"patient_program";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
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
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching patient_program.xml file......\n");
                            file = new File(xsdDir + "/patient_program.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file patient_program.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(PatientPrograms.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            patientPrograms = (PatientPrograms) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if(allPatientPrograms.isEmpty()) {
                        for (PatientProgram thePatientProgram : patientPrograms.getPatient_programs()) {
                            thePatientProgram.setUuid(UUID.randomUUID());
                            allPatientPrograms.add(thePatientProgram);
                        }
                    }

                    if(! allPatientPrograms.isEmpty()) {
                        PatientProgram currentPatientProgram = new PatientProgram();

                        String INSERT_SQL = "INSERT INTO patient_program"
                                + "(patient_program_id, patient_id, program_id, date_enrolled, date_completed, location_id," +
                                " creator, date_created, voided, date_voided, void_reason, uuid) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {

                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records
                                logToConsole("\n Loading Data.!!\n");
                                for (PatientProgram module : allPatientPrograms) {

                                    stmt.setInt(1, module.getPatient_program_id());
                                    stmt.setInt(2, module.getPatient_id());
                                    stmt.setInt(3, module.getProgram_id());

                                    if(module.getDate_enrolled() != null)
                                        stmt.setDate(4, new java.sql.Date(module.getDate_enrolled().getTime()));
                                    else
                                        stmt.setDate(4, null);
                                    //logToConsole("\n Loading Data...!!\n");
                                    if(module.getDate_completed() != null)
                                        stmt.setDate(5, new java.sql.Date(module.getDate_completed().getTime()));
                                    else
                                        stmt.setDate(5, null);
                                    //logToConsole("\n Loading Data....!!\n");
                                    stmt.setInt(6, module.getLocation_id());
                                    //stmt.setInt(7, module.getOutcome_concept_id());
                                    stmt.setInt(7, module.getCreator());
                                    //logToConsole("\n Loading Data.....!!\n");
                                    if(module.getDate_created() != null)
                                        stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(8, null);
                                    //logToConsole("\n Loading Data......!!\n");
                                    stmt.setBoolean(9, module.isVoided());
                                    if(module.getDate_voided() != null)
                                        stmt.setDate(10, new java.sql.Date(module.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(10, null);
                                    stmt.setString(11, module.getVoid_reason());
                                    stmt.setString(12, module.getUuid().toString());
                                    //logToConsole("\n Loading Data..........!!\n");
                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allPatientPrograms.size());
                                    Integer pDone = ((wDone + 1) / allPatientPrograms.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                logToConsole("\n Loading Data...!!\n");
                                stmt.executeBatch();
                                logToConsole("\n Loading Data....!!\n");
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allPatientPrograms);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Patient Program ###################################


    //########################### Person ###################################

    public void PersonToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Persons.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPersons, new File("person.xml"));
        marshaller.marshal(ArrayOfPersons, System.out);

    }

    private void loadPerson(){
        personTask = new Task<ObservableList<Person>>() {
            @Override
            protected ObservableList<Person> call() throws Exception {

                try {
                    allPersons = FXCollections.observableArrayList();
                    Persons persons = new Persons();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source "+dbTYPE+" Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Source Database connection successfully...\n");

                            //STEP 4: Execute a query

                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"person";
                            }
                            logToConsole("Creating Select statement...\n");
                            stmt = conn.createStatement();

                            ResultSet rs = stmt.executeQuery(sql);

                            //STEP 5: Extract data from result set
                            while(rs.next()){
                                //Retrieve by column name
                                Person person = new Person();
                                person.setPerson_id(rs.getInt("person_id"));
                                person.setGender(rs.getString("gender"));
                                if(rs.getDate("birthdate") != null)
                                    person.setBirthdate(rs.getDate("birthdate"));
                                person.setBirthdate_estimated(rs.getBoolean("birthdate_estimated"));
                                person.setDead(rs.getBoolean("dead"));
                                //person.setDeath_date(rs.getDate("death_date"));
                                //if(rs.findColumn("deathdate_estimated") <= 0)
                                    //person.setDeathdate_estimated(rs.getBoolean("deathdate_estimated"));
                                //if(rs.findColumn("birthtime") <= 0)
                                    //person.setBirthtime(rs.getTime("birthtime"));
                                //person.setUuid(UUID.randomUUID());
                                person.setUuid(UUID.randomUUID());
                                person.setCreator(1);
                                person.setDate_changed(rs.getDate("date_changed"));
                                person.setDate_created(rs.getDate("date_created"));
                                //person.setDate_voided(rs.getDate("date_voided"));
                                person.setDate_voided(null);
                                person.setVoid_reason(rs.getString("void_reason"));
                                person.setVoided(rs.getBoolean("voided"));

                                allPersons.add(person);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else{
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching person.xml file......\n");
                            file = new File(xsdDir + "/person.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file person.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(Persons.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            persons = (Persons) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if(allPersons.isEmpty()) {
                        for (Person thePerson : persons.getPersons()) {
                            thePerson.setUuid(UUID.randomUUID());
                            allPersons.add(thePerson);
                        }
                    }
                    if(! allPersons.isEmpty()) {
                        logToConsole("Loading Data......\n");
                        String INSERT_SQL = "INSERT INTO person"
                                + "(person_id, gender, birthdate, birthdate_estimated, dead, death_date, creator, date_created, " +
                                "date_changed, voided, date_voided, void_reason, uuid, deathdate_estimated, birthtime) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {

                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records
                                for (Person person : allPersons) {
                                    stmt.setInt(1, person.getPerson_id());
                                    stmt.setString(2, person.getGender());
                                    if(person.getBirthdate() != null)
                                        stmt.setDate(3, new java.sql.Date(person.getBirthdate().getTime()));
                                    else
                                        stmt.setDate(3, null);
                                    stmt.setBoolean(4, person.getBirthdate_estimated());
                                    stmt.setBoolean(5, person.getDead());
                                    if(person.getDeath_date() != null)
                                        stmt.setDate(6, new java.sql.Date(person.getDeath_date().getTime()));
                                    else
                                        stmt.setDate(6, null);
                                    stmt.setInt(7, person.getCreator());
                                    stmt.setDate(8, new java.sql.Date(person.getDate_created().getTime()));
                                    if(person.getDate_changed() != null)
                                        stmt.setDate(9, new java.sql.Date(person.getDate_changed().getTime()));
                                    else
                                        stmt.setDate(9, null);
                                    stmt.setBoolean(10, person.getVoided());
                                    if(person.getDate_voided() != null)
                                        stmt.setDate(11, new java.sql.Date(person.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(11, null);
                                    stmt.setString(12, person.getVoid_reason());
                                    stmt.setString(13, person.getUuid().toString());
                                    stmt.setBoolean(14, person.getDeathdate_estimated());
                                    if(person.getBirthtime() != null)
                                        stmt.setTime(15, new java.sql.Time(person.getBirthtime().getTime()));
                                    else
                                        stmt.setTime(15, null);

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allPersons.size());
                                    Integer pDone = ((wDone + 1) / allPersons.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allPersons);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Person ###################################

    //########################### Person Address ##################################

    public void PersonAddressToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PersonAddresses.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPersonAddresses, new File("person_address.xml"));
        marshaller.marshal(ArrayOfPersonAddresses, System.out);

    }

    private void loadPersonAddress(){
        personAddressTask = new Task<ObservableList<PersonAddress>>() {
            @Override
            protected ObservableList<PersonAddress> call() throws Exception {
                try {
                    allPersonAddresses = FXCollections.observableArrayList();
                    PersonAddresses personAddressses = new PersonAddresses();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"person_address";
                            }
                            logToConsole("Creating Select statement...\n");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
                                //Retrieve by column name
                                PersonAddress personAddress = new PersonAddress();
                                personAddress.setPerson_address_id(rs.getInt("person_address_id"));
                                personAddress.setCity_village(rs.getString("city_village"));
                                personAddress.setCountry(rs.getString("country"));
                                //personAddress.setStart_date(rs.getDate("start_date"));
                                //personAddress.setEnd_date(rs.getDate("end_date"));
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
                                /*personAddress.setAddress7(rs.getString("address7"));
                                personAddress.setAddress8(rs.getString("address8"));
                                personAddress.setAddress9(rs.getString("address9"));
                                personAddress.setAddress10(rs.getString("address10"));
                                personAddress.setAddress11(rs.getString("address11"));
                                personAddress.setAddress12(rs.getString("address12"));
                                personAddress.setAddress13(rs.getString("address13"));
                                personAddress.setAddress14(rs.getString("address14"));
                                personAddress.setAddress15(rs.getString("address15"));*/
                                personAddress.setUuid(UUID.randomUUID());
                                personAddress.setCreator(1);
                                //personAddress.setDate_changed(rs.getDate("date_changed"));
                                personAddress.setDate_created(rs.getDate("date_created"));
                                personAddress.setDate_voided(rs.getDate("date_voided"));
                                personAddress.setVoid_reason(rs.getString("void_reason"));
                                personAddress.setVoided(rs.getBoolean("voided"));

                                allPersonAddresses.add(personAddress);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching person_address.xml file......\n");
                            file = new File(xsdDir + "/person_address.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file person_address.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(PersonAddresses.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            personAddressses = (PersonAddresses) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if(allPersonAddresses.isEmpty()) {
                        for (PersonAddress thePersonAddress : personAddressses.getPerson_addresses()) {
                            thePersonAddress.setUuid(UUID.randomUUID());
                            allPersonAddresses.add(thePersonAddress);
                        }
                    }

                    if(! allPersonAddresses.isEmpty()) {
                        PersonAddress currentPersonAddress = new PersonAddress();

                        String INSERT_SQL = "INSERT INTO person_address"
                                + "(person_address_id, person_id, preferred, city_village, state_province, postal_code, country, latitude," +
                                "longitude, start_date, end_date, address1, address2, address3, address4, address5, " +
                                "address6, address7, address8, address9, address10, address11, address12, address13, address14, address15," +
                                " creator, date_created, date_changed, voided, date_voided, void_reason, uuid) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {

                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records
                                for (PersonAddress module : allPersonAddresses) {
                                    stmt.setInt(1, module.getPerson_address_id());
                                    stmt.setInt(2, module.getPerson_id());
                                    stmt.setBoolean(3, module.isPreferred());
                                    stmt.setString(4, module.getCity_village());
                                    stmt.setString(5, module.getState_province());
                                    stmt.setString(6, module.getPostal_code());
                                    stmt.setString(7, module.getCountry());
                                    stmt.setString(8, module.getLatitude());
                                    stmt.setString(9, module.getLongitude());
                                    if(module.getStart_date() != null)
                                        stmt.setDate(10, new java.sql.Date(module.getStart_date().getTime()));
                                    else
                                        stmt.setDate(10, null);
                                    if(module.getEnd_date() != null)
                                        stmt.setDate(11, new java.sql.Date(module.getEnd_date().getTime()));
                                    else
                                        stmt.setDate(11, null);
                                    stmt.setString(12, module.getAddress1());
                                    stmt.setString(13, module.getAddress2());
                                    stmt.setString(14, module.getAddress3());
                                    stmt.setString(15, module.getAddress4());
                                    stmt.setString(16, module.getAddress5());
                                    stmt.setString(17, module.getAddress6());
                                    stmt.setString(18, module.getAddress7());
                                    stmt.setString(19, module.getAddress8());
                                    stmt.setString(20, module.getAddress9());
                                    stmt.setString(21, module.getAddress10());
                                    stmt.setString(22, module.getAddress11());
                                    stmt.setString(23, module.getAddress12());
                                    stmt.setString(24, module.getAddress13());
                                    stmt.setString(25, module.getAddress14());
                                    stmt.setString(26, module.getAddress15());
                                    stmt.setInt(27, module.getCreator());
                                    if(module.getDate_created() != null)
                                        stmt.setDate(28, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(28, null);
                                    if(module.getDate_changed() != null)
                                        stmt.setDate(29, new java.sql.Date(module.getDate_changed().getTime()));
                                    else
                                        stmt.setDate(29, null);
                                    stmt.setBoolean(30, module.isVoided());
                                    if(module.getDate_voided() != null)
                                        stmt.setDate(31, new java.sql.Date(module.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(31, null);
                                    stmt.setString(32, module.getVoid_reason());
                                    stmt.setString(33, module.getUuid().toString());

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allPersonAddresses.size());
                                    Integer pDone = ((wDone + 1) / allPersonAddresses.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allPersonAddresses);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Person Address ###################################


    //########################### Person Attribute ###################################

    public void PersonAttributeToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PersonAttributes.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPersonAttributes, new File("person_attribute.xml"));
        marshaller.marshal(ArrayOfPersonAttributes, System.out);

    }

    private void loadPersonAttribute(){
        personAttributeTask = new Task<ObservableList<PersonAttribute>>() {
            @Override
            protected ObservableList<PersonAttribute> call() throws Exception {
                try {
                    allPersonAttributes = FXCollections.observableArrayList();
                    PersonAttributes personAttributes = new PersonAttributes();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"person_attribute";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
                                //Retrieve by column name
                                PersonAttribute personAttribute = new PersonAttribute();
                                personAttribute.setPerson_attribute_id(rs.getInt("person_attribute_id"));
                                personAttribute.setPerson_id(rs.getInt("person_id"));
                                personAttribute.setValue(rs.getString("value"));
                                personAttribute.setPerson_attribute_type_id(rs.getInt("person_attribute_type_id"));
                                personAttribute.setUuid(UUID.randomUUID());
                                personAttribute.setCreator(1);
                                personAttribute.setDate_created(rs.getDate("date_created"));
                                personAttribute.setDate_voided(rs.getDate("date_voided"));
                                personAttribute.setVoid_reason(rs.getString("void_reason"));
                                personAttribute.setVoided(rs.getBoolean("voided"));

                                allPersonAttributes.add(personAttribute);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching person_attribute.xml file......\n");
                            file = new File(xsdDir + "/person_attribute.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file person_attribute.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(PersonAttributes.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            personAttributes = (PersonAttributes) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }
                    }
                    if(allPersonAttributes.isEmpty()) {
                        for (PersonAttribute thePersonAttribute : personAttributes.getPersonAttributes()) {
                            if(thePersonAttribute.getValue() != null) {
                                thePersonAttribute.setUuid(UUID.randomUUID());
                                allPersonAttributes.add(thePersonAttribute);
                            }
                        }
                    }

                    if(! allPersonAttributes.isEmpty()) {
                        PersonAttribute currentPersonAttribute = new PersonAttribute();

                        String INSERT_SQL = "INSERT INTO person_attribute"
                                + "(person_id, value, person_attribute_type_id, " +
                                " creator, date_created, voided, date_voided, void_reason, uuid) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {

                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records
                                for (PersonAttribute module : allPersonAttributes) {
                                    //stmt.setInt(1, module.getPerson_attribute_id());
                                    stmt.setInt(1, module.getPerson_id());
                                    stmt.setString(2, module.getValue());
                                    stmt.setInt(3, module.getPerson_attribute_type_id());
                                    stmt.setInt(4, module.getCreator());
                                    if(module.getDate_created() != null)
                                        stmt.setDate(5, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(5, null);
                                    stmt.setBoolean(6, module.isVoided());
                                    if(module.getDate_voided() != null)
                                        stmt.setDate(7, new java.sql.Date(module.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(7, null);
                                    stmt.setString(8, module.getVoid_reason());
                                    stmt.setString(9, module.getUuid().toString());

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allPersonAttributes.size());
                                    Integer pDone = ((wDone + 1) / allPersonAttributes.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allPersonAttributes);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Person Attribute ###################################

    //########################### Person Name ###################################

    public void PersonNameToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PersonNames.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPersonNames, new File("person_name.xml"));
        marshaller.marshal(ArrayOfPersonNames, System.out);

    }

    private void loadPersonName(){
        personNameTask = new Task<ObservableList<PersonName>>() {
            @Override
            protected ObservableList<PersonName> call() throws Exception {
                try {
                    allPersonNames = FXCollections.observableArrayList();
                    PersonNames personNames = new PersonNames();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if(fromFile.isSelected()){
                                sql = getSQL();
                            }else{
                                sql = "SELECT * FROM "+suffix+"person_name";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next()){
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

                                allPersonNames.add(personName);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching person_name.xml file......\n");
                            file = new File(xsdDir + "/person_name.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file person_name.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(PersonNames.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            personNames = (PersonNames) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }
                    }
                    if(allPersonNames.isEmpty()) {
                        for (PersonName thePersonName : personNames.getPerson_names()) {
//                        logToConsole("loading: "+thePersonName);
                            thePersonName.setUuid(UUID.randomUUID());
                            allPersonNames.add(thePersonName);
                        }
                    }

                    if(! allPersonNames.isEmpty()) {
//                    logToConsole("Done loading.");
                        String INSERT_SQL = "INSERT INTO person_name"
                                + "(preferred, person_id, prefix, given_name, middle_name, family_name_prefix, " +
                                "family_name, family_name2, family_name_suffix, degree," +
                                " creator, date_created, voided, date_voided, void_reason, uuid) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {
//                        logToConsole("Connecting here!");
                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
//                            logToConsole("Preparing statement here!");
                                // Insert sample records
                                for (PersonName module : allPersonNames) {
//                                try {
//                                    logToConsole("\n Adding module to batch! " + module);
                                    //stmt.setInt(1, module.getPerson_name_id());
                                    stmt.setBoolean(1, module.isPreferred());
                                    stmt.setInt(2, module.getPerson_id());
                                    stmt.setString(3, module.getPrefix());
                                    stmt.setString(4, module.getGiven_name());
                                    stmt.setString(5, module.getMiddle_name());
                                    stmt.setString(6, module.getFamily_name_prefix());
                                    stmt.setString(7, module.getFamily_name());
                                    stmt.setString(8, module.getFamily_name2());
                                    stmt.setString(9, module.getFamily_name_suffix());
                                    stmt.setString(10, module.getDegree());
                                    stmt.setInt(11, module.getCreator());
                                    if(module.getDate_created() != null)
                                        stmt.setDate(12, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(12, null);
                                    stmt.setBoolean(13, module.isVoided());
                                    if(module.getDate_voided() != null)
                                        stmt.setDate(14, new java.sql.Date(module.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(14, null);
                                    stmt.setString(15, module.getVoid_reason());
                                    stmt.setString(16, module.getUuid().toString());
//                                    logToConsole("Parameters Set!");
                                    //Add statement to batch
                                    stmt.addBatch();
//                                    logToConsole("Module Added!");
                                    updateProgress(wDone + 1, allPersonNames.size());
                                    Integer pDone = ((wDone + 1) / allPersonNames.size()) * 100;
                                    wDone++;
//                                }catch (Exception ex){
//                                    ex.printStackTrace();
//                                }
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allPersonNames);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Person Name ###################################

    //########################### Visit ###################################

    public void VisitToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Visits.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfVisits, new File("visit.xml"));
        marshaller.marshal(ArrayOfVisits, System.out);
    }

    private void loadVisit(){
        visitTask = new Task<ObservableList<Visit>>() {
            @Override
            protected ObservableList<Visit> call() throws Exception {
                try {
                    allVisits = FXCollections.observableArrayList();
                    Visits visits = new Visits();

                    if(sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        Connection conn = null;
                        Statement stmt = null;
                        try{
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            if(tablesComboBox.getSelectionModel().getSelectedIndex() == 0) {
                                String sql = "";
                                if(fromFile.isSelected()){
                                    sql = getSQL();
                                }else{
                                    sql = "SELECT * FROM " + suffix + "encounter";
                                }
                                logToConsole("\n Creating Select statement...");
                                ResultSet rs = stmt.executeQuery(sql);
                                //STEP 5: Extract data from result set
                                while (rs.next()) {
                                    //Retrieve by column name
                                    Visit visit = new Visit();
                                    visit.setVisit_id(rs.getInt("encounter_id"));
                                    visit.setPatient_id(rs.getInt("patient_id"));
                                    visit.setVisit_type_id(1);
                                    visit.setDate_started(rs.getDate("encounter_datetime"));
                                    visit.setDate_stopped(rs.getDate("encounter_datetime"));
                                    visit.setLocation_id(2);
                                    visit.setUuid(UUID.randomUUID());
                                    visit.setCreator(1);
                                    visit.setDate_changed(rs.getDate("date_changed"));
                                    visit.setDate_created(rs.getDate("date_created"));
                                    visit.setDate_voided(rs.getDate("date_voided"));
                                    visit.setVoid_reason(rs.getString("void_reason"));
                                    visit.setVoided(rs.getBoolean("voided"));

                                    allVisits.add(visit);
                                }
                                rs.close();
                            }else{
                                String sql = "";
                                if(fromFile.isSelected()){
                                    sql = getSQL();
                                }else{
                                    sql = "SELECT * FROM " + suffix + "visit";
                                }
                                logToConsole("\n Creating Select statement...");
                                ResultSet rs = stmt.executeQuery(sql);
                                //STEP 5: Extract data from result set
                                while (rs.next()) {
                                    //Retrieve by column name
                                    Visit visit = new Visit();
                                    visit.setVisit_id(rs.getInt("visit_id"));
                                    visit.setPatient_id(rs.getInt("patient_id"));
                                    visit.setVisit_type_id(1);
                                    visit.setDate_started(rs.getDate("date_started"));
                                    visit.setDate_stopped(rs.getDate("date_stopped"));
                                    visit.setLocation_id(2);
                                    visit.setUuid(UUID.randomUUID());
                                    visit.setCreator(1);
                                    visit.setDate_changed(rs.getDate("date_changed"));
                                    visit.setDate_created(rs.getDate("date_created"));
                                    visit.setDate_voided(rs.getDate("date_voided"));
                                    visit.setVoid_reason(rs.getString("void_reason"));
                                    visit.setVoided(rs.getBoolean("voided"));

                                    allVisits.add(visit);
                                }
                                rs.close();
                            }
                            logToConsole("\n Data Successfully Fetched!");
                        }catch(SQLException se){
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n SQLException Error: "+se.getMessage());
                        }catch(Exception e){
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Exception Error: "+e.getMessage());
                        }finally{
                            //finally block used to close resources
                            try{
                                if(stmt!=null)
                                    conn.close();
                            }catch(SQLException se){
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    }else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching visit.xml file......\n");
                            file = new File(xsdDir + "/visit.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file visit.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(Visits.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            visits = (Visits) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");

                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }

                    if(allVisits.isEmpty()) {
                        for (Visit theVisit : visits.getVisits()) {
                            theVisit.setUuid(UUID.randomUUID());
                            allVisits.add(theVisit);
                        }
                    }

                    if(! allVisits.isEmpty()) {
                        Visit currentVisit = new Visit();

                        String INSERT_SQL = "INSERT INTO visit"
                                + "(visit_id, patient_id, visit_type_id, date_started, date_stopped, location_id," +
                                " creator, date_created, voided, date_voided, void_reason, uuid) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {

                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                int wDone = 0;
                                // Insert sample records
                                for (Visit module : allVisits) {
                                    try {
                                        stmt.setInt(1, module.getVisit_id());
                                        stmt.setInt(2, module.getPatient_id());
                                        stmt.setInt(3, module.getVisit_type_id());
                                        if (module.getDate_started() != null)
                                            stmt.setDate(4, new java.sql.Date(module.getDate_started().getTime()));
                                        else
                                            stmt.setDate(4, null);
                                        if (module.getDate_stopped() != null)
                                            stmt.setDate(5, new java.sql.Date(module.getDate_stopped().getTime()));
                                        else
                                            stmt.setDate(5, null);
                                        stmt.setInt(6, module.getLocation_id());
                                        stmt.setInt(7, module.getCreator());
                                        if (module.getDate_created() != null)
                                            stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
                                        else
                                            stmt.setDate(8, null);
                                        stmt.setBoolean(9, module.isVoided());
                                        if (module.getDate_voided() != null)
                                            stmt.setDate(10, new java.sql.Date(module.getDate_voided().getTime()));
                                        else
                                            stmt.setDate(10, null);
                                        stmt.setString(11, module.getVoid_reason());
                                        stmt.setString(12, module.getUuid().toString());

                                        //Add statement to batch
                                        stmt.addBatch();
                                        updateProgress(wDone + 1, allVisits.size());
                                        Integer pDone = ((wDone + 1) / allVisits.size()) * 100;
                                        wDone++;
                                    }catch(Exception ex){
                                        logToConsole("\n Error: "+ex.getMessage());
                                        ex.printStackTrace();
                                    }
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allVisits);
                }catch (ArrayIndexOutOfBoundsException ex){
                    logToConsole("Error: "+ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Visit ###################################

    @FXML
    public void closeApp(){
        checkConnection();
        Platform.exit();
    }

    @FXML
    private void testSourceConnection() {
        appConsole.clear();
        logToConsole("#################### CHECKING SOURCE DATABASE! \n");

        connectionSettings();


                Connection conn = null;
                try {
                    //STEP 2: Register JDBC driver
                    Class.forName(driver);

                    //STEP 3: Open a connection
                    logToConsole("\n Connecting to Source " + dbTYPE + " Database!!");
                    conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                    logToConsole("\n Source Database connection successful...");

                }catch(SQLException se){
                    //Handle errors for JDBC
                    se.printStackTrace();
                    logToConsole("\n Error: "+se.getMessage());
                }catch(Exception e){
                    //Handle errors for Class.forName
                    e.printStackTrace();
                    logToConsole("\n Error: "+e.getMessage());
                }finally{
                    //finally block used to close resources
                    closeConnection(conn);
                }//end try

    }

    @FXML
    private void testDestinationConnection(){
        appConsole.clear();
        logToConsole("#################### CHECKING DESTINATION DATABASE! \n");
        SessionManager.host = host.getText();
        SessionManager.port = port.getText();
        SessionManager.username = username.getText();
        SessionManager.password = password.getText();
        SessionManager.db = db.getText();
        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
        }catch (Exception exc){
            logToConsole("\n Error Registering DB Driver "+exc.getMessage()+"..");
        }
        try (Connection conn = DriverManager.getConnection(jdbcUrl, SessionManager.username, SessionManager.password );) {
            logToConsole("\n Destination Database connection successful..");
        } catch (SQLException e) {
            logToConsole("\n Error: "+e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleLocation(){
        if(sourceDB.getSelectionModel().getSelectedIndex() == 3){
            fromFile.setSelected(true);
            getDirectory();
        }else{
            fromFile.setSelected(false);
        }
    }

    @FXML
    private void enableForeignKeyChecks(){
        toggleForeignKeyChecks(1);
    }

    @FXML
    private void disableForeignKeyChecks(){
        toggleForeignKeyChecks(0);
    }

    private void toggleForeignKeyChecks(Integer number){
        String SET_SQL = "SET GLOBAL FOREIGN_KEY_CHECKS = "+number;
        appConsole.clear();
        logToConsole("#################### FOREIGN KEY CHECKS! \n");
        SessionManager.host = host.getText();
        SessionManager.port = port.getText();
        SessionManager.username = username.getText();
        SessionManager.password = password.getText();
        SessionManager.db = db.getText();
        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, SessionManager.username, SessionManager.password);) {

            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(SET_SQL);) {
                //execute batch
                stmt.execute(SET_SQL);
                conn.commit();

                logToConsole( " Foreign key checks set to "+number+". \n");
            } catch (SQLException e) {
                e.printStackTrace();
                rollbackTransaction(conn, e);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getTableName(){
        String tableName = null;
        switch (tablesComboBox.getSelectionModel().getSelectedItem().value()) {
            case "patientidentifier":
                tableName = "patient_identifier";
                break;
            case "patientprogram":
                tableName = "patient_program";
                break;
            case "personaddress":
                tableName = "person_address";
                break;
            case "personattribute":
                tableName = "person_attribute";
                break;
            case "personname":
                tableName = "person_name";
                break;
            default:
                tableName = tablesComboBox.getSelectionModel().getSelectedItem().value();
        }


        return tableName;
    }

    private void setFile(){

        logToConsole("Fetching file "+xsdDir + fileComboBox.getSelectionModel().getSelectedItem()+"......\n");
        try {
            file = new File(xsdDir + "/"+fileComboBox.getSelectionModel().getSelectedItem());
            logToConsole("File fetched......\n");
        }catch (Exception ex){
            logToConsole("Error fetched File...... "+ex.getMessage()+"\n");
            ex.printStackTrace();
        }

    }

    @FXML
    private void cleanUp(){
        appConsole.clear();
        logToConsole("#################### TRUNCATE! \n");
        String tableName = getTableName();

        String TRUNCATE_SQL = "TRUNCATE TABLE "+tableName;

        SessionManager.host = host.getText();
        SessionManager.port = port.getText();
        SessionManager.username = username.getText();
        SessionManager.password = password.getText();
        SessionManager.db = db.getText();
        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
        }catch (Exception exc){
            logToConsole("\n Error Registering DB Driver "+exc.getMessage()+"..");
        }
        try (Connection conn = DriverManager.getConnection(jdbcUrl, SessionManager.username, SessionManager.password);) {

            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(TRUNCATE_SQL);) {
                //execute batch
                stmt.execute(TRUNCATE_SQL);
                conn.commit();
                logToConsole(tablesComboBox.getSelectionModel().getSelectedItem().value().toUpperCase()+" Table Truncated successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                rollbackTransaction(conn, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getSQL(){
        setFile();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            StringBuffer sb = new StringBuffer();
            while ((str = in.readLine()) != null) {
                sb.append(str + "\n ");
            }
            in.close();
            return sb.toString();
        } catch (Exception e) {
            logToConsole("Failed to Execute" + file +". The error is"+ e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void executeQuery(){
        appConsole.clear();
        logToConsole("#################### RUN QUERY! \n");

        String sql = getSQL();

        suffix = tableSuffix.getText();
        source_username = username.getText();
        source_password = password.getText();


        if (targetComboBox.getSelectionModel().getSelectedItem() == "Destination") {
            source_username = username.getText();
            source_password = password.getText();
            dbTYPE = "MYSQL DB";
            try {
                driver = "com.mysql.jdbc.Driver";
                source_jdbcUrl = "jdbc:mysql://" + host.getText() + ":" + port.getText() + "/" + db.getText() +
                        "?useServerPrepStmts=false&rewriteBatchedStatements=true";
            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else{
            source_username = sourceUsername.getText();
            source_password = sourcePassword.getText();
            dbTYPE = "SQLSERVER";
            try {
                driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                source_jdbcUrl = "jdbc:sqlserver://" + sourceHost.getText() + ";databaseName=" + sourceDb.getText();
                //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (Exception ex) {
                logToConsole("Error: "+ex.getMessage()+"\n");
                ex.printStackTrace();
            }
        }
        try {
            //STEP 2: Register JDBC driver
            Class.forName(driver);
        }catch (Exception exc){
            logToConsole("\n Error Registering DB Driver "+exc.getMessage()+"..");
        }
        try (Connection conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);) {
//            if(actionComboBox.getSelectionModel().getSelectedItem() == "Create" ||
//                    actionComboBox.getSelectionModel().getSelectedItem() == "Delete" ||
//                    actionComboBox.getSelectionModel().getSelectedItem() == "Update"){
                conn.setAutoCommit(false);
                try (Statement smt = conn.createStatement();) {
                    //execute batch

                    smt.execute(sql);
                    conn.commit();
                    logToConsole("Query Executed Successfully!\n");
                } catch (SQLException e) {
                    e.printStackTrace();
                    rollbackTransaction(conn, e);
                }
            //}
//            else {
//                conn.setAutoCommit(false);
//                try (Statement stmt = conn.createStatement();) {
//                    //execute batch
//                    stmt.executeBatch();
//                    conn.commit();
//                    logToConsole("Query Executed Successfully!\n");
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    rollbackTransaction(conn, e);
//                }
//            }
        } catch (SQLException e) {
            logToConsole("Error: SQL STATE: " +e.getSQLState()+"... MESSAGE: "+e.getMessage()+"\n");
            e.printStackTrace();
        }
    }
}
