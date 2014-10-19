package base

import dtos.SettingsHelper
import dtos.base.SqlHelper
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.*

import static dtos.base.Constants.*

public class AnySqlTest {

    private final static Logger log = Logger.getLogger("AT   ")

    protected SqlHelper driver
    protected String database
    private String testBrowser
    public TangDbAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    def applicationConf = settingsHelper.applicationConf

    public Map<String, String> context = [:]

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext testContext) {

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
            databaseToRun = databaseToRun.replaceAll(".*:", "")
        }
        testContext.setAttribute(DATABASE_VENDOR, databaseToRun)
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
        Reporter.log(database)
        try {
            log.info(testContext.getOutputDirectory())
            driver = new SqlHelper(null, log, database, settings.dbRun, settings)
            tangAssert.assertTrue(driver.isConnectionOk(database), "Connection $database is not working")

            testContext.setAttribute(SQL_HELPER, driver)
            //tangAssert = new TangAssert(driver)
        } catch (Exception skipException) {
            log.error(skipException)
            throw new SkipException(skipException.toString())
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
        Reporter.log("Query: $query")
        Reporter.log("dbRecordLine: $dbRecordLine")
        def dbResult = driver.sqlConRun(message, dbRunType, query, dbRecordLine, database)
//        Reporter.log("\nResult\n###")
        tangAssert.assertNotNull(dbResult, "sqlConRun", "Can't get result from DB")

        Reporter.log("dbResult size<" + dbResult.size() + ">")
        return dbResult
    }

    public void checkDuplicateOfRowsRegardingFieldsInTableQuery(String table, String[] fields) {
        String fieldsString = fields.join(",")
        String query = "SELECT $fieldsString, COUNT(*) FROM $table \n"
        if (!fieldsString.contains(",")) {
            query += "WHERE  NOT $fieldsString IS NULL \n"
        }
        query += """GROUP BY $fieldsString
        HAVING COUNT(*) > 1;"""
        def dbResult = getDbResult("Checking duplicates of <$fieldsString> in table <$table> ", dbRunTypeRows, query, 0)
        int counter = 1
        if(dbResult.size()>0){
            Reporter.log("Showing first item")
            dbResult[0..0].each {
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
            Reporter.log("Showing first item")
            dbResult[0..0].each {
                Reporter.log(counter++ + " " + it.toString())
            }
        }
        tangAssert.assertTrue(dbResult.size() == 0, "No value $value", "Result should have value $value")
    }

    public void setSqlHelper(ITestContext testContext, SqlHelper driver) {
        this.driver = (SqlHelper) testContext.getAttribute(SQL_HELPER)
        database = (String) testContext.getAttribute(DATABASE)
    }
}
