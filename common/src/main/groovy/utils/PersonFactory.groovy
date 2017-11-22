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
    private static String settingsEmailDomain = "test.addtest.se"
    private settingsFirstNames = ['Female', 'Male']
    private settingsMaleFirstNames = ['Male1', 'Male2']
    private settingsFemaleFirstNames = ['Female1', 'Female2']
    private settingsLastNames = ['Family', 'Family']
    private addressesDefault = [['Vegagatan 1', '68680', 'Sunne'], ['Rådmansgatan 2', '11863', 'Stockholm']]
//    private static settingsAddressRange = (1..9)
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    SocialSecurityNumberFactory socialSecurityNumberFactory = new SocialSecurityNumberFactory()

    def femaleNames = []
    def maleNames = []
    def addresses = []
    def firstNames = []
    def lastNames = []

    public PersonFactory(){
        //def addressFile = settings.addressFile
        def addressFile = settings.addressCompleteFile
        if(settings["emailDomain"].size() != 0 && settings["emailDomain"] != ""){
            settingsEmailDomain = settings.emailDomain
        }
        try {
            def addressesFromFile = []
            def addressEntries = this.getClass().getResourceAsStream(addressFile).text
            addressEntries.eachLine {
                it = it.trim()
                if (it != ""){
                    addressesFromFile.add(it.split(","))
                }
            }
            addresses = addressesFromFile
        } catch (NullPointerException e) {
            Reporter.log("Can't find setting file $addressFile")
            Reporter.log("Using default values")
            addresses = addressesDefault

        }


//        if(settings["addressRange"].size() != 0 && settings["addressRange"] != ""){
//            settingsAddressRange = settings.addressRange
//        }
        if(settings["lastNames"].size() != 0 && settings["lastNames"] != ""){
            settingsLastNames = settings.lastNames
        }
        if(settings["firstNames"].size() != 0 && settings["firstNames"] != ""){
            settingsFirstNames = settings.firstNames
        }
        if(settings["firstNamesMale"].size() != 0 && settings["firstNamesMale"] != ""){
            settingsMaleFirstNames = settings.firstNamesMale
        }
        if(settings["firstNamesFemale"].size() != 0 && settings["firstNamesFemale"] != ""){
            settingsFemaleFirstNames = settings.firstNamesFemale
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

    public String getPeopleAtAgesWitDelimiterOrFormat(int maxNoOfPnrs, int fromAge, int toAge, delimiter = ";", format = "") {
        return getData(getPeopleAtAges(maxNoOfPnrs, fromAge, toAge, delimiter), format)
    }


    public String getPeopleOnDateWitDelimiterOrFormat(int maxNoOfPnrs, String yearMonthDayNum, delimiter = ";", format = "") {
        return getData(getPeopleOnDate(maxNoOfPnrs, yearMonthDayNum, delimiter), format)
    }

    private String getData(ArrayList<Object[]> people, String format){
        switch (format.toUpperCase()) {
            case "JSON":
                return getJsonPrettyString(people)
                break
            case "XML":
                return getXmlPrettyString(people)
                break
            default:
                return getPrettyString(people)
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
                            ip personIt.ip
                            pin personIt.pin
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
        def femaleNameSize = femaleNames.size()
        def maleNameSize = maleNames.size()
        def femaleNameCounter = 0
        def maleNameCounter = 0
        ssns.eachWithIndex{ ssn, int i ->
            if(i.mod(addressSize) == 0|| i >= addressSize){
                adressCounter = 0
            }else{
                adressCounter++
            }
            def firstName = femaleNames[femaleNameCounter][0]
            def lastName = femaleNames[femaleNameCounter][1]
            if(ssn.value.gender == "Female") {
                if (femaleNameCounter > 0 && i.mod(femaleNameSize) == 0 || i >= femaleNameSize) {
                    femaleNameCounter = 0
                } else {
                    femaleNameCounter++
                }
            }else{
                firstName = maleNames[maleNameCounter][0]
                lastName = maleNames[maleNameCounter][1]
                if(maleNameCounter > 0 && i.mod(maleNameSize) == 0 || i >= maleNameSize){
                    maleNameCounter = 0
                }else{
                    maleNameCounter++
                }
            }
            println i
            people.add(new  Person(i+1, ssn.value.age, firstName, lastName, ssn.value.socialSecurityNumberLong, ssn.value.socialSecurityNumberLongDashLess,  ssn.value.gender,
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
        def maxFirstNameFemaleListCount = 33
        def maxFirstNameMaleListCount = 33
        def maxLastNameListCount = 33
//        initFirstNames()
//        initLastNames()
        //initAddresses()
        if(maxNoOfPnrs <= settingsMaleFirstNames.size()){
            if(maxFirstNameMaleListCount > settingsMaleFirstNames.size()){
                maxFirstNameMaleListCount = settingsMaleFirstNames.size()
            }else{
                maxFirstNameMaleListCount = maxNoOfPnrs
            }
        }else{
            if(maxNoOfPnrs > settingsMaleFirstNames.size()){
                maxFirstNameMaleListCount = settingsMaleFirstNames.size()
            }
        }
        if(maxNoOfPnrs <= settingsFemaleFirstNames.size()){
            if(maxFirstNameFemaleListCount > settingsFemaleFirstNames.size()){
                maxFirstNameFemaleListCount = settingsFemaleFirstNames.size()
            }else{
                maxFirstNameFemaleListCount = maxNoOfPnrs
            }
        }else{
            if(maxNoOfPnrs > settingsFemaleFirstNames.size()){
                maxFirstNameFemaleListCount = settingsFemaleFirstNames.size()
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
        Collections.shuffle(settingsFemaleFirstNames)
        Collections.shuffle(settingsMaleFirstNames)
        Collections.shuffle(addresses)
        settingsFemaleFirstNames[0..maxFirstNameFemaleListCount-1].each { femaleFirstName ->
            settingsLastNames[0..maxLastNameListCount-1].each { lastName ->
                femaleNames.add([femaleFirstName, lastName])
            }
        }
        settingsMaleFirstNames[0..maxFirstNameMaleListCount-1].each { maleFirstName ->
            settingsLastNames[0..maxLastNameListCount-1].each { lastName ->
                maleNames.add([maleFirstName, lastName])
            }
        }
        Collections.shuffle(femaleNames)
        Collections.shuffle(maleNames)
    }

//    private initAddresses(){
//
//        addressesDefault.each { address ->
//            settingsAddressRange.each { no ->
//                println "$no $address"
//                addresses.add([address[0].trim() + " $no", address[1].trim(), address[2].trim()])
//            }
//        }
//
//    }

//    private initAddresses(){
//       addresses=[
//               ['Timmervägen 1','54164','Skövde'], ['Sturegatan 4','17223','Sundbyberg'], ['Kyrkovägen 50-52','63506','ESKILSTUNA'], ['Hammargärdsvägen 1','64040','STORA SUNDBY'], ['Fredsgatan 1 A','65225','KARLSTAD'], ['Järnvägsgatan 68','17235','Sundbyberg'], ['Kungsgatan 85','75375','Uppsala'], ['Tybble Mellangård','61660','TYSTBERGA'], ['Klostergatan 37 B','58181','LINKÖPING'], ['Blåbärsvägen 13','61337','Oxelösund'], ['Fyrisborgsgatan 1','75375','UPPSALA'], ['Torget 4','95332','HAPARANDA'], ['Östergatan 8','24180','ESLÖV'], ['Järnvägsgatan 8','45052','Dingle'], ['Olof Palmes gata 2','94133','PITEÅ'], ['Aratorpsvägen 27','51171','FRITSLA'], ['Matsarvsvägen 1','79177','Falun'], ['Kärramåla','31298','Våxtorp'], ['Högbacken 11','18437','Åkersberga'], ['Östra Brobanken','11149','STOCKHOLM'], ['Stentäppsgatan','71180','LINDESBERG'], ['Drottninggatan 45','58181','LINKÖPING'], ['Borgatan 13','64432','Torshälla'], ['Sturegatan 11','63230','ESKILSTUNA'], ['Björktorpsgatan 1','63227','Eskilstuna'], ['Hantverkaregatan 7','54231','MARIESTAD'], ['Köpmangatan 3','68380','HAGFORS'], ['Ekonomiavdelningen','69480','Hallsberg'], ['Nordanvinds. 7 A/286','45160','Uddevalla'], ['Hospitalsgatan 30','60181','NORRKÖPING'], ['Nygatan 17 A','57333','TRANÅS'], ['Bertil Muhrs Gata 15','21236','MALMÖ'], ['Zinkensv 59, 3 tr, läg.0131','11741','Stockholm'], ['Södra Järnvägsgatan 11','82732','Ljusdal'], ['Ernst & Young','40182','GÖTEBORG'], ['Nya Rådhuset, Ö Flygeln','27180','YSTAD'], ['Köpmannagatan 14','63356','ESKILSTUNA'], ['Klostergatan 29','63352','ESKILSTUNA'], ['Hantverksgatan 64','57235','OSKARSHAMN'], ['Allbog. 17','34230','ALVESTA'], ['Fjärdholmsgränd 7','12744','STOCKHOLM'], ['Storskärsgatan 4','11529','STOCKHOLM'], ['Ribbingsgatan 32','50466','Borås'], ['Jägmästarev. 46','43064','Hällingsjö'], ['Fyrisborgsgatan 1','75375','Uppsala'], ['Skolvägen 3','54157','Skövde'], ['Sundbyviksvägen 2','64045','KVICKSUND'], ['Stenhagsvägen 45-47','18433','ÅKERSBERGA'], ['Videum Science Park','35196','VÄXJÖ'], ['Askims Torg 5','43682','ASKIM'], ['Ribbingsgatan 32','50466','Borså'], ['Slåttervägen 7','52235','Tidaholm'], ['Döbelnsgatan 17','90330','UMEÅ'], ['Brämhultsvägen 4','50456','BORÅS'], ['Fredsg. 22B','85235','SUNDSVALL'], ['Hallunda Torg 5','14568','Norsborg'], ['Österv. 12','53494','VARA'], ['Oppeby gård 43','61155','NYKÖPING'], ['Sandfjärdsgatan 60','12056','Årsta'], ['Kvarngatan 4','68680','Sunne'], ['Södra Järnvägsg 11','82732','LJUSDAL'], ['Rosenlundsg 48 D','11863','Stockholm'], ['Odontologiska fakulteten','20506','Malmö']
//               ]
//    }
}