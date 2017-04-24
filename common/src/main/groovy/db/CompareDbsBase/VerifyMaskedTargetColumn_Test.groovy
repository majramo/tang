package db.CompareDbsBase

import base.AnySqlCompareTest
import javafx.scene.layout.ColumnConstraints
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeFirstRow
import static dtos.base.Constants.dbRunTypeRows

public class VerifyMaskedTargetColumn_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("VMT   ")
    private static int row = 0
    private String targetDb;
    private String sourceDb;
    private String system;
    private String sourceTargetSql;
    private String targetDbOwner;
    def table
    def column

    public VerifyMaskedTargetColumn_Test(ITestContext testContext, targetDb, sourceDb, system, table, column) {
        super.setup()
        this.targetDb = targetDb
        this.sourceDb = sourceDb
        this.system = system.toLowerCase()
        this.table = table.toLowerCase()
        this.column = column.toLowerCase()
        targetDbOwner = settings."$targetDb".owner
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)

        log.info("sourceTargetSql <$sourceTargetSql>")
    }

    @Test
    public void verifyTruncatedTargetTest(ITestContext testContext){
        def tmpColumn = column
        def tmpTable = table
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))
        row++
        reporterLogLn("Row: <$row> Verify masked TABLE/COLUMN ");
        reporterLogLn("Source Db: <$sourceDb> ");
        reporterLogLn("Target Db: <$targetDb> ");
        reporterLogLn("Tmp Table: <$tmpTable> ");
        //reporterLogLn("column: <$column> ");


        def checkColumnType = "SELECT data_type FROM USER_TAB_COLS WHERE lower(table_name) = '$tmpTable' AND lower(column_name) = '$column'"
        reporterLogLn("Sql to check column type:\n$checkColumnType\n")
        def checkColumnTypeResult = getDbResult(targetDbSqlDriver, checkColumnType, dbRunTypeFirstRow)


        def TARGET_TABLE_QUERY_ORACLE = "SELECT %s FROM %s\n" +
                " WHERE NOT %s IS NULL\n" +
                " AND ROWNUM < 1000\n"
        def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns WHERE table_name = '%s'" //Todo: change this  sqlserver sql and check in
        if(checkColumnTypeResult[0] == "CLOB" || checkColumnTypeResult[0] == "BLOB"){
            tmpColumn = "dbms_lob.substr( $column, 4000,1)"
            reporterLogLn("checkColumnType:\n$checkColumnType\n")
            reporterLogLn("Column <$table> <$column> is xLOB type<$checkColumnTypeResult> ==> <$tmpColumn>")
        }
        reporterLogLn("tmpColumn: <$tmpColumn> ");

        sourceTargetSql = "-- Verify masked column<$tmpColumn> in table <$tmpTable> in target<$targetDb> mot source<$sourceDb>\n"
        sourceTargetSql += String.format(TARGET_TABLE_QUERY_ORACLE, "$tmpColumn", "$tmpTable",  "$tmpColumn")

        if(getDbType(targetDb).equals("sqlserver")){//Todo: fix this code for sqlserver
//            sourceTargetSql = "-- Verify masked column<$column> in table <$table> in system <$system> \n"
//            sourceTargetSql = String.format(TARGET_TABLE_QUERY_SQLSERVER, table)
        }
        log.info("sourceTargetSql:\n$sourceTargetSql\n")
        reporterLogLn("TargetSql:\n$sourceTargetSql\n")
        reporterLogLn("#########")

        def sourceDbResult = getSourceDbRowsResult(sourceTargetSql)
        def targetDbResult = getTargetDbRowsResult(sourceTargetSql)

        boolean sameData = false
        if(sourceDbResult != null && targetDbResult != null ) {
            sameData = (targetDbResult == sourceDbResult)
            int index = 0
            if (sameData){
                def maxRows= settings.maxDiffsToShow
                def count = targetDbResult.size()
                if (count > maxRows ){
                    targetDbResult = targetDbResult[0..maxRows-1]
                    reporterLogLn("Showing max rows: <$maxRows>")
                }else{
                    reporterLogLn("Showing max rows: <$count>")
                }

                targetDbResult.each {
                    reporterLogLn(sourceDbResult[index].toString() + " == " + targetDbResult[index].toString())
                    index++
                }
            }
        }
        tangAssert.assertTrue(!sameData, "Table/Column <$table/$column> should be masked", "Table/Column unmasked rows is expected to be <0> ");

    }

}
