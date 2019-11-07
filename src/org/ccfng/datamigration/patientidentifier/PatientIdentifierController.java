package org.ccfng.datamigration.patientidentifier;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PatientIdentifierController {

    private final PatientIdentifierService personIdentifierService;

    public PatientIdentifierController() {
        this.personIdentifierService = new PatientIdentifierService();
    }

    private static PatientIdentifiers ArrayOfPatientIdentifier = new PatientIdentifiers();

//    static
//    {
//        ArrayOfPatientIdentifier.setPatient_identifiers(new ArrayList<PatientIdentifier>());
//
//        PatientIdentifier patient_identifier = new PatientIdentifier();
//        patient_identifier.setIdentifier("First One Entered");
//
//        PatientIdentifier patient_identifier2 = new PatientIdentifier();
//        patient_identifier2.setIdentifier("Second One Entered");
//
//        ArrayOfPatientIdentifier.getPatient_identifiers().add(patient_identifier);
//        ArrayOfPatientIdentifier.getPatient_identifiers().add(patient_identifier2);
//
//    }

    public void PatientIdentifierToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(PatientIdentifiers.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPatientIdentifier, new File("patient_identifier.xml"));
        marshaller.marshal(ArrayOfPatientIdentifier, System.out);


    }

    public void xmlToPatientIdentifier() {

        List<PatientIdentifier> allPatientIdentifiers = new ArrayList<PatientIdentifier>();
        PatientIdentifiers patientIdentifiers = new PatientIdentifiers();

            File file = null;

            try{
                file = new File(xsdDir +"/patient_identifier.xml");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(PatientIdentifiers.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            patientIdentifiers = (PatientIdentifiers) unmarshaller.unmarshal(file);

        }catch (Exception excx){
                System.out.println(excx.toString());
        }

        for(PatientIdentifier thePatientIdentifier: patientIdentifiers.getPatient_identifiers()){
            thePatientIdentifier.setUuid(UUID.randomUUID());
            allPatientIdentifiers.add(thePatientIdentifier);
            System.out.println("The PatientIdentifier: "+ thePatientIdentifier);
        }

        try {
            personIdentifierService.saveAll(allPatientIdentifiers);
            System.out.println("\n PatientIdentifier Data Successfully Loaded!!");
        }catch (Exception exc){
            System.out.println(exc.toString());
        }

        System.out.println(patientIdentifiers);

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
