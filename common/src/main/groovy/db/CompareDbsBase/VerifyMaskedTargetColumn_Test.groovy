package db.CompareDbsBase

import base.AnySqlCompareTest
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeFirstRow

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
    def type
    def actionColumn
    def masking
    def searchCriteria
    def searchExtraCondition
    def numberOfLinesInSqlCompare = 101

    public VerifyMaskedTargetColumn_Test(ITestContext testContext, targetDb, sourceDb, system, table, column, type, actionColumn, masking, searchCriteria = "", searchExtraCondition = "") {
        super.setup()
        this.targetDb = targetDb
        this.sourceDb = sourceDb
        this.system = system.toLowerCase()
        this.table = table.toLowerCase()
        this.type = type.toLowerCase()
        this.column = column.toLowerCase()
        this.actionColumn = actionColumn
        this.masking = masking
        this.searchCriteria = searchCriteria
        this.searchExtraCondition = searchExtraCondition
        targetDbOwner = settings."$targetDb".owner

        if(settings["numberOfLinesInSqlCompare"] != "" && settings["numberOfLinesInSqlCompare"].size() != 0 ){
            numberOfLinesInSqlCompare = Integer.parseInt(settings["numberOfLinesInSqlCompare"])
        }

        log.info("sourceTargetSql <$sourceTargetSql>")
    }

    @Test
    public void verifyMaskedTargetTest(ITestContext testContext){
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        def tmpColumn = column
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(sourceDb), getDbType(targetDb)))
        row++
        reporterLogLn("Row: <$row> Verify <$actionColumn> TABLE/COLUMN ");
        reporterLogLn("Source Db: <$sourceDb> ");
        reporterLogLn("Target Db: <$targetDb> ");
        reporterLogLn("Tmp Table: <$table> ");
        reporterLogLn("Column: <$column> ");
        reporterLogLn("SearchCriteria: <$searchCriteria> ");
        reporterLogLn("searchExtraCondition: <$searchExtraCondition> ");
        reporterLogLn("Masking: <$masking> ");


        def checkColumnType = "SELECT data_type FROM USER_TAB_COLS WHERE lower(table_name) = '$table' AND lower(column_name) = '$column'"
//        reporterLogLn("Sql to check column type:\n$checkColumnType\n")
        def checkColumnTypeResult = getDbResult(targetDbSqlDriver, checkColumnType, dbRunTypeFirstRow)
        def columnType = checkColumnTypeResult[0]
        reporterLogLn("ColumnType: <$columnType> ");

        def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns WHERE table_name = '%s'" //Todo: change this  sqlserver sql and check in
        if(checkColumnTypeResult[0] == "CLOB" ){
            reporterLogLn("Clob: <$column> ");
            tmpColumn = "DBMS_LOB.SUBSTR( $column, 50, 1)"
            reporterLogLn("checkColumnType:\n$checkColumnType\n")
            reporterLogLn("Column <$table> <$column> is xLOB type<$checkColumnTypeResult> ==> <$tmpColumn>")
        }
        reporterLogLn("tmpColumn: <$tmpColumn> ");

        def TARGET_TABLE_QUERY_ORACLE = "SELECT $tmpColumn FROM $table\n" +
                " WHERE NOT $tmpColumn IS NULL\n" +
                " AND ROWNUM < 21\n"

        def (sourceDbResult, targetDbResult) = getData(tmpColumn)

        boolean sameData = false
        if(sourceDbResult != null && targetDbResult != null ) {
            def targetDbResultSize = targetDbResult.size()
            if(targetDbResultSize < 100){
                def sourceDbResultSize = sourceDbResult.size()
                if(sourceDbResultSize == 0 && targetDbResultSize == 0) {
                    reporterLogLn("Source and Target size are zero")
                }else {
                    reporterLogLn("Target size is too low <$targetDbResultSize> check manually!")
                    skipTest("Target size is too low <$targetDbResultSize> check manually!")
                    sameData = false
                }
            }else {
                if (checkColumnTypeResult[0] == "CLOB") {
                    sameData = (targetDbResult.collect {it.toString()} == sourceDbResult.collect {it.toString()})
                } else {
                   sameData = (targetDbResult == sourceDbResult)
                }
            }
            int index = 0
            if (sameData){
                def maxRows= settings.maxDiffsToShow
                def count = targetDbResult.size()
                if (count > maxRows ){
                    targetDbResult = targetDbResult[0..maxRows-1]
                    reporterLogLn("Showing max rows: <$maxRows (total: $count)>")
                }else{
                    reporterLogLn("Showing rows: <total $count>")
                }

                targetDbResult.each {
                    reporterLogLn(sourceDbResult[index].toString() + " == " + targetDbResult[index].toString())
                    index++
                }
            }
        }
        sourceDbSqlDriver = null
        targetDbSqlDriver = null
        tangAssert.assertTrue(!sameData, "Table/Column <$table/$column> should be masked", "Table/Column seems to be unmasked ");

    }

    protected List getData(tmpColumn) {

        if (searchCriteria != "") {
            def numberOfLinesInSqlCompareTemp = numberOfLinesInSqlCompare
            if (numberOfLinesInSqlCompare.class.equals(String)) {
                numberOfLinesInSqlCompareTemp = Integer.parseInt(numberOfLinesInSqlCompare) + 1000
            } else {
                 numberOfLinesInSqlCompareTemp = numberOfLinesInSqlCompare + 1000
            }

            def fromId = 1
            def toMaxId = numberOfLinesInSqlCompareTemp
            //TODO change the sql and put value of Max in the sql
            def maxQuery = "SELECT MAX($searchCriteria)MAX_ID FROM $table " +
                    "where NOT $column IS NULL\n"+
                    "AND NOT $column || '' in( 'START', 'SLUT' , 'REDANGJORD', 'GENOMFORDA', 'FINNSINTE', 'FEL', 'ANTAL')"
            def sourceDbResult = getSourceDbRowsResult(maxQuery)
            def toMaxIdRaw = sourceDbResult[0]["MAX_ID"]
            try {
                if (toMaxIdRaw != null) {
                    toMaxId = new BigDecimal(toMaxIdRaw)
                }
            } catch (NumberFormatException e) {
                reporterLogLn("######")
                reporterLogLn("Wrong type of Id, MUST be NUMERIC <$searchCriteria>")
                reporterLogLn("Got <$toMaxIdRaw>")
                reporterLogLn("######")
                reporterLogLn(maxQuery)
                throw e
            }
            if (numberOfLinesInSqlCompareTemp < toMaxId) {
                fromId = toMaxId - numberOfLinesInSqlCompareTemp
            }

            sourceTargetSql = "-- Verify search criteria and masked column<$searchCriteria, $tmpColumn> in table <$table> in target<$targetDb> against source<$sourceDb>\n"
            def notNumberColumnCompare = ""
            if(!["number", "date", "time"].contains(type)){
                notNumberColumnCompare = " AND NOT $tmpColumn || ''  = ' '\n"
            }
            sourceTargetSql += "SELECT $searchCriteria, $tmpColumn FROM $table\n" +
                    " WHERE NOT $column IS NULL\n" +
                    // " AND LENGTH(REPLACE($tmpColumn, ' ' , '')) > 0\n" +
                    notNumberColumnCompare +
                    " AND length($tmpColumn) > 0\n" +
                    " AND $searchCriteria BETWEEN $fromId AND $toMaxId\n" +
                    " AND ROWNUM < 1001\n"
        } else {
            def subSelectQuery = "SELECT '''' || rowid || '''' rowid_  FROM $table " +
                    "where NOT $column IS NULL\n"+
                    "AND NOT $column || '' in( 'START', 'SLUT' , 'REDANGJORD', 'GENOMFORDA', 'FINNSINTE', 'FEL', 'ANTAL')\n" +
                    "AND ROWNUM < 1001"
            def subSelectResult = getSourceDbRowsResult(subSelectQuery)
            def rowidCriteria = "--No Rowids exist in criteria\n"
            if (subSelectResult.size()) {
                def rowids =  joinList(subSelectResult.collect{it.ROWID_}, ", ", 120)
                rowidCriteria += "--Rowids to add in criteria\nAND ROWID IN ($rowids)\n"

            }else{
                reporterLogLn("######")
                reporterLogLn("Can't get rowids")
                reporterLogLn("Got <$subSelectResult>")
                reporterLogLn("######")
            }



            sourceTargetSql = "-- Verify masked column<$tmpColumn> in table <$table> in target<$targetDb> against source<$sourceDb>\n"
            sourceTargetSql += "SELECT $tmpColumn FROM $table\n" +
                    " WHERE NOT $column IS NULL\n" +
                    " AND NOT $tmpColumn || ''  = ' '\n" +
                    " AND length($tmpColumn) > 0\n" +
                    " AND ROWNUM < 1001\n" +
                    rowidCriteria
        }
        if (searchExtraCondition != "") {
            sourceTargetSql += "\nAND $searchExtraCondition\n"
        }
        sourceTargetSql += "ORDER BY 1\n"

        if (getDbType(targetDb).equals("sqlserver")) {//Todo: fix this code for sqlserver
//            sourceTargetSql = "-- Verify masked column<$column> in table <$table> in system <$system> \n"
//            sourceTargetSql = String.format(TARGET_TABLE_QUERY_SQLSERVER, table)
        }
        log.info("\n")
        log.info("sourceTargetSql:\n$sourceTargetSql\n")
        reporterLogLn("TargetSql:\n$sourceTargetSql\n")
        reporterLogLn("#########")

        def sourceDbResult = getSourceDbRowsResult(sourceTargetSql)
        def targetDbResult = getTargetDbRowsResult(sourceTargetSql)
        reporterLogLn("Source data size: " + sourceDbResult.size())
        reporterLogLn("Target data size: " + targetDbResult.size())
        return [sourceDbResult, targetDbResult]
    }

}
