package utils

import dtos.SettingsHelper
import groovy.json.JsonBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.apache.log4j.Logger

import static org.apache.log4j.Logger.getLogger

public class PersonFactory implements Serializable {
    private static final long serialVersionUID = -160928058318117177L;

    private static final Logger LOG = getLogger(PersonFactory.class.getName());
    private static String settingsEmailDomain = "test.addtest.se"
    private settingsFirstNames = ['Female', 'Male']
    private settingsLastNames = ['Family', 'Family']
    private settingAddresses = [ ['Gatan','68680','Sunne'], ['Vägen','11863','Stockholm']]
    private static settingsAddressRange = (1..9)
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    SocialSecurityNumberFactory socialSecurityNumberFactory = new SocialSecurityNumberFactory()

    def names = []
    def addresses = []
    def firstNames = []
    def lastNames = []

    public PersonFactory(){
        if(settings["emailDomain"].size() != 0 && settings["emailDomain"] != ""){
            settingsEmailDomain = settings.emailDomain
        }
        if(settings["addresses"].size() != 0 && settings["addresses"] != ""){
            settingAddresses = settings.addresses
        }
        if(settings["addressRange"].size() != 0 && settings["addressRange"] != ""){
            settingsAddressRange = settings.addressRange
        }
        if(settings["lastNames"].size() != 0 && settings["lastNames"] != ""){
            settingsLastNames = settings.lastNames
        }
        if(settings["firstNames"].size() != 0 && settings["firstNames"] != ""){
            settingsFirstNames = settings.firstNames
        }
     }

    private  ArrayList<Person[]> getPeopleOnDate(int maxNoOfPnrs, String yearMonthDayNumIn, String delimiter){
        initNames(maxNoOfPnrs)
        returnPersons(socialSecurityNumberFactory.getPersonNummer(yearMonthDayNumIn, maxNoOfPnrs), delimiter)
    }

    private  ArrayList<Person[]> getPeopleAtAges(int maxNoOfPnrs = 0, int fromAge, int toAge, format = ""){
        initNames(maxNoOfPnrs * (toAge - fromAge + 1))
        returnPersons(socialSecurityNumberFactory.getPersonsMixedAges(maxNoOfPnrs, fromAge, toAge), format)
    }

    public getPeopleAtAgesWitDelimiterOrFormat(int maxNoOfPnrs, int fromAge, int toAge, delimiter = ";", format = "") {
        return getData(getPeopleAtAges(maxNoOfPnrs, fromAge, toAge, delimiter), format)
    }


    public getPeopleOnDateWitDelimiterOrFormat(int maxNoOfPnrs, String yearMonthDayNum, delimiter = ";", format = "") {
        return getData(getPeopleOnDate(maxNoOfPnrs, yearMonthDayNum, delimiter), format)
    }

    private getData(ArrayList<Object[]> people, String format){
        switch (format.toUpperCase()) {
            case "JSON":
                return [people.size(), getJsonPrettyString(people)]
                break
            case "XML":
                return [people.size(), getXmlPrettyString(people)]
                break
            default:
                return [people.size(),getPrettyString(people)]
        }

    }

    private String getPrettyString(ArrayList<Person[]> people) {
        def data = ""
        def firstPerson = true
        people.each { Person person ->
            if(firstPerson){
                data = person.getHeader() + "\n"
                firstPerson = false
            }
            data += person.toString() + "\n"
        }
        return data
    }

    private String getJsonPrettyString(ArrayList<Person[]> persons) {
        def data = persons.collect { Person person ->
            person.getJsonMap()
        }
        def builder = new JsonBuilder()
        builder {
            people data
        }
        return builder.toPrettyString()
    }

   private String getXmlPrettyString(ArrayList<Person[]> persons) {

        def xmls = new StreamingMarkupBuilder().bind {
            people {
                    persons.each { Person personIt ->
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

    private ArrayList<Person[]> returnPersons(AbstractMap ssns, delimiter =  ";") {
        ArrayList<Person[]> people = new ArrayList<Person[]>()
        def addressSize = addresses.size()
        def adressCounter = 0
        def nameSize = names.size()
        def nameCounter = 0
        ssns.eachWithIndex{ ssn, int i ->
            if(i.mod(addressSize) == 0|| i >= addressSize){
                adressCounter = 0
            }else{
                adressCounter++
            }
            if(i.mod(nameSize) == 0 || i >= nameSize){
                nameCounter = 0
            }else{
                nameCounter++
            }
            println i
            people.add(new  Person(i+1, ssn.value.age, names[nameCounter][0], names[nameCounter][1], ssn.value.socialSecurityNumberLong, ssn.value.socialSecurityNumberLongDashLess,  ssn.value.gender,
                    addresses[adressCounter][0],  addresses[adressCounter][1],  addresses[adressCounter][2], settingsEmailDomain, delimiter))
         }
        return people
    }



    private void initFirstNames(){
       // firstNames = settings.firstNames
    }

    private void initLastNames(){
        //lastNames = settings.lastNames
    }

    private void initNames(int maxNoOfPnrs = 0) {
        def maxFirstNameListCount = 10
        def maxLastNameListCount = 10
//        initFirstNames()
//        initLastNames()
        initAddresses()
        if(maxNoOfPnrs < settingsFirstNames.size()){
            maxFirstNameListCount = maxNoOfPnrs
        }else{
            if(maxNoOfPnrs > settingsFirstNames.size()){
                maxFirstNameListCount = settingsFirstNames.size()
            }
        }
        if(maxNoOfPnrs < settingsLastNames.size()){
            maxLastNameListCount = maxNoOfPnrs
        }else{
            if(maxNoOfPnrs > settingsLastNames.size()){
                maxLastNameListCount = settingsLastNames.size()
            }
        }

        Collections.shuffle(settingsLastNames)
        Collections.shuffle(settingsLastNames)
        Collections.shuffle(addresses)
        settingsFirstNames[0..maxFirstNameListCount-1].each { firstName ->
            settingsLastNames[0..maxLastNameListCount-1].each { lastName ->
                names.add([firstName, lastName])
            }
        }
        Collections.shuffle(names)
    }

    private initAddresses(){

   def adr = []
    settingAddresses.each {address->
        settingsAddressRange.each { no ->
            adr.add([address[0] + " $no", address[1], address[2]])
        }
    }

       addresses= adr
    }

//    private initAddresses(){
//       addresses=[
//               ['Timmervägen 1','54164','Skövde'], ['Sturegatan 4','17223','Sundbyberg'], ['Kyrkovägen 50-52','63506','ESKILSTUNA'], ['Hammargärdsvägen 1','64040','STORA SUNDBY'], ['Fredsgatan 1 A','65225','KARLSTAD'], ['Järnvägsgatan 68','17235','Sundbyberg'], ['Kungsgatan 85','75375','Uppsala'], ['Tybble Mellangård','61660','TYSTBERGA'], ['Klostergatan 37 B','58181','LINKÖPING'], ['Blåbärsvägen 13','61337','Oxelösund'], ['Fyrisborgsgatan 1','75375','UPPSALA'], ['Torget 4','95332','HAPARANDA'], ['Östergatan 8','24180','ESLÖV'], ['Järnvägsgatan 8','45052','Dingle'], ['Olof Palmes gata 2','94133','PITEÅ'], ['Aratorpsvägen 27','51171','FRITSLA'], ['Matsarvsvägen 1','79177','Falun'], ['Kärramåla','31298','Våxtorp'], ['Högbacken 11','18437','Åkersberga'], ['Östra Brobanken','11149','STOCKHOLM'], ['Stentäppsgatan','71180','LINDESBERG'], ['Drottninggatan 45','58181','LINKÖPING'], ['Borgatan 13','64432','Torshälla'], ['Sturegatan 11','63230','ESKILSTUNA'], ['Björktorpsgatan 1','63227','Eskilstuna'], ['Hantverkaregatan 7','54231','MARIESTAD'], ['Köpmangatan 3','68380','HAGFORS'], ['Ekonomiavdelningen','69480','Hallsberg'], ['Nordanvinds. 7 A/286','45160','Uddevalla'], ['Hospitalsgatan 30','60181','NORRKÖPING'], ['Nygatan 17 A','57333','TRANÅS'], ['Bertil Muhrs Gata 15','21236','MALMÖ'], ['Zinkensv 59, 3 tr, läg.0131','11741','Stockholm'], ['Södra Järnvägsgatan 11','82732','Ljusdal'], ['Ernst & Young','40182','GÖTEBORG'], ['Nya Rådhuset, Ö Flygeln','27180','YSTAD'], ['Köpmannagatan 14','63356','ESKILSTUNA'], ['Klostergatan 29','63352','ESKILSTUNA'], ['Hantverksgatan 64','57235','OSKARSHAMN'], ['Allbog. 17','34230','ALVESTA'], ['Fjärdholmsgränd 7','12744','STOCKHOLM'], ['Storskärsgatan 4','11529','STOCKHOLM'], ['Ribbingsgatan 32','50466','Borås'], ['Jägmästarev. 46','43064','Hällingsjö'], ['Fyrisborgsgatan 1','75375','Uppsala'], ['Skolvägen 3','54157','Skövde'], ['Sundbyviksvägen 2','64045','KVICKSUND'], ['Stenhagsvägen 45-47','18433','ÅKERSBERGA'], ['Videum Science Park','35196','VÄXJÖ'], ['Askims Torg 5','43682','ASKIM'], ['Ribbingsgatan 32','50466','Borså'], ['Slåttervägen 7','52235','Tidaholm'], ['Döbelnsgatan 17','90330','UMEÅ'], ['Brämhultsvägen 4','50456','BORÅS'], ['Fredsg. 22B','85235','SUNDSVALL'], ['Hallunda Torg 5','14568','Norsborg'], ['Österv. 12','53494','VARA'], ['Oppeby gård 43','61155','NYKÖPING'], ['Sandfjärdsgatan 60','12056','Årsta'], ['Kvarngatan 4','68680','Sunne'], ['Södra Järnvägsg 11','82732','LJUSDAL'], ['Rosenlundsg 48 D','11863','Stockholm'], ['Odontologiska fakulteten','20506','Malmö']
//               ]
//    }
}