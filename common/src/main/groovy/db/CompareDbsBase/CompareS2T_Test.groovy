package db.CompareDbsBase

import base.AnySqlCompareTest
import base.InitDbSettings
import org.testng.ITestContext
import org.testng.annotations.Test

public class CompareS2T_Test extends AnySqlCompareTest{
    private String row
    private String sourceDb;
    private String targetDb;
    private String sourceSql;
    private String targetSql;
    private float threshold = 0;
    private String comments;
    private String by;
    private DbCompareProperties dbCompareProperties

    public CompareS2T_Test(DbCompareProperties dbCompareProperties) {
        super.setup()
        row = dbCompareProperties.row
        sourceDb = dbCompareProperties.sourceDb
        targetDb = dbCompareProperties.targetDb
        comments = dbCompareProperties.comments
        by = dbCompareProperties.by

        String dbSourceOwner = settings."$sourceDb".owner
        String dbTargetOwner = settings."$targetDb".owner
        sourceSql = String.format(dbCompareProperties.sourceSql, dbSourceOwner.toUpperCase())
        targetSql = String.format(dbCompareProperties.targetSql, dbTargetOwner.toUpperCase())

        try{
            threshold = Float.parseFloat(dbCompareProperties.fields["threshold"])
        }catch(Exception e){

        }
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

        compareAllFromDb1InDb2(sourceSql, targetSql, threshold)
    }

}
