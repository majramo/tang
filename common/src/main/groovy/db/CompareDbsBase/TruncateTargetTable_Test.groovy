package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test

public class TruncateTargetTable_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String table;
    private String targetSql;
    private String targetCountSql;
    private String action;
//    private final static String TRUNCATE_TABLE_QUERY = "SELECT COUNT(*) COUNT_ FROM %s"
//    private final static String TRUNCATE_TABLE_QUERY = "TRUNCATE TABLE %s"
    private final static String TRUNCATE_TABLE_QUERY = "TRUNCATE TABLE %s"

    public TruncateTargetTable_Test(targetDb, system, table, action) {
        super.setup()
        this.table = table
        this.targetDb = targetDb
        this.action = action

        String dbTargetOwner = settings."$targetDb".owner
        targetCountSql = "SELECT COUNT(1) COUNT_ FROM $table"
        targetSql = "-- Truncate table <$table> in system <$system>\n"
        targetSql += String.format(TRUNCATE_TABLE_QUERY, "$table")

        reporterLogLn("$targetSql")
        println "$targetSql "
    }

    @Test
    public void truncateTargetTest(ITestContext testContext){
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))

        row++
        reporterLogLn("Row: <$row> TRUNCATE TABLE ");
        reporterLogLn("Target Db: <$targetDb> ");
        reporterLogLn("Action:    <$action> ");
        reporterLogLn("Target count Sql:\n$targetCountSql\n");
        reporterLogLn("Target Sql:\n$targetSql\n");
        //Check if table has rows
        def targetResult = getTargetDbRowsResult(targetCountSql)
        if(targetResult["COUNT_"][0] != 0){
            truncate(targetDbSqlDriver, targetSql)
        }else{
            skipTest("Table: <$table> has no records <$targetResult>\n");
        }
    }

}
