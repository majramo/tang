package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test

public class TruncateTargetTable_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String targetSql;
    private String action;
//    private final static String TRUNCATE_TABLE_QUERY = "SELECT COUNT(*) COUNT_ FROM %s"
    private final static String TRUNCATE_TABLE_QUERY = "TRUNCATE TABLE %s"

    public TruncateTargetTable_Test(targetDb, system, table, action) {
        super.setup()
        this.targetDb = targetDb
        this.action = action

        String dbTargetOwner = settings."$targetDb".owner
        targetSql = "-- Truncate table <$table> in system <$system>\n"
        targetSql += String.format(TRUNCATE_TABLE_QUERY, "$table")

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
        reporterLogLn("Target Sql:\n$targetSql\n");
        truncate(targetDbSqlDriver, targetSql)
    }

}
