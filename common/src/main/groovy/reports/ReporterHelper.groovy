package reports

import corebase.ISeleniumHelper
import corebase.ScreenshotReportNGUtils
import corebase.SeleniumHelper
import org.apache.log4j.Logger
import org.apache.velocity.VelocityContext
import org.testng.*
import org.uncommons.reportng.HTMLReporter

import static dtos.base.Constants.*


public class ReporterHelper extends Reporter {
    private final static Logger log = Logger.getLogger(getClass())


    public static void log(String msg) {
        //Reporter.log(msg + "<br/>")
        Reporter.log(msg)
    }


    public void takeScreenShotAndAddToReport(ITestResult testResult, String message) {
        ITestContext testContext = testResult.getTestContext()
        ISeleniumHelper seleniumHelper = (ISeleniumHelper) testContext.getAttribute(SELENIUM_HELPER)
        log(message)
        if (seleniumHelper.getClass().getName().equals(SeleniumHelper.getName()) && seleniumHelper != null) {
            seleniumHelper.takeScreenShot()
        }
    }

    public String addPassedIcon() {
        addIcon(this.class.getResource("/icons/passedTestIcon.jpg"))
    }

    public String addFaildIcon() {
        addIcon(this.class.getResource("/icons/failedTestIcon.jpg"))
    }

    public String addSkippedIcon() {
        addIcon(this.class.getResource("/icons/skippedTestIcon.jpg"))
    }

    private void addIcon(filePath) {
        def str =   "<img src=\"" + filePath + "\" width=\"80\" height=\"80\" hspace=\"10\" />"
        Reporter.log(str)
    }


}