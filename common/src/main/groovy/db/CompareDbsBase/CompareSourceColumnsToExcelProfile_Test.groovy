package db.CompareDbsBase

import base.SystemProfile
import excel.ExcelFileReader
import excel.ExcelFileWriter
import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test
import org.testng.annotations.Optional
import java.text.DecimalFormat

import static dtos.base.Constants.dbRunTypeRows

public class CompareSourceColumnsToExcelProfile_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")
    private final static String AND_QUERY_EXTENSION = "AND_QUERY_EXTENSION"
    def SOURCE_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def MESSAGE = "Comparing tables"
    ArrayList<String> headersExcel = ["System", "Table", "Column", "Type", "Sensitive", "Masking", "Action", "MaskOverride", "MaskOverrideAddon", "MaskExtra", "TargetSizeMinimumDiff", "TargetSizeMaximumDiff", "RunSql", "SearchCriteria", "SearchExtraCondition", "Verify"]
    ArrayList<String> headersDb = ["SYSTEMNAME", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "SENSITIVE", "MASKING", "ACTION", "MASKOVERRIDE", "MaskOverrideAddon"]
    DecimalFormat thousandSeparatorFormat = new DecimalFormat("###,###");

    @Parameters(["systemColumn"] )
    @Test
    public void compareSourceColumnsEqualsToExcelTest(String systemColumn, ITestContext testContext){
        def fileName = "/configFiles/SystemTableColumnSql.sql"
        URL is = this.getClass().getResource(fileName);

        def SOURCE_TABLE_QUERY_ORACLE
        if (is != null) {
            SOURCE_TABLE_QUERY_ORACLE = new File(is.toURI()).text
        }
        super.setup()
        def (ExcelObjectProvider excelObjectProviderMaskAction, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        def dbType = getDbType(sourceDb)
        def tablesQuery
        //TODO: change code to take care in case DB is SqlServer
         tablesQuery = SOURCE_TABLE_QUERY_ORACLE

        super.setSourceSqlHelper(testContext, sourceDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))

        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn("Query: <$tablesQuery>");

        SystemProfile excelSystemProfile = createSystemProfileFromExcelDataBody("Excel file", excelObjectProviderMaskAction.inputFile)
        SystemProfile dbSystemProfile = createSystemProfileFromDatabase(systemColumn, sourceDb, tablesQuery)

        def dbDataCompareOutput = compare(dbSystemProfile, excelSystemProfile)

        File excelFile = new File("C:/tmp/AF_Compare/${system}.DbCompareToExcel.xls")
        File tmpFile   = new File("C:/tmp/AF_Compare/${system}.tmp.DbCompareToExcel.xls")
        ExcelFileWriter excelFileWriter = new ExcelFileWriter(tmpFile.getPath(), "System")
        excelFileWriter.writeHeader(["Source", "Mode", "TableColumn"] + headersExcel);
        excelFileWriter.writeBody(dbDataCompareOutput);
        excelFileWriter.flushAndClose()

        //Remove excel file
        if(excelFile.exists()){
            excelFile.delete()
        }

        reporterLogLn("")
        reporterLogLn("")
        reporterLogLn("#################")
        //excel file couldn't be removed!
        if(excelFile.exists()) {
            println "Check file " + tmpFile.absolutePath
            reporterLogLn("File <$tmpFile> can't be renamed to >$excelFile>")
            reporterLogLn("Check file " + tmpFile.absolutePath)
            log.error("File <$tmpFile> can't be renamed to >$excelFile>")
        }else {
            tmpFile.renameTo(excelFile)
            println "Check file " + excelFile.absolutePath
            reporterLogLn("Check file " + excelFile.absolutePath)
        }
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
            dataCompareOutput.add(["DB", "New"]  + it.value.getValues())

        }
        excelData.getSystemProfileRowsContainingKeys(excelRemovedRows).each {
            dataCompareOutput.add(["Excel", "Removed"]  + it.value.getValues())

        }

        return dataCompareOutput.sort{it[2]}
    }

    private createSystemProfileFromExcelDataBody(name, fileName){
        def excelData = new ExcelFileReader(fileName).getBodyRows()
        def excelDataBody = []
        SystemProfile systemProfile = new SystemProfile(name)
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
                    field = name
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
