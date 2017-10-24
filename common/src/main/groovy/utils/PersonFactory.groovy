package utils

import dtos.SettingsHelper
import groovy.json.JsonBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.apache.log4j.Logger
import org.testng.Reporter

import static org.apache.log4j.Logger.getLogger

public class PersonFactory implements Serializable {
    private static final long serialVersionUID = -160928058318117177L;

    private static final Logger LOG = getLogger(PersonFactory.class.getName());
    private static String emailDomain = "test.addtest.se"
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    SocialSecurityNumberFactory socialSecurityNumberFactory = new SocialSecurityNumberFactory()

    def firstNames
    def lastNames
    def names = []
    def addresses = []

    public PersonFactory(){
        if(settings["emailDomain"].size() != 0 && settings["emailDomain"] != ""){
            emailDomain = settings.emailDomain
        }
    }

    public ArrayList<Person[]> getPersons(String yearMonthDayNumIn, int maxNoOfPnrs = 0){
        initNames(maxNoOfPnrs)
        returnPersons(socialSecurityNumberFactory.getPersonNummer(yearMonthDayNumIn, maxNoOfPnrs))
    }

    public String getPersons_Json(String yearMonthDayNumIn, int maxNoOfPnrs = 0){
        initNames(maxNoOfPnrs)
        ArrayList<Person[]> people = returnPersons(socialSecurityNumberFactory.getPersonNummer(yearMonthDayNumIn, maxNoOfPnrs))
        return getJsonPrettyString(people)

    }

   public String getPersons_Xml(String yearMonthDayNumIn, int maxNoOfPnrs = 0){
        initNames(maxNoOfPnrs)
        ArrayList<Person[]> people = returnPersons(socialSecurityNumberFactory.getPersonNummer(yearMonthDayNumIn, maxNoOfPnrs))
        return getXmlPrettyString(people)

    }


    public  ArrayList<Object[]> getPersonsAtAge(int age, int maxNoOfPnrs = 0) {
        initNames(maxNoOfPnrs)
        returnPersons(socialSecurityNumberFactory.getPersonNummerAtAge(age, maxNoOfPnrs))
    }

    public  String getPersonsAtAge_Json(int age, int maxNoOfPnrs = 0) {
        initNames(maxNoOfPnrs)
        ArrayList<Person[]> people = returnPersons(socialSecurityNumberFactory.getPersonNummerAtAge(age, maxNoOfPnrs))
        return getJsonPrettyString(people)
    }
    public  String getPersonsAtAge_Xml(int age, int maxNoOfPnrs = 0) {
        initNames(maxNoOfPnrs)
        ArrayList<Person[]> people = returnPersons(socialSecurityNumberFactory.getPersonNummerAtAge(age, maxNoOfPnrs))
        return getXmlPrettyString(people)
    }

    public String getJsonPrettyString(ArrayList<Person[]> people) {
        def data = people.collect { person ->
            [
                    no                              : person.no,
                    age                             : person.age,
                    firstName                       : person.firstName,
                    lastName                        : person.lastName,
                    socialSecurityNumberLong        : person.socialSecurityNumberLong,
                    socialSecurityNumberLongDashLess: person.socialSecurityNumberLongDashLess,
                    gender                          : person.gender,
                    address                         : person.address,
                    zip                             : person.zip,
                    city                            : person.city,
                    tel                             : person.tel,
                    mobile                          : person.mobile,
                    user                            : person.user,
                    pwd                             : person.pwd,
                    email                           : person.email,
                    url                             : person.url
            ]
        }
        def builder = new JsonBuilder()

        def root = builder {
            persons data
        }
        return builder.toPrettyString()
    }

   public String getXmlPrettyString(ArrayList<Person[]> people) {

        def xmls = new StreamingMarkupBuilder().bind {
            persons {
                    people.each { Person personIt ->
                        person {
                            no personIt.no
                            age personIt.age
                            firstName personIt.firstName
                            lastName personIt.lastName
                            socialSecurityNumberLong personIt.socialSecurityNumberLong
                            socialSecurityNumberLongDashLess personIt.socialSecurityNumberLongDashLess
                            gender personIt.gender
                            address personIt.address
                            zip personIt.zip
                            city personIt.city
                            tel personIt.tel
                            mobile personIt.mobile
                            user personIt.user
                            pwd personIt.pwd
                            email personIt.email
                            url personIt.url
                        }
                    }
                }
            }

       Node xmlNode = new XmlParser().parseText(XmlUtil.serialize(xmls))
       def xmlOutput = new StringWriter()
       def xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput))
       xmlNodePrinter.with {
           preserveWhitespace = true
           expandEmptyElements = true
           quote = "'" // Use single quote for attributes
       }
       xmlNodePrinter.print(xmlNode)
       return  (xmlOutput.toString())
   }

    public  ArrayList<Object[]> getPersonsAtAgeJson(int age, int maxNoOfPnrs = 0) {
        initNames(maxNoOfPnrs)
        returnPersons(socialSecurityNumberFactory.getPersonNummerAtAge(age, maxNoOfPnrs))
    }

    private ArrayList<Person[]> returnPersons(AbstractMap ssns) {
        ArrayList<Person[]> persons = new ArrayList<Person[]>()
        def addressSize = addresses.size()
        def adressCounter = 0
        ssns.eachWithIndex{ ssn, int i ->
            if(i.mod(addressSize) == 0){
                adressCounter = 0
            }else{
                adressCounter++
            }
            persons.add(new  Person(i+1, ssn.value.age, names[i][0], names[i][1], ssn.value.socialSecurityNumberLong, ssn.value.socialSecurityNumberLongDashLess,  ssn.value.gender,
                    addresses[adressCounter][0],  addresses[adressCounter][1],  addresses[adressCounter][2], emailDomain))
         }
        return persons
    }



    private void initFirstNames(){
        firstNames = ['Janina', 'Marielle', 'Jasmine', 'Jessica', 'Theres', 'Christin', 'Tiina', 'Seija', 'Hanan', 'Anki', 'Suzan', 'Jasmina', 'Edina', 'Tina', 'Angelika', 'Maha', 'Therése', 'Maritha', 'Marit', 'Catrine', 'Seija', 'Ann-Katrin', 'Izabella', 'Jessica', 'Elise', 'Paulina', 'Sofia', 'Susan', 'Marianne', 'Paulina', 'Gull-Britt', 'Veronika', 'Jelena', 'Liza', 'Seija', 'Linda', 'Iris', 'Janina', 'Maya', 'Emmy', 'Tiina', 'Åse', 'Päivi', 'Carolin', 'Rose-Marie', 'Liselotte', 'Nelly', 'Anitha', 'Ruth', 'Diana', 'Marita', 'Tiina', 'Alexandra', 'Ingela', 'Krystyna', 'Nathalie', 'Ewa', 'Stephanie', 'Izabella', 'Ann-Mari', 'Snezana', 'Monika', 'Eva-Lotta', 'Natasha', 'Anitha', 'Emmy', 'Fatemeh', 'Elin', 'Åse', 'Theresia', 'Susann', 'Anna Karin', 'Veronika', 'Kamilla', 'Amira', 'Dorota', 'Dorota', 'Mary', 'Rigmor', 'Marica', 'Amelie', 'Irina', 'Aleksandra', 'Sofie', 'Mary', 'Elise', 'Veronika', 'Arja', 'Marielle', 'Seija', 'Jaana', 'Mary', 'Izabella', 'Mirjam', 'Marja', 'Liv', 'Ylva', 'Jaana', 'ElenorH', 'Majid']
    }

    private void initLastNames(){
        lastNames = ['From', 'Ljungqvist', 'Rosén', 'Malmros', 'Leijon', 'Grundström', 'Johansen', 'Säll', 'Larsen', 'Rosander', 'Saliba', 'Oskarsson', 'Lindroth', 'Adolfsson', 'Yousif', 'Wallström', 'Engström', 'Sjölander', 'Hjelm', 'Carlsson', 'Engström', 'Kristiansson', 'Liljegren', 'Gullberg', 'Lindblad', 'Jernberg', 'Amin', 'Östberg', 'Falck', 'Christensen', 'Engström', 'Wendel', 'Klasson', 'Lindell', 'Högman', 'Sundvall', 'Liljegren', 'Ek', 'Hellström', 'Oskarsson', 'Berlin', 'Liljegren', 'Mohammed', 'Mousa', 'Säll', 'Larson', 'Bernhardsson', 'Lorentzon', 'Adolfsson', 'Långström', 'Liljegren', 'Marklund', 'Landin', 'Olsson', 'Larson', 'Nyman', 'Svensson', 'Hernandez', 'Nordmark', 'Westermark', 'Göransson', 'Andersson', 'Johnson', 'Berger', 'Pham', 'Ståhl', 'Asp', 'Helgesson', 'Olausson', 'Holst', 'Danielsson', 'Peci', 'Hugosson', 'Pham', 'Larson', 'Oskarsson', 'Nygren', 'Strömbäck', 'Broström', 'Hoffman', 'Åkerlund', 'Salomonsson', 'Hosseini', 'Sundin', 'Albinsson', 'Kvarnström', 'Broman', 'Källman', 'Lundström', 'Oskarsson', 'Kjellberg', 'Daoud', 'Nyman', 'Yildiz', 'Nyqvist', 'Sturesson', 'Henriksson', 'Amin', 'Adolfsson', 'Aram']
    }

    private void initNames(int maxNoOfPnrs = 0) {
        def maxFirstNameListCount = 10
        def maxLastNameListCount = 10
        initFirstNames()
        initLastNames()
        initAddresses()
        if(maxNoOfPnrs < firstNames.size){
            maxFirstNameListCount = maxNoOfPnrs
        }else{
            if(maxNoOfPnrs > firstNames.size){
                maxFirstNameListCount = firstNames.size
            }
        }
        if(maxNoOfPnrs < lastNames.size){
            maxLastNameListCount = maxNoOfPnrs
        }else{
            if(maxNoOfPnrs > lastNames.size){
                maxLastNameListCount = lastNames.size
            }
        }

        Collections.shuffle(firstNames)
        Collections.shuffle(lastNames)
        Collections.shuffle(addresses)
        firstNames[0..maxFirstNameListCount-1].each { firstName ->
            lastNames[0..maxLastNameListCount-1].each { lastName ->
                names.add([firstName, lastName])
            }
        }
        Collections.shuffle(names)
    }

    private initAddresses(){
       def map = [
               ['Timmervägen','54164','Skövde'],
               ['Sturegatan','17223','Sundbyberg'],
               ['Kyrkovägen','63506','ESKILSTUNA'],
               ['Hammargärdsvägen','64040','STORA SUNDBY'],
               ['Fredsgatan','65225','KARLSTAD'],
               ['Järnvägsgatan','17235','Sundbyberg'],
               ['Kungsgatan','75375','Uppsala'],
               ['Klostergatan','58181','LINKÖPING'],
               ['Blåbärsvägen','61337','Oxelösund'],
               ['Fyrisborgsgatan','75375','UPPSALA'],
               ['Torget','95332','HAPARANDA'],
               ['Östergatan','24180','ESLÖV'],
               ['Järnvägsgatan','45052','Dingle'],
               ['Olof Palmes gata','94133','PITEÅ'],
               ['Aratorpsvägen','51171','FRITSLA'],
               ['Matsarvsvägen ','79177','Falun'],
               ['Högbacken','18437','Åkersberga'],
               ['Drottninggatan','58181','LINKÖPING'],
               ['Borgatan','64432','Torshälla'],
               ['Sturegatan','63230','ESKILSTUNA'],
               ['Björktorpsgatan','63227','Eskilstuna'],
               ['Hantverkaregatan','54231','MARIESTAD'],
               ['Köpmangatan','68380','HAGFORS'],
               ['Ekonomiavdelningen','69480','Hallsberg'],
               ['Hospitalsgatan','60181','NORRKÖPING'],
               ['Nygatan','57333','TRANÅS'],
               ['Bertil Muhrs Gata','21236','MALMÖ'],
               ['Södra Järnvägsgatan','82732','Ljusdal'],
               ['Köpmannagatan','63356','ESKILSTUNA'],
               ['Klostergatan','63352','ESKILSTUNA'],
               ['Askims Torg','43682','ASKIM'],
               ['Ribbingsgatan','50466','Borså'],
               ['Slåttervägen','52235','Tidaholm'],
               ['Döbelnsgatan','90330','UMEÅ'],
               ['Brämhultsvägen','50456','BORÅS'],
               ['Fredsg.','85235','SUNDSVALL'],
               ['Hallunda Torg','14568','Norsborg'],
               ['Österv.','53494','VARA'],
               ['Oppeby gård','61155','NYKÖPING'],
               ['Sandfjärdsgatan','12056','Årsta'],
               ['Kvarngatan','68680','Sunne'],
               ['Rosenlundsg','11863','Stockholm']
                                ]
   def adr = []
    map.each {
        (1..9).each { no ->
            adr.add([it[0] + " $no", it[1], it[2]])
        }
    }

       addresses= adr
    }

    private initAddressesOrg(){
       addresses=[
               ['Timmervägen 1','54164','Skövde'], ['Sturegatan 4','17223','Sundbyberg'], ['Kyrkovägen 50-52','63506','ESKILSTUNA'], ['Hammargärdsvägen 1','64040','STORA SUNDBY'], ['Fredsgatan 1 A','65225','KARLSTAD'], ['Järnvägsgatan 68','17235','Sundbyberg'], ['Kungsgatan 85','75375','Uppsala'], ['Tybble Mellangård','61660','TYSTBERGA'], ['Klostergatan 37 B','58181','LINKÖPING'], ['Blåbärsvägen 13','61337','Oxelösund'], ['Fyrisborgsgatan 1','75375','UPPSALA'], ['Torget 4','95332','HAPARANDA'], ['Östergatan 8','24180','ESLÖV'], ['Järnvägsgatan 8','45052','Dingle'], ['Olof Palmes gata 2','94133','PITEÅ'], ['Aratorpsvägen 27','51171','FRITSLA'], ['Matsarvsvägen 1','79177','Falun'], ['Kärramåla','31298','Våxtorp'], ['Högbacken 11','18437','Åkersberga'], ['Östra Brobanken','11149','STOCKHOLM'], ['Stentäppsgatan','71180','LINDESBERG'], ['Drottninggatan 45','58181','LINKÖPING'], ['Borgatan 13','64432','Torshälla'], ['Sturegatan 11','63230','ESKILSTUNA'], ['Björktorpsgatan 1','63227','Eskilstuna'], ['Hantverkaregatan 7','54231','MARIESTAD'], ['Köpmangatan 3','68380','HAGFORS'], ['Ekonomiavdelningen','69480','Hallsberg'], ['Nordanvinds. 7 A/286','45160','Uddevalla'], ['Hospitalsgatan 30','60181','NORRKÖPING'], ['Nygatan 17 A','57333','TRANÅS'], ['Bertil Muhrs Gata 15','21236','MALMÖ'], ['Zinkensv 59, 3 tr, läg.0131','11741','Stockholm'], ['Södra Järnvägsgatan 11','82732','Ljusdal'], ['Ernst & Young','40182','GÖTEBORG'], ['Nya Rådhuset, Ö Flygeln','27180','YSTAD'], ['Köpmannagatan 14','63356','ESKILSTUNA'], ['Klostergatan 29','63352','ESKILSTUNA'], ['Hantverksgatan 64','57235','OSKARSHAMN'], ['Allbog. 17','34230','ALVESTA'], ['Fjärdholmsgränd 7','12744','STOCKHOLM'], ['Storskärsgatan 4','11529','STOCKHOLM'], ['Ribbingsgatan 32','50466','Borås'], ['Jägmästarev. 46','43064','Hällingsjö'], ['Fyrisborgsgatan 1','75375','Uppsala'], ['Skolvägen 3','54157','Skövde'], ['Sundbyviksvägen 2','64045','KVICKSUND'], ['Stenhagsvägen 45-47','18433','ÅKERSBERGA'], ['Videum Science Park','35196','VÄXJÖ'], ['Askims Torg 5','43682','ASKIM'], ['Ribbingsgatan 32','50466','Borså'], ['Slåttervägen 7','52235','Tidaholm'], ['Döbelnsgatan 17','90330','UMEÅ'], ['Brämhultsvägen 4','50456','BORÅS'], ['Fredsg. 22B','85235','SUNDSVALL'], ['Hallunda Torg 5','14568','Norsborg'], ['Österv. 12','53494','VARA'], ['Oppeby gård 43','61155','NYKÖPING'], ['Sandfjärdsgatan 60','12056','Årsta'], ['Kvarngatan 4','68680','Sunne'], ['Södra Järnvägsg 11','82732','LJUSDAL'], ['Rosenlundsg 48 D','11863','Stockholm'], ['Odontologiska fakulteten','20506','Malmö']
               ]
    }
}