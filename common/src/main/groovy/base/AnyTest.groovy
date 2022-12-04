package base

import corebase.ISeleniumHelper
import corebase.SeleniumDummyHelper
import corebase.SeleniumHelper
import dtos.SettingsHelper
import dtos.base.SqlHelper
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.codehaus.groovy.runtime.StackTraceUtils
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.SkipException
import org.testng.annotations.*
import reports.ReporterHelper

import static corebase.GlobalConstants.CHROME
import static corebase.GlobalConstants.FIREFOX
import static corebase.GlobalConstants.HTMLUNIT
import static corebase.GlobalConstants.INTERNET_EXPLORER
import static corebase.GlobalConstants.OPERA
import static corebase.GlobalConstants.REPORT_NG_ESCAPE_OUTPUT_PROPERTY
import static corebase.GlobalConstants.SAFARI
import static dtos.base.Constants.BROWSER
import static dtos.base.Constants.BROWSER_ICON
import static dtos.base.Constants.SELENOID
import static dtos.base.Constants.DATABASE
import static dtos.base.Constants.CR
import static dtos.base.Constants.ENVIRONMENT
import static corebase.GlobalConstants.SELENIUM_HELPER
import static corebase.GlobalConstants.WEB_DRIVER
import static dtos.base.Constants.ISSUE_LINK
import static dtos.base.Constants.dbRunTypeRows

public class /**/AnyTest {

    private final static Logger log = LogManager.getLogger("AnT  ")
    protected final static ReporterHelper reporterHelper = new ReporterHelper()

    protected ISeleniumHelper driver
    protected ISeleniumHelper seleniumHelper
    private String testBrowser
    public TangAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
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
        System.setProperty(REPORT_NG_ESCAPE_OUTPUT_PROPERTY, "false")
        if (StringUtils.isBlank(browser)) {
            browser = settings.defaultBrowser
        }
        testContext.setAttribute(BROWSER, browser)
        testContext.setAttribute(BROWSER_ICON, getBrowserIcon(browser))
        testContext.setAttribute(ENVIRONMENT, environment)
        if(browser.toUpperCase().contains(SELENOID)){
            testContext.setAttribute(SELENOID,  browser.toUpperCase().replaceAll("${SELENOID}.*", SELENOID))

        }
        log.info(testContext.getName())

    }

    String getBrowserIcon(String browser){
        def browserIcon = ""
        switch (browser?.toLowerCase()) {
            case ~/.*firefox.*/:
                browserIcon = FIREFOX
                break
            case ~/.*explorer.*/:
                browserIcon = INTERNET_EXPLORER
                break
            case ~/.*chrome.*/:
                browserIcon = CHROME
                break
            case ~/.*opera.*/:
                browserIcon = OPERA
                break
            case ~/.*safari.*/:
                browserIcon = SAFARI
                break
            case ~/.*htmlunit.*/:
                browserIcon = HTMLUNIT
                break
            default:
                browserIcon = FIREFOX

        }
        return browserIcon
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
                new HostsEntries(browser, environment)
                driver = new SeleniumHelper().init(browser, testContext.getOutputDirectory())
                seleniumHelper = driver
                driver.setTestName("tang_")
                Reporter.log(SeleniumHelper.getSimpleName())

            } else {
                driver = new SeleniumDummyHelper().init("", "")
                seleniumHelper = driver
                Reporter.log(SeleniumDummyHelper.getSimpleName())
            }
            testContext.setAttribute(SELENIUM_HELPER, driver)
            testContext.setAttribute(WEB_DRIVER, driver)
            tangAssert = new TangAssert(driver)
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
        Reporter.log("<br>")
        driver.takeScreenShot(message)
//        Reporter.log(message)
    }


    public getDbResult(message, dbRunType, query, ins, dbName) {
        if (settingsHelper == null) {
            settingsHelper = SettingsHelper.getInstance()
            settings = settingsHelper.settings
        }
        if(dbName == ""){
            dbName = settings.defaultDatabase
        }
        Reporter.log("Query<$dbName>: $query")
        SqlHelper sqlHelper = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        def dbResult = sqlHelper.sqlConRun(message, dbRunType, query, ins, dbName)
        return dbResult
    }

    public getDbResult(ITestContext testContext, dbName, query, message) {
        if (settingsHelper == null) {
            settingsHelper = SettingsHelper.getInstance()
            settings = settingsHelper.settings
        }
        if(dbName == ""){
            dbName = settings.defaultDatabase
        }
        testContext.setAttribute(DATABASE, dbName)
        Reporter.log("Query<$dbName>: $query")
        String databaseToRun = settings."$dbName".dbDriverName
        if(databaseToRun != ""){
            databaseToRun = databaseToRun.replaceAll(".*:", "")
        }
        Reporter.log("<br>" + reporterHelper.addIcons(databaseToRun))
        Reporter.log("Query: $query")
        SqlHelper sqlHelper = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        def dbResult = sqlHelper.sqlConRun(message, dbRunTypeRows, query, 0, dbName)
        return dbResult
    }

    public getDbResult(ITestContext testContext, query, message) {

        if (settingsHelper == null) {
            settingsHelper = SettingsHelper.getInstance()
            settings = settingsHelper.settings
        }
        return getDbResult(testContext, settings.defaultDatabase, query, message)
    }

    public reporterLogLn() {
        Reporter.log(CR)
    }

    public reporterLogLn(message, String htmlTag = "") {
        if(htmlTag.isEmpty()){
            Reporter.log("$message$CR")
        }else{
            Reporter.log("<$htmlTag>$message</$htmlTag>$CR")
        }
        log.info(message)
    }

    public reporterLogLnH1(message) {
        reporterLogLnFormatted(message)
    }

    public reporterLogLnH2(message) {
        reporterLogLnFormatted(message)
    }

    public reporterLogLnH3(message) {
        reporterLogLnFormatted(message)
    }

    public reporterLogLnStrong(message) {
        reporterLogLnFormatted(message)
    }

    private reporterLogLnFormatted(message) {
        StackTraceElement[] stackTrace = StackTraceUtils.sanitize(new Throwable()).stackTrace
        def callinMethodName = stackTrace[2].methodName
        def htmlTag = callinMethodName.replaceAll("reporterLogLn", "")
        reporterLogLn(message, htmlTag)
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
