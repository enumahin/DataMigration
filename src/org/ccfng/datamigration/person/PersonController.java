package org.ccfng.datamigration.person;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;

public class PersonController {

    private final PersonService personService;

    public PersonController() {
        this.personService = new PersonService();
    }

    final static Persons ArrayOfPerson = new Persons();

//    static
//    {
//        ArrayOfPerson.setPersons(new ArrayList<Person>());
//
//        Person pers = new Person();
//        pers.setGender("Male");
//
//        Person pers2 = new Person();
//        pers2.setGender("Female");
//
//        ArrayOfPerson.getPersons().add(pers);
//        ArrayOfPerson.getPersons().add(pers2);
//
//    }

    public void personToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(Persons.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPerson, new File("person.xml"));
        marshaller.marshal(ArrayOfPerson, System.out);

    }

    public void xmlToPerson() {

        List<Person> allPersons = new ArrayList<Person>();
        Persons persons = new Persons();
        File file = null;

            try{
                file = new File(xsdDir +"/person.xml");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Persons.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            persons = (Persons) unmarshaller.unmarshal(file);

        }catch (Exception exc){
            System.out.println(exc.toString());
        }
            for (Person thePerson : persons.getPersons()) {
                thePerson.setUuid(UUID.randomUUID());
                allPersons.add(thePerson);
                System.out.println("The person: " + thePerson);
            }
        try{
            personService.saveAll(allPersons);
            System.out.println("\n Persons Successfully Loaded!!");
        }
        catch(Exception jExc){
            System.out.println(jExc.toString());
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
