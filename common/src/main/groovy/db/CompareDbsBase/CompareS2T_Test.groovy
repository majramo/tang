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
    private String considerSystemTableColumnAnalyse;
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
        considerSystemTableColumnAnalyse = dbCompareProperties.considerSystemTableColumnAnalyse
        tableFieldToExclude = dbCompareProperties.tableFieldToExclude
        actionTypeColumn = dbCompareProperties.actionTypeColumn
        by = dbCompareProperties.by

        String dbSourceOwner = settings."$sourceDb".owner
        String dbTargetOwner = settings."$targetDb".owner
        sourceSql = String.format(dbCompareProperties.sourceSql, dbSourceOwner.toUpperCase()).replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)
        targetSql = String.format(dbCompareProperties.targetSql, dbTargetOwner.toUpperCase()).replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)

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
        system = system[0].toUpperCase() + system[1..-1].toLowerCase()
        ArrayList tableFieldsToExcludeMap = []
        if(!considerSystemTableColumnAnalyse.isEmpty() && considerSystemTableColumnAnalyse.equals("true") && !tableFieldToExclude.isEmpty()){
            def inputFile = sourceDb.replaceAll(".Source", "")
            ExcelObjectProvider excelObjectProvider = SystemPropertiesInitation.getExcelProvider(inputFile)

            reporterLogLn("considerSystemTableColumnAnalyse: <$considerSystemTableColumnAnalyse>");
            reporterLogLn("Systemanalyse inputFile: <$inputFile>");
            reporterLogLn("TableFieldToExclude:   <$tableFieldToExclude>");
            reporterLogLn("System:                <$system>");
            tableFieldsToExcludeMap = getTableFieldsToExcludeMap (excelObjectProvider, system)
        }


        compareAllFromDb1InDb2(testContext, sourceSql, targetSql, threshold, comments, tableFieldsToExcludeMap, tableFieldToExclude, actionTypeColumn, system)
    }

    private getTableFieldsToExcludeMap (ExcelObjectProvider excelObjectProvider, schema){

        def result = []

        excelObjectProvider.addColumnsToRetriveFromFile(["Table"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", schema)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "Truncate")
        def excelBodyRows = SystemPropertiesInitation.readExcel(excelObjectProvider)



        excelBodyRows.unique().each {
           result.add(it["Table"])
        }
        return result;
    }

}
