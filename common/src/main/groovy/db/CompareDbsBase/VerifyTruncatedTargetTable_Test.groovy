package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeFirstRow

public class VerifyTruncatedTargetTable_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String targetSql;
    private String atgard;
    private final static String VERIFY_TRUNCATED_TABLE_QUERY = "SELECT COUNT(*) COUNT_ FROM %s"


    public VerifyTruncatedTargetTable_Test(targetDb, system, table, atgard) {
        super.setup()
        this.targetDb = targetDb
        this.atgard = atgard

        String dbTargetOwner = settings."$targetDb".owner
        targetSql = "-- Verify truncated table size <$table> in system <$system> iz zero\n"
        targetSql += String.format(VERIFY_TRUNCATED_TABLE_QUERY, "$table")

        println "$targetSql "
    }

    @Test
    public void verifyTruncatedTargetTest(ITestContext testContext){
        row++
        reporterLogLn("Row: <$row> TRUNCATE TABLE ");
        reporterLogLn("targetDb:  <$targetDb> ");
        reporterLogLn("Atgard:    <$atgard> ");
        reporterLogLn("targetSql: <$targetSql> ");
        reporterLogLn("#########")

        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))

        getDbResult(targetDbSqlDriver, targetSql, dbRunTypeFirstRow)
    }

}
