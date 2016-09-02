package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test

public class CompareT2S_Value_Test extends AnySqlCompareTest{
    private String row
    private String sourceValue;
    private String targetDb;
    private String sourceSql;
    private String targetSql;
    private float threshold = 0;
    private String comments;
    private String by;
    private DbTargetCompareProperties dbTargetCompareProperties

    public CompareT2S_Value_Test(DbTargetCompareProperties dbTargetCompareProperties) {
        super.setup()
        row = dbTargetCompareProperties.row
        sourceValue = dbTargetCompareProperties.sourceValue
        targetDb = dbTargetCompareProperties.targetDb
        comments = dbTargetCompareProperties.comments
        by = dbTargetCompareProperties.by

        String dbTargetOwner = settings."$targetDb".owner
        targetSql = String.format(dbTargetCompareProperties.targetSql, dbTargetOwner.toUpperCase())

        try{
            threshold = Float.parseFloat(dbTargetCompareProperties.fields["threshold"])
        }catch(Exception e){

        }
        this.dbTargetCompareProperties = dbTargetCompareProperties
    }

    @Test
    public void compareSourceEqualsTargetTest(ITestContext testContext){
        reporterLogLn("Row: <$row> $comments ");
        reporterLogLn("By: <$by>");
        reporterLogLn("#########")

        if(!dbTargetCompareProperties.isComplete){
            skipTest("Värden för databas jämförelsen är inte kompletta: <${dbTargetCompareProperties.skipReason}> \n ${dbTargetCompareProperties.toString()}")
        }
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(targetDb)))

        compareSourceEqualsTarget(sourceValue, targetSql, threshold)
    }

}
