package base

import corebase.ISeleniumHelper
import corebase.SeleniumDummyHelper
import corebase.SeleniumHelper
import dtos.SettingsHelper
import dtos.base.SqlHelper
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.*
import reports.ReporterHelper

import static dtos.base.Constants.BROWSER
import static dtos.base.Constants.ENVIRONMENT
import static corebase.GlobalConstants.SELENIUM_HELPER
import static corebase.GlobalConstants.WEB_DRIVER

public class AnyTest {

    private final static Logger log = Logger.getLogger("AT   ")
    protected final static ReporterHelper reporterHelper = new ReporterHelper()

    protected ISeleniumHelper driver
    private String testBrowser
    public VemAssert vemAssert
    SettingsHelper settingsHelper = new SettingsHelper()
    def settings = settingsHelper.settings
    def applicationConf = settingsHelper.applicationConf

    public Map<String, String> context = [:]

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext testContext) {

    }

    @Parameters(["environment", "browser"])
    @BeforeTest(alwaysRun = true)
    public void beforeTest(ITestContext testContext, @Optional String environment, @Optional String browser) {
        log.info("BeforeTest " + testContext.getName())
        if (StringUtils.isBlank(browser)) {
            browser = settings.defaultBrowser
        }
        testContext.setAttribute(BROWSER, browser)
        testContext.setAttribute(ENVIRONMENT, environment)
        log.info(testContext.getName())

    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext testContext) {
        log.info("BeforeClass " + testContext.getName())
        String browser = testContext.getAttribute(BROWSER)
        String environment = testContext.getAttribute(ENVIRONMENT)
        Reporter.log("environment $environment")
        Reporter.log("browser $browser")
        try {
            log.info(testContext.getOutputDirectory())
            if (settings.guiRun) {
                driver = new SeleniumHelper().init(browser, testContext.getOutputDirectory())
                driver.setTestName("tang_")
                Reporter.log(SeleniumHelper.getSimpleName())

            } else {
                driver = new SeleniumDummyHelper().init("", "")
                Reporter.log(SeleniumDummyHelper.getSimpleName())

            }
            testContext.setAttribute(SELENIUM_HELPER, driver)
            testContext.setAttribute(WEB_DRIVER, driver)
            vemAssert = new VemAssert(driver)
        } catch (Exception skipException) {
            log.error(skipException)
            throw new SkipException(skipException.toString())
        }
    }


    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestContext testContext) {
        log.info("BeforeMethod " + testContext.getName())
        driver = (ISeleniumHelper) testContext.getAttribute(SELENIUM_HELPER)
        log.info("BeforeMethod " + testContext.getName())

    }

    @AfterClass(alwaysRun = true)
    public void afterClass(ITestContext testContext) {
        log.info("afterClass " + testContext.getName())
        String browser = testContext.getAttribute(BROWSER)
        if (driver != null) {
            driver.quit()
            log.info("quit " + testContext.getName())
        } else {
            log.info("No driver <$browser> is set up " + testContext.getName())
        }
    }


    public static String getHtmlLinkTag(final String fileName) {
        return "<a href='$fileName' target='_blank'>$fileName</a><br/>"
    }

    public void takeScreenshot(boolean take = true) {
        if (take) {
            takeScreenshot("")
        }
    }

    public void takeScreenshot(String message) {
        driver.takeScreenShot(message)
        Reporter.log(message)
    }


    public getDbResult(message, dbRunType, query, ins, dbName) {
        if (settingsHelper == null) {
            settingsHelper = new SettingsHelper()
            settings = settingsHelper.settings
        }
        SqlHelper sqlHelper = new SqlHelper(null, log, "mySqlDb", settings.dbRun, settings)
        def dbResult = sqlHelper.sqlConRun(message, dbRunType, query, ins, dbName)
        return dbResult
    }

}
