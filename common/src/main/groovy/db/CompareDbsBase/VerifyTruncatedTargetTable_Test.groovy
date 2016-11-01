package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeFirstRow

public class VerifyTruncatedTargetTable_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String targetSql;
    private String targetDbOwner;
    private String atgard;
    private final static String VERIFY_TRUNCATED_TABLE_QUERY = "SELECT COUNT(*) COUNT_ FROM %s"
    def TARGET_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE table_name = '%s' AND NOT table_name IN (select view_name from all_views) AND OWNER = '%s'"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns WHERE table_name = '%s'"
    def table

    public VerifyTruncatedTargetTable_Test(targetDb, system, table, atgard) {
        super.setup()
        this.targetDb = targetDb
        this.atgard = atgard
        this.table = table
        targetDbOwner = settings."$targetDb".owner
        targetSql = "-- Verify truncated table size <$table> in system <$system> is zero\n"
        targetSql += String.format(VERIFY_TRUNCATED_TABLE_QUERY, "$table")

        println "$targetSql "
    }

    @Test
    public void verifyTruncatedTargetTest(ITestContext testContext){
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))
        row++
        reporterLogLn("Row: <$row> TRUNCATE TABLE ");
        reporterLogLn("targetDb:  <$targetDb> ");
        reporterLogLn("table: <$table> ");
        reporterLogLn("Atgard:    <$atgard> ");
        reporterLogLn("targetSql: <$targetSql> ");
        reporterLogLn("#########")

        //Search for table before executing, skip if table not exists
        def targetTableSql = String.format(TARGET_TABLE_QUERY_ORACLE, table, targetDbOwner.toUpperCase())
        if(getDbType(targetDb).equals("sqlserver")){
            targetTableSql = String.format(TARGET_TABLE_QUERY_SQLSERVER, table)
        }
        def dbResult = getDbResult(targetDbSqlDriver, targetTableSql, dbRunTypeFirstRow)
        if(dbResult == null){
            Reporter.log("Table <$table> does not exist, skipping the test")
            Reporter.log("targetTableSql <$targetTableSql>")
            throw new SkipException("Table <$table> does not exist")
        }

        dbResult = getDbResult(targetDbSqlDriver, targetSql, dbRunTypeFirstRow)
        def count = dbResult["COUNT_"]
        reporterLogLn("Table count: <$count>")
        tangAssert.assertEquals(count, 0, "Tabellen ska vara tom", "Tabellen har  <$count> rader, förväntat är <0>");

    }

}
