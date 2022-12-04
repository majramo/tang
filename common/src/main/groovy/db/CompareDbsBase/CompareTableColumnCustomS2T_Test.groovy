package db.CompareDbsBase

import base.AnySqlCompareTest
import dtos.base.SqlHelper
import excel.ExcelObjectProvider
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.testng.ITestContext
import org.testng.SkipException
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import java.text.DecimalFormat

import static dtos.base.Constants.CompareType.DIFF
import static dtos.base.Constants.dbRunTypeFirstRow
import static dtos.base.Constants.dbRunTypeRows

public class CompareTableColumnCustomS2T_Test extends AnySqlCompareTest{
    private final static Logger log = LogManager.getLogger("CSC  ")

    def SOURCE_TABLE_QUERY_ORACLE = """SELECT DISTINCT '''' || table_name || '_'  || column_name || '''' || ',' value
FROM USER_TAB_COLS
Where Not Table_Name In (Select View_Name From All_Views)
--and rownum <5
And Not Table_Name Like '---\$\$\$---'
And Nullable = 'N'
Order by 1"""

    def alter1 = "'ALTER TABLE ' || table_name || ' MODIFY (' || column_name||' NULL);' "
    def alter2 = "'ALTER TABLE ' || table_name || ' MODIFY (' || column_name||' NOT NULL ENABLE);'"
    def selectTarget = "'--SELECT ID || '','''  || ' FROM ' || table_name || ' WHERE ' || column_name ||' IS NULL;'"
    def selectSource = "'--SELECT ID , ' || column_name  || ' FROM ' || table_name || ' WHERE ID IN ();'"
    def updateTargte = "'--UPDATE ' || table_name || ' SET ' || column_name || ' = ' || '''' || '-' || '''' || ' WHERE ID IN ();'"
    def TARGET_TABLE_QUERY_ORACLE
    def SOURCE_TABLE_QUERY_SQLSERVER = SOURCE_TABLE_QUERY_ORACLE
    def TARGET_TABLE_QUERY_SQLSERVER = TARGET_TABLE_QUERY_ORACLE
    def MESSAGE = "Comparing columns should have zero differences"

    DecimalFormat thousandSeparatorFormat = new DecimalFormat("###,###");
    def sourceSizeOut
    def targetSizeOut
    def diffCountOut
    @Parameters(["systemColumn", "excelModifiedTablesOnly"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String systemColumn, @Optional("false")boolean excelModifiedTablesOnly, ITestContext testContext) {
        super.setup()
        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        TARGET_TABLE_QUERY_ORACLE = "SELECT DISTINCT \n" +
                "$alter1 ||\n" +
                "$alter2 ||\n" +
                "$selectTarget ||\n" +
                "$selectSource ||\n" +
                "$updateTargte value\n" +
                "FROM USER_TAB_COLS\n" +
                "WHERE NOT table_name IN (select view_name from all_views) \n" +
                "And Not Table_Name Like '---\$\$\$---' \n" +
                "And NOT Nullable = 'N' "


        def sourceTableSql = SOURCE_TABLE_QUERY_ORACLE
        def targetTableSql = TARGET_TABLE_QUERY_ORACLE
        if (getDbType(sourceDb).equals("sqlserver")) {
            sourceTableSql = SOURCE_TABLE_QUERY_SQLSERVER
        }
        if (getDbType(targetDb).equals("sqlserver")) {
            targetTableSql = TARGET_TABLE_QUERY_SQLSERVER
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb), getDbType(targetDb)))

        reporterLogLn("Source: <$sourceDb>\n");
        reporterLogLn("Target: <$targetDb>\n");
        reporterLogLn("Source query: <\n$sourceTableSql\n>");

        def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
        def sourceEntriesToCheck = sourceDbResult.size()
        def maximuQueryValuesInSelect = 1000
        if (maximuQueryValuesInSelect > sourceEntriesToCheck) {
            maximuQueryValuesInSelect = sourceEntriesToCheck
        }
        def values = ""
        if (maximuQueryValuesInSelect > 1) {
        sourceDbResult[0..maximuQueryValuesInSelect - 1].each {
            values += it.value + "\n"
           }
        }
        def targetDbResult
        if(values.size() == 0){
            reporterLogLn("Target query: <\n$sourceTableSql\n>");
            targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, sourceTableSql, 0, targetDb)
            if(targetDbResult.size() == 0){
                reporterLogLn("Source result: <${sourceDbResult.size()}>");
                reporterLogLn("Target result: <${targetDbResult.size()}>");
                reporterLogLn("Source and target have no results!")
                tangAssert.assertTrue(sourceDbResult.size() == targetDbResult.size(), MESSAGE, MESSAGE)
                return
            }
            reporterLogLn("Skipped: Nothing in Source to Compare <$values> Check target manually!")
            reporterLogLn("Target query: <\n$targetTableSql\n>");
            throw new SkipException("Test is skipped: Nothing in Source to Compare <$values> Check target manually!")
        }
        values = values[0..values.length()-3]
        targetTableSql += "And Table_Name || '_'  || Column_Name In (\n$values\n)"

        reporterLogLn("Target query: <$targetTableSql>");
        targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, targetTableSql, 0, targetDb)

        def targetDbResultDiff = targetDbResult.size()

        def numberOfTablesToCheckColumn = (settingsHelper.settings.numberOfTablesToCheckColumn).toString()
        numberOfTablesToCheckColumn = Integer.parseInt(numberOfTablesToCheckColumn)
        reporterLogLn("");
        reporterLogLn("Number of entries from source to check: <$sourceEntriesToCheck>");
        reporterLogLn("Number of differences in target: <$targetDbResultDiff>");

        /*
        If numberOfTablesToCheckColumn parameter is set then the db result set is reduced to this size before compare id executed
         */
        if(targetDbResultDiff > 0){
            if(targetDbResultDiff < numberOfTablesToCheckColumn) {
                numberOfTablesToCheckColumn = targetDbResultDiff
            }
        }else{
            numberOfTablesToCheckColumn = 0
        }
        if(targetDbResultDiff > 0) {
            targetDbResult[0..numberOfTablesToCheckColumn - 1].eachWithIndex { entry, int i ->
                def k = i + 1
                def v = entry.value.replaceAll(/\{/, '').replaceAll(/\}/, '')
                def v1 = v.replaceAll(";", ";\n")
                reporterLogLn("")
                reporterLogLn("-- $k:$targetDbResultDiff")
                reporterLogLn("$v1")
            }
        }

        tangAssert.assertTrue(targetDbResultDiff == 0, MESSAGE, MESSAGE)

    }

    private compareTableSizes(sourceDb, SqlHelper sourceDbSqlDriver, sourceDbResult, targetDb, targetDbSqlDriver, targetDbResult, String system, String inputFile) {
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
            def icon = " "
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
                def dbSourceSizeResult = sourceDbSqlDriver.sqlConRun(" $sourceDb", dbRunTypeFirstRow, sqlSourceTarget, 0, sourceDb)
                sourceSize = dbSourceSizeResult["COUNT_"]
            }catch (Exception e){
                loopException = true
                noExceptionAtRun = false
                str = aggregate(str, "Exception i source <$sourceDb> $e")
            }
            sourceSizeOut = thousandSeparatorFormat.format(new BigDecimal(sourceSize))

            str = aggregate(str, "Source size <$sourceSizeOut>")
            try {
                def dbTargetSizeResult = targetDbSqlDriver.sqlConRun("$targetDb", dbRunTypeFirstRow, sqlSourceTarget, 0, targetDb)
                targetSize = dbTargetSizeResult["COUNT_"]
                targetSizeOut = thousandSeparatorFormat.format(new BigDecimal(targetSize))
            }catch (Exception e){
                loopException = true
                noExceptionAtRun = false
                str = aggregate(str, "Exception i target <$targetDb> $e")
            }
            str = aggregate(str, "target size <$targetSizeOut>")
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
                icon = "-"
                if (targetSize > 0 || loopException) {
                    diffCount = targetSize
                    diffCountOut = thousandSeparatorFormat.format(new BigDecimal(diffCount))
                    totalDiffCount += diffCount
                    icon = "T"
                    numberOfTableDiff++
                    nok = aggregate(nok, "$str a. Table $table has <$targetSizeOut> rows, expected to be truncated\n\n")
                    reporterLogLn("$icon D " + "$diffCountOut".padLeft(12) + " | S " + "$sourceSizeOut".padLeft(12) + " | T " + "$targetSizeOut".padLeft(12)+ " | " + row.padRight(45) + " * should be truncated" )
                } else {
                    ok = aggregate(ok, "$str a. Table $table has <$targetSizeOut> rows as expected, is truncated\n\n")
                    reporterLogLn("$icon D " + "$diffCountOut".padLeft(12) + " | S " + "$sourceSizeOut".padLeft(12) + " | T " + "$targetSizeOut".padLeft(12)+ " | " + row.padRight(45) + " * is truncated" )
                }

            }else {
                diffCount = (sourceSize - targetSize).abs()
                diffCountOut = thousandSeparatorFormat.format(new BigDecimal(diffCount))
                def SourceTargetMax = sourceSize
                if (targetSize > sourceSize) {
                    SourceTargetMax = targetSize
                }
                float diffCountPercent = 0
                if (SourceTargetMax > 0) {
                    diffCountPercent = (100 * diffCount / SourceTargetMax).abs()
                }
//                reporterLogLn(row.padRight(25) + " <D " + "$diffCount>".padLeft(12) + " <S " + "$sourceSize>".padLeft(12) + " <T " + "$targetSize>".padLeft(12))

                totalDiffCount += diffCount


                if(SourceTargetMax > 0) {
                    totalDiffCountExpected += (expectedMaximumDiff * SourceTargetMax / 100).round(0)
                }
                def expectedMaximumDiff_Ok  = false
                if (expectedMaximumDiff > 0) {
                    if (diffCountPercent > expectedMaximumDiff || loopException) {
                        icon = "*"
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str Table $table has <$diffCountOut> diff, <$diffCountPercent %>, expected maximum diff in target was <$expectedMaximumDiff %>\n\n")
                    } else {
                        ok = aggregate(ok, "$str Table $table has <$diffCountOut> diff, <$diffCountPercent %>, expected maximum diff in target was <$expectedMaximumDiff %>\n\n")

                    }
                } else {
                    if (diffCountPercent > 0 || loopException) {
                        icon = "*"
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str table $table has <$diffCountOut> diff, <$diffCountPercent>, expected maximum diff in target was <0.0 %>\n\n")
                    } else {
                        expectedMaximumDiff_Ok = true
                    }
                }
                reporterLogLn("$icon D " + "$diffCountOut".padLeft(12) + " | S " + "$sourceSizeOut".padLeft(12) + " | T " + "$targetSizeOut".padLeft(12)+ " | " + row.padRight(25) )
                def expectedMinimumDiff_Ok = false
                if (expectedMinimumDiff > 0) {
                    if (diffCountPercent < expectedMinimumDiff || loopException) {
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str a. Table $table has <$diffCountOut> diff, <$diffCountPercent %>, expected minimum diff in target was <$expectedMinimumDiff %>\n\n")
                    } else {
                        ok = aggregate(ok, "$str b. Table $table has <diffCountOut> diff, <$diffCountPercent %>, expected minimum diff in target was <$expectedMinimumDiff %>\n\n")
                    }

                } else {
                    if (diffCountPercent < 0 || loopException) {
                        numberOfTableDiff++
                        nok = aggregate(nok, "$str c. table $table has <$diffCountOut> diff, <$diffCountPercent %>, expected minimum diff in target was <0.0 %>\n\n")
                    } else {
                        expectedMinimumDiff_Ok = true
                    }
                }
                if (expectedMaximumDiff_Ok || expectedMinimumDiff_Ok){
                    ok = aggregate(ok, str)
                }
            }

            //Comparing

            numberOfTablesChecked++

        }
        if (numberOfTableDiff > 0 ){
            def totalDiffCountOut = thousandSeparatorFormat.format(new BigDecimal(totalDiffCount))
            reporterLogLn("Tables with diff <$numberOfTableDiff>")
            reporterLogLn("Total diff count <$totalDiffCountOut>")
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
