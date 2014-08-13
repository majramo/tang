package reports

import corebase.ISeleniumHelper
import corebase.ScreenshotReportNGUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.apache.velocity.VelocityContext
import org.testng.*
import org.uncommons.reportng.HTMLReporter

import static corebase.GlobalConstants.SELENIUM_HELPER
import static dtos.base.Constants.*

/**
 * Created with IntelliJ IDEA.
 * User: majidaram
 * Date: 2013-05-12
 * Time: 23:59
 * To change this template use File | Settings | File Templates.
 */
public class VemHtmlReporter extends HTMLReporter implements ITestListener, IConfigurationListener {
    protected static final ScreenshotReportNGUtils reportNGScreenShotUtils = new ScreenshotReportNGUtils()
    private final static String CLASS_NAME = this.getSimpleName() + ": "
    private final static Logger log = Logger.getLogger(getClass())
    protected final static ReporterHelper reporterHelper = new ReporterHelper()

    public VemHtmlReporter() {
    }
    /* (non-Javadoc)
    * @see org.uncommons.reportng.AbstractReporter#createContext()
    * override to use a custom utils class
    */

    protected VelocityContext createContext() {
        VelocityContext testContext = super.createContext()
        testContext.put("utils", reportNGScreenShotUtils)
        return testContext
    }


    @Override
    void onConfigurationSuccess(ITestResult testResult) {
        log.debug("onConfigurationSuccess: " + testResult.getMethod().getMethodName() + CLASS_NAME + testResult.getMethod().getTestClass())
    }

    @Override
    void onConfigurationFailure(ITestResult testResult) {
        log.debug("onConfigurationFailure: " + testResult.getMethod().getMethodName() + CLASS_NAME + testResult.getMethod().getTestClass())
        takeScreenShotAndAddToReport(testResult, "onConfigurationFailure")
    }

    @Override
    void onConfigurationSkip(ITestResult testResult) {
        log.debug("onConfigurationSkip: " + testResult.getMethod().getMethodName() + CLASS_NAME + testResult.getMethod().getTestClass())
        takeScreenShotAndAddToReport(testResult, "onConfigurationSkip")
    }

    @Override
    void onTestStart(ITestResult testResult) {
        log.debug("Test is started: " + testResult.getMethod().getMethodName())
        String environment = testResult.getTestContext().getAttribute(ENVIRONMENT)
        String browser = testResult.getTestContext().getAttribute(BROWSER)
        String browserIcon = testResult.getTestContext().getAttribute(BROWSER_ICON)
        String description = testResult.getMethod().getDescription()

        if(StringUtils.isNotBlank(browserIcon)){
            browserIcon = browserIcon.toLowerCase()
        }
        if(StringUtils.isNotBlank(environment)){
            environment = environment.toLowerCase()
        }
        testResult.setAttribute(ENVIRONMENT, environment)
        testResult.setAttribute(BROWSER, browser)
//        browserIcon = FIREFOX

        testResult.setAttribute(ICONS, reporterHelper.addIcons(browserIcon, environment))
//        testResult.setAttribute(ICONS, reporterHelper.addIcons(browserIcon.toLowerCase(), "haf", "hrf", "trf", "opera"))

    }

    @Override
    void onTestSuccess(ITestResult testResult) {
        log.debug("onTestSuccess: " + testResult.getMethod().getMethodName())
        reporterHelper.addPassedIcon()
    }

    @Override
    void onTestFailure(ITestResult testResult) {
        log.debug("onTestFailure: " + testResult.getMethod().getMethodName())
        takeScreenShotAndAddToReport(testResult, "onTestFailure")
        reporterHelper.addFaildIcon()
    }

    @Override
    void onTestSkipped(ITestResult testResult) {
        if (null == testResult.getThrowable()) {
            Throwable throwable = getThrowableFromBeforeMethodResult(testResult.getTestContext().getSkippedConfigurations(), testResult)
            if (throwable == null) {
                throwable = getThrowableFromBeforeMethodResult(testResult.getTestContext().getFailedConfigurations(), testResult)
            }
            testResult.setThrowable(throwable)
        }
        log.debug("onTestSkipped: " + testResult.getMethod().getMethodName() + CLASS_NAME + testResult.getMethod().getTestClass())
        takeScreenShotAndAddToReport(testResult, "onTestSkipped")
        reporterHelper.addSkippedIcon()

    }

    @Override
    void onTestFailedButWithinSuccessPercentage(ITestResult testResult) {
        log.debug("FailedButWithinSuccessPercentage")
    }

    @Override
    void onStart(ITestContext testContext) {
        log.debug("onStart")
    }

    @Override
    void onFinish(ITestContext testContext) {
        log.debug("onFinish")
    }


    private Throwable getThrowableFromBeforeMethodResult(IResultMap skippedOrFailedConfigurations, ITestResult testResult) {
        Throwable throwable = null
        Set<ITestResult> allTestResults = skippedOrFailedConfigurations.getAllResults()
        for (ITestResult currentTestResult : allTestResults) {
            if (currentTestResult.getInstanceName().equals(testResult.getInstanceName())) {
                throwable = currentTestResult.getThrowable()
                return throwable
            }
        }
        return throwable
    }

    private void takeScreenShotAndAddToReport(ITestResult testResult, String message) {
        ITestContext testContext = testResult.getTestContext()
        ISeleniumHelper seleniumHelper = (ISeleniumHelper) testContext.getAttribute(SELENIUM_HELPER)
        Reporter.log(message)
        if (seleniumHelper != null) {
            seleniumHelper.takeScreenShotAndSource()
        }
        testResult.setAttribute(DESCRIPTION, testResult.getMethod().getDescription())
    }

}