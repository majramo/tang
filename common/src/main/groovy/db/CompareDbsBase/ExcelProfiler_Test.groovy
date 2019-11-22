package db.CompareDbsBase

import base.AnySqlCompareTest
import base.SystemProfile
import excel.ExcelFileReader
import excel.ExcelFileWriter
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import java.text.DecimalFormat

import static dtos.base.Constants.dbRunTypeRows

public class ExcelProfiler_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")
    private final static String AND_QUERY_EXTENSION = "AND_QUERY_EXTENSION"
    public static final String TAB = "\t"
    def SOURCE_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def MESSAGE = "Comparing tables"
    ArrayList<String> headersExcel = ["System", "Table", "Column", "Type", "Sensitive", "Masking", "Action", "MaskOverride", "MaskOverrideAddon", "MaskExtra", "TargetSizeMinimumDiff", "TargetSizeMaximumDiff", "RunSql", "SearchCriteria", "SearchExtraCondition", "Verify"]
    ArrayList<String> headersDb = ["SYSTEMNAME", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "SENSITIVE", "MASKING", "ACTION", "MASKOVERRIDE", "MaskOverrideAddon", "C1", "C2", "C3", "C4", "C5", "C6", "C7"]
    DecimalFormat thousandSeparatorFormat = new DecimalFormat("###,###");
    def databaseTypeColumn
    def SQL_SERVER = "SQL_SERVER"

    @Parameters(["systemColumn", "databaseTypeColumn", "startTable", "startColumn"] )
    @Test
    public void compareSourceColumnsEqualsToExcelTest(String systemColumn, String databaseTypeColumn , String startTable, String startColumn, ITestContext testContext){
        def fileName = "/configFiles/${systemColumn}.systemTableColumn.excel.97.xls"
        reporterLogLn("systemColumn       <$systemColumn>")
        reporterLogLn("databaseTypeColumn <$databaseTypeColumn>")
        reporterLogLn("startTableC        <$startTable>")
        reporterLogLn("startolumn         <$startColumn>")
        reporterLogLn("fileName           <$fileName>")
        URL is = this.getClass().getResource(fileName)
        this.databaseTypeColumn = databaseTypeColumn
        def SOURCE_TABLE_QUERY_ORACLE
        if (is != null) {
            SOURCE_TABLE_QUERY_ORACLE = new File(is.toURI()).text
        }
        super.setup()
        def (ExcelObjectProvider excelObjectProviderMaskAction, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def profileDataPath = settings.profileDataPath
        if(profileDataPath.size() == 0 || profileDataPath == ""){
            profileDataPath = "."
        }
        File directory = new File(String.valueOf("$profileDataPath"));

        if(!directory.exists()){
            directory.mkdir();
        }
        File tmpFile   = new File("$profileDataPath/${system}.tmp.dataProfiled.xls")
        def dbType = getDbType(sourceDb)
        def tablesQuery
        //TODO: change code to take care in case DB is SqlServer



        SystemProfile excelSystemProfile = createSystemProfileFromExcelDataBody("Excel file", excelObjectProviderMaskAction.inputFile, startTable, startColumn)
        def excludes = "Workflow|Solutions|Database|AppInstallations|SortBehavior|PortalName|date|context|urlid|type|resource|role|cache|AppInstallations|AppPrincipals|AppRuntimeMetadata"
        def profileData =[:]
        profileData["Diarienummer"] = [ [ DD:"AF_Diarienummer"], [Meta:"(?i).*diarie.*"], [Exclude:""]]
        profileData["Epost"] = [ [ DD:"AF_Epost"], [Meta:"(?i).*E-?(POST|MAIL).*"], [Exclude:"(?i).*(svarsadress|_id|$excludes).*"]]
        profileData["Fodelsedatum"] = [ [ DD:"AF_Fodelsedatum"], [Meta:"(?i).*FODELSE.*"], [Exclude:"(?i).*svarsadress.*"]]
        profileData["Foretagsnamn"] = [ [ DD:"AF_Namn"], [Meta:"(?i).*(ORG|FTG|FORETAG).*Namn|(HUVUD)?(ARBETSSTALLE)(_)?(BENAMNING)?(_)?(UPPER)?|(INFO)?(_)?(FIRMA)(_)?(UPPER|TECKN(ARE)?)?.*"], [Exclude:"(?i).*(nr|nummer|enhet|_id|ARBETSTAGARORG|ANTAL|orgid|space).*"]]
        profileData["Fritext"] = [ [ DD:"AF_Fritext"], [Meta:"(?i).*(REFERENS|SARSKILDAVIL|RESULTAT|ATT_ANT|TEXT|MEDD|UPPL|MOTIV|OVRIG|BESK|KOMMENT|ANTECK|ANSOK|BILAG|BENAMNING|OBJEKTSNAMN|INNEHALL).*"], [Exclude:"(?i).*(mall|vf_|hjalp|_id|skic|error|AGKLASS_BENAMNING|upplag|bilag|sni|ssyk|lan|datum|amotext|$excludes).*"]]
        profileData["Kontonummer"] = [ [ DD:"AF_Kontonummer"], [Meta:"(?i).*((BG|PG|PGBG|GIRO)|(BANK|PLUS)(GIRO|KONTO)|(BANK|KONTO))_?(NR|NUMMER)?.*"], [Exclude:"(?i).*(Upgrad|grpup|web|kontor|dokument|_id|_guid|skic|amo_text|kat|kod|finans|PGM|BGRUPP|ARBGIVAREID|kontrollupp|_KR|CLEARINGNR).*"]]
        profileData["Losenord"] = [ [ DD:"AF_Losenord"], [Meta:"(?i).*(pw(d)?|losen(ord)?|pass(word)?).*"], [Exclude:"(?i).*(DomainId|webid|passa|_dat|passiv|medpass).*"]]
        profileData["Namn"] = [ [ DD:"AF_Namn"], [Meta:"(?i).*(name|SOKANDE[^_ID]|(AG|KPERS|SOK|FOR|EFTER|F|E|FULL|anordnar|kontakt)_?(NAMN)|ER_REFERENS|VAR|INFO_BESL_FATT|INFO_PRONAMN_REFERENS\$|^(NAMN)|NAMN_VER).*"], [Exclude:"(?i).*(klass|AlertTemplateName|CollationName|DatabaseName|DirName|EventName|FolderName|GroupName|ItemName|LastPullerHostName|LeafName|ListDirName|MachineName|NameResource|ParentLeafName|PatchName|PatchableUnitName|PortalName|ResourceName|ServerName|SourceName|StatusFieldName|SubscriptionName|TargetDirName|TargetLeafName|TransName|ColumnName|WebApplicationName|tp_DisplayName|vf_|kontor|mall|proje|PARAMETER|text|proc|hjalp|filnam|filenam|atgard|dokument|lock|foretag|firma|instan|sche|batch|category|header|format|calend|trigger|job|lock|gui|benamn|handl|utbnamn|kommun_namn|projnamn|projnamn|kontonamn|lan_namn|kort_namn|omrade_namn|stalle_namn|status_namn|avtalsnamn|kategori|TABLE_NAME|space|NAMESPACE|PortalName|$excludes).*"]]
        profileData["Orgnr"] = [ [ DD:"AF_PnrOrgnr"], [Meta:"(?i).*(org(anisation|anisations)?)(nr|nummer)|(huvud)?(arbetsstalle)|firma.*"], [Exclude:"(?i).*(namn|kod|enhet|lopnummer|antal).*"]]
        profileData["Pnr"] = [ [ DD:"AF_PnrOrgnr"], [Meta:"(?i).*(BEG_IDENTITET|PERSONNUMER|(pnr|(pers(on)?)(nr|nummer))).*"], [Exclude:"(?i).*(lopnr|_id|persid|referens|personal|avvik|privat|kod|_avst|_avrund|TENDSIGN).*"]]
        profileData["Postnummer"] = [ [ DD:"AF_PostnrOrt"], [Meta:"(?i).*(postnr|postnummer).*"], [Exclude:"(?i).*(svarsadress|prj|_kommun|_lan).*"]]
        profileData["Postort"] = [ [ DD:"AF_PostnrOrt"], [Meta:"(?i).*(ORT|Postadress).*"], [Exclude:"(?i).*(svarsadress|kort|kursort|lock|errort|rapport|sortb|portal|sortering|kort|ORT_FRAN|ORT_TILL|sort_ord|ort_id|sortn|sortering|rapport|borttag|_sort|prj|datum|SORTORDNR).*"]]
        profileData["Postadress"] = [ [ DD:"AF_Adress"], [Meta:"(?i).*(BACKE|BOX|GATA|STIG|TORP|TOMT|ADR|ADDR).*"], [Exclude:"(?i).*(svarsadress|box|id|kod|prj|teleadr).*"]]
        profileData["Signatur"] = [ [ DD:"Eleg"], [Meta:"(?i).*(Sign|_by|_av).*"], [Exclude:"(?i).*(signal|_id|lank_|TENDSIGN).*"]]
        profileData["Telefonnummer"] = [ [ DD:"AF_Telefonnummer"], [Meta:"(?i).*(tel|vaxel|mobil|fax).*"], [Exclude:"(?i).*(site|date|svarsadress|sortn|kapitel|_id|hanterar|teleadr).*"]]
        profileData["URL"] = [ [ DD:"AF_Url"], [Meta:"(?i).*(Url|hemsida|lank).*"], [Exclude:"(?i).*(hour|blankett|lankod|TENDSIGN|$excludes).*"]]
        profileData["Eleg"] = [ [ DD:"AF_Eleg"], [Meta:"(?i).*eleg.*"], [Exclude:""]]

        def dataProfiled = profile(excelSystemProfile, profileData)

        ExcelFileWriter excelFileWriter = new ExcelFileWriter(tmpFile.getPath(), "System")
        excelFileWriter.writeHeader( headersExcel);
        excelFileWriter.writeBody(dataProfiled);
        excelFileWriter.flushAndClose()



        reporterLogLn("")
        reporterLogLn("See <$system> profile: $tmpFile")
        reporterLogLn("")
        reporterLogLn("#################")


    }
    private profile(SystemProfile excelData, profileData){
        def dashes8 = ["-", "-", "-", "-", "-", "-", "-", "-", "-", "-" ]
        def dashes12 = ["-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-" ]
        def dashes3 = ["-", "-", "-"]
        def excelDataFiltered = excelData.getDbValues()//.findAll{it.Column.matches("INN.*")}
        println "####"
        def dataProfiled = []
        println ("\n$headersExcel")
        excelDataFiltered.each {excleBodyLine->
            excleBodyLine["System"] =  excleBodyLine["System"] + "Profiled"
            if(excleBodyLine["Type"].toLowerCase().matches("time.*") || excleBodyLine["Type"].toLowerCase().matches("date.*") ||
                    excleBodyLine["Type"].toLowerCase() == "bit" || excleBodyLine["Type"].toLowerCase() == "uniqueidentifier" ||
                    excleBodyLine["Type"].toLowerCase() == "row" || excleBodyLine["Type"].toLowerCase() == "rowid"   ){
                //skip
                def line = excleBodyLine.collect{it.value} + dashes12
                dataProfiled.add(line)
            }else {
                def rowDone = false
                def column = excleBodyLine.Column
                def table = excleBodyLine.Table
                def type = excleBodyLine.Type
                println column
                def select = "SELECT $column FROM $table WHERE $column IS NOT NULL AND $column != '' AND ROWNUM < 51;"
                if(databaseTypeColumn.equals(SQL_SERVER)){
                    select = "SELECT TOP 50 $column FROM $table WHERE $column IS NOT NULL AND $column != '';"
                }
                profileData.findAll { profile ->
                    def profileDD = profile.value.DD[0]
                    def profileMeta = profile.value.Meta[1]
                    def profileExclude = profile.value.Exclude[2]

                    //println  "  " + profileDD + " " + profileMeta
                    if (column.matches(profileMeta) ){
                        if(profileExclude == "" || (profileExclude != "" && !column.matches(profileExclude))) {
                            def line = excleBodyLine.collect { it.value } + "JA" + profileDD + "Mask"
                            if(profileDD == "AF_Fritext" && type.matches(".*LOB") ){
                                line = excleBodyLine.collect { it.value } + "JA" + profileDD + "Nullify"
                            }
                                    dataProfiled.add(line + select + dashes8)

                            rowDone = true
                         }
                    }
                }
                if (!rowDone) {
                    def line = excleBodyLine.collect { it.value } + dashes3 + select + dashes8
                    dataProfiled.add(line)

                }
            }
        }


        return dataProfiled
    }
    private compare(SystemProfile dbData, SystemProfile excelData){
        def sameRows = dbData.getSystemProfileKeys().intersect(excelData.getSystemProfileKeys())
        def dbNewRows = dbData.getSystemProfileKeys() - sameRows
        def excelRemovedRows =excelData.getSystemProfileKeys() - sameRows
        def dataCompareOutput = []
        println ("\n$headersExcel")
        excelData.getSystemProfileRowsContainingKeys(sameRows).each {
            dataCompareOutput.add(["--", "Same"] + it.value.getValues())
        }
        dbData.getSystemProfileRowsContainingKeys(dbNewRows).each {
            dataCompareOutput.add(["DB", "New"]  + it.value.getValues() )

        }
        excelData.getSystemProfileRowsContainingKeys(excelRemovedRows).each {
            dataCompareOutput.add(["Excel", "Removed"]  + it.value.getValues())

        }

        return dataCompareOutput.sort{it[2]}
    }

    private createSystemProfileFromExcelDataBody(name, fileName, startTable, startColumn){
        SystemProfile systemProfile = new SystemProfile(name)
        def excelData
        try {
            excelData = new ExcelFileReader(fileName).getBodyRows()
        } catch (Exception e) {
            reporterLogLn("########## Warning")
            reporterLogLn("########## Warning")
            reporterLogLn("Exception <$e>")
            reporterLogLn("Could not find file <$fileName>")
            reporterLogLn("Assuming empty file!")
            return systemProfile
        }
        def excelDataBody = []
        if(!startTable.isEmpty()){
            excelData = excelData.findAll{it[0]["excelBodyMap"]["Table"] == startTable}
        }
        if(!startColumn.isEmpty()){
            excelData = excelData.findAll{it[0]["excelBodyMap"]["Column"] == startColumn}
        }
        excelData.each {
            def excelBodyMap= it.excelBodyMap
            def excelBodyRow = []
            headersExcel.each {
                excelBodyRow.add(excelBodyMap[it][0])
            }
            excelDataBody.add(excelBodyRow)
            systemProfile.add(excelBodyRow)
        }
        systemProfile.print()
        return systemProfile
    }

    private SystemProfile createSystemProfileFromDatabase(name, sourceDb, tablesQuery){
        def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, tablesQuery, 0, sourceDb)

        SystemProfile systemProfile = new SystemProfile(name)
        def excelDataBody = []
        sourceDbResult.each { dbRow->
            def excelBodyRow = []
            headersDb.each {header->
                def field = dbRow[header]
                if(header  == 'SYSTEMNAME'){
                    field = name.capitalize()
                }
                excelBodyRow.add(field)
            }
            excelDataBody.add(excelBodyRow)
            systemProfile.add(excelBodyRow)
        }
        systemProfile.print()
        return systemProfile
    }
}
