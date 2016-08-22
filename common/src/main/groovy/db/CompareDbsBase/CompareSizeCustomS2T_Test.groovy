package db.CompareDbsBase

import base.AnySqlCompareTest
import dtos.base.SqlHelper
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test
import org.testng.annotations.Optional

import static dtos.base.Constants.dbRunTypeFirstRow
import static dtos.base.Constants.dbRunTypeRows


public class CompareSizeCustomS2T_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")

    def SOURCE_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE NOT table_name IN (select view_name from all_views) AND OWNER = '%s' ORDER BY 1"
    def TARGET_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE NOT table_name IN (select view_name from all_views) AND OWNER = '%s' ORDER BY 1"
    def SOURCE_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def MESSAGE = "Jämför tabeller"



    @Parameters(["schemaColumn", "numberOfTablesToCheckColumn", "inputFile"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String schemaColumn, @Optional("0") int numberOfTablesToCheckColumn, @Optional("") String inputFile, ITestContext testContext){
        super.setup()

        def targetDb = schemaColumn.toLowerCase() + "_Target"
        def sourceDb = schemaColumn.toLowerCase() + "_Source"
        def system = schemaColumn[0].toUpperCase() + schemaColumn[1..-1].toLowerCase()

        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn("Target: <$targetDb>");
        reporterLogLn("numberOfTablesToCheckColumn: <$numberOfTablesToCheckColumn>");
        String sourceDbOwner = settings."$sourceDb".owner
        String targetDbOwner = settings."$targetDb".owner
        def sourceTableSql = String.format(SOURCE_TABLE_QUERY_ORACLE, sourceDbOwner.toUpperCase())
        def targetTableSql = String.format(TARGET_TABLE_QUERY_ORACLE, targetDbOwner.toUpperCase())
        if(getDbType(sourceDb).equals("sqlserver")){
            sourceTableSql = SOURCE_TABLE_QUERY_SQLSERVER
        }
        if(getDbType(targetDb).equals("sqlserver")){
            targetTableSql = TARGET_TABLE_QUERY_SQLSERVER
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb), getDbType(targetDb)))
        def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
        def targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, targetTableSql, 0, targetDb)

        def diffCount
        def totalDiffCountExpected
        boolean noExceptionAtRun
        def sourceDbResultTableToCheck = sourceDbResult.size()-1
        def targetDbResultTableToCheck = targetDbResult.size()-1
        if(numberOfTablesToCheckColumn > 0){
            if(sourceDbResultTableToCheck > numberOfTablesToCheckColumn){
                sourceDbResultTableToCheck = numberOfTablesToCheckColumn - 1
            }
            if(targetDbResultTableToCheck > numberOfTablesToCheckColumn){
                targetDbResultTableToCheck = numberOfTablesToCheckColumn - 1
            }
            (diffCount, totalDiffCountExpected, noExceptionAtRun)  = compareTableSizes(sourceDb, sourceDbSqlDriver, sourceDbResult[0..sourceDbResultTableToCheck], targetDb, targetDbSqlDriver, targetDbResult[0..targetDbResultTableToCheck], schemaColumn, inputFile)
        }else{
            (diffCount, totalDiffCountExpected, noExceptionAtRun)  = compareTableSizes(sourceDb, sourceDbSqlDriver, sourceDbResult, targetDb, targetDbSqlDriver, targetDbResult, system, inputFile)
        }
            tangAssert.assertTrue(noExceptionAtRun, "Fick exception vid körning", "Fick exception vid körning")
            tangAssert.assertEquals(diffCount, totalDiffCountExpected, "$MESSAGE: ska inte ha några diff/ar", "$MESSAGE: diffCount $diffCount <> $totalDiffCountExpected ")

    }

    private compareTableSizes(sourceDb, SqlHelper dbDriverSource, sourceDbResult, targetDb, dbDriverTarget, targetDbResult, String system, String inputFile) {
        int totalDiffCount = 0
        float expectedDiff = 0
        Map expectdValues = [:]
        float totalDiffCountExpected = 0
        def dbSourceResultCount = sourceDbResult.size
        def dbTargetResultCount = targetDbResult.size
        def uniqueDbResult = (sourceDbResult + targetDbResult).unique()
//        uniqueDbResult = (targetDbResult - sourceDbResult  ).unique()
        def dbUniqeResultCount = uniqueDbResult.size
        reporterLogLn("Antal tabeller som ska kontrolleras i $sourceDb <$dbSourceResultCount>")
        reporterLogLn("Antal tabeller som ska kontrolleras i $targetDb <$dbTargetResultCount>")
        reporterLogLn("Antal tabeller som ska kontrolleras i bägge <$dbUniqeResultCount>")
        def nok = aggregate("", "\n#####################\n")
        nok = aggregate(nok, "Dessa jämförelser var inte ok\n")

        ArrayList<Object[][]> excelBodyRows
        if(inputFile != ""){
            //Tables that have targetSize overRide TargetSize
            ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
            excelObjectProvider.addColumnsToRetriveFromFile(["Tabell", "Atgard"])
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("Atgard", "Trunkera")
            excelBodyRows = excelObjectProvider.getGdcObjects(0)
            excelObjectProvider.printRow(excelBodyRows, ["System", "Tabell", "Trunkera"])
            excelBodyRows.unique().each{
                expectdValues[it["Tabell"] ] = 0
            }

            excelObjectProvider = new ExcelObjectProvider(inputFile)
            excelObjectProvider.addColumnsToRetriveFromFile(["Tabell", "TargetSize"])
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
            excelObjectProvider.addColumnsCapabilitiesNotEmptyToRetrieve("TargetSize")
            excelBodyRows = excelObjectProvider.getGdcObjects(0)
            excelObjectProvider.printRow(excelBodyRows, ["System", "Tabell", "TargetSize"])

            excelBodyRows.unique().each{
                expectdValues[it["Tabell"] ] = it["TargetSize"]
            }


        }
        def ok = aggregate("", "\n#####################\n")
        ok = aggregate(ok, "Dessa jämförelser var ok\n")
        float  diffCount = 0
        def numberOfTableDiff = 0
        def numberOfTablesChecked = 0
        boolean noExceptionAtRun = true
        uniqueDbResult.eachWithIndex { it, i ->
            boolean  loopException = false
            expectedDiff = 0
            def table = it[0]
            log.info("$i:$dbSourceResultCount tabell <$table>")
            def str =""
            str = aggregate(str, "$i:$dbSourceResultCount tabell <$table>")
//            SqlHelper dbDriverSource = new SqlHelper(null, null, sourceDb, settings.dbRun, settings)
//            SqlHelper dbDriverTarget = new SqlHelper(null, null, targetDb, settings.dbRun, settings)

            String sqlSourceCompare = "SELECT COUNT(1) AS count_ FROM $table"
            str = aggregate(str, "sqlSourceCompare <$sqlSourceCompare>")

            def sourceSize = 0
            def targetSize = 0
            try {
//                def sourceDbResult = dbDriverSource.sqlConRun("$DB_DATA_LOG_INFO $dbNameSource", dbRunTypeRows, "SELECT COUNT(1) AS count_ FROM ACCESS_TYP", 0, dbNameSource)
                def dbSourceSizeResult = dbDriverSource.sqlConRun(" $sourceDb", dbRunTypeFirstRow, sqlSourceCompare, 0, sourceDb)
                sourceSize = dbSourceSizeResult["COUNT_"]
            }catch (Exception e){
                loopException = true
                noExceptionAtRun = false
                str = aggregate(str, "Fick exception i source <$sourceDb> $e")
            }
//            sourceSize = sourceSize * 2 //debug
            str = aggregate(str, "Source size <$sourceSize>")

            try {
                def dbTargetSizeResult = dbDriverTarget.sqlConRun("$targetDb", dbRunTypeFirstRow, sqlSourceCompare, 0, targetDb)
                targetSize = dbTargetSizeResult["COUNT_"]
            }catch (Exception e){
                loopException = true
                noExceptionAtRun = false
                str = aggregate(str, "Fick exception i target <$targetDb> $e")
            }
//            targetSize = targetSize *  2 //debug
            str = aggregate(str, "target size <$targetSize>")

            diffCount = (sourceSize - targetSize).abs()
            def SourceTargetMax = sourceSize
            if(targetSize > sourceSize){
                SourceTargetMax = targetSize
            }
            float diffCountPercent = 0
            if(SourceTargetMax > 0){
                diffCountPercent =  (100 * diffCount / SourceTargetMax).abs()
            }
            totalDiffCount += diffCount
            String expectedTableValue = expectdValues[table]
            if(expectedTableValue != "" && expectedTableValue != null ){
                //Om tabellen ska reduceras
                expectedDiff = (Float.parseFloat(expectedTableValue)*100).trunc(2)
                totalDiffCountExpected += (expectedDiff * SourceTargetMax / 100).round(0)
            }
            if(expectedDiff > 0) {
                //Todo: Att hantera vid generering då vi kan utöka och få en diff >100%
                if(sourceSize < targetSize || loopException){
                    nok = aggregate(nok, "$str Tabell $table source <$sourceSize> är mindre än target <$targetSize>\n\n")
                }else {
                    if (diffCountPercent < expectedDiff || loopException) {
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str Tabell $table har <$diffCount> diff/ar, <% $diffCountPercent>, förväntat är target <% $expectedDiff>\n\n")
                    }
                }
            }else{
                if (diffCountPercent > 0 || loopException) {
                    numberOfTableDiff++
                    nok = aggregate(nok, "$str Tabell $table har <$diffCount> diff/ar, <% $diffCountPercent>, förväntat är target <% 0.0>\n\n")
                }else{
                    ok = aggregate(ok, str)
                }
            }
            numberOfTablesChecked++

        }
        if (numberOfTableDiff > 0 ){
            reporterLogLn("Antal tabeller som har diff <$numberOfTableDiff>")
            reporterLogLn("Total antal diff <$totalDiffCount>")
            reporterLogLn(nok)
        }
        if(settings.printOkCheck == true){
            reporterLogLn(ok)
        }
        reporterLogLn("#####################")
        reporterLogLn("#####################")
        reporterLogLn("Antal tabeller som har kontrollerats <$numberOfTablesChecked>")
        reporterLogLn("Antal tabeller som har diff <$numberOfTableDiff>")
        return [totalDiffCount, totalDiffCountExpected, noExceptionAtRun]

    }

    public String aggregate(str, message){
        return str + "$message\n"
    }


    private void reportCompareMessage(message, dbNameSource, dbNameTarget) {
        reporterLogLn("$message")
        reporterLogLn("Source: $dbNameSource")
        reporterLogLn("Target: $dbNameTarget")
        reporterLogLn("")
    }
}
