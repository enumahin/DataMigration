package org.ccfng.datamigration.obs;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;

public class ObsController {

    private final ObsService ObsService = new ObsService();

    public ObsController() {
    }

    private static Obses ArrayOfObs = new Obses();

//    static
//    {
//        ArrayOfObs.setObses(new ArrayList<Obs>());
//
//        Obs obs = new Obs();
//        obs.setComments("First One Entered");
//
//        Obs obs2 = new Obs();
//        obs2.setComments("Second One Entered");
//
//        ArrayOfObs.getObses().add(obs);
//        ArrayOfObs.getObses().add(obs2);
//
//    }

    public void ObsToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Obses.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfObs, new File("obs.xml"));
        marshaller.marshal(ArrayOfObs, System.out);

    }

    public void xmlToObs() throws JAXBException {

        List<Obs> allObses = new ArrayList<Obs>();
        Obses Obses = new Obses();

            File file = null;

            try{
                file = new File(xsdDir +"/obs.xml");
            }
            catch (Exception e){
                System.out.println(e);
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Obses.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Obses = (Obses) unmarshaller.unmarshal(file);

        }catch (Exception excx){
                System.out.println(excx.toString());
        }
        for(Obs theObs: Obses.getObses()){
            theObs.setUuid(UUID.randomUUID());
            allObses.add(theObs);
            System.out.println("The Obs: "+ theObs);
        }
        try {
            ObsService.saveAll(allObses);
            System.out.println("\n Obs Data Successfully Loaded!!");
        }
        catch (Exception exc){
            System.out.println(exc.toString());
        }

        System.out.println(Obses);

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
