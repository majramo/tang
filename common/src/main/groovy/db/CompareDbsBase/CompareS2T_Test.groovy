package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.testng.ITestContext
import org.testng.annotations.Test

public class CompareS2T_Test extends AnySqlCompareTest{
    private String row
    private String sourceDb;
    private String targetDb;
    private String sourceSql;
    private String targetSql;
    private String threshold = "0";
    private String comments;
    private String tableFieldsFileColumn;
    private String tableFieldToExclude;
    private String actionTypeColumn;
    private String by;
    private DbCompareProperties dbCompareProperties

    public CompareS2T_Test(DbCompareProperties dbCompareProperties) {
        super.setup()
        row = dbCompareProperties.row
        sourceDb = dbCompareProperties.sourceDb
        targetDb = dbCompareProperties.targetDb
        comments = dbCompareProperties.comments
        tableFieldsFileColumn = dbCompareProperties.tableFieldsFileColumn
        tableFieldToExclude = dbCompareProperties.tableFieldToExclude
        actionTypeColumn = dbCompareProperties.actionTypeColumn
        by = dbCompareProperties.by

        String dbSourceOwner = settings."$sourceDb".owner
        String dbTargetOwner = settings."$targetDb".owner
        sourceSql = String.format(dbCompareProperties.sourceSql, dbSourceOwner.toUpperCase()).replaceAll(/\$\$\$/, /\%\$\%/)
        targetSql = String.format(dbCompareProperties.targetSql, dbTargetOwner.toUpperCase()).replaceAll(/\$\$\$/, /\%\$\%/)

        threshold = dbCompareProperties.fields["threshold"]

        this.dbCompareProperties = dbCompareProperties
    }

    @Test
    public void compareSourceEqualsTargetTest(ITestContext testContext){
         if(!dbCompareProperties.isComplete){
            skipTest("Compare object is not complete: <${dbCompareProperties.skipReason}> \n ${dbCompareProperties.toString()}")
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb), getDbType(targetDb)))

        reporterLogLn("Row: <$row> $comments ");
        reporterLogLn("By: <$by>");

        if(!actionTypeColumn.isEmpty()){
            super.setRepositorySqlHelper(testContext, "repository")
        }

        def system = sourceDb.replaceAll(/_Source/, "")
        ArrayList tableFieldsToExcludeMap = []
        if(!tableFieldsFileColumn.isEmpty() && !tableFieldToExclude.isEmpty()){
            reporterLogLn("TableFieldsFileColumn: <$tableFieldsFileColumn>");
            reporterLogLn("TableFieldToExclude:   <$tableFieldToExclude>");
            reporterLogLn("System:                <$system>");
            tableFieldsToExcludeMap = getTableFieldsToExcludeMap (tableFieldsFileColumn, system)
        }


        compareAllFromDb1InDb2(testContext, sourceSql, targetSql, threshold, comments, tableFieldsToExcludeMap, tableFieldToExclude, actionTypeColumn, system)
    }

    private getTableFieldsToExcludeMap (inputFile, schema){

        def result = []

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
        excelObjectProvider.addColumnsToRetriveFromFile(["Table"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", schema)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "Truncate")
        def excelBodyRows = excelObjectProvider.getGdcRows()



        excelBodyRows.unique().each {
           result.add(it["Table"])
        }
        return result;
    }

}
