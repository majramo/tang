package base

import dtos.SettingsHelper
import dtos.base.Constants
import dtos.base.SqlHelper
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.*
import reports.ReporterHelper

import java.text.DateFormat
import java.text.SimpleDateFormat

import static corebase.GlobalConstants.REPORT_NG_REPORTING_TITLE
import static dtos.base.Constants.*

public class AnySqlCompareTest {
    private final static Logger log = Logger.getLogger("ASC   ")
    protected final static ReporterHelper reporterHelper = new ReporterHelper()
    public static final String BREAK_CLOSURE = "BreakClosure"
    public static final String REPOSITORY_DB = "repository"

    protected SqlHelper sourceDbSqlDriver = null
    protected SqlHelper targetDbSqlDriver = null
    protected SqlHelper repositroyDbSqlDriver = null
    public TangDbAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    def applicationConf = settingsHelper.applicationConf
    private static boolean settingChanged
    public static final String ENABLED = "enabled"
    public static final String SOURCE_VALUE = "sourceValue"
    private final String DATABASE_STRUCTURE = "DATABASE_STRUCTURE"
    private final String DATABASE_CONSTRAINTS = "DATABASE_CONSTRAINTS"
    private final String DATABASE_SOURCE = "DATABASE_SOURCE"
    private final String DATABASE_INDEXES = "DATABASE_INDEXES"
    public static final String SCHEMA_NAME = "SCHEMA_NAME"
    public static final String SOURCE_SQL = "SOURCE_SQL"
    public static final String TIME = "TIME"
    public static final String TARGET_SQL = "TARGETSQL"
    public static final String ROW_ID = "ROW_ID"
    public static final String TARGET_DB = "targetDb"
    public static final String THRESHOLD = "threshold"
    public static final String COMMENTS = "comments"
    public static final String ROW = "row"
    public static final String BY = "by"

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext testContext) {
        setup()
    }

    @Parameters(["environment", "sourceDb"])

    @BeforeTest(alwaysRun = true)
    public void beforeTest(ITestContext testContext, @Optional String environment, @Optional String database) {
        log.info("BeforeTest " + testContext.getName())
        System.setProperty(REPORT_NG_REPORTING_TITLE, "Db Compare")
    }


    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext testContext) {
        log.info("BeforeClass " + testContext.getName())
        tangAssert = new TangDbAssert()
        sourceDbSqlDriver = (SqlHelper) testContext.getAttribute(SOURCE_SQL_HELPER)
        targetDbSqlDriver = (SqlHelper) testContext.getAttribute(TARGET_SQL_HELPER)
        log.info("BeforeClass " + testContext.getName())

    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestContext testContext) {
        log.info("BeforeMethod " + testContext.getName())

    }

    @AfterClass(alwaysRun = true)
    public void afterClass(ITestContext testContext) {
        log.info("afterClass " + testContext.getName())
    }

    def getSourceDbRowsResult(String dbQuery) {
        return getDbResult(sourceDbSqlDriver, dbQuery, Constants.dbRunTypeRows)
    }

    def getTargetDbRowsResult(String dbQuery) {
        return getDbResult(targetDbSqlDriver, dbQuery, Constants.dbRunTypeRows)
    }

    def getSourceDbFirstRowResult(String dbQuery) {
        return getDbResult(sourceDbSqlDriver, dbQuery, Constants.dbRunTypeFirstRow)
    }

    def getTargetDbFirstRowResult(String dbQuery) {
        return getDbResult(targetDbSqlDriver, dbQuery, Constants.dbRunTypeFirstRow)
    }


    def getDbResult(SqlHelper sqlDriver, String dbQuery, dbQueryType) {
        sqlDriver.dbQueryType = dbQueryType
        sqlDriver.dbQuery = dbQuery
        return sqlDriver.getDb_result(sqlDriver.dbName)
    }

    protected void truncate(SqlHelper sqlHelper, String targetSql) {
        sqlHelper.dbQueryType = Constants.dbRunTypeFirstRow
        sqlHelper.dbQuery = targetSql
        def dbType = getDbType(sqlHelper.dbName)
        def skipException = settings.skipException."$dbType"
        println sqlHelper.executeAndSkipException(sqlHelper.dbName, targetSql, skipException)
    }

    protected void compareSourceEqualsTarget(sourceValue, targetSql, threshold) {
        compareSourceValueToTarget(sourceValue, targetSql, threshold)
    }

    protected void compareAllFromDb1InDb2(ITestContext testContext, String sourceSql, String targetSql, threshold, comments, ArrayList tableFieldsToExcludeMap = [], String tableFieldToExclude = "", lastSourceColumn = "", system = "") {
        boolean isCountQuery
        if(!(lastSourceColumn == "SAVE")) {
            reporterLogLn("\nComparing (reading) structure from db\n#######\n");
        }else{
            reporterLogLn("\nSaving structure in db\n#######\n");
        }
        reporterLogLn("Source: <${sourceDbSqlDriver.dbName}> ");
        reporterLogLn("Source Sql:\n$sourceSql\n");
        if(!(lastSourceColumn == "SAVE")){
            reporterLogLn("");
            reporterLogLn("Target: <$targetDbSqlDriver.dbName> ");
            reporterLogLn("Target Sql:\n$targetSql\n");
            reporterLogLn("Threshold: <$threshold%> ");
        }
        reporterLogLn("");
        reporterLogLn "####################"
        def sourceResult = getSourceDbRowsResult(sourceSql)
        def targetResult = getTargetDbRowsResult(targetSql)
        if(lastSourceColumn == "SAVE"){
            def size = sourceResult.size()
            //Save sourceResult in Repository database
            reporterLogLn("Saving system <$system> <$size> rows to db")
            saveResultToDb(testContext, system, comments, sourceResult, sourceSql)
            reporterLogLn("Saved to database")
            return
        }else{
            if(lastSourceColumn == "READ"){
                //READ sourceResult from Repository database
                reporterLogLn("Reading system <$system> from db")
                targetResult = readSavedResultFromDb(testContext, system, comments, sourceSql)
            }
        }

        if(tableFieldsToExcludeMap.size() && !tableFieldToExclude.isEmpty()){
            tableFieldsToExcludeMap.each {exclude ->
                def found = sourceResult.findAll {it[tableFieldToExclude] ==  exclude}
                if(found.size()){
                    sourceResult -= found
                    targetResult -= found
                }
            }
        }
        if (sourceSql.replace(" ", "").toLowerCase().contains("SELECT COUNT(1) COUNT_".replace(" ", "").toLowerCase())) {
            isCountQuery = true
        }

        equals(sourceResult, targetResult, threshold, isCountQuery, "should be the same")
    }

    protected void compareSourceValueToTarget(String sourceValue, String targetSql, threshold) {
        reporterLogLn("Source value:\n$sourceValue\n");
        reporterLogLn("Target: <$targetDbSqlDriver.dbName> ");
        reporterLogLn("Target Sql:\n$targetSql\n");
        reporterLogLn("Threshold: <$threshold%> ");
        reporterLogLn "####################"
        def targetDbResult = getTargetDbRowsResult(targetSql)
        def targetValue = "7777777"
        try{
            targetValue = targetDbResult[0]["COUNT_"]
        } catch (Exception e) {
            reporterHelper.log("Exception $e")
            reporterHelper.log("targetValue set to 7777777")
        }
        equals(sourceValue, targetValue, threshold,  "should be the same")
    }
    protected void equals(sourceValue, targetValue, threshold,  msg = "") {
        boolean diffLessThanThreshold = true
        reporterLogLn("");
        float tmpSourceValue = sourceValue.toFloat().trunc(2)
        float tmpTargetValue = targetValue.toFloat().trunc(2)
        reporterLogLn("Source value: <$tmpSourceValue>");
        reporterLogLn("Target value: <$tmpTargetValue>");
        def biggestValue = [tmpSourceValue , tmpTargetValue].max()
        def diff = tmpSourceValue - tmpTargetValue
        float tmpDiffProc = 0
        if (biggestValue > 0) {
            try {
                tmpDiffProc = (100 * diff / biggestValue).trunc(2)
            } catch (Exception e) {
                tmpDiffProc = 100
            }
        }

        if (tmpDiffProc.abs() > threshold ) {
            diffLessThanThreshold = false
        }
        reporterLogLn ""
        reporterLogLn "####################"
        reporterLogLn("Diff size: <$diff> <$tmpDiffProc%>");
        reporterLogLn("Threshold: <$threshold%> ");

        reporterLogLn ""
        tangAssert.assertTrue(diffLessThanThreshold, "Values should be the same", "Dif is <$diff: $tmpDiffProc%>");
    }


    protected void equals(ArrayList sourceMap, ArrayList targetMap, thresholdString, isCountQuery = false, msg = "", String tableFieldsFileColumn = "", String tableFieldToExclude = "", sourceDb = "") {
        boolean thresholdPassed = true
        ArrayList tableFieldsToExcludeMap
        Float thresholdValue = 0
        try{
            thresholdValue = Float.parseFloat(thresholdString)
        }catch(Exception e){
            thresholdValue = 0
        }
        if(!tableFieldsFileColumn.isEmpty()){
            reporterLogLn("TableFieldsFileColumn: <$tableFieldsFileColumn>");
        }
        def sourceMapSize = sourceMap.size()
        def targetMapSize = targetMap.size()
        if(sourceMap.size() == 1){
            try{
                sourceMapSize = sourceMap[0]["COUNT_"]
                targetMapSize = targetMap[0]["COUNT_"]
            }catch(Exception e){
                throw new Exception("Assuming result is a COUNT_ value", e)
            }
        }
        def diffCount = sourceMapSize - targetMapSize

        float tmpSizeDiffProc = 100
        if (sourceMapSize > 0) {
            tmpSizeDiffProc = 100 * diffCount / sourceMapSize
        } else {
            if (targetMapSize == 0) {
                tmpSizeDiffProc = 0
            }
        }
        float diffSizeProc = tmpSizeDiffProc.trunc(2)
        reporterLogLn("");
        reporterLogLn("Source size: <$sourceMapSize>");
        reporterLogLn("Target size: <$targetMapSize>");

        if (isCountQuery) {
            reporterLogLn("Source result: <$sourceMap>");
            reporterLogLn("Target result: <$targetMap>");
        }


        reporterLogLn("");

        if (diffCount != 0) {
            reporterLogLn("Showing max no of diff: " + settings.maxDiffsToShow)
        }

        int diffDataCounter = 0
        try {
            sourceMap.eachWithIndex { it, index ->
                if (diffDataCounter >= settings.maxDiffsToShow) {
                    throw new Exception(BREAK_CLOSURE, new Throwable(BREAK_CLOSURE))
                }
                if (!targetMap.contains(it)) {
                    diffDataCounter++
                    reporterLogLn "  Missing in target: $diffDataCounter:$index <$it>"
                }
            }
        } catch (groovy.lang.MissingPropertyException e) {
            Reporter.log("Source and target result must have same columnname!")
            throw new SkipException("Source and target result must have same columnname")
        } catch (Exception e) {
            if (!e.cause.toString().contains(BREAK_CLOSURE)) {
                throw e
            }
        }

        diffDataCounter = 0
        try {
            targetMap.eachWithIndex { it, index ->
                if (diffDataCounter >= settings.maxDiffsToShow) {
                    throw new Exception(BREAK_CLOSURE, new Throwable(BREAK_CLOSURE))
                }
                if (!sourceMap.contains(it)) {
                    diffDataCounter++
                    reporterLogLn "  Missing in source: $diffDataCounter:$index <$it>"
                }
            }
        } catch (groovy.lang.MissingPropertyException e) {
            Reporter.log("Source and target result must have same columnname!")
            throw new SkipException("Source and target result must have same columnname")
        } catch (Exception e) {
            if (!e.cause.toString().contains(BREAK_CLOSURE)) {
                throw e
            }
        }

        float tmpDataDiffProc = 100
        if (sourceMapSize > 0) {
            tmpDataDiffProc = 100 * diffDataCounter / sourceMapSize
        } else {
            if (targetMapSize == 0) {
                tmpDataDiffProc = 0
            }
        }
        float diffDataCounterProc = tmpDataDiffProc.trunc(2)

        switch (thresholdString) {
            case ~/^\+.*/:
                if (diffSizeProc > thresholdValue || diffDataCounterProc > thresholdValue) {
                    thresholdPassed = false
                }
                break
            case ~/^\-.*/:
                if (diffSizeProc < thresholdValue || diffDataCounterProc < thresholdValue) {
                    thresholdPassed = false
                }
                break
            default:
                if (diffSizeProc.abs() > thresholdValue || diffDataCounterProc.abs() > thresholdValue) {
                    thresholdPassed = false
                }
                break
        }

        reporterLogLn ""
        reporterLogLn "####################"
        reporterLogLn("Diff size: <$diffCount> <$diffSizeProc%>");
        reporterLogLn("Diff data: <$diffDataCounter> <$diffDataCounterProc%>");
        reporterLogLn("Threshold: <$thresholdString%> (+10: positiv, -10: negative, 10: abs( )) diff");

        reporterLogLn ""
        tangAssert.assertTrue(thresholdPassed, "List should be equal", "Diff is <Size $diffCount: $diffSizeProc%> <Data $diffDataCounter: $diffDataCounterProc>");
    }

    protected int equals(String message, Map map1, Map map2) {
        reporterLogLn "<Map>"
        int diffCounter = 1

        reporterLogLn("Showing max no of diff: " + settings.maxDiffsToShow)

        try {
            map1.eachWithIndex { key, value, index ->
                if (diffCounter >= settings.maxDiffsToShow) {
                    throw new Exception(BREAK_CLOSURE)
                }
                try {
                    if (map2[key] != value) {
                        diffCounter++
                        reporterLogLn "  Missing $diffCounter:$index <$key: $value>"
                    }
                } catch (groovy.lang.MissingPropertyException e) {
                    reporterLogLn "  Missing $diffCounter:$index <$key: $value>"
                }

            }
        } catch (Exception e) {
        }
        reporterLogLn ""
        return diffCounter

    }

    public void reporterLogLn(message = "") {
        Reporter.log("$message")
//        Reporter.log("$message")
    }


    protected void setSourceSqlHelper(ITestContext testContext, dbName) {
        sourceDbSqlDriver = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        testContext.setAttribute(SOURCE_SQL_HELPER, sourceDbSqlDriver)
    }

    protected void setTargetSqlHelper(ITestContext testContext, dbName) {
        targetDbSqlDriver = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        testContext.setAttribute(TARGET_SQL_HELPER, targetDbSqlDriver)
    }

    protected void setRepositorySqlHelper(ITestContext testContext, dbName) {
        repositroyDbSqlDriver = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        testContext.setAttribute(REPOSITORY_SQL_HELPER, repositroyDbSqlDriver)
    }

    public void skipTest(msg) {
        reporterLogLn("Test is skipped: $msg")
        throw new SkipException("Test is skipped: $msg")
    }

    public String getDbType(dbName) {
        String dbDriverName = settings."$dbName".dbDriverName
        String icon = ""
        switch (dbDriverName.toLowerCase()) {
            case ~/.*oracle.*/:
                icon = "oracle"
                break
            case ~/.*sqlserver.*/:
                icon = "sqlserver"
                break
            case ~/.*mysql.*/:
                icon = "mysql"
                break
            case ~/.*db2.*/:
                icon = "db2"
                break
        }
        return icon
    }


    public void setup() {
        if (!settingChanged) {
            new InitDbSettings().setupDatabases()
            settingChanged = true
        }
    }

    private saveResultToDb(ITestContext testContext, system, comments, sourceResult, sourceSql){
        def repositoryTable = DATABASE_STRUCTURE

        //Connect to DB , delete old values and save new values
        if(sourceResult.size()) {

            def savedComments = cleanUp(comments)

            def fields = sourceResult[0].keySet().join(",")
            (repositoryTable, fields) = getRepositoryDatabase(sourceSql)
            Reporter.log("Saving table <$repositoryTable>")

            def fieldsStr = ""
            def TABBS = "$TABB$TABB$TABB"
            fields.split(",").collect().each{
                fieldsStr += "$TABBS\"${it.trim()}\" VARCHAR2(1000 CHAR),\n"
            }
            fieldsStr = fieldsStr[0..fieldsStr.length()-3]
            def createTableQuery = """\n
            drop  TABLE "$repositoryTable" ;
            CREATE TABLE "$repositoryTable"
               (
            --Common columns
            "ROW_ID" int,
            "SCHEMA_NAME" VARCHAR2(50 CHAR),
            "SOURCE_SQL" VARCHAR2(50 CHAR),
            "TIME" VARCHAR2(20 CHAR),

            --Custom coulmns
$fieldsStr
            ) SEGMENT CREATION IMMEDIATE;"""
            println createTableQuery
            SqlHelper repositroyDbSqlDriver = testContext.getAttribute(REPOSITORY_SQL_HELPER)
            def time = getCurrentTime()
            def dbSelectQuery = "SELECT COUNT(*) COUNT_ FROM $repositoryTable " +
                    "WHERE $SCHEMA_NAME = '$system' "

            String deleteQuery = "DELETE $repositoryTable WHERE $SCHEMA_NAME = '" + system  + "'"
            def dbResult = repositroyDbSqlDriver.execute(REPOSITORY_DB, deleteQuery)
            dbResult = getDbResult(repositroyDbSqlDriver, dbSelectQuery, Constants.dbRunTypeRows)
            if(dbResult["COUNT_"][0] != 0 ){
                Reporter.log("Can't execute $deleteQuery")
                throw new SkipException("Can't truncate table <$repositoryTable> ")
            }
            def dbInsertQuery = "INSERT INTO $repositoryTable " +
                    "($SCHEMA_NAME, $SOURCE_SQL, $TIME, $ROW_ID, $fields) " +
                    "values " +
                    "('$system', '$savedComments', '$time', "
            repositroyDbSqlDriver.dbQueryRun = ""
            //Each field will be singleQuoted ''
            sourceResult.eachWithIndex { Map it, index ->
                it.findAll().each { i->
                    String value = i.value
                    if(value != "" && value != null){
                        value = value.replaceAll(/'/, /''/)
                    }
                    i.value = "'$value'"
                }
                def values = it.values().join (",")
                String insertQuery = "$dbInsertQuery  $index, " + values + "  )"
                dbResult = repositroyDbSqlDriver.execute(REPOSITORY_DB, insertQuery)
            }
        }
    }

    private readSavedResultFromDb(ITestContext testContext, system, comments, String sourceSql){
        //Connect to DB and read values
        def repositoryTable
        def fields
        (repositoryTable, fields) = getRepositoryDatabase(sourceSql)

        if(repositoryTable == ""){
           reporterLogLn("Could not decide Database table")
           return
        }

//         ToDO: Fix switch!
//        switch (sourceSqlUpperCase) {
//            case ~/.*ALL_TAB_COLS.*/:
//                repositoryTable = DATABASE_STRUCTURE
//                break
//            case ~/USER_CONSTRAINTS/:
//                repositoryTable = DATABASE_CONSTRAINTS
//                break
//            case ~/DBA_SOURCE/:
//                repositoryTable = DATABASE_SOURCE
//                break
//            case ~/USER_INDEXES/:
//                repositoryTable = DATABASE_INDEXES
//                 break
//            default:
//                return
//        }


        reporterLogLn("Reading Targetsaved system<$system> from Table <$repositoryTable>")

        def savedSourceSql = cleanUp(comments)
        SqlHelper repositroyDbSqlDriver = testContext.getAttribute(REPOSITORY_SQL_HELPER)
        def dbQuery = "SELECT * FROM $repositoryTable " +
                "WHERE $SCHEMA_NAME = '$system' " +
                "AND $SOURCE_SQL =  '$savedSourceSql'" +
                "ORDER BY $ROW_ID"
        def dbResult = getDbResult(repositroyDbSqlDriver, dbQuery, Constants.dbRunTypeRows)

        ArrayList dbResultModified = new ArrayList()
        reporterLogLn("${dbResult.size()} rows read in system <$system> from Table <$repositoryTable>")

        dbResult.findAll().each {
            it.keySet().removeAll([SCHEMA_NAME, SOURCE_SQL, TIME, ROW_ID])
            dbResultModified.add(it)
        }
        return dbResultModified
    }

    private getRepositoryDatabase(String sourceSql) {
        def repositoryTable
        def sourceSqlUpperCase = sourceSql.toUpperCase()
        if (sourceSqlUpperCase.contains("USER_TAB_COLS")) {
            repositoryTable = DATABASE_STRUCTURE
        }
        if (sourceSqlUpperCase.contains("USER_CONSTRAINTS")) {
            repositoryTable = DATABASE_CONSTRAINTS
        }
        if (sourceSqlUpperCase.contains("DBA_SOURCE")) {
            repositoryTable = DATABASE_SOURCE
        }
        if (sourceSqlUpperCase.contains("USER_INDEXES")) {
            repositoryTable = DATABASE_INDEXES
        }
        def fields = sourceSqlUpperCase.replaceAll(/\n/, ' ').replaceAll(/\n/, ' ').replaceAll(/FROM.*/, '').replaceAll(/.*(DISTINCT|SELECT)/, '').replaceAll("FROM.*", '').replaceAll(" ", "")
        reporterLogLn("Repository table <$repositoryTable>")
        reporterLogLn("Fields <$fields>")
        return [repositoryTable, fields]
    }

    def getCurrentTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss")
        return dateFormat.format(new Date())
    }

    def  cleanUp(String sourceSql){
        def savedSourceSql = sourceSql.replaceAll(/( |\.|,|\')/, "_").replaceAll( "\n", "___").replaceAll(/[åäöÅÄÖ]/,'_');
        return savedSourceSql
    }

}
