package org.ccfng.datamigration.personattribute;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;

public class PersonAttributeController {
    
    private final PersonAttributeService personAttributeService;

    public PersonAttributeController() {
        this.personAttributeService = new PersonAttributeService();
    }

    private static PersonAttributes ArrayOfPersonAttribute = new PersonAttributes();
//
//    static
//    {
//        ArrayOfPersonAttribute.setPersonAttributes(new ArrayList<PersonAttribute>());
//
//        PersonAttribute person_attribute = new PersonAttribute();
//
//        person_attribute.setValue("First One Entered");
//
//        PersonAttribute person_attribute2 = new PersonAttribute();
//        person_attribute2.setValue("Second One Entered");
//
//        ArrayOfPersonAttribute.getPersonAttributes().add(person_attribute);
//        ArrayOfPersonAttribute.getPersonAttributes().add(person_attribute2);
//
//    }

    public String PersonAttributeToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(PersonAttributes.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPersonAttribute, new File("person_attribute.xml"));
        marshaller.marshal(ArrayOfPersonAttribute, System.out);

        return "index";

    }

    public void xmlToPersonAttribute() throws JAXBException {

        List<PersonAttribute> allPersonAttributes = new ArrayList<PersonAttribute>();
        PersonAttributes personAttributes = new PersonAttributes();

          File file = null;

            try{
                file = new File(xsdDir +"/person_attribute.xml");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(PersonAttributes.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            personAttributes = (PersonAttributes) unmarshaller.unmarshal(file);

                System.out.println("Generated :"+personAttributes);

                for(PersonAttribute thePersonAttribute: personAttributes.getPersonAttributes()){
                    thePersonAttribute.setUuid(UUID.randomUUID());
                    allPersonAttributes.add(thePersonAttribute);
                    System.out.println("The PersonAttribute: "+ thePersonAttribute);
                }
        }catch (Exception excx){
                System.out.println(excx.toString());
        }



        try {
            personAttributeService.saveAll(allPersonAttributes);
            System.out.println("\n PersonAttribute Data Successfully Loaded!!");
        }catch (Exception exc){
            System.out.println(exc.toString());
        }

        System.out.println(personAttributes);

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
