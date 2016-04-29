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



    @Parameters(["dbSource", "dbTarget", "numberOfTablesToCheck"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String dbSource, String dbTarget, @Optional("0") int numberOfTablesToCheck, ITestContext testContext){
        super.setup()
        reporterLogLn("Source: <$dbSource> ");
        reporterLogLn("Target: <$dbTarget ");
        reporterLogLn("numberOfTablesToCheck: <$numberOfTablesToCheck>");

        String dbSourceOwner = settings."$dbSource".owner
        String dbTargetOwner = settings."$dbTarget".owner
        def sourceTableSql = String.format(SOURCE_TABLE_QUERY_ORACLE, dbSourceOwner.toUpperCase())
        def targetTableSql = String.format(TARGET_TABLE_QUERY_ORACLE, dbSourceOwner.toUpperCase())
        if(getDbType(dbSource).equals("sqlserver")){
            sourceTableSql = String.format(SOURCE_TABLE_QUERY_SQLSERVER, dbSourceOwner.toUpperCase())
        }
        if(getDbType(dbTarget).equals("sqlserver")){
            targetTableSql = String.format(TARGET_TABLE_QUERY_SQLSERVER, dbSourceOwner.toUpperCase())
        }
        super.setSourceSqlHelper(testContext, dbSource)
        super.setTargetSqlHelper(testContext, dbTarget)
        reporterLogLn(reporterHelper.addIcons(getDbType(dbSource), getDbType(dbTarget)))
        def dbSourceResult = sourceSqlDriver.sqlConRun("Get data from $dbSource", dbRunTypeRows, sourceTableSql, 0, dbSource)
        def dbTargetResult = targetSqlDriver.sqlConRun("Get data from $dbTarget", dbRunTypeRows, targetTableSql, 0, dbTarget)

        int diffCount
        if(numberOfTablesToCheck > 0){
            diffCount = compareTableSizes(dbSource, sourceSqlDriver, dbSourceResult[0..numberOfTablesToCheck], dbTarget, targetSqlDriver, dbTargetResult[0..numberOfTablesToCheck])
        }else{
            diffCount = compareTableSizes(dbSource, sourceSqlDriver, dbSourceResult, dbTarget, targetSqlDriver, dbTargetResult)
        }
        tangAssert.assertEquals(diffCount, 0, "$MESSAGE: ska inte ha några diffar", "$MESSAGE: diffCount $diffCount <> 0 ")

    }

    private int compareTableSizes(dbSource, dbDriverSource, dbSourceResult, dbTarget, dbDriverTarget, dbTargetResult) {
        int totalDiffCount = 0
        def dbSourceResultCount = dbSourceResult.size
        def dbTargetResultCount = dbTargetResult.size
        def uniqueDbResult = (dbSourceResult + dbTargetResult).unique()
        def dbUniqeResultCount = uniqueDbResult.size
        reporterLogLn("Antal tabeller som ska kontrolleras i $dbSource <$dbSourceResultCount>")
        reporterLogLn("Antal tabeller som ska kontrolleras i $dbTarget <$dbTargetResultCount>")
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
//            SqlHelper dbDriverSource = new SqlHelper(null, null, dbSource, settings.dbRun, settings)
//            SqlHelper dbDriverTarget = new SqlHelper(null, null, dbTarget, settings.dbRun, settings)

            String sqlSourceCompare = "SELECT COUNT(1) AS count_ FROM $table"
            str = aggregate(str, "sqlSourceCompare <$sqlSourceCompare>")

            def sourceSize = 0
            def targetSize = 0
            try {
//                def dbSourceResult = dbDriverSource.sqlConRun("$DB_DATA_LOG_INFO $dbNameSource", dbRunTypeRows, "SELECT COUNT(1) AS count_ FROM ACCESS_TYP", 0, dbNameSource)
                def dbSourceSizeResult = dbDriverSource.sqlConRun(" $dbSource", dbRunTypeFirstRow, sqlSourceCompare, 0, dbSource)
                sourceSize = dbSourceSizeResult["COUNT_"]
            }catch (Exception e){
                str = aggregate(str, "Fick exception i source <$dbSource> $e")
            }
            str = aggregate(str, "Source size <$sourceSize>")

            try {
                def dbTargetSizeResult = dbDriverTarget.sqlConRun("$dbTarget", dbRunTypeFirstRow, sqlSourceCompare, 0, dbTarget)
                targetSize = dbTargetSizeResult["COUNT_"]
            }catch (Exception e){
                str = aggregate(str, "Fick exception i target <$dbTarget> $e")
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
