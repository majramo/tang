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
import static dtos.base.Constants.CompareType.DIFF

public class CompareSizeCustomS2T_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")

    def SOURCE_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE NOT table_name IN (select view_name from all_views) AND OWNER = '%s' ORDER BY 1"
    def TARGET_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE NOT table_name IN (select view_name from all_views) AND OWNER = '%s' ORDER BY 1"
    def SOURCE_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def MESSAGE = "Comparing tables"



    @Parameters(["systemColumn", "numberOfTablesToCheckColumn", "inputFileColumn"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String systemColumn, @Optional("0") int numberOfTablesToCheckColumn, @Optional("") String inputFileColumn, ITestContext testContext){
        super.setup()

        def targetDb = systemColumn.toLowerCase() + "_Target"
        def sourceDb = systemColumn.toLowerCase() + "_Source"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()

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

        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn("Target: <$targetDb>");
        reporterLogLn("Number of tables to check: <$numberOfTablesToCheckColumn>\n");

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
            (diffCount, totalDiffCountExpected, noExceptionAtRun)  = compareTableSizes(sourceDb, sourceDbSqlDriver, sourceDbResult[0..sourceDbResultTableToCheck], targetDb, targetDbSqlDriver, targetDbResult[0..targetDbResultTableToCheck], systemColumn, inputFileColumn)
        }else{
            (diffCount, totalDiffCountExpected, noExceptionAtRun)  = compareTableSizes(sourceDb, sourceDbSqlDriver, sourceDbResult, targetDb, targetDbSqlDriver, targetDbResult, system, inputFileColumn)
        }
            tangAssert.assertTrue(noExceptionAtRun, "No exception", "Got exception")
            tangAssert.assertEquals(diffCount, totalDiffCountExpected, "$MESSAGE: should have no diff", "$MESSAGE: diffCount $diffCount <> $totalDiffCountExpected ")

    }

    private compareTableSizes(sourceDb, SqlHelper dbDriverSource, sourceDbResult, targetDb, dbDriverTarget, targetDbResult, String system, String inputFile) {
        int totalDiffCount = 0
        float expectedMaximumDiff = 0
        float expectedMinimumDiff = 0
        Map truncateTables = [:]
        Map expectedMaximumDiffValues = [:]
        Map expectedMinimumDiffValues = [:]
        float totalDiffCountExpected = 0
        def dbSourceResultCount = sourceDbResult.size
        def dbTargetResultCount = targetDbResult.size
        def uniqueDbResult = (sourceDbResult + targetDbResult).unique()
//        uniqueDbResult = (targetDbResult - sourceDbResult  ).unique()
        def dbUniqeResultCount = uniqueDbResult.size
        reporterLogLn("Number of tables to check in $sourceDb <$dbSourceResultCount>")
        reporterLogLn("Number of tables to check in $targetDb <$dbTargetResultCount>")
        reporterLogLn("Number of tables to check in both <$dbUniqeResultCount>\n")
        def nok = aggregate("", "\n#####################\n")
        nok = aggregate(nok, "These comparasions were not ok:\n")

        ArrayList<Object[][]> excelBodyRows
        if(inputFile != ""){
            //Tables that have targetSize overRide TargetSize
            ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
            excelObjectProvider.addColumnsToRetriveFromFile(["Table", "Action"])
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "Truncate")
            excelBodyRows = excelObjectProvider.getGdcRows()
            excelObjectProvider.printRow(excelBodyRows.unique(), ["System", "Table", "Truncate"])
            excelBodyRows.unique().each{
               truncateTables[it["Table"] ] = "Truncate"
            }

            excelObjectProvider = new ExcelObjectProvider(inputFile)
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
            excelObjectProvider.addColumnsToRetriveFromFile(["Table", "Action"])
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("TargetSizeMaximumDiff", "-", DIFF)
            excelBodyRows = excelObjectProvider.getGdcRows()
            excelObjectProvider.printRow(excelBodyRows.unique(), ["System", "Table", "TargetSizeMaximumDiff"])

            excelBodyRows.unique().each{
                expectedMaximumDiffValues[it["Table"] ] = it["TargetSizeMaximumDiff"]
            }

            excelObjectProvider = new ExcelObjectProvider(inputFile)
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
            excelObjectProvider.addColumnsToRetriveFromFile(["Table", "Action"])
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("TargetSizeMinimumDiff", "-", DIFF)
            excelBodyRows = excelObjectProvider.getGdcRows()
            excelObjectProvider.printRow(excelBodyRows.unique(), ["System", "Table", "TargetSizeMinimumDiff"])

            excelBodyRows.unique().each{
                expectedMinimumDiffValues[it["Table"] ] = it["TargetSizeMinimumDiff"]
            }



        }
        def ok = aggregate("", "\n#####################\n")
        ok = aggregate(ok, "These comparasions were ok:\n")
        BigDecimal  diffCount = 0
        def numberOfTableDiff = 0
        def numberOfTablesChecked = 0
        boolean noExceptionAtRun = true
        uniqueDbResult.eachWithIndex { it, i ->
            def j = i + 1
            boolean  loopException = false
            expectedMaximumDiff = 0
            expectedMinimumDiff = 0
            def table = it[0]
            def row = "$j:$dbSourceResultCount $table"
            log.info("$row")
            def str =""
            str = aggregate(str, "$j:$dbSourceResultCount table <$table>")

            String sqlSourceTarget = "SELECT COUNT(1) AS count_ FROM $table"
            str = aggregate(str, "Sql <$sqlSourceTarget>")

            def sourceSize = 0
            def targetSize = 0
            try {
                def dbSourceSizeResult = dbDriverSource.sqlConRun(" $sourceDb", dbRunTypeFirstRow, sqlSourceTarget, 0, sourceDb)
                sourceSize = dbSourceSizeResult["COUNT_"]
            }catch (Exception e){
                loopException = true
                noExceptionAtRun = false
                str = aggregate(str, "Exception i source <$sourceDb> $e")
            }
            str = aggregate(str, "Source size <$sourceSize>")
            try {
                def dbTargetSizeResult = dbDriverTarget.sqlConRun("$targetDb", dbRunTypeFirstRow, sqlSourceTarget, 0, targetDb)
                targetSize = dbTargetSizeResult["COUNT_"]
            }catch (Exception e){
                loopException = true
                noExceptionAtRun = false
                str = aggregate(str, "Exception i target <$targetDb> $e")
            }
            str = aggregate(str, "target size <$targetSize>")
            String truncateTable = truncateTables[table]
            String expectedMaximumDiffTableValue = expectedMaximumDiffValues[table]
            String expectedMinimumDiffTableValue = expectedMinimumDiffValues[table]
            if(truncateTable != null) {
                str = aggregate(str, "Truncate")
            }
            if(expectedMinimumDiffTableValue != "" && expectedMinimumDiffTableValue != null) {
                expectedMinimumDiff = (Float.parseFloat(expectedMinimumDiffTableValue) * 100).trunc(2)
                str = aggregate(str, "Expected minimum diff value <$expectedMinimumDiff %>")
            }
            if(expectedMaximumDiffTableValue != "" && expectedMaximumDiffTableValue != null) {
                expectedMaximumDiff = (Float.parseFloat(expectedMaximumDiffTableValue) * 100).trunc(2)
                str = aggregate(str, "Expected maximum diff value <$expectedMaximumDiff %>")
            }

            //Comparing
            if(truncateTables[table]){
                if (targetSize > 0 || loopException) {
                    numberOfTableDiff++
                    nok = aggregate(nok, "$str a. Table $table has <$targetSize> rows, expected to be truncated\n\n")
                } else {
                    ok = aggregate(ok, "$str a. Table $table has <$targetSize> rows as expected, is truncated\n\n")
                }

            }else {
                diffCount = (sourceSize - targetSize).abs()
                def SourceTargetMax = sourceSize
                if (targetSize > sourceSize) {
                    SourceTargetMax = targetSize
                }
                float diffCountPercent = 0
                if (SourceTargetMax > 0) {
                    diffCountPercent = (100 * diffCount / SourceTargetMax).abs()
                }
                reporterLogLn(row.padRight(25) + " <D " + "$diffCount>".padLeft(12) + " <S " + "$sourceSize>".padLeft(12) + " <T " + "$targetSize>".padLeft(12))

                totalDiffCount += diffCount


                if(SourceTargetMax > 0) {
                    totalDiffCountExpected += (expectedMaximumDiff * SourceTargetMax / 100).round(0)
                }
                if (expectedMaximumDiff > 0) {
                    //Todo: Att hantera vid generering då vi kan utöka och få en diff >100%
                    if (diffCountPercent > expectedMaximumDiff || loopException) {
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str Table $table has <$diffCount> diff, <$diffCountPercent %>, expected maximum diff in target was <$expectedMaximumDiff %>\n\n")
                    } else {
                        ok = aggregate(ok, "$str Table $table has <$diffCount> diff, <$diffCountPercent %>, expected maximum diff in target was <$expectedMaximumDiff %>\n\n")
                    }
                } else {
                    if (diffCountPercent > 0 || loopException) {
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str table $table has <$diffCount> diff, <$diffCountPercent>, expected maximum diff in target was <0.0 %>\n\n")
                    } else {
                        ok = aggregate(ok, str)
                    }
                }
                if (expectedMinimumDiff > 0) {
                    //Todo: Att hantera vid generering då vi kan utöka och få en diff >100%
                    if (diffCountPercent < expectedMinimumDiff || loopException) {
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str a. Table $table has <$diffCount> diff, <$diffCountPercent %>, expected minimum diff in target was <$expectedMinimumDiff %>\n\n")
                    } else {
                        ok = aggregate(ok, "$str b. Table $table has <$diffCount> diff, <$diffCountPercent %>, expected minimum diff in target was <$expectedMinimumDiff %>\n\n")
                    }

                } else {
                    if (diffCountPercent < 0 || loopException) {
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str c. table $table has <$diffCount> diff, <$diffCountPercent %>, expected minimum diff in target was <0.0 %>\n\n")
                    } else {
                        ok = aggregate(ok, str)
                    }
                }
            }
            //Comparing

            numberOfTablesChecked++

        }
        if (numberOfTableDiff > 0 ){
            reporterLogLn("Tables with diff <$numberOfTableDiff>")
            reporterLogLn("Total diff count <$totalDiffCount>")
            reporterLogLn(nok)
        }
        if(settings.printOkCheck == true){
            reporterLogLn(ok)
        }
        reporterLogLn("#####################")
        reporterLogLn("Number of tabels checked <$numberOfTablesChecked>")
        reporterLogLn("Number of tabels with diff <$numberOfTableDiff>")
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
