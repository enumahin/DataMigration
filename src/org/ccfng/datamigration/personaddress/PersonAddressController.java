package org.ccfng.datamigration.personaddress;

import static org.ccfng.datamigration.filepaths.FilePath.xsdDir;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersonAddressController {
    private final PersonAddressService PersonAddressService;

    public PersonAddressController(PersonAddressService personAddressService) {
        this.PersonAddressService = personAddressService;
    }

    private static PersonAddresses ArrayOfPersonAddress = new PersonAddresses();
//
//    static
//    {
//        ArrayOfPersonAddress.setPerson_addresses(new ArrayList<PersonAddress>());
//
//        PersonAddress person_address = new PersonAddress();
//        person_address.setAddress1("First One Entered");
//
//        PersonAddress person_address2 = new PersonAddress();
//        person_address2.setAddress2("Second One Entered");
//
//        ArrayOfPersonAddress.getPerson_addresses().add(person_address);
//        ArrayOfPersonAddress.getPerson_addresses().add(person_address2);
//
//    }

    public void PersonAddressToXml() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(PersonAddresses.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(ArrayOfPersonAddress, new File("person_address.xml"));
        marshaller.marshal(ArrayOfPersonAddress, System.out);

    }

    public void xmlToPersonAddress() {

        List<PersonAddress> allPersonAddresses = new ArrayList<PersonAddress>();
        PersonAddresses PersonAddresses = new PersonAddresses();
        File file = null;

            try{
                file = new File(xsdDir +"/person_address.xml");
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
            try{
            JAXBContext jaxbContext = JAXBContext.newInstance(org.ccfng.datamigration.personaddress.PersonAddresses.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            PersonAddresses = (org.ccfng.datamigration.personaddress.PersonAddresses) unmarshaller.unmarshal(file);

        }catch (Exception excx){
                System.out.println(excx.toString());
        }
        for(PersonAddress thePersonAddress: PersonAddresses.getPerson_addresses()){
            thePersonAddress.setUuid(UUID.randomUUID());
            allPersonAddresses.add(thePersonAddress);
            System.out.println("The PersonAddress: "+ thePersonAddress);
        }
        try {
            PersonAddressService.saveAll(allPersonAddresses);
            System.out.println("\n PersonAddress Data Successfully Loaded!!");
        }catch (Exception exc){
            System.out.println(exc.toString());
        }

        System.out.println(PersonAddresses);

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
