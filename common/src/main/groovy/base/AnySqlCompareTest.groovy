package base

import dtos.SettingsHelper
import dtos.base.Constants
import dtos.base.SqlHelper
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.*
import reports.ReporterHelper

import static corebase.GlobalConstants.REPORT_NG_REPORTING_TITLE
import static dtos.base.Constants.*

public class AnySqlCompareTest {
    private final static Logger log = Logger.getLogger("ASC   ")
    protected final static ReporterHelper reporterHelper = new ReporterHelper()
    public static final String BREAK_CLOSURE = "BreakClosure"

    protected SqlHelper sourceDbSqlDriver = null
    protected SqlHelper targetDbSqlDriver = null
    public TangDbAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    def applicationConf = settingsHelper.applicationConf
    private static boolean settingChanged
    public static final String ENABLED = "enabled"
    public static final String SOURCE_VALUE = "sourceValue"
    public static final String SOURCE_DB = "sourceDb"
    public static final String SOURCE_SQL = "sourceSql"
    public static final String TARGET_SQL = "targetSql"
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
        reporterLogLn("Target: <$sqlHelper.dbName> ");
        reporterLogLn("Target Sql: >>>\n$targetSql\n<<< ");
        sqlHelper.dbQueryType = Constants.dbRunTypeFirstRow
        sqlHelper.dbQuery = targetSql
        def dbType = getDbType(sqlHelper.dbName)
        def skipException = settings.skipException."$dbType"
        Reporter.log("Skiping <$dbType> exception containing <$skipException>")
        println sqlHelper.executeAndSkipException(sqlHelper.dbName, targetSql, skipException)
    }

    protected void compareSourceEqualsTarget(sourceValue, targetSql, threshold) {
        compareSourceValueToTarget(sourceValue, targetSql, threshold)
    }

    protected void compareAllFromDb1InDb2(String sourceSql, String targetSql, threshold, ArrayList tableFieldsToExcludeMap = [], String tableFieldToExclude = "") {
        boolean isCountQuery
        reporterLogLn("Source: <${sourceDbSqlDriver.dbName}> ");
        reporterLogLn("Target: <$targetDbSqlDriver.dbName> ");
        reporterLogLn("");
        reporterLogLn("Source Sql:");
        reporterLogLn("###");
        reporterLogLn(sourceSql);
        reporterLogLn("");
        reporterLogLn("Target Sql:");
        reporterLogLn("###");
        reporterLogLn(targetSql);
        reporterLogLn("");
        reporterLogLn("Threshold: <$threshold%> ");
        reporterLogLn("###");
        def sourceResult = getSourceDbRowsResult(sourceSql)
        def targetResult = getTargetDbRowsResult(targetSql)
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

        equals(sourceResult, targetResult, threshold, isCountQuery, "ska vara lika")
    }

    protected void compareSourceValueToTarget(String sourceValue, String targetSql, threshold) {
        reporterLogLn("Target: <$targetDbSqlDriver.dbName> ");
        reporterLogLn("");
        reporterLogLn("Source value:");
        reporterLogLn("###");
        reporterLogLn(sourceValue);
        reporterLogLn("");
        reporterLogLn("Target Sql:");
        reporterLogLn("###");
        reporterLogLn(targetSql);
        reporterLogLn("");
        reporterLogLn("Threshold: <$threshold%> ");
        reporterLogLn("###");
        def targetDbResult = getTargetDbRowsResult(targetSql)
        def targetValue = targetDbResult[0]["COUNT_"]
        equals(sourceValue, targetValue, threshold,  "ska vara lika")
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
        tangAssert.assertTrue(diffLessThanThreshold, "Värden ska vara lika", "Diffen är <$diff: $tmpDiffProc%>");
    }


    protected void equals(ArrayList sourceMap, ArrayList targetMap, threshold, isCountQuery = false, msg = "", String tableFieldsFileColumn = "", String tableFieldToExclude = "", sourceDb = "") {
        boolean diffLessThanThreshold = true
        ArrayList tableFieldsToExcludeMap
        if(!tableFieldsFileColumn.isEmpty()){
            reporterLogLn("TableFieldsFileColumn: <$tableFieldsFileColumn}>");
            tableFieldsToExcludeMap = getTableFieldsToExcludeMap (tableFieldsFileColumn, sourceDb)
        }


        def diffCount = sourceMap.size() - targetMap.size()
//        def biggestValue = sourceMap.size() + targetMap.size()
        def biggestValue = [ sourceMap.size() + targetMap.size()].max()

        float tmpSizeDiffProc = 0
        if (biggestValue > 0) {
            try {
                tmpSizeDiffProc = 100 * diffCount / biggestValue
            } catch (Exception e) {
                tmpSizeDiffProc = 100
            }
        }
        float diffSizeProc = tmpSizeDiffProc.trunc(2)
        reporterLogLn("");
        reporterLogLn("Source size: <${sourceMap.size()}>");
        reporterLogLn("Target size: <${targetMap.size()}>");

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
            Reporter.log("Source och target result måste ha samma kolumnnamn!")
            throw new SkipException("Source och target result måste ha samma kolumnnamn")
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
            Reporter.log("Source och target result måste ha samma kolumnnamn!")
            throw new SkipException("Source och target result måste ha samma kolumnnamn")
        } catch (Exception e) {
            if (!e.cause.toString().contains(BREAK_CLOSURE)) {
                throw e
            }
        }

        float tmpDataDiffProc = 0
        if (biggestValue > 0) {
            try {
                tmpDataDiffProc = 100 * diffDataCounter / biggestValue
            } catch (Exception e) {
                tmpDataDiffProc = 100
            }
        }
        float diffDataCounterProc = tmpDataDiffProc.trunc(2)

        if (diffSizeProc.abs() > threshold || diffDataCounterProc.abs() > threshold) {
            diffLessThanThreshold = false
        }
        reporterLogLn ""
        reporterLogLn "####################"
        reporterLogLn("Diff size: <$diffCount> <$diffSizeProc%>");
        reporterLogLn("Diff data: <$diffDataCounter> <$diffDataCounterProc%>");
        reporterLogLn("Threshold: <$threshold%> ");

        reporterLogLn ""
        tangAssert.assertTrue(biggestValue > 0, "Det ska finnas data i tabellerna", "Det finns inget data i tabellerna <$biggestValue>");
        tangAssert.assertTrue(diffLessThanThreshold, "Listor ska vara lika", "Diffen är <Size $diffCount: $diffSizeProc%> <Data $diffDataCounter: $diffDataCounterProc>");

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
}
