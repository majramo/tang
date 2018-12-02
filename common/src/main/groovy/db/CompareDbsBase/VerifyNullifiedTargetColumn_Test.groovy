package db.CompareDbsBase

import base.AnySqlCompareTest
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeFirstRow

public class VerifyNullifiedTargetColumn_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("VMT   ")
    private static int row = 0
    private String targetDb;
    private String system;
    private String targetSql;
    private String targetDbOwner;
    def table
    def column
    def actionColumn
    def searchCriteria
    def searchExtraCondition
    def numberOfLinesInSqlCompare = 101
    public VerifyNullifiedTargetColumn_Test(ITestContext testContext, targetDb, sourceDb, system, table, column, actionColumn, searchCriteria = "", searchExtraCondition = "") {
        super.setup()
        this.targetDb = targetDb
        this.system = system.toLowerCase()
        this.table = table.toLowerCase()
        this.column = column.toLowerCase()
        this.actionColumn = actionColumn
        this.searchCriteria = searchCriteria
        this.searchExtraCondition = searchExtraCondition
        targetDbOwner = settings."$targetDb".owner
        if(settings["numberOfLinesInSqlCompare"] != "" && settings["numberOfLinesInSqlCompare"].size() != 0 ){
            numberOfLinesInSqlCompare = settings["numberOfLinesInSqlCompare"]
        }

        log.info("TargetSql <$targetSql>")
    }

    @Test
    public void verifyNullifiedTargetTest(ITestContext testContext){
        super.setTargetSqlHelper(testContext, targetDb)
        def tmpColumn = column
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))
        row++
        reporterLogLn("Row: <$row> Verify <$actionColumn> TABLE/COLUMN ");
        reporterLogLn("Target Db: <$targetDb> ");
        reporterLogLn("Tmp Table: <$table> ");
        reporterLogLn("Column: <$column> ");


        def checkColumnType = "SELECT data_type FROM USER_TAB_COLS WHERE lower(table_name) = '$table' AND lower(column_name) = '$column'"
//        reporterLogLn("Sql to check column type:\n$checkColumnType\n")
        def checkColumnTypeResult = getDbResult(targetDbSqlDriver, checkColumnType, dbRunTypeFirstRow)
        def columnType = checkColumnTypeResult[0]
        reporterLogLn("ColumnType: <$columnType> ");

        reporterLogLn("tmpColumn: <$tmpColumn> ");

       if (searchCriteria != "") {
            def numberOfLinesInSqlCompareTemp = numberOfLinesInSqlCompare
            if(numberOfLinesInSqlCompare.class.equals(String)){
                numberOfLinesInSqlCompareTemp  = Integer.parseInt(numberOfLinesInSqlCompare) + 1000
            }else {
                numberOfLinesInSqlCompareTemp = numberOfLinesInSqlCompare + 1000
            }

            targetSql = "-- Verify nullified column<$searchCriteria, $tmpColumn> in table <$table> in target<$targetDb>\n"
                targetSql += "SELECT * FROM $table\n" +
                        " WHERE NOT $tmpColumn IS NULL\n" +
                        " AND $searchCriteria BETWEEN (SELECT MAX($searchCriteria)- $numberOfLinesInSqlCompare FROM $table where NOT $tmpColumn IS NULL) AND (SELECT MAX($searchCriteria) FROM $table where NOT $tmpColumn IS NULL)\n" +
                    " AND ROWNUM < $numberOfLinesInSqlCompareTemp\n"
        }else{
            targetSql = "-- Verify nullified column<$tmpColumn> in table <$table> in target<$targetDb>\n"
            targetSql += "SELECT * FROM $table\n" +
                    " WHERE NOT $tmpColumn IS NULL\n" +
                    " AND ROWNUM < 100\n"
        }
        if( searchExtraCondition != ""){
            targetSql += "\nAND $searchExtraCondition\n"
        }

        if(getDbType(targetDb).equals("sqlserver")){//Todo: fix this code for sqlserver
//              targetSql = "-- Verify nullified column<$column> in table <$table> in system <$system> \n"
//              targetSql = String.format(TARGET_TABLE_QUERY_SQLSERVER, table)
        }
        log.info("TargetSql:\n$targetSql\n")
        reporterLogLn("TargetSql:\n$targetSql\n")
        reporterLogLn("#########")

        def targetDbResult = getTargetDbRowsResult(targetSql)
        def count = targetDbResult.size()
        if(targetDbResult != null && !count.equals(0)){
            reporterLogLn("Target has at least <$count> errors ")
            def maxRows= settings.maxDiffsToShow
            if (count > maxRows ){
                targetDbResult = targetDbResult[0..maxRows-1]
                reporterLogLn("Showing max rows: <$maxRows>")
            }else{
                reporterLogLn("Showing max rows: <$count>")
            }
            targetDbResult.each {
                reporterLogLn(it.toString())
             }
        }
        tangAssert.assertTrue(count == 0, "Table/Column <$table/$column> should be nullified", "Table/Column seems to be not null ");

    }

}
