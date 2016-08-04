package db.CompareDbsBase

import base.AnySqlCompareTest
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



    @Parameters(["sourceDbColumn", "targetDbColumn", "numberOfTablesToCheckColumn"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String sourceDb, String targetDb, @Optional("0") int numberOfTablesToCheckColumn, ITestContext testContext){
        super.setup()
        reporterLogLn("Source: <$sourceDb> ");
        reporterLogLn("Target: <$targetDb ");
        reporterLogLn("numberOfTablesToCheckColumn: <$numberOfTablesToCheckColumn>");

        String sourceDbOwner = settings."$sourceDb".owner
        String targetDbOwner = settings."$targetDb".owner
        def sourceTableSql = String.format(SOURCE_TABLE_QUERY_ORACLE, sourceDbOwner.toUpperCase())
        def targetTableSql = String.format(TARGET_TABLE_QUERY_ORACLE, sourceDbOwner.toUpperCase())
        if(getDbType(sourceDb).equals("sqlserver")){
            sourceTableSql = String.format(SOURCE_TABLE_QUERY_SQLSERVER, sourceDbOwner.toUpperCase())
        }
        if(getDbType(targetDb).equals("sqlserver")){
            targetTableSql = String.format(TARGET_TABLE_QUERY_SQLSERVER, sourceDbOwner.toUpperCase())
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb), getDbType(targetDb)))
        def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
        def targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, targetTableSql, 0, targetDb)

        int diffCount
        if(numberOfTablesToCheckColumn > 0){
            diffCount = compareTableSizes(sourceDb, sourceDbSqlDriver, sourceDbResult[0..numberOfTablesToCheckColumn], targetDb, targetDbSqlDriver, targetDbResult[0..numberOfTablesToCheckColumn])
        }else{
            diffCount = compareTableSizes(sourceDb, sourceDbSqlDriver, sourceDbResult, targetDb, targetDbSqlDriver, targetDbResult)
        }
        tangAssert.assertEquals(diffCount, 0, "$MESSAGE: ska inte ha några diffar", "$MESSAGE: diffCount $diffCount <> 0 ")

    }

    private int compareTableSizes(sourceDb, dbDriverSource, sourceDbResult, targetDb, dbDriverTarget, targetDbResult) {
        int totalDiffCount = 0
        def dbSourceResultCount = sourceDbResult.size
        def dbTargetResultCount = targetDbResult.size
        def uniqueDbResult = (sourceDbResult + targetDbResult).unique()
        def dbUniqeResultCount = uniqueDbResult.size
        reporterLogLn("Antal tabeller som ska kontrolleras i $sourceDb <$dbSourceResultCount>")
        reporterLogLn("Antal tabeller som ska kontrolleras i $targetDb <$dbTargetResultCount>")
        reporterLogLn("Antal tabeller som ska kontrolleras i bägge <$dbUniqeResultCount>")
        def nok = aggregate("", "\n#####################\n")
        nok = aggregate(nok, "Dessa jämförelser var inte ok\n")

        def ok = aggregate("", "\n#####################\n")
        ok = aggregate(ok, "Dessa jämförelser var ok\n")
        def diffCount = 0
        def numberOfTableDiff = 0
        def numberOfTablesChecked = 0
        uniqueDbResult.eachWithIndex { it, i ->
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
                str = aggregate(str, "Fick exception i source <$sourceDb> $e")
            }
            str = aggregate(str, "Source size <$sourceSize>")

            try {
                def dbTargetSizeResult = dbDriverTarget.sqlConRun("$targetDb", dbRunTypeFirstRow, sqlSourceCompare, 0, targetDb)
                targetSize = dbTargetSizeResult["COUNT_"]
            }catch (Exception e){
                str = aggregate(str, "Fick exception i target <$targetDb> $e")
            }
            str = aggregate(str, "target size <$targetSize>")

            diffCount = sourceSize - targetSize
            totalDiffCount += diffCount.abs()
            if (diffCount != 0) {
                numberOfTableDiff++
                nok = aggregate(nok, "$str Tabell $table har <$diffCount> diff/ar\n\n")
            }else{
                ok = aggregate(ok, str)
            }
            numberOfTablesChecked++

        }
        if (totalDiffCount > 0 ){
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
        return totalDiffCount

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
