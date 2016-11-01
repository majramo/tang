package db.CompareDbsBase

import base.AnySqlCompareTest
import javafx.scene.layout.ColumnConstraints
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeFirstRow
import static dtos.base.Constants.dbRunTypeRows

public class VerifyMaskedTargetColumn_Test extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String sourceDb;
    private String system;
    private String targetSql;
    private String targetDbOwner;
    def table
    def column

    public VerifyMaskedTargetColumn_Test(targetDb, sourceDb, system, table, column) {
        super.setup()
        this.targetDb = targetDb
        this.sourceDb = sourceDb
        this.system = system.toLowerCase()
        this.table = table
        this.column = column
        targetDbOwner = settings."$targetDb".owner


        println "$targetSql "
    }

    @Test
    public void verifyTruncatedTargetTest(ITestContext testContext){
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))
        row++
        reporterLogLn("Row: <$row> Verify masked TABLE/COLUMN ");
        reporterLogLn("sourceDb:  <$sourceDb> ");
        reporterLogLn("targetDb:  <$targetDb> ");
        reporterLogLn("table: <$table> ");
        reporterLogLn("column: <$column> ");
        def TARGET_TABLE_QUERY_ORACLE = "SELECT %s,  ROWIDTONCHAR(ROWID) FROM %s WHERE %s || '_' || ROWID IN(\n" +
                " SELECT %s || '_' || ROWID FROM %s --%s@$sourceDb\n" +
                " WHERE NOT %s IS NULL\n" +
                " AND ROWNUM < 100\n" +
                ")"
        def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns WHERE table_name = '%s'"
        //Search for table before executing, skip if table not exists
        targetSql = "-- Verify masked column<$column> in table <$table> in system <$system> in target<$targetDb> mot source<$sourceDb>\n"
        targetSql += String.format(TARGET_TABLE_QUERY_ORACLE, "$column", "$table",  "$column",  "$column", "$table", "$table", "$column")
        if(getDbType(targetDb).equals("sqlserver")){
            targetSql = "-- Verify masked column<$column> in table <$table> in system <$system> \n"
            targetSql = String.format(TARGET_TABLE_QUERY_SQLSERVER, table)
        }
        reporterLogLn("TargetSql: <\n$targetSql\n>")
        reporterLogLn("#########")

        def dbResult = getDbResult(targetDbSqlDriver, targetSql, dbRunTypeRows)

        def count = dbResult.size()
        reporterLogLn("Table count: <$count>")
        def maxRows= settings.maxDiffsToShow
        if (count > maxRows ){
            dbResult = dbResult[0..maxRows-1]
            reporterLogLn("Showing max rows: <$maxRows>")
        }else{
            reporterLogLn("Showing max rows: <$count>")
        }
        dbResult.each {
        reporterLogLn(it)
        }
        tangAssert.assertEquals(count, 0, "Tabellen/kolumn <$table/$column> ska vara avidentiferad", "Tabellen har  <$count> ej avidentiferade rader, förväntat är <0>");

    }

}
