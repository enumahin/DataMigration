package org.ccfng.datamigration;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.ccfng.datamigration.encounter.Encounter;
import org.ccfng.datamigration.encounter.Encounters;
import org.ccfng.datamigration.encounterprovider.EncounterProvider;
import org.ccfng.datamigration.encounterprovider.EncounterProviders;
import org.ccfng.datamigration.encountertype.EncounterType;
import org.ccfng.datamigration.filepaths.ConceptMap;
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
import org.ccfng.datamigration.provider.Provider;
import org.ccfng.datamigration.provider.Providers;
import org.ccfng.datamigration.session.SessionManager;
import org.ccfng.datamigration.users.User;
import org.ccfng.datamigration.users.Users;
import org.ccfng.datamigration.visit.Visit;
import org.ccfng.datamigration.visit.Visits;
import org.ccfng.global.DBMiddleMan;
import org.ccfng.openmrscleanup.OpenmrsCleanupController;
import org.hibernate.HibernateException;

public class Controller {

    @FXML
    private AnchorPane mainLayer;

    @FXML
    private Label showDirectory;

    //##########################
    @FXML
    public TextField sourceHost;

    @FXML
    public TextField sourcePort;

    @FXML
    public TextField sourceUsername;

    @FXML
    public TextField sourcePassword;

    @FXML
    public TextField sourceDb;

    @FXML
    public TextField tableSuffix;
    //##########################

    @FXML
    public TextField host;

    @FXML
    public TextField port;

    @FXML
    public TextField username;

    @FXML
    public TextField password;

    @FXML
    public TextField db;

    @FXML
    private TextArea appConsole;

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
    public ComboBox<String> sourceDB;

    @FXML
    private Label totalObs;

    @FXML
    private VBox vBoxTables;

    @FXML
    private VBox vBoxDestination;

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

    private Task<ObservableList<User>> userTask;

    private ObservableList<User> allUsers;

    private Task<ObservableList<Provider>> providerTask;

    private ObservableList<Provider> allProviders;

    private Task<ObservableList<EncounterProvider>> encounterProviderTask;

    private ObservableList<EncounterProvider> allEncounterProviders;

    private ObservableList<EncounterType> allEncounterTypes;

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

    //Set<ConceptMap> concepts = new HashSet<>();

//    private ToggleGroup sourceType = new ToggleGroup();

//    @FXML
//    private RadioButton fromDB;

    @FXML
    private CheckBox fromFile;

    @FXML
    private ProgressIndicator progressIndicator;

    public Controller() {
    }

    // Create a HashMap
    HashMap<Integer, Integer> concepts = new HashMap<>();

    HashMap<Integer, String> encounterTypeMap = new HashMap<>();

    @FXML
    private TextField firstID;

    @FXML
    private TextField lastID;

    @FXML
    private ComboBox<EncounterType> encounterTypes;

    @FXML
    private CheckBox loadByEnc;


    public void initialize() {

        vBoxTables.setDisable(true);

        vBoxDestination.setDisable(true);

        //this.concepts =
        readConceptMapsFromCSV("conceptMapping.csv");

        // let's print all the person read from CSV file


           // for (HashMap<Integer, Integer> c : concepts) {

         //}

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
            if (!textFile.exists()) {
                textFile.createNewFile();
                FileOutputStream oFile = new FileOutputStream(textFile, false);
                oFile.close();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();

        }
        try {
            File textFile = new File("source-db-config.txt");
            if (!textFile.exists()) {
                textFile.createNewFile();
                FileOutputStream oFile = new FileOutputStream(textFile, false);
                oFile.close();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, ex.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }

        try (Stream<String> stream = Files.lines(Paths.get("db-config.txt"))) {
            List<String> files = FXCollections.observableArrayList();
            stream.forEach(files::add);
            if (!files.isEmpty()) {
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
            if (!db_files.isEmpty()) {
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

        if (FilePath.xsdDir != null) {
            showDirectory.setText("Source Directory: " + FilePath.xsdDir);
        }
        this.tablesComboBox.setItems(FXCollections.observableArrayList(tables.values()));
        Platform.runLater(() -> {
            this.sourceDB.setItems(FXCollections.observableArrayList("OpenMRS Migration",
                    "SEEDsCare Migration", "IQCare Migration", "XML Based Migration"));
        });

            logToConsole("Loading Data Please wait...");

        Thread loderThread = new Thread(this::dataLoader);

        loderThread.start();

    }

    public void dataLoader(){
        DBMiddleMan.getObs();
        DBMiddleMan.getPatients();
        DBMiddleMan.getPatientIdentifiers();
        DBMiddleMan.getPatientProgram();
        DBMiddleMan.getPeople();
        DBMiddleMan.getPeopleNames();
        DBMiddleMan.getAllPeopleAddresses();
        DBMiddleMan.getEncounters();
        Platform.runLater(() -> {
            logToConsole("\n Data loaded successfully.");
        });
    }

    @FXML
    public void getDirectory() {
        fileComboBox.setItems(null);
        if (fromFile.isSelected()) {
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


    public void logToConsole(String text) {
        Platform.runLater(() -> {
            if (text != null)
                appConsole.appendText(text);
        });

    }

    @FXML
    public void handleUpload() {

        appConsole.clear();
        logToConsole("############################# Data Migration ##############################\n");

        if (sourceDB.getSelectionModel().getSelectedItem() == null) {
            logToConsole("Select Source First!");
        } else if (FilePath.xsdDir == null && sourceDB.getSelectionModel().getSelectedIndex() == 3) {
            logToConsole("Please select XML Location first!");
        } else {

            try {

                try {
                    tab = tablesComboBox.getSelectionModel().getSelectedItem().value();
                    totalObs.setText("");
                    if(tab == "obs")
                        checkLastObs();

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
                            checkLastObs();
                            //new Thread(()->loadObs());
                            break;
                        case "patient":
                            loadPatient();
                            //new Thread(()->loadPatient());
                            break;
                        case "patient_identifier":
                            loadPatientIdentifier();
                            //new Thread(()->loadPatientIdentifier());
                            break;
                        case "patient_program":
                            loadPatientProgram();
                            //new Thread(()->loadPatientProgram());
                            break;
                        case "person":
                            loadPerson();
                            //new Thread(()->loadPerson());
                            break;
                        case "person_address":
                            loadPersonAddress();
                            //new Thread(()->loadPersonAddress());
                            break;
                        case "person_attribute":
                            loadPersonAttribute();
                            //new Thread(()->loadPersonAttribute());
                            break;
                        case "person_name":
                            loadPersonName();
                            //new Thread(()->loadPersonName());
                            break;
                        case "visit":
                            loadVisit();
                            //new Thread(()->loadVisit());
                            break;
                        case "users":
                            loadUsers();
                            break;
                        case "provider":
                            loadProvider();
                            break;
                        case "encounter_provider":
                            loadEncounterProvider();
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
                                    case "patient_identifier":
                                        progressBar.progressProperty().bind(patientIdentifierTask.progressProperty());
                                        progressIndicator.progressProperty().bind(patientIdentifierTask.progressProperty());
                                        new Thread(patientIdentifierTask).start();
                                        break;
                                    case "patient_program":
                                        progressBar.progressProperty().bind(patientProgramTask.progressProperty());
                                        progressIndicator.progressProperty().bind(patientProgramTask.progressProperty());
                                        new Thread(patientProgramTask).start();
                                        break;
                                    case "person":
                                        progressBar.progressProperty().bind(personTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personTask.progressProperty());
                                        new Thread(personTask).start();
                                        break;
                                    case "person_address":
                                        progressBar.progressProperty().bind(personAddressTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personAddressTask.progressProperty());
                                        new Thread(personAddressTask).start();
                                        break;
                                    case "person_attribute":
                                        progressBar.progressProperty().bind(personAttributeTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personAttributeTask.progressProperty());
                                        new Thread(personAttributeTask).start();
                                        break;
                                    case "person_name":
                                        progressBar.progressProperty().bind(personNameTask.progressProperty());
                                        progressIndicator.progressProperty().bind(personNameTask.progressProperty());
                                        new Thread(personNameTask).start();
                                        break;
                                    case "visit":
                                        progressBar.progressProperty().bind(visitTask.progressProperty());
                                        progressIndicator.progressProperty().bind(visitTask.progressProperty());
                                        new Thread(visitTask).start();
                                        break;
                                    case "users":
                                        progressBar.progressProperty().bind(userTask.progressProperty());
                                        progressIndicator.progressProperty().bind(userTask.progressProperty());
                                        new Thread(userTask).start();
                                        break;
                                    case "provider":
                                        progressBar.progressProperty().bind(providerTask.progressProperty());
                                        progressIndicator.progressProperty().bind(providerTask.progressProperty());
                                        new Thread(providerTask).start();
                                        break;
                                    case "encounter_provider":
                                        progressBar.progressProperty().bind(encounterProviderTask.progressProperty());
                                        progressIndicator.progressProperty().bind(encounterProviderTask.progressProperty());
                                        new Thread(encounterProviderTask).start();
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


    private void checkConnection() {

        try (PrintWriter writer = new PrintWriter("db-config.txt", "UTF-8")) {
            writer.println(FilePath.xsdDir);
            writer.println(host.getText());
            writer.println(port.getText());
            writer.println(username.getText());
            writer.println(password.getText());
            writer.println(db.getText());

        } catch (IOException exc) {
            logToConsole("Error writing Configs to file: " + exc.getMessage() + "..... \n");
        }

        try (PrintWriter writer = new PrintWriter("source-db-config.txt", "UTF-8")) {

            writer.println(sourceHost.getText());
            writer.println(sourcePort.getText());
            writer.println(sourceUsername.getText());
            writer.println(sourcePassword.getText());
            writer.println(sourceDb.getText());

        } catch (IOException exc) {
            logToConsole("Error writing Configs to source file: " + exc.getMessage() + "..... \n");
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

    public void connectionSettings() {

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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                sourceDB.getSelectionModel().getSelectedIndex() == 2) {

            if (sourceDB.getSelectionModel().getSelectedIndex() == 1)
                logToConsole("#################### SEEDsCare MIGRATION!");
            else
                logToConsole("#################### IQCare MIGRATION!");

            dbTYPE = "SQLSERVER";
            try {
                driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                source_jdbcUrl = "jdbc:sqlserver://" + sourceHost.getText() + ";databaseName=" + sourceDb.getText();
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                            Connection conn = DriverManager.getConnection(url, source_username, source_password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
            logToConsole("\n Error: " + se.getMessage());
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

    private void rollbackTransaction(Connection conn, Exception e) {
        if (conn != null) {
            try {
                logToConsole("Transaction is being rolled back: " + e.getMessage());
                conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    Set<Visit> fetchedVisits = new HashSet<>();

    private Visit searchVisit(Integer patient, java.util.Date date){
        for( Visit v: fetchedVisits){
            if(v.getPatient_id() == patient && v.getDate_created() == date){
                return v;
            }
        }
        return null;
    }
    private void loadEncounter() {

        fetchedVisits = getVisits();

        encounterTask = new Task<ObservableList<Encounter>>() {
            @Override
            protected ObservableList<Encounter> call() throws Exception {

                try {
                    allEncounters = FXCollections.observableArrayList();
                    Encounters Encounters = new Encounters();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();
//
                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                if (sourceDB.getSelectionModel().getSelectedIndex() == 0)
                                    sql = "SELECT * FROM " + suffix + "encounter WHERE form_id IN (1,53,46,12,47,56,70,71,72,67,47,65)"
                                            + "group by patient_id,encounter_type, form_id, encounter_datetime";
                                else
                                    sql = "SELECT * FROM " + suffix + "encounter";
                                //sql = "SELECT * FROM " + suffix + "encounter WHERE form_id IN (1,19,53,46,12,47,28,56,70,71,72,67,47,65)";
                                //sql = "SELECT * FROM " + suffix + "encounter WHERE form_id IN (1,53,46,12,47,56,70,71,72,67,47,65)";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
                                //Retrieve by column name
                                Visit v = searchVisit(rs.getInt("patient_id"), rs.getDate("encounter_datetime"));
                                Encounter encounter = new Encounter();
                                encounter.setEncounter_id(rs.getInt("encounter_id"));
                                encounter.setEncounter_datetime(rs.getDate("encounter_datetime"));
                                if (sourceDB.getSelectionModel().getSelectedIndex() == 0) {

                                    if (rs.getInt("form_id") == 1) {
                                        if(isAdult(rs.getInt("patient_id"))) {
                                            encounter.setForm_id(22);
                                            encounter.setEncounter_type(8);
                                        }else{
                                            encounter.setForm_id(20);
                                            encounter.setEncounter_type(8);
                                        }
                                    }
                                    else if (rs.getInt("form_id") == 56 ||
                                            rs.getInt("form_id") == 70 ||
                                            rs.getInt("form_id") == 71 ||
                                            rs.getInt("form_id") == 47 ||
                                            rs.getInt("form_id") == 72) {
                                        encounter.setForm_id(14);
                                        encounter.setEncounter_type(12);
                                    }
                                    else if (rs.getInt("form_id") == 28 ||  rs.getInt("form_id") == 19
                                            ||  rs.getInt("form_id") == 65) {
                                        encounter.setForm_id(23);
                                        encounter.setEncounter_type(14);
                                    }
//                                    else if (rs.getInt("form_id") == 18) {
//                                        encounter.setForm_id(22);
//                                        encounter.setEncounter_type(8);
//                                    }
                                    else if (rs.getInt("form_id") == 46 || rs.getInt("form_id") == 53) {
                                        encounter.setForm_id(27);
                                        encounter.setEncounter_type(13);
                                    }
                                    else if (rs.getInt("form_id") == 12) {
                                        encounter.setForm_id(15);
                                        encounter.setEncounter_type(16);
                                    }
                                    else if (rs.getInt("form_id") == 67) {
                                        encounter.setForm_id(21);
                                        encounter.setEncounter_type(11);
                                    }
//                                    else if (rs.getInt("form_id") == 47) {
//                                        encounter.setForm_id(1);
//                                        encounter.setEncounter_type(rs.getInt("encounter_type"));
//                                    }
//                                    else if (rs.getInt("form_id") == 53) {
//                                        encounter.setForm_id(27);
//                                        encounter.setEncounter_type(rs.getInt("encounter_type"));
//                                    }
                                    else {
                                        encounter.setForm_id(rs.getInt("form_id"));
                                        encounter.setEncounter_type(rs.getInt("encounter_type"));
                                    }
//                                    else if (rs.getInt("form_id") == 46) {
//                                        encounter.setForm_id(27);
//                                        encounter.setEncounter_type(13);
//                                    }
                                } else {
                                    encounter.setForm_id(rs.getInt("form_id"));
                                    encounter.setEncounter_type(rs.getInt("encounter_type"));
                                }

                                encounter.setUuid(UUID.randomUUID());
                                encounter.setCreator(1);
                                encounter.setDate_changed(rs.getDate("date_changed"));
                                encounter.setDate_created(rs.getDate("date_created"));
                                encounter.setDate_voided(rs.getDate("date_voided"));

                                encounter.setLocation_id(2);
                                encounter.setPatient_id(rs.getInt("patient_id"));
                                if(v != null)
                                    encounter.setVisit_id(v.getVisit_id());
                                else
                                    encounter.setVisit_id(rs.getInt("encounter_id"));

                                encounter.setVoid_reason(rs.getString("void_reason"));
                                encounter.setVoided(rs.getBoolean("voided"));

                                allEncounters.add(encounter);
                                //logToConsole(" Encounter Type : "+rs.getInt("encounter_type"));
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!");
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else if (sourceDB.getSelectionModel().getSelectedIndex() == 3) {
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

                    if (allEncounters.isEmpty()) {
                        for (Encounter theEncounter : Encounters.getEncounters()) {
                            theEncounter.setUuid(UUID.randomUUID());
                            if (theEncounter.getPatient_id() < 1) {
                                logToConsole("Error: No Patient ID in: " + theEncounter);
                            } else if (theEncounter.getEncounter_id() < 1) {
                                logToConsole("Error: No Encounter ID in: " + theEncounter);
                            } else if (theEncounter.getForm_id() < 1) {
                                logToConsole("Error: No Form ID in: " + theEncounter);
                            }
                            allEncounters.add(theEncounter);
                        }
                    }
                    if (!allEncounters.isEmpty()) {

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
                                        if (module.getDate_created() != null)
                                            stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
                                        else
                                            stmt.setDate(8, null);
                                        if (module.getDate_changed() != null)
                                            stmt.setDate(9, new java.sql.Date(module.getDate_changed().getTime()));
                                        else
                                            stmt.setDate(9, null);
                                        stmt.setBoolean(10, module.isVoided());
                                        if (module.getDate_voided() != null)
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
                                    } catch (Exception ex) {
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
                                } catch (Exception ex) {
                                    logToConsole("Error in batch insert: " + ex.getMessage());
                                    closeConnection(conn);
                                }
                            } catch (SQLException e) {
                                logToConsole("Error running Insert statement: " + e.getMessage());
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            logToConsole("Error Establishing connection: " + e.getMessage());
                            e.printStackTrace();

                        }
                    } else {

                    }
                    return FXCollections.observableArrayList(allEncounters);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private static ConceptMap createConceptMap(String[] metadata) {
        Integer openmrs =  Integer.parseInt(metadata[0]);
        Integer nmrs = Integer.parseInt(metadata[1]);

        // create and return book of this metadata
        return new ConceptMap(openmrs, nmrs);

    }

    private void readConceptMapsFromCSV(String fileName) {
        //Set<ConceptMap> concepts = new HashSet<>();
        Path pathToFile = Paths.get(fileName);

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
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

    }

    private void loadObs() {

        obsTask = new Task<ObservableList<Obs>>() {
            @Override
            protected ObservableList<Obs> call() throws Exception {
                try {
                    allObses = FXCollections.observableArrayList();
                    Obses obses = new Obses();
                    if((encounterTypes.getSelectionModel().getSelectedItem().getId() == 19 ||
                            encounterTypes.getSelectionModel().getSelectedItem().getId() == 28 ||
                            encounterTypes.getSelectionModel().getSelectedItem().getId() == 65) &&
                            sourceDB.getSelectionModel().getSelectedIndex() == 0
                            ){
                        logToConsole("\n Going for data merging!!!");
                        //logToConsole(hivEnrollment().toString());
                        for(Obs ob : hivEnrollment()) {
                            allObses.add(ob);
                        }
                        //logToConsole("All Obs:" +allObses);

                    }else if((encounterTypes.getSelectionModel().getSelectedItem().getId() == 1 ||
                            encounterTypes.getSelectionModel().getSelectedItem().getId() == 20 ||
                            encounterTypes.getSelectionModel().getSelectedItem().getId() == 18) &&
                            sourceDB.getSelectionModel().getSelectedIndex() == 0
                            ){
                        logToConsole("\n Going for data merging!!!");
                        //logToConsole(hivEnrollment().toString());
                        for(Obs ob : initialEvaluation()) {
                            allObses.add(ob);
                        }
                        //logToConsole("All Obs:" +allObses);

                    }else if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {
                        logToConsole("\n No data merging!!!");
                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                if(loadByEnc.isSelected()) {
                                        if (encounterTypes.getSelectionModel().getSelectedItem() != null) {
                                            if(! firstID.getText().isEmpty() && ! lastID.getText().isEmpty()) {
                                                if (Integer.parseInt(firstID.getText()) > 0
                                                        && Integer.parseInt(lastID.getText()) > 0) {
                                                    logToConsole("\n Fetching Data from Range of ID: " + Integer
                                                            .parseInt(firstID.getText()) + " TO " + lastID.getText() + "\n");
                                                    sql = "SELECT * FROM " + suffix
                                                            + "obs LEFT JOIN encounter on obs.encounter_id = encounter.encounter_id where obs_id >= "
                                                            +
                                                            Integer.parseInt(firstID.getText()) +
                                                            " && obs_id < " + Integer.parseInt(lastID.getText()) +
                                                            " && encounter.form_id = " +
                                                            encounterTypes.getSelectionModel().getSelectedItem().getId();

                                                    }
                                                } else {
                                                    sql = "SELECT * FROM " + suffix
                                                            + "obs LEFT JOIN encounter on obs.encounter_id = encounter.encounter_id where form_id = "
                                                            +
                                                            encounterTypes.getSelectionModel().getSelectedItem().getId();
                                                }

                                        } else {
                                            logToConsole(
                                                    "\n Please select obs Encounter Type you want to migrate and try again!....");
                                        }
                                }else {
                                    logToConsole("\n All Data...");
                                    if (Integer.parseInt(firstID.getText()) > 0
                                            && Integer.parseInt(lastID.getText()) > 0) {
                                        sql = "SELECT * FROM " + suffix + "obs where obs_id >= "+
                                                Integer.parseInt(firstID.getText()) +
                                                " && obs_id < " + Integer.parseInt(lastID.getText());
                                    }else{
                                        sql = "SELECT * FROM " + suffix + "obs";
                                    }
                                }

                            }
                            logToConsole("USING: "+sql);
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
                                //Retrieve by column name
                                if(sourceDB.getSelectionModel().getSelectedIndex() == 0) {
                                   // logToConsole("\nFetching Obs ID: "+rs.getInt("obs_id"));
                                  try (PrintWriter writer = new PrintWriter(encounterTypes.getSelectionModel().getSelectedItem().getName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase()+".csv", "UTF-8")) {
                                    if(concepts.containsKey(rs.getInt("concept_id"))){
                                        Obs obs = new Obs();
                                        if (rs.getString("accession_number") != null)
                                            obs.setAccession_number(rs.getString("accession_number"));

                                        if (rs.getString("comments") != null)
                                            obs.setComments(rs.getString("comments"));

                                        // DO Concept Mapping here
                                            obs.setConcept_id(concepts.get(rs.getInt("concept_id")));

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
                                        if (rs.getInt("order_id") > 0)
                                            obs.setOrder_id(rs.getInt("order_id"));

                                        obs.setPerson_id(rs.getInt("person_id"));
                                        //obs.setPrevious_version(rs.getInt("previous_version"));
                                        //Do Concept Mapping here
                                        if(concepts.containsKey(rs.getInt("value_coded"))){
                                            obs.setValue_coded(concepts.get(rs.getInt("value_coded")));
                                        }else{
                                            obs.setValue_coded(concepts.get(rs.getInt("value_coded")));
                                            writer.println(rs.getInt("value_coded"));
                                        }
                                        //obs.setValue_coded(rs.getInt("value_coded"));
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
                                        allObses.add(obs);
                                    }else{
                                        writer.println(rs.getInt("concept_id"));
                                    }
                                 } catch (IOException exc) {

                                        logToConsole("Error writing Configs to file: " + exc.getMessage() + "..... \n");
                                 }

                                }else {
                                    Obs obs = new Obs();
                                    if (rs.getString("accession_number") != null)
                                        obs.setAccession_number(rs.getString("accession_number"));
                                    //                                else
                                    //                                    obs.setAccession_number(null);

                                    if (rs.getString("comments") != null)
                                        obs.setComments(rs.getString("comments"));
                                    //                                else
                                    //                                    obs.setComments(null);

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
                                    if (rs.getInt("order_id") > 0)
                                        obs.setOrder_id(rs.getInt("order_id"));

                                    obs.setPerson_id(rs.getInt("person_id"));
                                    //obs.setPrevious_version(rs.getInt("previous_version"));
                                    //Do Concept Mapping here

                                        obs.setValue_coded(rs.getInt("value_coded"));

                                    //obs.setValue_coded(rs.getInt("value_coded"));
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
                                    allObses.add(obs);
                                }
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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

                    if (allObses.isEmpty()) {
                        for (Obs theObs : obses.getObses()) {
                            theObs.setUuid(UUID.randomUUID());
                            allObses.add(theObs);
                        }
                    }

                    if (!allObses.isEmpty()) {

                        Obs currentObs = new Obs();

                        String INSERT_SQL = "INSERT INTO obs"
                                + "(person_id, concept_id, encounter_id, order_id, obs_datetime, location_id," +
                                "accession_number, value_group_id, value_coded, value_coded_name_id, value_drug, value_datetime, " +
                                "value_numeric, value_modifier, value_text, value_complex, comments," +
                                "creator, date_created, voided, date_voided, void_reason, uuid, form_namespace_and_path, obs_group_id,obs_id) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
                                    ///try {
                                    currentObs = module;
                                    stmt.setInt(26, module.getObs_id());
                                    if(module.getObs_group_id() != null)
                                        stmt.setInt(25, module.getObs_group_id());
                                    else
                                        stmt.setString(25, null);
                                    stmt.setInt(1, module.getPerson_id());
                                    stmt.setInt(2, module.getConcept_id());
                                    stmt.setInt(3, module.getEncounter_id());
                                    if(module.getOrder_id() != null)
                                        stmt.setInt(4, module.getOrder_id());
                                    else
                                        stmt.setString(4, null);
                                    if (module.getObs_datetime() != null)
                                        stmt.setDate(5, new java.sql.Date(module.getObs_datetime().getTime()));
                                    else
                                        stmt.setDate(5, null);
                                    if (module.getLocation_id() >= 0)
                                        stmt.setInt(6, module.getLocation_id());
                                    else
                                        stmt.setInt(6, 0);
                                    if(module.getAccession_number() != null)
                                        stmt.setString(7, module.getAccession_number());
                                    else
                                        stmt.setString(7, null);
                                    if(module.getValue_group_id() != null)
                                        stmt.setInt(8, module.getValue_group_id());
                                    else
                                        stmt.setString(8, null);
                                    if(module.getValue_coded() != null)
                                        stmt.setInt(9, module.getValue_coded());
                                    else
                                        stmt.setString(9, null);
                                    if(module.getValue_coded_name_id() != null)
                                        stmt.setInt(10, module.getValue_coded_name_id());
                                    else
                                        stmt.setString(10, null);
                                    if(module.getValue_drug() != null)
                                        stmt.setInt(11, module.getValue_drug());
                                    else
                                        stmt.setString(11, null);
                                    if (module.getValue_datetime() != null)
                                        stmt.setDate(12, new java.sql.Date(module.getValue_datetime().getTime()));
                                    else
                                        stmt.setDate(12, null);
                                    if(module.getValue_numeric() != null)
                                        stmt.setDouble(13, module.getValue_numeric());
                                    else
                                        stmt.setString(13, null);
                                    if(module.getValue_modifier() != null)
                                        stmt.setString(14, module.getValue_modifier());
                                    else
                                        stmt.setString(14, null);
                                    if(module.getValue_text() != null)
                                        stmt.setString(15, module.getValue_text());
                                    else
                                        stmt.setString(15, null);
                                    if(module.getValue_complex() != null)
                                        stmt.setString(16, module.getValue_complex());
                                    else
                                        stmt.setString(16, null);
                                    if(module.getComments() != null)
                                        stmt.setString(17, module.getComments());
                                    else
                                        stmt.setString(17, null);
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
                                    if(module.getVoid_reason() != null)
                                        stmt.setString(22, module.getVoid_reason());
                                    else
                                        stmt.setString(22, null);
                                    stmt.setString(23, module.getUuid().toString());
                                    //stmt.setInt(24, module.getPrevious_version());
                                    if(module.getForm_namespace_and_path() != null)
                                        stmt.setString(24, module.getForm_namespace_and_path());
                                    else
                                        stmt.setString(24, null);
                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allObses.size());
                                    Integer pDone = ((wDone + 1) / allObses.size()) * 100;
                                    wDone++;
//                                    }catch(Exception ex){
//                                        logToConsole("\n Exception Error: "+ ex.getMessage() + " in Obs with "+currentObs.getEncounter_id()+" Encounter ID!");
//                                        ex.printStackTrace();
//                                    }
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("Transaction is committed successfully.");
                            } catch (SQLException e) {
                                logToConsole(e.getMessage());
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            logToConsole(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allObses);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("ArrayIndexOutOfBoundsException Error: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                } catch (HibernateException ex) {
                    logToConsole("HibernateException Error: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                } catch (Exception ex) {
                    logToConsole("Exception Error: " + ex.getMessage());
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

    private void loadPatient() {
        patientTask = new Task<ObservableList<Patient>>() {
            @Override
            protected ObservableList<Patient> call() throws Exception {
                try {
                    allPatients = FXCollections.observableArrayList();
                    Patients patients = new Patients();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "patient";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
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
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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
                    if (allPatients.isEmpty()) {
                        for (Patient thePatient : patients.getPatients()) {
                            //thePatient.setUuid(UUID.randomUUID());
                            allPatients.add(thePatient);
                        }
                    }

                    if (!allPatients.isEmpty()) {
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
                                    if (module.getDate_created() != null)
                                        stmt.setDate(3, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(3, null);
                                    stmt.setBoolean(4, module.isVoided());
                                    if (module.getDate_voided() != null)
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
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private void loadPatientIdentifier() {
        patientIdentifierTask = new Task<ObservableList<PatientIdentifier>>() {
            @Override
            protected ObservableList<PatientIdentifier> call() throws Exception {
                try {
                    allPatientIdentifiers = FXCollections.observableArrayList();
                    PatientIdentifiers patientIdentifiers = new PatientIdentifiers();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "patient_identifier";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
                                //Retrieve by column name
                                PatientIdentifier patientIdentifier = new PatientIdentifier();
                                patientIdentifier.setPatient_identifier_id(rs.getInt("patient_identifier_id"));
                                patientIdentifier.setPreferred(rs.getBoolean("preferred"));
                                if (rs.getInt("identifier_type") == 3 && sourceDB.getSelectionModel().getSelectedIndex() == 0)
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
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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
                    if (allPatientIdentifiers.isEmpty()) {
                        for (PatientIdentifier thePatientIdentifier : patientIdentifiers.getPatient_identifiers()) {
                            thePatientIdentifier.setUuid(UUID.randomUUID());
                            if (thePatientIdentifier.getIdentifier() != null) {
                                //logToConsole("NULL Found in: "+thePatientIdentifier);
                                allPatientIdentifiers.add(thePatientIdentifier);
                            }

                        }
                    }

                    if (!allPatientIdentifiers.isEmpty()) {

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
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private void loadPatientProgram() {
        patientProgramTask = new Task<ObservableList<PatientProgram>>() {
            @Override
            protected ObservableList<PatientProgram> call() throws Exception {
                try {
                    allPatientPrograms = FXCollections.observableArrayList();
                    PatientPrograms patientPrograms = new PatientPrograms();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "patient_program";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
                                //Retrieve by column name
                                PatientProgram patientProgram = new PatientProgram();
                                patientProgram.setPatient_program_id(rs.getInt("patient_program_id"));
                                if(sourceDB.getSelectionModel().getSelectedIndex() == 0){
                                    if(rs.getInt("program_id") == 3){
                                        patientProgram.setProgram_id(1);
                                    }else if(rs.getInt("program_id") == 1){
                                        patientProgram.setProgram_id(2);
                                    }else if(rs.getInt("program_id") == 9){
                                        patientProgram.setProgram_id(3);
                                    }else{
                                        patientProgram.setProgram_id(rs.getInt("program_id"));
                                    }
                                }else{
                                    patientProgram.setProgram_id(rs.getInt("program_id"));
                                }


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
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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
                    if (allPatientPrograms.isEmpty()) {
                        for (PatientProgram thePatientProgram : patientPrograms.getPatient_programs()) {
                            thePatientProgram.setUuid(UUID.randomUUID());
                            allPatientPrograms.add(thePatientProgram);
                        }
                    }

                    if (!allPatientPrograms.isEmpty()) {
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

                                    if (module.getDate_enrolled() != null)
                                        stmt.setDate(4, new java.sql.Date(module.getDate_enrolled().getTime()));
                                    else
                                        stmt.setDate(4, null);
                                    //logToConsole("\n Loading Data...!!\n");
                                    if (module.getDate_completed() != null)
                                        stmt.setDate(5, new java.sql.Date(module.getDate_completed().getTime()));
                                    else
                                        stmt.setDate(5, null);
                                    //logToConsole("\n Loading Data....!!\n");
                                    stmt.setInt(6, module.getLocation_id());
                                    //stmt.setInt(7, module.getOutcome_concept_id());
                                    stmt.setInt(7, module.getCreator());
                                    //logToConsole("\n Loading Data.....!!\n");
                                    if (module.getDate_created() != null)
                                        stmt.setDate(8, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(8, null);
                                    //logToConsole("\n Loading Data......!!\n");
                                    stmt.setBoolean(9, module.isVoided());
                                    if (module.getDate_voided() != null)
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
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private void loadPerson() {
        personTask = new Task<ObservableList<Person>>() {
            @Override
            protected ObservableList<Person> call() throws Exception {

                try {
                    allPersons = FXCollections.observableArrayList();
                    Persons persons = new Persons();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source " + dbTYPE + " Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Source Database connection successfully...\n");

                            //STEP 4: Execute a query

                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "person";
                            }
                            logToConsole("Creating Select statement...\n");
                            stmt = conn.createStatement();

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
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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
                    if (allPersons.isEmpty()) {
                        for (Person thePerson : persons.getPersons()) {
                            thePerson.setUuid(UUID.randomUUID());
                            allPersons.add(thePerson);
                        }
                    }
                    if (!allPersons.isEmpty()) {
                        logToConsole("Loading Data......\n");
                        String INSERT_SQL = "INSERT INTO person"
                                + "(person_id, gender, birthdate, birthdate_estimated, dead, death_date, creator, date_created, " +
                                "date_changed, voided, date_voided, void_reason, uuid, deathdate_estimated, birthtime) " +
                                "VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "gender = VALUES(gender), " +
                                "date_changed = NOW()";

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
                                    if (person.getBirthdate() != null)
                                        stmt.setDate(3, new java.sql.Date(person.getBirthdate().getTime()));
                                    else
                                        stmt.setDate(3, null);
                                    stmt.setBoolean(4, person.getBirthdate_estimated());
                                    stmt.setBoolean(5, person.getDead());
                                    if (person.getDeath_date() != null)
                                        stmt.setDate(6, new java.sql.Date(person.getDeath_date().getTime()));
                                    else
                                        stmt.setDate(6, null);
                                    stmt.setInt(7, person.getCreator());
                                    stmt.setDate(8, new java.sql.Date(person.getDate_created().getTime()));
                                    if (person.getDate_changed() != null)
                                        stmt.setDate(9, new java.sql.Date(person.getDate_changed().getTime()));
                                    else
                                        stmt.setDate(9, null);
                                    stmt.setBoolean(10, person.getVoided());
                                    if (person.getDate_voided() != null)
                                        stmt.setDate(11, new java.sql.Date(person.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(11, null);
                                    stmt.setString(12, person.getVoid_reason());
                                    stmt.setString(13, person.getUuid().toString());
                                    stmt.setBoolean(14, person.getDeathdate_estimated());
                                    if (person.getBirthtime() != null)
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
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private void loadPersonAddress() {
        personAddressTask = new Task<ObservableList<PersonAddress>>() {
            @Override
            protected ObservableList<PersonAddress> call() throws Exception {
                try {
                    allPersonAddresses = FXCollections.observableArrayList();
                    PersonAddresses personAddressses = new PersonAddresses();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "person_address";
                            }
                            logToConsole("Creating Select statement...\n");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
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
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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
                    if (allPersonAddresses.isEmpty()) {
                        for (PersonAddress thePersonAddress : personAddressses.getPerson_addresses()) {
                            thePersonAddress.setUuid(UUID.randomUUID());
                            allPersonAddresses.add(thePersonAddress);
                        }
                    }

                    if (!allPersonAddresses.isEmpty()) {
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
                                    if (module.getStart_date() != null)
                                        stmt.setDate(10, new java.sql.Date(module.getStart_date().getTime()));
                                    else
                                        stmt.setDate(10, null);
                                    if (module.getEnd_date() != null)
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
                                    if (module.getDate_created() != null)
                                        stmt.setDate(28, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(28, null);
                                    if (module.getDate_changed() != null)
                                        stmt.setDate(29, new java.sql.Date(module.getDate_changed().getTime()));
                                    else
                                        stmt.setDate(29, null);
                                    stmt.setBoolean(30, module.isVoided());
                                    if (module.getDate_voided() != null)
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
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private void loadPersonAttribute() {
        personAttributeTask = new Task<ObservableList<PersonAttribute>>() {
            @Override
            protected ObservableList<PersonAttribute> call() throws Exception {
                try {
                    allPersonAttributes = FXCollections.observableArrayList();
                    PersonAttributes personAttributes = new PersonAttributes();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "person_attribute";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
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
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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
                    if (allPersonAttributes.isEmpty()) {
                        for (PersonAttribute thePersonAttribute : personAttributes.getPersonAttributes()) {
                            if (thePersonAttribute.getValue() != null) {
                                thePersonAttribute.setUuid(UUID.randomUUID());
                                allPersonAttributes.add(thePersonAttribute);
                            }
                        }
                    }

                    if (!allPersonAttributes.isEmpty()) {
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
                                    if (module.getDate_created() != null)
                                        stmt.setDate(5, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(5, null);
                                    stmt.setBoolean(6, module.isVoided());
                                    if (module.getDate_voided() != null)
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
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private void loadPersonName() {
        personNameTask = new Task<ObservableList<PersonName>>() {
            @Override
            protected ObservableList<PersonName> call() throws Exception {
                try {
                    allPersonNames = FXCollections.observableArrayList();
                    PersonNames personNames = new PersonNames();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "person_name";
                            }
                            logToConsole("\n Creating Select statement...");
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

                                allPersonNames.add(personName);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!");
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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
                    if (allPersonNames.isEmpty()) {
                        for (PersonName thePersonName : personNames.getPerson_names()) {
//                        logToConsole("loading: "+thePersonName);
                            thePersonName.setUuid(UUID.randomUUID());
                            allPersonNames.add(thePersonName);
                        }
                    }

                    if (!allPersonNames.isEmpty()) {
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
                                    if (module.getDate_created() != null)
                                        stmt.setDate(12, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(12, null);
                                    stmt.setBoolean(13, module.isVoided());
                                    if (module.getDate_voided() != null)
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
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
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

    private void loadVisit() {
        visitTask = new Task<ObservableList<Visit>>() {
            @Override
            protected ObservableList<Visit> call() throws Exception {
                try {
                    allVisits = FXCollections.observableArrayList();
                    Visits visits = new Visits();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            if (sourceDB.getSelectionModel().getSelectedIndex() == 0) {
                                String sql = "";
                                if (fromFile.isSelected()) {
                                    logToConsole("\n Loading Visits from file...");
                                    sql = getSQL();
                                } else {
                                    logToConsole("\n Fetching visit from OpenMRS through encounter...");
                                    sql = "SELECT * FROM " + suffix + "encounter WHERE form_id IN (1,53,46,12,47,56,70,71,72,67,47,65) group by patient_id, encounter_datetime";
                                    //sql = "SELECT * FROM " + suffix + "encounter WHERE form_id IN (1,53,46,12,47,56,70,71,72,67,47,65)";
                                    //sql = "SELECT * FROM " + suffix + "encounter WHERE form_id IN (1,19,53,46,12,47,28,56,70,71,72,67,47,65)";
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
                            } else {
                                String sql = "";
                                if (fromFile.isSelected()) {
                                    sql = getSQL();
                                } else {
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
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n SQLException Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Exception Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
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

                    if (allVisits.isEmpty()) {
                        for (Visit theVisit : visits.getVisits()) {
                            theVisit.setUuid(UUID.randomUUID());
                            allVisits.add(theVisit);
                        }
                    }

                    if (!allVisits.isEmpty()) {
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
                                    } catch (Exception ex) {
                                        logToConsole("\n Error: " + ex.getMessage());
                                        ex.printStackTrace();
                                    }
                                }
                                //execute batch
                                stmt.executeBatch();
                                conn.commit();
                                logToConsole("\n Transaction is committed successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allVisits);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
                    return null;
                }

            }

        };
    }

    //########################### End of Visit ###################################


    private void loadUsers() {
        userTask = new Task<ObservableList<User>>() {
            @Override
            protected ObservableList<User> call() throws Exception {
                try {
                    allUsers = FXCollections.observableArrayList();
                    Users users = new Users();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                sql = "SELECT * FROM " + suffix + "users";
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
                                //Retrieve by column name
                                User user = new User();
                                user.setUser_id(rs.getInt("user_id"));
                                user.setSystem_id(rs.getString("system_id"));
                                user.setUsername(rs.getString("username"));
                                user.setPassword(rs.getString("password"));
                                user.setSalt(rs.getString("salt"));
                                user.setSecret_question(rs.getString("secret_question"));
                                user.setSecret_answer(rs.getString("secret_answer"));
                                user.setPerson_id(rs.getInt("person_id"));
                                user.setRetired(rs.getBoolean("retired"));
                                user.setRetired_by(rs.getInt("retired_by"));
                                user.setChanged_by(rs.getInt("changed_by"));
                                user.setCreator(1);
                                user.setDate_changed(rs.getDate("date_changed"));
                                user.setDate_created(rs.getDate("date_created"));
                                user.setDate_retired(rs.getDate("date_retired"));
                                user.setRetire_reason(rs.getString("retire_reason"));
                                user.setUuid(UUID.randomUUID());
                                allUsers.add(user);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching patient.xml file......\n");
                            file = new File(xsdDir + "/users.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file users.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            users = (Users) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if (allUsers.isEmpty()) {
                        for (User theUser : users.getUsers()) {
                            //thePatient.setUuid(UUID.randomUUID());
                            allUsers.add(theUser);
                        }
                    }

                    if (!allUsers.isEmpty()) {
                        logToConsole("\n Loading Data.!\n");
                        String INSERT_SQL = "INSERT INTO users"
                                + "(user_id, system_id, username, password, salt, secret_question, secret_answer, person_id," +
                                " retired, retired_by, changed_by, creator, date_changed, date_created, date_retired, retire_reason," +
                                "uuid) " +
                                "VALUES (?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        Class.forName("com.mysql.jdbc.Driver");
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {
                            logToConsole("\n Loading Data..!\n");
                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                logToConsole("\n Loading Data...!\n");
                                int wDone = 0;
                                // Insert sample records
                                for (User module : allUsers) {
                                    //logToConsole("\n Loading Data...!!\n");
                                    stmt.setInt(1, module.getUser_id());
                                    stmt.setString(2, module.getSystem_id());
                                    stmt.setString(3, module.getUsername());
                                    stmt.setString(4, module.getPassword());
                                    stmt.setString(5, module.getSalt());
                                    stmt.setString(6, module.getSecret_question());
                                    stmt.setString(7, module.getSecret_answer());
                                    stmt.setInt(8, module.getPerson_id());
                                    stmt.setBoolean(9, module.getRetired());
                                    stmt.setInt(10, module.getRetired_by());
                                    stmt.setInt(11, module.getChanged_by());
                                    stmt.setInt(12, module.getCreator());
                                    if (module.getDate_changed() != null)
                                        stmt.setDate(13, new java.sql.Date(module.getDate_changed().getTime()));
                                    else
                                        stmt.setDate(13, null);
                                    if (module.getDate_created() != null)
                                        stmt.setDate(14, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(14, null);
                                    if (module.getDate_retired() != null)
                                        stmt.setDate(15, new java.sql.Date(module.getDate_retired().getTime()));
                                    else
                                        stmt.setDate(15, null);
                                    stmt.setString(16, module.getRetire_reason());
                                    stmt.setString(17, module.getUuid().toString());

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allUsers.size());
                                    Integer pDone = ((wDone + 1) / allUsers.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                logToConsole("\n Loading Data....!\n");
                                stmt.executeBatch();
                                logToConsole("\n Loading Data.....!\n");
                                conn.commit();
                                logToConsole("Data Loaded successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allUsers);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
                    return null;
                }

            }

        };
    }

    private void loadProvider() {
        providerTask = new Task<ObservableList<Provider>>() {
            @Override
            protected ObservableList<Provider> call() throws Exception {
                try {
                    allProviders = FXCollections.observableArrayList();
                    Providers providers = new Providers();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                if (sourceDB.getSelectionModel().getSelectedIndex() == 0){
                                    sql = "SELECT * FROM " + suffix + "users where users.person_id IN (Select person_id from person)";
                                }else {
                                    sql = "SELECT * FROM " + suffix + "provider";
                                }
                            }
                            logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
                                //Retrieve by column name
                                Provider provider = new Provider();
                                if (sourceDB.getSelectionModel().getSelectedIndex() == 0) {
                                    provider.setProvider_id(rs.getInt("user_id"));
                                    provider.setName(rs.getString("username"));
                                    provider.setIdentifier(rs.getString("username"));
                                    provider.setProvider_role_id(1);
                                }else{
                                    provider.setProvider_id(rs.getInt("provider_id"));
                                    provider.setName(rs.getString("name"));
                                    provider.setIdentifier(rs.getString("identifier"));
                                    provider.setProvider_role_id(rs.getInt("provider_role_id"));
                                }
                                provider.setPerson_id(rs.getInt("person_id"));
                                provider.setRetired(rs.getBoolean("retired"));
                                provider.setRetired_by(rs.getInt("retired_by"));
                                provider.setChanged_by(rs.getInt("changed_by"));
                                provider.setCreator(1);
                                provider.setDate_changed(rs.getDate("date_changed"));
                                provider.setDate_created(rs.getDate("date_created"));
                                provider.setDate_retired(rs.getDate("date_retired"));
                                provider.setRetire_reason(rs.getString("retire_reason"));
                                provider.setUuid(UUID.randomUUID());
                                allProviders.add(provider);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching patient.xml file......\n");
                            file = new File(xsdDir + "/provider.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file provider.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(Providers.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            providers = (Providers) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if (allProviders.isEmpty()) {
                        for (Provider theProvider : providers.getProviders()) {
                            //thePatient.setUuid(UUID.randomUUID());
                            allProviders.add(theProvider);
                        }
                    }

                    if (!allProviders.isEmpty()) {
                        logToConsole("\n Loading Data.!\n");
                        String INSERT_SQL = "INSERT INTO provider"
                                + "(provider_id, name, identifier, provider_role_id, person_id," +
                                " retired, retired_by, changed_by, creator, date_changed, date_created, date_retired, retire_reason," +
                                "uuid) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        Class.forName("com.mysql.jdbc.Driver");
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {
                            logToConsole("\n Loading Data..!\n");
                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                logToConsole("\n Loading Data...!\n");
                                int wDone = 0;
                                // Insert sample records
                                for (Provider module : allProviders) {
                                    //logToConsole("\n Loading Data...!!\n");
                                    stmt.setInt(1, module.getProvider_id());
                                    stmt.setString(2, module.getName());
                                    stmt.setString(3, module.getIdentifier());
                                    stmt.setInt(4, module.getProvider_role_id());
                                    stmt.setInt(5, module.getPerson_id());
                                    stmt.setBoolean(6, module.isRetired());
                                    stmt.setInt(7, module.getRetired_by());
                                    stmt.setInt(8, module.getChanged_by());
                                    stmt.setInt(9, module.getCreator());
                                    if (module.getDate_changed() != null)
                                        stmt.setDate(10, new java.sql.Date(module.getDate_changed().getTime()));
                                    else
                                        stmt.setDate(10, null);
                                    if (module.getDate_created() != null)
                                        stmt.setDate(11, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(11, null);
                                    if (module.getDate_retired() != null)
                                        stmt.setDate(12, new java.sql.Date(module.getDate_retired().getTime()));
                                    else
                                        stmt.setDate(12, null);
                                    stmt.setString(13, module.getRetire_reason());
                                    stmt.setString(14, module.getUuid().toString());

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allProviders.size());
                                    Integer pDone = ((wDone + 1) / allProviders.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                logToConsole("\n Loading Data....!\n");
                                stmt.executeBatch();
                                logToConsole("\n Loading Data.....!\n");
                                conn.commit();
                                logToConsole("Data Loaded successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                rollbackTransaction(conn, e);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allProviders);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
                    return null;
                }

            }

        };
    }


    private void loadEncounterProvider() {
        encounterProviderTask = new Task<ObservableList<EncounterProvider>>() {
            @Override
            protected ObservableList<EncounterProvider> call() throws Exception {
                try {
                    allEncounterProviders = FXCollections.observableArrayList();
                    EncounterProviders encounterProviders = new EncounterProviders();

                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                            sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                        connectionSettings();

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            //STEP 2: Register JDBC driver
                            Class.forName(driver);

                            //STEP 3: Open a connection
                            logToConsole("\n Connecting to Source Database!!");
                            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                            logToConsole("\n Connected to database successfully...");

                            //STEP 4: Execute a query

                            stmt = conn.createStatement();
                            String sql = "";
                            if (fromFile.isSelected()) {
                                sql = getSQL();
                            } else {
                                if (sourceDB.getSelectionModel().getSelectedIndex() == 0){
                                    sql = "SELECT * FROM " + suffix + "encounter";
                                }else {
                                    sql = "SELECT * FROM " + suffix + "encounter_provider";
                                }
                            }
                            logToConsole("\n Creating Select statement...");

                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while (rs.next()) {
                                //Retrieve by column name
                                EncounterProvider encounterProvider = new EncounterProvider();
                                if (sourceDB.getSelectionModel().getSelectedIndex() == 0){
                                    encounterProvider.setEncounter_provider_id(null);
                                    encounterProvider.setProvider_id(1);
                                    encounterProvider.setEncounter_id(rs.getInt("encounter_id"));
                                    encounterProvider.setEncounter_role_id(1);
                                }else {
                                    encounterProvider.setEncounter_provider_id(rs.getInt("encounter_provider_id"));
                                    encounterProvider.setProvider_id(rs.getInt("provider_id"));
                                    encounterProvider.setEncounter_id(rs.getInt("encounter_id"));
                                    encounterProvider.setEncounter_role_id(rs.getInt("encounter_role_id"));
                                }
                                encounterProvider.setVoided(rs.getBoolean("voided"));
                                encounterProvider.setVoided_by(rs.getInt("voided_by"));
                                encounterProvider.setChanged_by(rs.getInt("changed_by"));
                                encounterProvider.setCreator(1);
                                encounterProvider.setDate_changed(rs.getDate("date_changed"));
                                encounterProvider.setDate_created(rs.getDate("date_created"));
                                encounterProvider.setDate_voided(rs.getDate("date_voided"));
                                encounterProvider.setVoid_reason(rs.getString("void_reason"));
                                encounterProvider.setUuid(UUID.randomUUID());
                                allEncounterProviders.add(encounterProvider);
                            }
                            rs.close();
                            logToConsole("\n Data Successfully Fetched!\n");
                        } catch (SQLException se) {
                            //Handle errors for JDBC
                            se.printStackTrace();
                            logToConsole("\n Error: " + se.getMessage());
                        } catch (Exception e) {
                            //Handle errors for Class.forName
                            e.printStackTrace();
                            logToConsole("\n Error: " + e.getMessage());
                        } finally {
                            //finally block used to close resources
                            try {
                                if (stmt != null)
                                    conn.close();
                            } catch (SQLException se) {
                            }// do nothing
                            closeConnection(conn);
                        }//end try

                    } else {
                        logToConsole("#################### XML BASED MIGRATION!");

                        File file = null;

                        try {
                            logToConsole("Fetching patient.xml file......\n");
                            file = new File(xsdDir + "/encounter_provider.xml");
                            logToConsole("File fetched......\n");
                        } catch (Exception e) {
                            logToConsole("Error opening file encounter_provider.xml: " + e.getMessage() + "\n");
                        }

                        try {
                            logToConsole("Converting file to a Model......\n");
                            JAXBContext jaxbContext = JAXBContext.newInstance(EncounterProviders.class);
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                            encounterProviders = (EncounterProviders) unmarshaller.unmarshal(file);
                            logToConsole("Conversion Done......\n");
                        } catch (Exception exc) {
                            logToConsole("Error Loading File Content to a Model: " + exc.getMessage() + "\n");
                        }

                    }
                    if (allEncounterProviders.isEmpty()) {
                        for (EncounterProvider theEncounterProvider : encounterProviders.getEncounterProviders()) {
                            //thePatient.setUuid(UUID.randomUUID());
                            allEncounterProviders.add(theEncounterProvider);
                        }
                    }

                    if (!allEncounterProviders.isEmpty()) {
                        logToConsole("\n Loading Data.!\n");
                        String INSERT_SQL = "INSERT INTO encounter_provider"
                                + "(encounter_provider_id, provider_id, encounter_id, encounter_role_id," +
                                " voided, voided_by, changed_by, creator, date_changed, date_created, date_voided, void_reason," +
                                "uuid) " +
                                "VALUES (null,?,?,?,?,?,?,?,?,?,?,?,?)";

                        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
                        String username = SessionManager.username;
                        String password = SessionManager.password;
                        Class.forName("com.mysql.jdbc.Driver");
                        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);) {
                            logToConsole("\n Loading Data..!\n");
                            conn.setAutoCommit(false);
                            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);) {
                                logToConsole("\n Loading Data...!\n");
                                int wDone = 0;
                                // Insert sample records
                                for (EncounterProvider module : allEncounterProviders) {
                                    //logToConsole("\n Working on Encounter ID of: "+module.getEncounter_id()+" \n");
                                    //stmt.setInt(1, module.getEncounter_provider_id());
                                    stmt.setInt(1, module.getProvider_id());
                                    stmt.setInt(2, module.getEncounter_id());
                                    stmt.setInt(3, module.getEncounter_role_id());
                                    stmt.setBoolean(4, module.isVoided());
                                    stmt.setInt(5, module.getVoided_by());
                                    stmt.setInt(6, module.getChanged_by());
                                    stmt.setInt(7, module.getCreator());
                                    if (module.getDate_changed() != null)
                                        stmt.setDate(8, new java.sql.Date(module.getDate_changed().getTime()));
                                    else
                                        stmt.setDate(8, null);
                                    if (module.getDate_created() != null)
                                        stmt.setDate(9, new java.sql.Date(module.getDate_created().getTime()));
                                    else
                                        stmt.setDate(9, null);
                                    if (module.getDate_voided() != null)
                                        stmt.setDate(10, new java.sql.Date(module.getDate_voided().getTime()));
                                    else
                                        stmt.setDate(10, null);
                                    stmt.setString(11, module.getVoid_reason());
                                    stmt.setString(12, module.getUuid().toString());

                                    //Add statement to batch
                                    stmt.addBatch();
                                    updateProgress(wDone + 1, allEncounterProviders.size());
                                    Integer pDone = ((wDone + 1) / allEncounterProviders.size()) * 100;
                                    wDone++;
                                }
                                //execute batch
                                logToConsole("\n Loading Data....!\n");
                                stmt.executeBatch();
                                logToConsole("\n Loading Data.....!\n");
                                conn.commit();
                                logToConsole("Data Loaded successfully.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                logToConsole("SQLException: "+e.getMessage());
                                rollbackTransaction(conn, e);
                            }catch (Exception ex){
                                logToConsole("Exception: "+ex.getMessage());
                                ex.printStackTrace();
                                rollbackTransaction(conn, ex);
                            }
                        } catch (SQLException e) {
                            logToConsole("Root SQLException: "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    return FXCollections.observableArrayList(allEncounterProviders);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logToConsole("Error: " + ex.getMessage());
                    return null;
                }

            }

        };
    }

    @FXML
    public void closeApp() {
        checkConnection();
        Platform.exit();
        System.exit(0);
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

            Platform.runLater( () -> {
                try {
                    encounterTypes.getItems().removeAll();
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
                getEncounterTypes();

                encounterTypes.getItems().addAll(allEncounterTypes);
            });

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            logToConsole("\n Error: " + se.getMessage());
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            logToConsole("\n Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection(conn);
        }//end try

    }

    @FXML
    private void testDestinationConnection() {
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
        } catch (Exception exc) {
            logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
        }
        try (Connection conn = DriverManager.getConnection(jdbcUrl, SessionManager.username, SessionManager.password);) {
            logToConsole("\n Destination Database connection successful..");
        } catch (SQLException e) {
            logToConsole("\n Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleLocation() {
        if (sourceDB.getSelectionModel().getSelectedIndex() == 3) {
            fromFile.setSelected(true);
            getDirectory();
        } else {
            fromFile.setSelected(false);
        }
    }

    @FXML
    private void enableForeignKeyChecks() {
        toggleForeignKeyChecks(1);
    }

    @FXML
    private void disableForeignKeyChecks() {
        toggleForeignKeyChecks(0);
    }

    private void toggleForeignKeyChecks(Integer number) {
        String SET_SQL = "SET GLOBAL FOREIGN_KEY_CHECKS = " + number;
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

                logToConsole(" Foreign key checks set to " + number + ". \n");
            } catch (SQLException e) {
                e.printStackTrace();
                rollbackTransaction(conn, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getTableName() {
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

    private void setFile() {

        logToConsole("Fetching file " + xsdDir + fileComboBox.getSelectionModel().getSelectedItem() + "......\n");
        try {
            file = new File(xsdDir + "/" + fileComboBox.getSelectionModel().getSelectedItem());
            logToConsole("File fetched......\n");
        } catch (Exception ex) {
            logToConsole("Error fetched File...... " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }

    }

    @FXML
    private void cleanUp() {
        appConsole.clear();
        logToConsole("#################### TRUNCATE! \n");
        String tableName = getTableName();

        String TRUNCATE_SQL = "TRUNCATE TABLE " + tableName;

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
        } catch (Exception exc) {
            logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
        }
        try (Connection conn = DriverManager.getConnection(jdbcUrl, SessionManager.username, SessionManager.password);) {

            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(TRUNCATE_SQL);) {
                //execute batch
                stmt.execute(TRUNCATE_SQL);
                conn.commit();
                logToConsole(tablesComboBox.getSelectionModel().getSelectedItem().value().toUpperCase() + " Table Truncated successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                rollbackTransaction(conn, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getSQL() {
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
            logToConsole("Failed to Execute" + file + ". The error is" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void executeQuery() {
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            source_username = sourceUsername.getText();
            source_password = sourcePassword.getText();
            dbTYPE = "SQLSERVER";
            try {
                driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                source_jdbcUrl = "jdbc:sqlserver://" + sourceHost.getText() + ";databaseName=" + sourceDb.getText();
                //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (Exception ex) {
                logToConsole("Error: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }
        }
        try {
            //STEP 2: Register JDBC driver
            Class.forName(driver);
        } catch (Exception exc) {
            logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
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
            logToConsole("Error: SQL STATE: " + e.getSQLState() + "... MESSAGE: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    @FXML
    private void checkLastObs(){

            try {

                if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                        sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                        sourceDB.getSelectionModel().getSelectedIndex() == 2) {
                    connectionSettings();
                    Connection conn = null;
                    Statement stmt = null;
                    try {
                        //STEP 2: Register JDBC driver
                        Class.forName(driver);

                        //STEP 3: Open a connection
                        //logToConsole("\n Connecting to Source Database!!");
                        conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                        //logToConsole("\n Connected to database successfully...");

                        //STEP 4: Execute a query

                        stmt = conn.createStatement();
                        if (sourceDB.getSelectionModel().getSelectedIndex() == 0) {
                            String sql = "select obs_id from obs order by obs_id DESC limit 1";
                            //logToConsole("\n Creating Select statement...");
                            ResultSet rs = stmt.executeQuery(sql);
                            //STEP 5: Extract data from result set
                            while(rs.next())
                             totalObs.setText( "Last Obs Id: " + Integer.toString(rs.getInt("obs_id")));
                            rs.close();
                        }
                        //logToConsole("\n Data Successfully Fetched!");
                    } catch (SQLException se) {
                        //Handle errors for JDBC
                        se.printStackTrace();
                        logToConsole("\n SQLException Error: " + se.getMessage());
                    } catch (Exception e) {
                        //Handle errors for Class.forName
                        e.printStackTrace();
                        logToConsole("\n Exception Error: " + e.getMessage());
                    } finally {
                        //finally block used to close resources
                        try {
                            if (stmt != null)
                                conn.close();
                        } catch (SQLException se) {
                        }// do nothing
                        closeConnection(conn);
                    }//end try

                } else {
                    //logToConsole("#################### XML BASED MIGRATION!");

                    File file = null;


                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                logToConsole("Error: " + ex.getMessage());

            }
    }

    private void getEncounterTypes(){
        allEncounterTypes =  FXCollections.observableArrayList();
        try {

            if (sourceDB.getSelectionModel().getSelectedIndex() == 0 ||
                    sourceDB.getSelectionModel().getSelectedIndex() == 1 ||
                    sourceDB.getSelectionModel().getSelectedIndex() == 2) {

                Connection conn = null;
                Statement stmt = null;
                try {
                    //STEP 2: Register JDBC driver
                    Class.forName(driver);

                    //STEP 3: Open a connection
                    //logToConsole("\n Connecting to Source Database!!");
                    conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                    //logToConsole("\n Connected to database successfully...");

                    //STEP 4: Execute a query

                    stmt = conn.createStatement();
                    if (sourceDB.getSelectionModel().getSelectedIndex() == 0) {
                        String sql = "select * from form";
                        //logToConsole("\n Creating Select statement...");
                        ResultSet rs = stmt.executeQuery(sql);
                        //STEP 5: Extract data from result set
                        while(rs.next())
                           allEncounterTypes.add( new EncounterType(rs.getInt("form_id"), rs.getString("name")));
                        rs.close();
                    }
                    //logToConsole("\n Data Successfully Fetched!");
                } catch (SQLException se) {
                    //Handle errors for JDBC
                    se.printStackTrace();
                    logToConsole("\n SQLException Error: " + se.getMessage());
                } catch (Exception e) {
                    //Handle errors for Class.forName
                    e.printStackTrace();
                    logToConsole("\n Exception Error: " + e.getMessage());
                } finally {
                    //finally block used to close resources
                    try {
                        if (stmt != null)
                            conn.close();
                    } catch (SQLException se) {
                    }// do nothing
                    closeConnection(conn);
                }//end try

            } else {
                //logToConsole("#################### XML BASED MIGRATION!");

                File file = null;


            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            logToConsole("Error: " + ex.getMessage());

        }
    }

    private Set<Obs> hivEnrollment(){
        Connection conn = null;
        Statement stmt = null;
        Connection conn2 = null;
        Statement stmt2 = null;
        Connection conn3 = null;
        Statement stmt3 = null;
        Set<Obs> getObs = new HashSet<Obs>();
        try {
            //STEP 2: Register JDBC driver
            Class.forName(driver);

            //STEP 3: Open a connection
            //logToConsole("\n Connecting to Source Database!!");
            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
            //logToConsole("\n Connected to database successfully...");

            //STEP 4: Execute a query
            Set<Integer> patientIds = new HashSet<>();
            stmt = conn.createStatement();
                String sql = "SELECT \n"
                        + "patient_id\n"
                        + " FROM openmrs.encounter \n"
                        + "where (form_id= 65 || form_id=28 || form_id = 19) \n"
                        + " group by patient_id\n"
                        + " order by patient_id  Asc";
                //logToConsole("\n Creating Select statement...");
                ResultSet rs = stmt.executeQuery(sql);
                //STEP 5: Extract data from result set
                while(rs.next())
                    patientIds.add( rs.getInt("patient_id"));
                rs.close();
            try (PrintWriter writer = new PrintWriter("hiv_enrollment_missing_concepts.csv", "UTF-8")) {

                for(Integer id : patientIds) {
                sql = "SELECT encounter_id, form_id FROM openmrs.encounter \n"
                        + "where (form_id= 65 || form_id=28 || form_id = 19) And patient_id = " + id
                        + " group by patient_id, form_id, encounter_datetime";
                try{
                    logToConsole("On Person ID of : "+id+" \n");
                conn2 = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                stmt2 = conn2.createStatement();
                ResultSet rs2 = stmt2.executeQuery(sql);
                Integer encID = 0;
                while (rs2.next()) {
                    if (rs2.getInt("form_id") == 65) {
                        encID = rs2.getInt("encounter_id");
                    }
                }
                rs2.beforeFirst();
                    while (rs2.next()) {
                    try{
                        conn3 = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                        stmt3 = conn3.createStatement();
                    ResultSet rs3 = stmt3
                            .executeQuery("SELECT * FROM obs WHERE encounter_id = " + rs2.getInt("encounter_id"));
                    while (rs3.next()) {
                        if (concepts.containsKey(rs3.getInt("concept_id"))) {
                            Obs permObs = new Obs();
                            //                        permObs.setUuid(UUID.fromString(rs3.getString("uuid")));
                            //                        permObs.setAccession_number(rs3.getString("accession_number"));
                            //                        permObs.setComments(rs3.getString("comments"));
                            //                        permObs.setConcept_id(encID);
                            //                        permObs.setCreator(rs3.getInt("creator"));
                            //                        permObs.setDate_created(rs3.getDate("date_created"));
                            if (rs3.getString("accession_number") != null)
                                permObs.setAccession_number(rs3.getString("accession_number"));

                            if (rs3.getString("comments") != null)
                                permObs.setComments(rs3.getString("comments"));

                            // DO Concept Mapping here
                            permObs.setConcept_id(concepts.get(rs3.getInt("concept_id")));

                            permObs.setUuid(UUID.randomUUID());
                            permObs.setCreator(1);
                            permObs.setDate_created(rs3.getDate("date_created"));

                            if (rs3.getDate("date_voided") != null)
                                permObs.setDate_voided(rs3.getDate("date_voided"));
                            if(encID > 0)
                             permObs.setEncounter_id(encID);
                            else
                             permObs.setEncounter_id(rs3.getInt("encounter_id"));
                            permObs.setLocation_id(2);
                            //obs.setForm_namespace_and_path(rs3.getString("form_namespace_and_path"));
                            // obs.setVisit_id(rs3.getInt("visit_id"));

                            if (rs3.getString("void_reason") != null)
                                permObs.setVoid_reason(rs3.getString("void_reason"));

                            permObs.setVoided(rs3.getBoolean("voided"));
                            permObs.setObs_datetime(rs3.getDate("obs_datetime"));

                            if (rs3.getInt("obs_group_id") > 0)
                                permObs.setObs_group_id(rs3.getInt("obs_group_id"));

                            if (rs3.getInt("obs_id") > 0)
                                permObs.setObs_id(rs3.getInt("obs_id"));
                            if (rs3.getInt("order_id") > 0)
                                permObs.setOrder_id(rs3.getInt("order_id"));

                            permObs.setPerson_id(rs3.getInt("person_id"));
                            //permObs.setPrevious_version(rs3.getInt("previous_version"));
                            //Do Concept Mapping here
                            if(concepts.containsKey(rs3.getInt("value_coded"))) {
                                permObs.setValue_coded(concepts.get(rs3.getInt("value_coded")));
                            }else{
                                writer.println(rs3.getInt("concept_id"));
                                permObs.setValue_coded(concepts.get(rs3.getInt("value_coded")));
                            }
                            //permObs.setValue_coded(rs3.getInt("value_coded"));
                            if (rs3.getInt("value_coded_name_id") > 0)
                                permObs.setValue_coded_name_id(rs3.getInt("value_coded_name_id"));
                            if (rs3.getString("value_complex") != null)
                                permObs.setValue_complex(rs3.getString("value_complex"));
                            if (rs3.getDate("value_datetime") != null)
                                permObs.setValue_datetime(rs3.getDate("value_datetime"));
                            if (rs3.getInt("value_drug") > 0)
                                permObs.setValue_drug(rs3.getInt("value_drug"));
                            if (rs3.getInt("value_group_id") > 0)
                                permObs.setValue_group_id(rs3.getInt("value_group_id"));
                            if (rs3.getString("value_modifier") != null)
                                permObs.setValue_modifier(rs3.getString("value_modifier"));
                            if (rs3.getDouble("value_numeric") != 0)
                                permObs.setValue_numeric(rs3.getDouble("value_numeric"));
                            if (rs3.getString("value_text") != null)
                                permObs.setValue_text(rs3.getString("value_text"));
                            if (rs3.getInt("voided_by") > 0)
                                permObs.setVoided_by(rs3.getInt("voided_by"));
                            getObs.add(permObs);
                        }else{
                            writer.println(rs3.getInt("concept_id"));
                        }
                    }

                    rs3.close();
                } catch (SQLException se) {
                        //Handle errors for JDBC
                        se.printStackTrace();
                        logToConsole("\n SQLException Error: " + se.getMessage());
                    } catch (Exception e) {
                        //Handle errors for Class.forName
                        e.printStackTrace();
                        logToConsole("\n Exception Error: " + e.getMessage());
                    } finally {
                        //finally block used to close resources
                        try {
                            if (stmt3 != null)
                                conn3.close();
                        } catch (SQLException se) {
                        }// do nothing
                        closeConnection(conn3);
                    }//end try
                }
                rs2.close();
                } catch (SQLException se) {
                    //Handle errors for JDBC
                    se.printStackTrace();
                    logToConsole("\n SQLException Error: " + se.getMessage());
                } catch (Exception e) {
                    //Handle errors for Class.forName
                    e.printStackTrace();
                    logToConsole("\n Exception Error: " + e.getMessage());
                } finally {
                    //finally block used to close resources
                    try {
                        if (stmt2 != null)
                            conn2.close();
                    } catch (SQLException se) {
                    }// do nothing
                    closeConnection(conn2);
                }//end try
            }
            //logToConsole("\n Data Successfully Fetched!");
            } catch (IOException exc) {
                logToConsole("Error writing Configs to file: " + exc.getMessage() + "..... \n");
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            logToConsole("\n SQLException Error: " + se.getMessage());
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            logToConsole("\n Exception Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
            closeConnection(conn);
        }//end try
        return getObs;
    }

    private Set<Obs> initialEvaluation(){
        allObses.removeAll();
        Connection conn = null;
        Statement stmt = null;
        Connection conn2 = null;
        Statement stmt2 = null;
        Connection conn3 = null;
        Statement stmt3 = null;
        Set<Obs> getObs = new HashSet<Obs>();
        try {
            //STEP 2: Register JDBC driver
            Class.forName(driver);

            //STEP 3: Open a connection
            //logToConsole("\n Connecting to Source Database!!");
            conn = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
            //logToConsole("\n Connected to database successfully...");

            //STEP 4: Execute a query
            Set<Integer> patientIds = new HashSet<>();
            stmt = conn.createStatement();
                String sql = "SELECT \n"
                        + "patient_id\n"
                        + " FROM openmrs.encounter \n"
                        + " where (form_id= 18 || form_id=1 || form_id = 20) \n"
                        + " group by patient_id\n"
                        + " order by patient_id Asc";
                //logToConsole("\n Creating Select statement...");
                ResultSet rs = stmt.executeQuery(sql);
                //STEP 5: Extract data from result set
                while(rs.next())
                    patientIds.add( rs.getInt("patient_id"));
                rs.close();
            try (PrintWriter writer = new PrintWriter("initial_clinical_evaluation.csv", "UTF-8")) {
            for(Integer id : patientIds) {
                sql = "SELECT encounter_id, form_id FROM openmrs.encounter \n"
                        + "where (form_id = 18 || form_id = 1 || form_id = 20) And patient_id = " + id
                        + " group by patient_id, form_id, encounter_datetime";
                try{
                    logToConsole("On Person ID of : "+id+" \n");
                conn2 = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                stmt2 = conn2.createStatement();
                ResultSet rs2 = stmt2.executeQuery(sql);
                Integer encID = 0;
                while (rs2.next()) {
                    if (rs2.getInt("form_id") == 1) {
                        logToConsole("Patient has Initial Visit! \n");
                        encID = rs2.getInt("encounter_id");
                    }
                }
                rs2.beforeFirst();
                    while (rs2.next()) {
                        logToConsole("Checking Initial Visit Obs! \n");
                    try{
                        conn3 = DriverManager.getConnection(source_jdbcUrl, source_username, source_password);
                        stmt3 = conn3.createStatement();
                    ResultSet rs3 = stmt3
                            .executeQuery("SELECT * FROM obs WHERE encounter_id = " + rs2.getInt("encounter_id"));
                    while (rs3.next()) {
                        logToConsole("Visit has Obs! \n");

                        if (concepts.containsKey(rs3.getInt("concept_id"))) {
                            Obs permObs = new Obs();
                            if (rs3.getString("accession_number") != null)
                                permObs.setAccession_number(rs3.getString("accession_number"));

                            if (rs3.getString("comments") != null)
                                permObs.setComments(rs3.getString("comments"));

                            // DO Concept Mapping here
                            permObs.setConcept_id(concepts.get(rs3.getInt("concept_id")));

                            permObs.setUuid(UUID.randomUUID());
                            permObs.setCreator(1);
                            permObs.setDate_created(rs3.getDate("date_created"));

                            if (rs3.getDate("date_voided") != null)
                                permObs.setDate_voided(rs3.getDate("date_voided"));

                            permObs.setEncounter_id(encID);
                            permObs.setLocation_id(2);

                            if (rs3.getString("void_reason") != null)
                                permObs.setVoid_reason(rs3.getString("void_reason"));

                            permObs.setVoided(rs3.getBoolean("voided"));
                            permObs.setObs_datetime(rs3.getDate("obs_datetime"));

                            if (rs3.getInt("obs_group_id") > 0)
                                permObs.setObs_group_id(rs3.getInt("obs_group_id"));

                            if (rs3.getInt("obs_id") > 0)
                                permObs.setObs_id(rs3.getInt("obs_id"));
                            if (rs3.getInt("order_id") > 0)
                                permObs.setOrder_id(rs3.getInt("order_id"));

                            permObs.setPerson_id(rs3.getInt("person_id"));
                            if(rs3.getInt("value_coded") > 0){
                                if(! concepts.containsKey(rs3.getInt("value_coded"))) {
                                    writer.println(rs3.getInt("value_coded"));
                                    permObs.setValue_coded(rs3.getInt("value_coded"));
                                }
                                else
                                    permObs.setValue_coded(concepts.get(rs3.getInt("value_coded")));
                            }else{
                                permObs.setValue_coded(null);
                            }


                            //permObs.setValue_coded(rs3.getInt("value_coded"));
                            if (rs3.getInt("value_coded_name_id") > 0)
                                permObs.setValue_coded_name_id(rs3.getInt("value_coded_name_id"));
                            if (rs3.getString("value_complex") != null)
                                permObs.setValue_complex(rs3.getString("value_complex"));
                            if (rs3.getDate("value_datetime") != null)
                                permObs.setValue_datetime(rs3.getDate("value_datetime"));
                            if (rs3.getInt("value_drug") > 0)
                                permObs.setValue_drug(rs3.getInt("value_drug"));
                            if (rs3.getInt("value_group_id") > 0)
                                permObs.setValue_group_id(rs3.getInt("value_group_id"));
                            if (rs3.getString("value_modifier") != null)
                                permObs.setValue_modifier(rs3.getString("value_modifier"));
                            if (rs3.getDouble("value_numeric") != 0)
                                permObs.setValue_numeric(rs3.getDouble("value_numeric"));
                            if (rs3.getString("value_text") != null)
                                permObs.setValue_text(rs3.getString("value_text"));
                            if (rs3.getInt("voided_by") > 0)
                                permObs.setVoided_by(rs3.getInt("voided_by"));
                            getObs.add(permObs);

                        }else {
                            logToConsole("Concept not found in Map!!\n");

                                writer.println(rs3.getInt("concept_id"));


                        }

                    }

                    rs3.close();
                } catch (SQLException se) {
                        //Handle errors for JDBC
                        se.printStackTrace();
                        logToConsole("\n SQLException Error: " + se.getMessage());
                    } catch (Exception e) {
                        //Handle errors for Class.forName
                        e.printStackTrace();
                        logToConsole("\n Exception Error: " + e.getMessage());
                    } finally {
                        //finally block used to close resources
                        try {
                            if (stmt3 != null)
                                conn3.close();
                        } catch (SQLException se) {
                        }// do nothing
                        closeConnection(conn3);
                    }//end try
                }
                rs2.close();
                } catch (SQLException se) {
                    //Handle errors for JDBC
                    se.printStackTrace();
                    logToConsole("\n SQLException Error: " + se.getMessage());
                } catch (Exception e) {
                    //Handle errors for Class.forName
                    e.printStackTrace();
                    logToConsole("\n Exception Error: " + e.getMessage());
                } finally {
                    //finally block used to close resources
                    try {
                        if (stmt2 != null)
                            conn2.close();
                    } catch (SQLException se) {
                    }// do nothing
                    closeConnection(conn2);
                }//end try
            }
            //logToConsole("\n Data Successfully Fetched!");
            } catch (IOException exc) {
                logToConsole("Error writing Configs to file: " + exc.getMessage() + "..... \n");
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            logToConsole("\n SQLException Error: " + se.getMessage());
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            logToConsole("\n Exception Error: " + e.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
            closeConnection(conn);
        }//end try
        return getObs;
    }

    private Boolean isAdult(int patientID){
        return true;
    }

    private Set<Visit> getVisits() {
        Set<Visit> visits = new HashSet<>();
        appConsole.clear();
        logToConsole("#################### CHECKING DESTINATION DATABASE! \n");
        SessionManager.host = host.getText();
        SessionManager.port = port.getText();
        SessionManager.username = username.getText();
        SessionManager.password = password.getText();
        SessionManager.db = db.getText();
        String jdbcUrl = "jdbc:mysql://" + SessionManager.host + ":" + SessionManager.port + "/" + SessionManager.db +
                "?useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=false";
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception exc) {
            logToConsole("\n Error Registering DB Driver " + exc.getMessage() + "..");
        }
        try (Connection conn = DriverManager.getConnection(jdbcUrl, SessionManager.username, SessionManager.password);) {
            logToConsole("\n Destination Database connection successful..");


            stmt = conn.createStatement();
                String sql = "SELECT * FROM visit";
                logToConsole("\n Fetching Visits..");
                ResultSet rs = stmt.executeQuery(sql);
                //STEP 5: Extract data from result set
                while (rs.next()) {
                    Visit visit = new Visit();
                    visit.setVisit_id(rs.getInt("visit_id"));
                    visit.setPatient_id(rs.getInt("patient_id"));
                    visit.setVisit_type_id(1);
                    visit.setDate_started(rs.getDate("date_started"));
                    visit.setDate_stopped(rs.getDate("date_stopped"));
                    visit.setLocation_id(2);
                    visit.setUuid(UUID.fromString(rs.getString("uuid")));
                    visit.setCreator(1);
                    visit.setDate_changed(rs.getDate("date_changed"));
                    visit.setDate_created(rs.getDate("date_created"));
                    visit.setDate_voided(rs.getDate("date_voided"));
                    visit.setVoid_reason(rs.getString("void_reason"));
                    visit.setVoided(rs.getBoolean("voided"));
                    visits.add(visit);
                }
                rs.close();
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
        return visits;
    }


    @FXML
    private void activateDataMigration(){

        vBoxTables.setDisable(! vBoxTables.isDisabled());

        vBoxDestination.setDisable(! vBoxDestination.isDisabled());
    }


    @FXML
    private void openOpenMRSCleanup(){
        try {

            checkConnection();

            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader();

            Pane root = (Pane) fxmlLoader.load(getClass().getResource("/org/ccfng/openmrscleanup/openmrs_cleanup.fxml").openStream());

            OpenmrsCleanupController openmrsController = (OpenmrsCleanupController) fxmlLoader.getController();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("OpenMRS Data CleanUp");
            stage.setResizable(false);
            stage.alwaysOnTopProperty();
            stage.show();
        }catch (Exception ex){
            logToConsole(ex.getMessage());
            ex.printStackTrace();
        }
    }


    @FXML
    private void patientTracker(){
        try {

            checkConnection();

            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader();

            Pane root = (Pane) fxmlLoader.load(getClass().getResource("/org/ccfng/patienttracker/patienttracker.fxml").openStream());

            org.ccfng.patienttracker.Controller controller = (org.ccfng.patienttracker.Controller) fxmlLoader.getController();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Patient Tracker");
            stage.setResizable(false);
            stage.alwaysOnTopProperty();
            stage.show();
        }catch (Exception ex){
            logToConsole(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void vlEligibility(){
        try {

            checkConnection();

            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader();

            Pane root = (Pane) fxmlLoader.load(getClass().getResource("/org/ccfng/viralload/viralload.fxml").openStream());

            org.ccfng.viralload.Controller controller = (org.ccfng.viralload.Controller) fxmlLoader.getController();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Viral Load Eligibility");
            stage.setResizable(false);
            stage.alwaysOnTopProperty();
            stage.show();
        }catch (Exception ex){
            logToConsole(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void lineList(){
        try {

            checkConnection();

            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader();

            Pane root = (Pane) fxmlLoader.load(getClass().getResource("/org/ccfng/linelist/linelist.fxml").openStream());

            org.ccfng.linelist.Controller controller = (org.ccfng.linelist.Controller) fxmlLoader.getController();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Line List");
            stage.setResizable(false);
            stage.alwaysOnTopProperty();
            stage.show();
            stage.setOnCloseRequest(e -> controller.shutdown());
        }catch (Exception ex){
            logToConsole(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
