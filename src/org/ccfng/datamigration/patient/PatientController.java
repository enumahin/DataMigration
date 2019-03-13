package org.ccfng.datamigration.patient;

import org.ccfng.datamigration.patient.Patient;
import org.ccfng.datamigration.patient.Patients;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;

public class PatientController {

    private final PatientService patientService = new PatientService();

    public PatientController() {

    }

    private static Patients ArrayOfPatient = new Patients();

//    static
//    {
//        ArrayOfPatient.setPatients(new ArrayList<Patient>());
//
//        Patient pers = new Patient();
//        pers.setAllergy_status("Male");
//
//        Patient pers2 = new Patient();
//        pers2.setAllergy_status("Female");
//
//        ArrayOfPatient.getPatients().add(pers);
//        ArrayOfPatient.getPatients().add(pers2);
//
//    }

    public void patientToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Patients.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPatient, new File("patient.xml"));
        marshaller.marshal(ArrayOfPatient, System.out);

    }

    public void xmlToPatient() {

        List<Patient> allPatients = new ArrayList<Patient>();
        Patients patients = new Patients();

            File file = null;

            try{
                file = new File(xsdDir +"/patient.xml");
            }
            catch (Exception e){
                System.out.println(e);
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Patients.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            patients = (Patients) unmarshaller.unmarshal(file);

        }catch (Exception exc){
                System.out.println(exc);
        }
        for (Patient thePatient : patients.getPatients()) {
            thePatient.setAllergy_status("None");
            allPatients.add(thePatient);
            System.out.println("The patient: " + thePatient);
        }
        try{
            patientService.saveAll(allPatients);
            System.out.println("\n Patient Successfully Loaded!!");
        }
        catch(Exception jExc){
            System.out.println(jExc);
        }

        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("user.home"));
        File directory = new File(xsdDir);
        if(directory.exists()){
            System.out.println("XDS Exists at: " +xsdDir);
        }else{
            System.out.println("XSD Does not Exist at: " +xsdDir+". Please Create it and put all XML files into it.");
        }
    }
}
