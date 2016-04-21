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

import static corebase.GlobalConstants.REPORT_NG_REPORTING_TITLE
import static dtos.base.Constants.*

public class AnySqlCompareTest {
    private final static Logger log = Logger.getLogger("ASC   ")
    protected final static ReporterHelper reporterHelper = new ReporterHelper()
    public static final String BREAK_CLOSURE = "BreakClosure"

    protected SqlHelper sourceSqlDriver = null
    protected SqlHelper targetSqlDriver = null
    public TangDbAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    def applicationConf = settingsHelper.applicationConf


    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext testContext) {

    }

    @Parameters(["environment", "sourceDb"])
    @BeforeTest(alwaysRun = true)
    public void beforeTest(ITestContext testContext, @Optional String environment, @Optional String database) {
        log.info("BeforeTest " + testContext.getName())
        System.setProperty(REPORT_NG_REPORTING_TITLE, "Crm Sql Test Compare")
    }



    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext testContext) {
        log.info("BeforeClass " + testContext.getName())
        tangAssert = new TangDbAssert()
        sourceSqlDriver = (SqlHelper) testContext.getAttribute(SOURCE_SQL_HELPER)
        targetSqlDriver = (SqlHelper) testContext.getAttribute(TARGET_SQL_HELPER)
        log.info("BeforeClass " + testContext.getName())

    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestContext testContext) {
        log.info("BeforeMethod " + testContext.getName())

        log.info("BeforeMethod " + testContext.getName())

    }

    @AfterClass(alwaysRun = true)
    public void afterClass(ITestContext testContext) {
        log.info("afterClass " + testContext.getName())
    }

    def getSourceDbRowsResult( String dbQuery) {
        return getDbResult(sourceSqlDriver, dbQuery, Constants.dbRunTypeRows)
    }

    def getTargetDbRowsResult(String dbQuery) {
        return getDbResult(targetSqlDriver, dbQuery, Constants.dbRunTypeRows)
    }

    def getSourceDbFirstRowResult( String dbQuery) {
        return getDbResult(sourceSqlDriver, dbQuery, Constants.dbRunTypeFirstRow)
    }

    def getTargetDbFirstRowResult(String dbQuery) {
        return getDbResult(targetSqlDriver, dbQuery, Constants.dbRunTypeFirstRow)
    }


    def getDbResult(SqlHelper sqlDriver, String dbQuery, dbQueryType) {
        sqlDriver.dbQueryType = dbQueryType
        sqlDriver.dbQuery = dbQuery
        return sqlDriver.getDb_result(sqlDriver.dbName)
    }



    protected void compareSourceEqualsTarget(sourceSql, targetSql, threshold) {
        compareAllFromDb1InDb2(sourceSql, targetSql, threshold)
    }

    protected void compareAllFromDb1InDb2(String sourceSql, String targetSql, threshold) {
        def sourceResult = getSourceDbRowsResult(sourceSql)
        def targetResult = getTargetDbRowsResult(targetSql)
        reporterLogLn("Source: <${sourceSqlDriver.dbName}> ");
        reporterLogLn("Target: <$targetSqlDriver.dbName> ");
        reporterLogLn("Source Sql: <$sourceSql> ");
        reporterLogLn("Target Sql: <$targetSql> ");
        reporterLogLn("Threshold: <$threshold %> ");
        equals(sourceResult, targetResult, threshold, "ska vara lika")
    }


    protected void equals(ArrayList sourceMap, ArrayList targetMap, threshold, msg ="") {
        boolean diffLessThanThreshold = true
        def diffCount = sourceMap.size() - targetMap.size()
        def totalCount = sourceMap.size() + targetMap.size()
        float tmpSizeDiffProc = 0
        try{
            tmpSizeDiffProc = 100 * diffCount / totalCount
        }catch (Exception e){
            tmpSizeDiffProc = 100
        }
        float diffSizeProc = tmpSizeDiffProc.trunc(2)
        reporterLogLn("");
        reporterLogLn("Source size: <${sourceMap.size()}>");
        reporterLogLn("Target size: <${targetMap.size()}>");
        reporterLogLn("");

        if(diffCount != 0){
            reporterLogLn("Showing max no of diff: " + settings.maxDiffsToShow)
        }

        int diffDataCounter = 0
        try {
            sourceMap.eachWithIndex { it, index ->
                if (diffDataCounter >= settings.maxDiffsToShow) {
                    throw new Exception(BREAK_CLOSURE)
                }
                if (!targetMap.contains(it)) {
                    diffDataCounter++
                    reporterLogLn "  Missing in target: $diffDataCounter:$index <$it>"
                }
            }
        } catch (Exception e) {
        }

        try {
            targetMap.eachWithIndex { it, index ->
                if (diffDataCounter >= settings.maxDiffsToShow) {
                    throw new Exception(BREAK_CLOSURE)
                }
                if (!sourceMap.contains(it)) {
                    diffDataCounter++
                    reporterLogLn "  Missing in source: $diffDataCounter:$index <$it>"
                }
            }
        } catch (Exception e) {
        }


        float tmpDataDiffProc = 0
        if(totalCount>0) {
            try {
                tmpDataDiffProc = 100 * diffDataCounter / totalCount
            } catch (Exception e) {
                tmpDataDiffProc = 100
            }
        }
        float diffDataCounterProc = tmpDataDiffProc.trunc(2)

        if(diffSizeProc.abs() > threshold || diffDataCounterProc.abs() > threshold){
            diffLessThanThreshold = false
        }
        reporterLogLn ""
        reporterLogLn "####################"
        reporterLogLn("Diff size: <$diffCount> <%$diffSizeProc>");
        reporterLogLn("Diff data: <$diffDataCounter)> <%$diffDataCounterProc>");
        reporterLogLn("Threshold: <$threshold %> ");

        reporterLogLn ""
        tangAssert.assertTrue(totalCount > 0, "Det ska finnas data i tabellerna", "Det finns inget data i tabellerna <$totalCount>");
        tangAssert.assertTrue(diffLessThanThreshold, "Listor ska vara lika", "Diffen är <Size $diffCount: $diffSizeProc %> <Data $diffDataCounter: $diffDataCounterProc>");
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
        sourceSqlDriver = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        testContext.setAttribute(SOURCE_SQL_HELPER, sourceSqlDriver)
    }

    protected void setTargetSqlHelper(ITestContext testContext, dbName) {
        targetSqlDriver = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        testContext.setAttribute(TARGET_SQL_HELPER, targetSqlDriver)
    }

    public void skipTest(msg) {
        reporterLogLn("Test is skipped: $msg")
        throw new SkipException("Test is skipped: $msg")
    }

    public String getDbIcon(dbName){
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

}
