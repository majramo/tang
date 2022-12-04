package base

import dtos.SettingsHelper
import dtos.base.SqlHelper
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.*
import reports.ReporterHelper

import static dtos.base.Constants.*
import static excel.ExcelObjectProvider.getObjects

public class AnySqlTest {

    private final static Logger log = LogManager.getLogger("AT   ")
    protected final static ReporterHelper reporterHelper = new ReporterHelper()

    protected SqlHelper driver
    protected String database
    private String testBrowser
    public TangDbAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    def applicationConf = settingsHelper.applicationConf
    private static boolean settingChanged

    public Map<String, String> context = [:]

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext testContext) {
        setup()

    }

    @Parameters(["environment", "database"])
    @BeforeTest(alwaysRun = true)
    public void beforeTest(ITestContext testContext, @Optional String environment, @Optional String database) {
        log.info("BeforeTest " + testContext.getName())
        if (StringUtils.isBlank(database)) {
            if (environment != null && applicationConf."$environment".db != null && applicationConf."$environment".db != "") {
                database = applicationConf."$environment".db;
            } else {
                database = settings.defaultDatabase
            }
        }
        testContext.setAttribute(DATABASE, database)
        String databaseToRun = settings."$database".dbDriverName
        if(databaseToRun != ""){
            if(databaseToRun.contains("oracle")){
                databaseToRun = "oracle"
            }else{
                databaseToRun = databaseToRun.replaceAll(".*:", "")
            }
        }
        testContext.setAttribute(DATABASE_VENDOR_1, databaseToRun)
        testContext.setAttribute(ENVIRONMENT, environment)
        println testContext.getName()

    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext testContext) {
        log.info("BeforeClass " + testContext.getName())
        database = testContext.getAttribute(DATABASE)
        String environment = testContext.getAttribute(ENVIRONMENT)
        tangAssert = new TangDbAssert()
        Reporter.log("environment $environment")
        Reporter.log("DATABASE $database")
        if (database == "" || database == null) {
            Reporter.log("Default database is empty <$database>")
        }else{
            try {
                Reporter.log("Default database <$database>")
                log.info(testContext.getOutputDirectory())
                driver = new SqlHelper(null, log, database, settings.dbRun, settings)
                tangAssert.assertTrue(driver.isConnectionOk(database), "Connection $database is not working")

                testContext.setAttribute(SQL_HELPER, driver)
                //tangAssert = new TangAssert(driver)
            } catch (Exception skipException) {
                Reporter.log("Connection to db <$database> failed")
                log.error(skipException)
                throw new SkipException(skipException.toString())
            }
        }
    }


    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestContext testContext) {
        log.info("BeforeMethod " + testContext.getName())
        driver = (SqlHelper) testContext.getAttribute(SQL_HELPER)
        log.info("BeforeMethod " + testContext.getName())

    }

    @AfterClass(alwaysRun = true)
    public void afterClass(ITestContext testContext) {
        log.info("afterClass " + testContext.getName())

    }


    public getDbResult(message, dbRunType, query, dbRecordLine) {
        if (settingsHelper == null) {
            settingsHelper = SettingsHelper.getInstance()
            settings = settingsHelper.settings
        }
        Reporter.log("$message")
        Reporter.log("Query<$database>: \n$query\n")
        Reporter.log("dbRecordLine: $dbRecordLine")
        def dbResult = driver.sqlConRun(message, dbRunType, query, dbRecordLine, database)
//        Reporter.log("\nResult\n###")
        tangAssert.assertNotNull(dbResult, "sqlConRun", "Can't get result from DB")

        Reporter.log("dbResult size<" + dbResult.size() + ">")
        return dbResult
    }

    public void checkDuplicateOfRowsRegardingFieldsInTableQuery(String table, String field) {
        String query = "SELECT $field, COUNT(*) count FROM $table \n"
        if (!field.contains(",")) {
            query += "WHERE  NOT $field IS NULL \n"
        }
        query += """GROUP BY $field
        HAVING COUNT(*) > 1;"""
        def dbResult = getDbResult("Checking duplicates of <$field> in table <$table> ", dbRunTypeRows, query, 0)
        int counter = 1
        if(dbResult.size()>0){
            int dbResultsToPrint = dbResult.size() < MAX_DB_RESULTS_TO_PRINT ? dbResult.size():MAX_DB_RESULTS_TO_PRINT
            Reporter.log("Showing first $dbResultsToPrint items")
            dbResult[0..dbResultsToPrint-1].each {
                Reporter.log(counter++ + " " + it.toString())
            }
        }
        tangAssert.assertTrue(dbResult.size() == 0, "No duplicates", "Result should have no duplicates")
    }

    public void checkNullValueInFieldInTableQuery(String table, String field) {
        checkValueInFieldInTableQuery(table, field, " IS NULL")
    }

    public void checkEmptyValueInFieldInTableQuery(String table, String field) {
        checkValueInFieldInTableQuery(table, field, " = ''")
    }


    public void checkValueInFieldInTableQuery(String table, String field, String value) {
        String query = """SELECT * FROM $table
        WHERE $field $value;"""
        def dbResult = getDbResult("Checking field <$field> with value <$value> in table <$table> ", dbRunTypeRows, query, 0)
        int counter = 1
        if(dbResult.size()>0){
            int dbResultsToPrint = dbResult.size() < MAX_DB_RESULTS_TO_PRINT ? dbResult.size():MAX_DB_RESULTS_TO_PRINT
            Reporter.log("Showing first $dbResultsToPrint items ")
            dbResult[0..dbResultsToPrint-1].each {
                Reporter.log(counter++ + " " + it.toString())
            }
        }
        tangAssert.assertTrue(dbResult.size() == 0, "No value $value", "Result should have value $value")
    }

    public void setSqlHelper(ITestContext testContext, SqlHelper driver) {
        this.driver = (SqlHelper) testContext.getAttribute(SQL_HELPER)
        database = (String) testContext.getAttribute(DATABASE)
    }

    public void setup() {
        if(!settingChanged) {
            new InitDbSettings().setupDatabases()
            settingChanged = true
        }
    }

    public setIssueLink(String issueStr){

        if(!issueStr.isEmpty() && !issueStr.equals("-")) {
            if(!issueStr.isEmpty()) {
                def issueLinkStr = issueStr
                if(settings["issueLink"]){
                    issueLinkStr = String.format(settings.issueLink, issueStr, issueStr)
                }
                Reporter.getCurrentTestResult().setAttribute(ISSUE_LINK, issueLinkStr)
            }
        }

    }
}
