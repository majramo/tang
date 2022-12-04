package db.CompareDbsBase

import base.SystemProfile
import excel.ExcelFileReader
import excel.ExcelFileWriter
import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test
import org.testng.annotations.Optional
import java.text.DecimalFormat

import static dtos.base.Constants.dbRunTypeRows

public class CompareSourceColumnsToExcelProfile_Test extends AnySqlCompareTest{
    private final static Logger log = LogManager.getLogger("CSC  ")
    private final static String AND_QUERY_EXTENSION = "AND_QUERY_EXTENSION"
    def SOURCE_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def MESSAGE = "Comparing tables"
    ArrayList<String> headersExcel = ["System", "Table", "Column", "Type", "Sensitive", "Masking", "Action", "MaskOverride", "MaskOverrideAddon", "MaskExtra", "TargetSizeMinimumDiff", "TargetSizeMaximumDiff", "RunSql", "SearchCriteria", "SearchExtraCondition", "Verify"]
    ArrayList<String> headersDb = ["SYSTEMNAME", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "SENSITIVE", "MASKING", "ACTION", "MASKOVERRIDE", "MaskOverrideAddon", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "VERIFY"]
    DecimalFormat thousandSeparatorFormat = new DecimalFormat("###,###");
    def newCount = 0
    def removedCount = 0
    def sameCount = 0

    @Parameters(["systemColumn"] )
    @Test
    public void compareSourceColumnsEqualsToExcelTest(String systemColumn, ITestContext testContext){
        def fileName = "/configFiles/AfSystemsProfilerSql.txt"
        URL is = this.getClass().getResource(fileName);

        def SOURCE_TABLE_QUERY_ORACLE
        if (is != null) {
            SOURCE_TABLE_QUERY_ORACLE = new File(is.toURI()).text
        }
        super.setup()
        def (ExcelObjectProvider excelObjectProviderMaskAction, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def compareDataPath = settings.compareDataPath
        if(compareDataPath.size() == 0 || compareDataPath == ""){
            compareDataPath = "."
        }
        File excelFile = new File("$compareDataPath/${system}.DbCompareToExcel.xls")
        File tmpFile   = new File("$compareDataPath/${system}.tmp.DbCompareToExcel.xls")

        def dbType = getDbType(sourceDb)
        def tablesQuery
        //TODO: change code to take care in case DB is SqlServer
         tablesQuery = SOURCE_TABLE_QUERY_ORACLE

        super.setSourceSqlHelper(testContext, sourceDb)
        reporterLogPrint(reporterHelper.addIcons(getDbType(sourceDb)))

        reporterLogPrint("Source: <$sourceDb>");
        reporterLogPrint("Query: <$tablesQuery>");

        SystemProfile excelSystemProfile = createSystemProfileFromExcelDataBody("Excel file", excelObjectProviderMaskAction.inputFile)
        SystemProfile dbSystemProfile = createSystemProfileFromDatabase(systemColumn, sourceDb, tablesQuery)

        def dbDataCompareOutput = compare(dbSystemProfile, excelSystemProfile)

        ExcelFileWriter excelFileWriter = new ExcelFileWriter(tmpFile.getPath(), "System")
        excelFileWriter.writeHeader(["Source", "Mode"] + headersExcel);
        excelFileWriter.writeBody(dbDataCompareOutput);
        excelFileWriter.flushAndClose()

        //Remove excel file
        if(excelFile.exists()){
            excelFile.delete()
        }

        reporterLogPrint("")
        reporterLogPrint("")
        reporterLogPrint("#################")
        //excel file couldn't be removed!
        if(excelFile.exists()) {
            println "Check file " + tmpFile.absolutePath
            reporterLogPrint("File <$tmpFile> can't be renamed to >$excelFile>")
            reporterLogPrint("Check file " + tmpFile.absolutePath)
            log.error("File <$tmpFile> can't be renamed to >$excelFile>")
        }else {
            tmpFile.renameTo(excelFile)
            println "Check file " + excelFile.absolutePath
            reporterLogPrint("Check file " + excelFile.absolutePath)
        }
        reporterLogPrint("Same columns: " + sameCount)
        reporterLogPrint("new columns: " + newCount)
        reporterLogPrint("Removed columns: " + removedCount)
    }

    private compare(SystemProfile dbData, SystemProfile excelData){
        def sameRows = dbData.getSystemProfileKeys().intersect(excelData.getSystemProfileKeys())
        def dbNewRows = dbData.getSystemProfileKeys() - sameRows
        def excelRemovedRows =excelData.getSystemProfileKeys() - sameRows
        def dataCompareOutput = []
        println ("\n$headersExcel")
        sameCount = sameRows.size()
        newCount = dbNewRows.size()
        removedCount = excelRemovedRows.size()
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

    private createSystemProfileFromExcelDataBody(name, fileName){
        SystemProfile systemProfile = new SystemProfile(name)
        def excelData
        try {
            excelData = new ExcelFileReader(fileName).getBodyRows()
        } catch (Exception e) {
            reporterLogPrint("########## Warning")
            reporterLogPrint("########## Warning")
            reporterLogPrint("Exception <$e>")
            reporterLogPrint("Could not find file <$fileName>")
            reporterLogPrint("Assuming empty file!")
            return systemProfile
        }
        def excelDataBody = []
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
                def field = dbRow[header.toUpperCase()]
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
