package db.CompareDbsBase

import base.AnySqlCompareTest
import dtos.base.SqlHelper
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.CompareType.DIFF
import static dtos.base.Constants.dbRunTypeRows

public class ReportDbObjectsDiff extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")




    @Parameters(["systemColumn", "objectType", "queryFirst" ,"sourceQuerySettings", "targetQuerySettings", "sqlCriteriaColumn"] )
    @Test
    public void reportDbObjectsDiff_Test(String systemColumn, String objectType, String queryFirst, String sourceQuerySettings, String targetQuerySettings, @Optional ("") String sqlCriteriaColumn, ITestContext testContext){
        super.setup()

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def TARGET_TABLE_QUERY_ORACLE =  settings."$targetQuerySettings"
        def SOURCE_TABLE_QUERY_ORACLE =  settings."$sourceQuerySettings"

        String sourceDbOwner = settings."$sourceDb".owner
        String targetDbOwner = settings."$targetDb".owner
//        def sourceTableSql = String.format(SOURCE_TABLE_QUERY_ORACLE, sourceDbOwner.toUpperCase())
        def sourceTableSql = SOURCE_TABLE_QUERY_ORACLE.replaceAll("_OWNER_", sourceDbOwner.toUpperCase()).replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)
        def targetTableSql = TARGET_TABLE_QUERY_ORACLE.replaceAll("_OWNER_", sourceDbOwner.toUpperCase()).replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)
        if(!sqlCriteriaColumn.isEmpty()){
            sourceTableSql += "AND $sqlCriteriaColumn"
            targetTableSql += "AND $sqlCriteriaColumn"
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        reporterLogLn(reporterHelper.addIcons(getDbType(targetDb)))

        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn("Target: <$targetDb>");
        reporterLogLn("DB copmpare type: <$objectType>");
        reporterLogLn("Query first: <$queryFirst>");
        def diffDbResult
        def targetDbResult
        //read database
        if(queryFirst.equals("target")){
            reporterLogLn("\n### targetTableSql: \n$targetTableSql");
            targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, targetTableSql, 0, targetDb)
            def dataFromTarget = joinList(targetDbResult.collect{it[0]})
            def targetCount = targetDbResult.size()
            reporterLogLn("Target <$objectType>: <$targetCount>");
            if(targetCount.equals(0)){
                skipTest("Nothing in Target to Compare: sourceCount is <$targetCount> ")
            }
            sourceTableSql = sourceTableSql.replaceAll("__-DATA-__", dataFromTarget)
            reporterLogLn("\n### sourceTableSql: \n$sourceTableSql");
            def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
            diffDbResult = sourceDbResult
        }else{
            reporterLogLn("\n### sourceTableSql: \n$sourceTableSql");
            def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
            def dataFromSource = joinList(sourceDbResult.collect{it[0]})
            def sourceCount = dataFromSource.size()
            reporterLogLn("Source <$objectType>: <$sourceCount>");
            if(sourceCount.equals(0)){
                skipTest("Nothing in Source to Compare: sourceCount is <$sourceCount> ")
            }
            targetTableSql = targetTableSql.replaceAll("__-DATA-__", dataFromSource)
            reporterLogLn("\n### targetTableSql: \n$targetTableSql");
            targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, targetTableSql, 0, targetDb)
            diffDbResult = targetDbResult
        }
        def diffCount =  diffDbResult.size()
        if( diffCount >0) {
            reporterLogLn("")
            reporterLogLn("Missing <$objectType> in <$queryFirst> <$diffCount>")
            reporterLogLn("")
            def dbDifffDataToAdd = diffDbResult.collect{"'" + it[0] + "'"}.join(",\n")
            dbDifffDataToAdd = joinList(diffDbResult.collect{  "'" + it[0] + "'" }, ", ", 44)
            def objectQuery = ""
            switch (objectType.toLowerCase()) {
                case "constraint":
                    objectQuery = "SELECT 'ALTER TABLE ' || table_name || ' ENABLE CONSTRAINT ' || CONSTRAINT_NAME; --enable" +
                            "--ALTER TABLE ' || table_name || ' DROP CONSTRAINT ' || CONSTRAINT_NAME; --drop" +
                            "FROM all_constraints " +
                            "WHERE CONSTRAINT_NAME IN( " + dbDifffDataToAdd +");"
                    targetDbResult
                    objectQuery += "\n\n--ENABLE CONSTRAINT\n" +
                            diffDbResult["ENABLE_"].join("\n")
                    objectQuery += "\n\n--DROP CONSTRAINT\n" +
                            diffDbResult["DROP_"].join("\n")
                    break
                case "index":
                    objectQuery = diffDbResult.collect{it[0].replaceAll(/"|'|,/, "").replaceAll(/^/, "DROP INDEX ").replaceAll(/$/,";") }.join("\n")
                    objectQuery += "\n\n--DROP INDEX\n" + diffDbResult.collect{"DROP INDEX " + it["INDEX_NAME"] + ";"}.join("\n")
                    break
            }
            reporterLogLn("objectQuery\n$objectQuery")
            diffDbResult.eachWithIndex { it, i ->
                reporterLogLn("-- " + "${i + 1} ".padLeft(5)  + it[0])
            }
            reporterLogLn("\n--Query <$objectType>:\nIN( $dbDifffDataToAdd );\n\n")
        }else{
            reporterLogLn("\n<$objectType> Source = Target")

        }
        tangAssert.assertEquals(diffCount, 0, "Should have no diff", "Should have no diff: diffCount $diffCount > 0 ")

    }


}
