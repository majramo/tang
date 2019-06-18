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

class ReportSourceTableSizes_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")

    def SOURCE__GET_TABLE_NAMES_QUERY_ORACLE = "SELECT DISTINCT table_name FROM user_tab_cols WHERE NOT table_name IN (select view_name from all_views)  And NOT Table_Name Like 'EXT___---'  And NOT Table_Name Like 'TMPEXT___---' And Not Table_Name Like '---\\\$\\\$\\\$---' ORDER BY 1"
    def SOURCE__GET_TABLE_SIZE_QUERY = "SELECT COUNT(1) COUNT_  FROM %s "
    def SOURCE__GET_TABLE_NAMES_QUERY_SQLSERVER = "SELECT DISTINCT '[' + Table_name + ']' Table_name FROM Information_schema.columns ORDER BY 1"

    @Parameters(["systemColumn", "excelModifiedTablesOnly"] )
    @Test
    void compareSourceTableSizeEqualsTargetTableSizeTest(String systemColumn, @Optional("false")boolean excelModifiedTablesOnly, ITestContext testContext){
        super.setup()

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        def sourceTableSql = SOURCE__GET_TABLE_NAMES_QUERY_ORACLE.replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)
        if(getDbType(sourceDb).equals("sqlserver")){
            sourceTableSql = SOURCE__GET_TABLE_NAMES_QUERY_SQLSERVER
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        if(excelModifiedTablesOnly) {
            reporterLogLn("Tables are from excelfile $excelObjectProvider.inputFile")
        }else{
            reporterLogLn("Tables are from database $system")
        }

        reporterLogLn("Source: <$sourceDb>")

        def numberOfTablesToCheckColumn = (settingsHelper.settings.numberOfTablesToCheckColumn).toString()
        if(numberOfTablesToCheckColumn != "[:]" && numberOfTablesToCheckColumn != "") {
            numberOfTablesToCheckColumn = Integer.parseInt(numberOfTablesToCheckColumn)
        }else{
            numberOfTablesToCheckColumn = 0
        }
        reporterLogLn("Max number of tables to check: <$numberOfTablesToCheckColumn>\n")
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
            def sourceTableSizeSql = String.format(SOURCE__GET_TABLE_SIZE_QUERY, tableName)
            def sourceDbTableSizeResult
            def tableNameReport = tableName.replaceAll(/\[|\]/,'')
            try{
                    sourceDbTableSizeResult = sourceDbSqlDriver.sqlConRun("Get table <$tableName> size from $sourceDb", dbRunTypeRows, sourceTableSizeSql, 0, sourceDb)
                    sizeMap[tableNameReport] = new BigInteger(sourceDbTableSizeResult["COUNT_"][0].toString(), 10)
            }catch(Exception e){
                if(e.toString().contains("ORA-06564") || e.toString().contains("ORA-29913")){
                    reporterLogLn("Exception ORA-06564: can't find <$tableNameReport>")
                    sizeMap["* n/a $tableNameReport"] = 0
                }else{
                    throw e
                }
            }
        }
        reporterLogLn("Sort by size:")
        int i = 1
        sizeMap.sort{ it.value }.reverseEach{tableName, size->
            reporterLogLn( String.format("%04d:", i++) + String.format("%,d", size).padLeft(20) + " $tableName")
        }

        reporterLogLn("\n\nSort by name:")
        i = 1
        sizeMap.sort().each{tableName, size->
            reporterLogLn( String.format("%04d:", i++) + String.format("%,d", size).padLeft(20) + " $tableName")
        }
    }

}
