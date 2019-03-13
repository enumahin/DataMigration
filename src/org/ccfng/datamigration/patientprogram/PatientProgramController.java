package org.ccfng.datamigration.patientprogram;

import org.ccfng.datamigration.patientprogram.PatientProgram;
import org.ccfng.datamigration.patientprogram.PatientPrograms;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;

public class PatientProgramController {

    private final PatientProgramService personProgramService;

    public PatientProgramController(PatientProgramService personProgramService) {
        this.personProgramService = personProgramService;
    }

    static PatientPrograms ArrayOfPatientProgram = new PatientPrograms();
//
//    static
//    {
//        ArrayOfPatientProgram.setPatient_programs(new ArrayList<PatientProgram>());
//
//        PatientProgram patient_program = new PatientProgram();
//        patient_program.setVoid_reason("First One Entered");
//
//        PatientProgram patient_program2 = new PatientProgram();
//        patient_program2.setVoid_reason("Second One Entered");
//
//        ArrayOfPatientProgram.getPatient_programs().add(patient_program);
//        ArrayOfPatientProgram.getPatient_programs().add(patient_program2);
//
//    }

    public void PatientProgramToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(PatientPrograms.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPatientProgram, new File("patient_program.xml"));
        marshaller.marshal(ArrayOfPatientProgram, System.out);

    }

    public void xmlToPatientProgram() throws JAXBException {

        List<PatientProgram> allPatientPrograms = new ArrayList<PatientProgram>();
        PatientPrograms patientPrograms = new PatientPrograms();
        File file = null;

            try{
                file = new File(xsdDir +"/patient_program.xml");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(PatientPrograms.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            patientPrograms = (PatientPrograms) unmarshaller.unmarshal(file);

        }catch (Exception excx){
                System.out.println(excx.toString());
        }

        for(PatientProgram thePatientProgram: patientPrograms.getPatient_programs()){
            thePatientProgram.setUuid(UUID.randomUUID());
            allPatientPrograms.add(thePatientProgram);
            System.out.println("The PatientProgram: "+ thePatientProgram);
        }

        try {
            personProgramService.saveAll(allPatientPrograms);
            System.out.println("\n PatientProgram Data Successfully Loaded!!");
        }catch (Exception exc){
            System.out.println(exc.toString());
        }

        System.out.println(patientPrograms);

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
