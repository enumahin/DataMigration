package org.ccfng.datamigration.personname;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersonNameController {
    private final PersonNameService PersonNameService;

    public PersonNameController() {
        this.PersonNameService = new PersonNameService();
    }

    private static PersonNames ArrayOfPersonName = new PersonNames();

//    static
//    {
//        ArrayOfPersonName.setPerson_names(new ArrayList<PersonName>());
//
//        PersonName person_name = new PersonName();
//        person_name.setFamily_name("First One Entered");
//
//        PersonName person_name2 = new PersonName();
//        person_name2.setFamily_name("Second One Entered");
//
//        ArrayOfPersonName.getPerson_names().add(person_name);
//        ArrayOfPersonName.getPerson_names().add(person_name2);
//
//    }

    public void PersonNameToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(PersonNames.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPersonName, new File("person_name.xml"));
        marshaller.marshal(ArrayOfPersonName, System.out);

    }

    public void xmlToPersonName() {

        List<PersonName> allPersonNames = new ArrayList<PersonName>();
        PersonNames PersonNames = new PersonNames();

            File file = null;

            try{
                file = new File(xsdDir +"/person_name.xml");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(org.ccfng.datamigration.personname.PersonNames.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            PersonNames = (org.ccfng.datamigration.personname.PersonNames) unmarshaller.unmarshal(file);

        }catch (Exception excx){
                System.out.println(excx.toString());
        }
        for(PersonName thePersonName: PersonNames.getPerson_names()){
            thePersonName.setUuid(UUID.randomUUID());
            allPersonNames.add(thePersonName);
            System.out.println("The PersonName: "+ thePersonName);
        }
        try {
            PersonNameService.saveAll(allPersonNames);
            System.out.println("\n PersonName Data Successfully Loaded!!");
        }catch (Exception exc){
            System.out.println(exc.toString());
        }

        System.out.println(PersonNames);

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
