package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.testng.ITestContext
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeRows

public class ReportDbObjectsDiff extends AnySqlCompareTest{
    private final static Logger log = LogManager.getLogger("CSC  ")
    def DROP_REF_CONSTRATINTS_ORACLE = "SELECT  ('ALTER TABLE ' || table_name || ' DROP CONSTRAINT ' || CONSTRAINT_NAME || ' cascade ;--DropRefConstraint') DROP_CASCADE, \n" +
            "('ALTER TABLE ' || table_name || ' DROP CONSTRAINT ' || CONSTRAINT_NAME || ' ;--DropRefConstraint') DROP_ \n" +
            "FROM user_constraints \n" +
            "where CONSTRAINT_NAME "
   def DROP_PRIMARY_KEY_ORACLE = "WITH all_primary_keys AS (\n" +
           "  SELECT constraint_name AS pk_name,\n" +
           "         table_name\n" +
           "    FROM user_constraints\n" +
           "   WHERE  constraint_type = 'P'\n" +
           "   --and table_name ='ARENDE'\n" +
           ")\n" +
           "    SELECT distinct ('ALTER TABLE ' || ac.table_name || ' DROP PRIMARY KEY cascade ;--Drop parent primary key cascade' || ac.table_name || '.*primary and create again') DROP_CASCADE , \n" +
           "    ('ALTER TABLE ' || ac.table_name || ' DROP PRIMARY KEY ;--Drop parent primary key ' || ac.table_name || '.*primary and create again') DROP_  \n" +
           "\n" +
           "    FROM all_constraints ac\n" +
           "         LEFT JOIN all_primary_keys apk\n" +
           "                ON ac.r_constraint_name = apk.pk_name\n" +
           "   WHERE   ac.constraint_type = 'R'\n" +
           "and ac.constraint_name \n"



    @Parameters(["systemColumn", "objectType", "queryFirst" ,"sourceQuerySettings", "targetQuerySettings", "sqlCriteriaColumn"] )
    @Test
    public void reportDbObjectsDiff_Test(String systemColumn, String objectType, String queryFirst, String sourceQuerySettings, String targetQuerySettings, @Optional ("") String sqlCriteriaColumn, ITestContext testContext){
        super.setup()

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def sourceDbType = getDbType(sourceDb)
        def targetDbType = getDbType(targetDb)
        def targetObjectQuery =  settings."$targetQuerySettings"."$targetDbType"
        def sourceObjectQuery =  settings."$sourceQuerySettings"."$sourceDbType"
        if(sourceObjectQuery.size() == 0 || targetObjectQuery.size() == 0){
            reporterLogLn("Can't run: sourceObjectQuery <$sourceQuerySettings.$sourceDbType> <$sourceObjectQuery> or targetObjectQuery <$targetQuerySettings.$targetDbType> <$targetObjectQuery> is empty!")
            skipTest("Can't run: sourceObjectQuery <$sourceQuerySettings.$sourceDbType> or targetObjectQuery <$targetQuerySettings.$targetDbType> is empty!")
        }
        targetObjectQuery = targetObjectQuery.trim()
        sourceObjectQuery = sourceObjectQuery.trim()

        String sourceDbOwner = settings."$sourceDb".owner
        String targetDbOwner = settings."$targetDb".owner
//        def sourceObjectSql = String.format(SOURCE_TABLE_QUERY, sourceDbOwner.toUpperCase())
        def sourceObjectSql = sourceObjectQuery.replaceAll("_OWNER_", sourceDbOwner.toUpperCase()).replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)
        def targetObjectSql = targetObjectQuery.replaceAll("_OWNER_", targetDbOwner.toUpperCase()).replaceAll(/\$\$\$/, /\$/).replaceAll(/___---'/, /\\_%'  ESCAPE '\\'/).replaceAll(/---/, /\%/).replaceAll(/___/, /\\_  ESCAPE '\\'/)
        if(!sqlCriteriaColumn.isEmpty()){
            sourceObjectSql += "AND $sqlCriteriaColumn"
            targetObjectSql += "AND $sqlCriteriaColumn"
        }
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(sourceDbType))
        reporterLogLn(reporterHelper.addIcons(targetDbType))

        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn("Target: <$targetDb>");
        reporterLogLn("DB copmpare type: <$objectType>");
        reporterLogLn("Query first: <$queryFirst>");
        def diffDbResult
        def targetDbResult
        //read database
        if(queryFirst.equals("target")){
            reporterLogLn("\n### targetObjectSql: $targetQuerySettings\n$targetObjectSql");
            targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, targetObjectSql, 0, targetDb)
            def dataFromTarget = joinList(targetDbResult.collect{it[0]}, ", ", 120)
            if(targetDbResult.size() > 1000){
                reporterLogLn("targetDbResult has ${targetDbResult.size()} results. The list in Where is truncated to 1000 rows")
                dataFromTarget = joinList(targetDbResult[0..999].collect{it[0]}, ", ", 120)
            }
            def targetCount = targetDbResult.size( )
            reporterLogLn("Target <$objectType>: <$targetCount>");
            if(targetCount.equals(0)){ReportDbObjectsDiff
                skipTest("Nothing in Target to Compare: sourceCount is <$targetCount> ")
            }
            sourceObjectSql = sourceObjectSql.replace("__-DATA-__", dataFromTarget)
            reporterLogLn("\n### sourceObjectSql: $sourceQuerySettings\n$sourceObjectSql");
            def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceObjectSql, 0, sourceDb)
            def sourceCount = sourceDbResult.size( )
            reporterLogLn("Source <$objectType>: missing count<$sourceCount>");
            diffDbResult = sourceDbResult
        }else{
            reporterLogLn("\n### sourceObjectSql: $sourceQuerySettings\n$sourceObjectSql");
            def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceObjectSql, 0, sourceDb)
            def dataFromSource = joinList(sourceDbResult.collect{it[0]}, ", ", 120)
            if(sourceDbResult.size() > 1000){
                reporterLogLn("sourceDbResult has ${sourceDbResult.size()} results. The list in Where is truncated to 1000 rows")
                dataFromSource = joinList(sourceDbResult[0..999].collect{it[0]}, ", ", 120)
            }
            def sourceCount = sourceDbResult.size()
            reporterLogLn("Source <$objectType>: <$sourceCount>");
            if(sourceCount.equals(0)){
                skipTest("Nothing in Source to Compare: sourceCount is <$sourceCount> ")
            }
            targetObjectSql = targetObjectSql.replace("__-DATA-__", dataFromSource)
            targetDbResult = targetDbSqlDriver.sqlConRun("Get data from $targetDb", dbRunTypeRows, targetObjectSql, 0, targetDb)
            def targetCount = targetDbResult.size( )
            reporterLogLn("Target <$objectType>: missing count<$targetCount>");
            reporterLogLn("\n### targetObjectSql: $targetQuerySettings\n$targetObjectSql");
            diffDbResult = targetDbResult
        }
        def diffCount =  diffDbResult.size()
        if (diffCount > 0) {

            reporterLogLn("")
            reporterLogLn("Missing <$objectType> in <$queryFirst> <$diffCount>")
            reporterLogLn("")
            def dbDifffDataToAdd = joinList(diffDbResult.collect { "'" + it[0] + "'" }, (",\n"), 120)
            dbDifffDataToAdd = joinList(diffDbResult.collect { "'" + it[0] + "'" }, ", ", 120)
            def objectQuery = ""
            switch (objectType.toLowerCase()) {
                case "constraint":
                    objectQuery = "SELECT 'ALTER TABLE ' || table_name || ' ENABLE CONSTRAINT ' || CONSTRAINT_NAME; --enable" +
                            "--ALTER TABLE ' || table_name || ' DROP CONSTRAINT ' || CONSTRAINT_NAME; --drop" +
                            "FROM user_constraints " +
                            "WHERE CONSTRAINT_NAME IN( " + dbDifffDataToAdd + ");"
                    targetDbResult
                    objectQuery += "\n\n--ENABLE CONSTRAINT\n" +
                            diffDbResult["ENABLE_"].join("\n")
                    objectQuery += "\n\n--DROP CONSTRAINT\n" +
                            diffDbResult["DROP_"].join("\n")
                    break
                case "index":
                    objectQuery = diffDbResult.collect { it[0].replaceAll(/"|'|,/, "").replaceAll(/^/, "DROP INDEX ").replaceAll(/$/, ";") }.join("\n")
                    objectQuery += "\n\n--DROP INDEX\n" + diffDbResult.collect { "DROP INDEX " + it["INDEX_NAME"] + ";" }.join("\n")
                    break
            }
            reporterLogLn("objectQuery\n$objectQuery")
            diffDbResult.eachWithIndex { it, i ->
                reporterLogLn("-- " + "${i + 1} ".padLeft(5) + it[0])
            }
            dbDifffDataToAdd = dbDifffDataToAdd.replaceAll(/\'\'/, /'/)
            if (objectType.toLowerCase().matches(".*constraint.*")) {
                reporterLogLn("\n--Query <$objectType>:\nIN( $dbDifffDataToAdd );\n\n")
                String refQuery = "$DROP_REF_CONSTRATINTS_ORACLE\nIN( $dbDifffDataToAdd )\n\n"
                reporterLogLn("\n--Query\n $refQuery;")
                def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get drop FK ref from $sourceDb", dbRunTypeRows, refQuery, 0, sourceDb)
                sourceDbResult.each {
                    reporterLogLn("--" + it["DROP_CASCADE"])
                    reporterLogLn(it["DROP_"])
                }
            }
            String refPrimaryKeyQuery = "$DROP_PRIMARY_KEY_ORACLE\nIN( $dbDifffDataToAdd )\n\n"
            reporterLogLn("\n--Query\n $refPrimaryKeyQuery;")
            def sourceDbResult = sourceDbSqlDriver.sqlConRun("Get drop FK ref from $sourceDb", dbRunTypeRows, refPrimaryKeyQuery, 0, sourceDb)
            sourceDbResult.each {
                reporterLogLn("--" + it["DROP_CASCADE"])
                reporterLogLn(it["DROP_"])
            }
        } else {
            reporterLogLn("\n<$objectType> Source = Target")

        }
        tangAssert.assertEquals(diffCount, 0, "Should have no diff", "Should have no diff: diffCount $diffCount > 0 ")

    }


}
