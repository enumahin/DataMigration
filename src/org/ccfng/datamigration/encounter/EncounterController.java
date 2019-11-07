package org.ccfng.datamigration.encounter;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EncounterController {

    private final EncounterService EncounterService = new EncounterService();

    public EncounterController() {

    }

    static Encounters ArrayOfEncounter = new Encounters();
//
//    static
//    {
//        ArrayOfEncounter.setEncounters(new ArrayList<Encounter>());
//
//        Encounter pers = new Encounter();
//        pers.setPatient_id(1);
//
//        Encounter pers2 = new Encounter();
//        pers2.setPatient_id(2);
//
//        ArrayOfEncounter.getEncounters().add(pers);
//        ArrayOfEncounter.getEncounters().add(pers2);
//
//    }


    public void EncounterToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Encounters.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfEncounter, new File("encounter.xml"));
        marshaller.marshal(ArrayOfEncounter, System.out);

    }

    public void xmlToEncounter() {

        List<Encounter> allEncounters = new ArrayList<Encounter>();
        Encounters Encounters = new Encounters();

        File file = null;

        try{
            file = new File(xsdDir +"/encounter.xml");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        try{


            JAXBContext jaxbContext = JAXBContext.newInstance(Encounters.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Encounters = (Encounters) unmarshaller.unmarshal(file);

        }catch (Exception exc){
            System.out.println(exc.getMessage());
        }
        for(Encounter theEncounter: Encounters.getEncounters()){
            theEncounter.setUuid(UUID.randomUUID());
            allEncounters.add(theEncounter);
            System.out.println("The Encounter: "+ theEncounter);
        }


        try{

            EncounterService.saveAll(allEncounters);

            System.out.println(Encounters);

            File directory = new File(xsdDir);
            if(directory.exists()){
                System.out.println("XDS Exists at: " +xsdDir);
            }else{
                System.out.println("XSD Does not Exist at: " +xsdDir+". Please Create it and put all XML files into it.");
            }
            System.out.println("\n Encounter Data Successfully Loaded!!");
        }catch (Exception exc){
            System.out.println( exc.toString());
            //throw new NotFoundException();
        }

    }
}
