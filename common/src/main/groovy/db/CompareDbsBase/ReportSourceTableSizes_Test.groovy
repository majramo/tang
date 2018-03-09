package db.CompareDbsBase

import base.AnySqlCompareTest
import dtos.base.SqlHelper
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.CompareType.DIFF
import static dtos.base.Constants.dbRunTypeFirstRow
import static dtos.base.Constants.dbRunTypeRows

public class ReportSourceTableSizes_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")

    def SOURCE_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE NOT table_name IN (select view_name from all_views) AND OWNER = '_OWNER_' And NOT Table_Name Like 'EXT___---'  And NOT Table_Name Like 'TMPEXT___---' And Not Table_Name Like '---\\\$\\\$\\\$---' ORDER BY 1"
    def SOURCE_TABLE_SIZE_QUERY_ORACLE = "SELECT COUNT(1) COUNT_  FROM %s "
    def TARGET_TABLE_QUERY_ORACLE = SOURCE_TABLE_QUERY_ORACLE
    def SOURCE_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"



    @Parameters(["systemColumn", "excelModifiedTablesOnly"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String systemColumn, @Optional("false")boolean excelModifiedTablesOnly, ITestContext testContext){
        super.setup()

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        String sourceDbOwner = settings."$sourceDb".owner
        String targetDbOwner = settings."$targetDb".owner
//        def sourceTableSql = String.format(SOURCE_TABLE_QUERY_ORACLE, sourceDbOwner.toUpperCase())
        def sourceTableSql = SOURCE_TABLE_QUERY_ORACLE.replaceAll("_OWNER_", sourceDbOwner.toUpperCase()).replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)
        if(getDbType(sourceDb).equals("sqlserver")){
            sourceTableSql = SOURCE_TABLE_QUERY_SQLSERVER
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        if(excelModifiedTablesOnly) {
            reporterLogLn("Tables are from excelfile $excelObjectProvider.inputFile")
        }else{
            reporterLogLn("Tables are from database $system")
        }

        reporterLogLn("Source: <$sourceDb>");

        def numberOfTablesToCheckColumn = (settingsHelper.settings.numberOfTablesToCheckColumn).toString()
        if(numberOfTablesToCheckColumn != "[:]" && numberOfTablesToCheckColumn != "") {
            numberOfTablesToCheckColumn = Integer.parseInt(numberOfTablesToCheckColumn)
        }else{
            numberOfTablesToCheckColumn = 0
        }
        reporterLogLn("Max number of tables to check: <$numberOfTablesToCheckColumn>\n");
        def tablesSizes = [:]
        if(excelModifiedTablesOnly){
            //read file
            ArrayList<Object[][]> excelBodyRows
            if(excelObjectProvider.inputFile != "") {
                //Tables that have targetSize overRide TargetSize
                excelObjectProvider.addColumnsToRetriveFromFile(["Table", "Action"])
                excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
                excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "-", DIFF)
                excelBodyRows = excelObjectProvider.getGdcRows()
                excelObjectProvider.printRow(excelBodyRows.unique(), ["System", "Table", "Action"])
                excelBodyRows.unique().eachWithIndex {it, i->
                    if(numberOfTablesToCheckColumn > 0){
                        if(i < numberOfTablesToCheckColumn) {
                            tablesSizes[it["Table"]] = 0
                        }
                    }else {
                        tablesSizes[it["Table"]] = 0
                    }
                }
            }
        }else{
            //read database
            def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
            sourceDbResult.eachWithIndex {it, i->
                if(numberOfTablesToCheckColumn > 0){
                    if(i < numberOfTablesToCheckColumn) {
                        tablesSizes[it["TABLE_NAME"]] = 0
                    }
                }else{
                    tablesSizes[it["TABLE_NAME"]] = 0
                }
            }
        }
        reportTableSizes(sourceDb, sourceDbSqlDriver, tablesSizes, system, excelObjectProvider.inputFile)
    }

    private reportTableSizes(sourceDb, SqlHelper sourceDbSqlDriver, tablesSizes, system, String inputFile) {

        reporterLogLn("Number of tables to check in $sourceDb <" + tablesSizes.size() + ">")
        reporterLogLn("")
        def sizeMap = [:]
        tablesSizes.each {tableName, value->
            def sourceTableSizeSql = String.format(SOURCE_TABLE_SIZE_QUERY_ORACLE, tableName)
            def sourceDbTableSizeResult
            try{
                    sourceDbTableSizeResult = sourceDbSqlDriver.sqlConRun("Get table <$tableName> size from $sourceDb", dbRunTypeRows, sourceTableSizeSql, 0, sourceDb)
                    sizeMap[tableName] = new BigInteger(sourceDbTableSizeResult["COUNT_"][0].toString(), 10)
            }catch(Exception e){
                if(e.toString().contains("ORA-06564")){
                    reporterLogLn("Exception ORA-06564: can't find <$tableName>")
                    sizeMap["* n/a $tableName"] = 0
                }else{
                    throw e
                }
            }
        }
        int i = 1
        sizeMap.sort{ it.value }.reverseEach{tableName, size->
            reporterLogLn( String.format("%04d:", i++) + String.format("%,d", size).padLeft(20) + " $tableName")

        }

    }

}
