package db.CompareDbsBase

import base.AnySqlCompareTest
import base.InitDbSettings
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
        by = dbCompareProperties.by

        String dbSourceOwner = settings."$sourceDb".owner
        String dbTargetOwner = settings."$targetDb".owner
        sourceSql = String.format(dbCompareProperties.sourceSql, dbSourceOwner.toUpperCase())
        targetSql = String.format(dbCompareProperties.targetSql, dbTargetOwner.toUpperCase())

        threshold = dbCompareProperties.fields["threshold"]

        this.dbCompareProperties = dbCompareProperties
    }

    @Test
    public void compareSourceEqualsTargetTest(ITestContext testContext){
        reporterLogLn("Row: <$row> $comments ");
        reporterLogLn("By: <$by>");
        reporterLogLn("#########")

        if(!dbCompareProperties.isComplete){
            skipTest("Värden för databas jämförelsen är inte kompletta: <${dbCompareProperties.skipReason}> \n ${dbCompareProperties.toString()}")
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb), getDbType(targetDb)))

        def schema = sourceDb.replaceAll(/_Source/, "")
        ArrayList tableFieldsToExcludeMap = []
        if(!tableFieldsFileColumn.isEmpty() && !tableFieldToExclude.isEmpty()){
            reporterLogLn("TableFieldsFileColumn: <$tableFieldsFileColumn}>");
            reporterLogLn("tableFieldToExclude:   <$tableFieldToExclude}>");
            reporterLogLn("schema:                <$schema}>");
            tableFieldsToExcludeMap = getTableFieldsToExcludeMap (tableFieldsFileColumn, schema)
        }


        compareAllFromDb1InDb2(sourceSql, targetSql, threshold, tableFieldsToExcludeMap, tableFieldToExclude)
    }

    private getTableFieldsToExcludeMap (inputFile, schema){

        def result = []

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
        excelObjectProvider.addColumnsToRetriveFromFile(["Tabell"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", schema)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Atgard", "Trunkera")
        def excelBodyRows = excelObjectProvider.getGdcObjects(6)



        excelBodyRows.unique().each {
           result.add(it["Tabell"])
        }
        return result;
    }

}
