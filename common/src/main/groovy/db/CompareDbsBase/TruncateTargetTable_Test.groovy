package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test

public class TruncateTargetTable_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String targetSql;
    private String atgard;
    private final static String TRUNCATE_TABLE_QUERY = "SELECT COUNT(*) COUNT_ FROM %s"
//    private final static String TRUNCATE_TABLE_QUERY = "TRUNCATE TABLE %s"

    public TruncateTargetTable_Test(targetDb, system, table, atgard) {
        super.setup()
        this.targetDb = targetDb
        this.atgard = atgard

        String dbTargetOwner = settings."$targetDb".owner
        targetSql = "-- Truncate table <$table> in system <$system>\n"
        targetSql += String.format(TRUNCATE_TABLE_QUERY, "$table")

        println "$targetSql "
    }

    @Test
    public void truncateTargetTest(ITestContext testContext){
        row++
        reporterLogLn("Row: <$row> TRUNCATE TABLE ");
        reporterLogLn("targetDb:  <$targetDb> ");
        reporterLogLn("Atgard: <$atgard> ");
        reporterLogLn("#########")

        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))

        truncate(targetDbSqlDriver, targetSql)
    }

}
