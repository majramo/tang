package reports

import corebase.ISeleniumHelper
import corebase.SeleniumHelper
import dtos.FileHelper
import dtos.FileUtilsHelper
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.ITestResult
import org.testng.Reporter

public class ReporterHelper extends Reporter {
    private final static Logger log = Logger.getLogger(getClass())
    private final static FileUtilsHelper fileUtilsHelper = new FileUtilsHelper()

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
        addIcon("/icons/passedTest.jpg")
    }

    public String addFaildIcon() {
        addIcon("/icons/failedTest.jpg")
    }

    public String addSkippedIcon() {
        addIcon("/icons/skippedTest.jpg")
    }

    private void addIcon(resource) {
        File file = fileUtilsHelper.loadResourceFileIfExists(resource)
        if(file != null){
            def str = "<img src=\"" + file.getPath() + "\" width=\"80\" height=\"80\" hspace=\"10\" />"
            Reporter.log(str)
        }
    }

    private void addFireFoxIcon() {
        addIcon("/icons/firefox.jpg")
    }
}