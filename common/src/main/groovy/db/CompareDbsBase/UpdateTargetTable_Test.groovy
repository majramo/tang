package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test
/*
 this class uses split of incoming query by ";" and will execute each sub query
 */
public class UpdateTargetTable_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String action;
    private String targetSql;
    private boolean skipTest = false

    public UpdateTargetTable_Test(targetDb, system, table, action, String targetSql) {
        super.setup()
        this.action = action
        this.targetDb = targetDb

        String dbTargetOwner = settings."$targetDb".owner
        this.targetSql = "-- Update table <$table> in system <$system>\n"
        this.targetSql += targetSql
        if(!targetSql.toUpperCase().startsWith("UPDATE ")){
            skipTest = true
        }

        println this.targetSql
    }

    @Test
    public void updateTargetTest(ITestContext testContext){
        if(skipTest){
            skipTest("targetSql doesn't contain <UPDATE >\n\n$targetSql")
        }
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))

        row++
        reporterLogLn("Row: <$row> UPDATE TABLE ");
        reporterLogLn("Target Db: <$targetDb> ");
        reporterLogLn("Action:    <$action> ");
        reporterLogLn("Target Sql:\n$targetSql\n");
        def queries = targetSql.split(";")
        queries.eachWithIndex {query, i->
            reporterLogLn("\n###\nQuery <$i>:\n$query");
            if(query.replaceAll(/\s/,'') != "") {
                reporterLogLn(">Executing target Sql <$i>:\n$query\n");
                execute(targetDbSqlDriver, query)
            }else{
                reporterLogLn(">Skipping empty Sql <$i>:\n$query\n");
            }

        }
    }

}
