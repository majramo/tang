package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test

public class UpdateTargetTable_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String action;
    private String targetSql;

    public UpdateTargetTable_Test(targetDb, system, table, action, targetSql) {
        super.setup()
        this.action = action
        this.targetDb = targetDb

        String dbTargetOwner = settings."$targetDb".owner
        this.targetSql = "-- Update table <$table> in system <$system>\n"
        this.targetSql += targetSql

        println this.targetSql
    }

    @Test
    public void updateTargetTest(ITestContext testContext){
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))

        row++
        reporterLogLn("Row: <$row> TRUNCATE TABLE ");
        reporterLogLn("Target Db: <$targetDb> ");
        reporterLogLn("Action:    <$action> ");
        reporterLogLn("Target Sql:\n$targetSql\n");
        execute(targetDbSqlDriver, targetSql)
    }

}
