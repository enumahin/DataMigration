package org.ccfng.datamigration.visit;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VisitController {

    private final VisitService VisitService;

    public VisitController() {
        this.VisitService = new VisitService();
    }

    private static Visits ArrayOfVisit = new Visits();

//    static
//    {
//        ArrayOfVisit.setVisits(new ArrayList<Visit>());
//
//        Visit visit = new Visit();
//        visit.setVoid_reason("First One Entered");
//
//        Visit visit2 = new Visit();
//        visit2.setVoid_reason("Second One Entered");
//
//        ArrayOfVisit.getVisits().add(visit);
//        ArrayOfVisit.getVisits().add(visit2);
//
//    }

    public void VisitToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Visits.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfVisit, new File("visit.xml"));
        marshaller.marshal(ArrayOfVisit, System.out);

    }

    public void xmlToVisit() {

        List<Visit> allVisits = new ArrayList<Visit>();
        Visits Visits = new Visits();

            File file = null;

            try{
                file = new File(xsdDir +"/visit.xml");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(org.ccfng.datamigration.visit.Visits.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Visits = (org.ccfng.datamigration.visit.Visits) unmarshaller.unmarshal(file);

        }catch (Exception excx){
                System.out.println(excx.toString());
        }
        for(Visit theVisit: Visits.getVisits()){
            theVisit.setUuid(UUID.randomUUID());
            //theVisit.setIndication_concept_id(0);
            allVisits.add(theVisit);
            System.out.println("The Visit: "+ theVisit);
        }
        try {
            VisitService.saveAll(allVisits);
            System.out.println("\n Visit Data Successfully Loaded!!");
        }
        catch (Exception exc){
            System.out.println(exc.toString());
        }

        System.out.println(Visits);

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
