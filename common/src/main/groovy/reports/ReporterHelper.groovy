package reports

import corebase.ISeleniumHelper
import corebase.SeleniumHelper
import dtos.FileUtilsHelper
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.ITestResult
import org.testng.Reporter

import static corebase.GlobalConstants.*

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


    public String addIcons(String[] icons) {
        return getIconsString(icons)

    }


    private void addIcon(resource) {
        File file = fileUtilsHelper.loadResourceFileIfExists(resource)
        if (file != null) {
            def str = "<img src=\"" + file.getPath() + "\" width=\"80\" height=\"80\" hspace=\"10\" />"
            Reporter.log(str)
        }
    }

    private String getIconsString(String[] resources) {
        def str = ""
        resources.each {
            File file = fileUtilsHelper.loadResourceFileIfExists("/icons/${it}.jpg")
            if (file != null) {
                str += "<img src=\"" + file.getPath() + "\" width=\"30\" height=\"30\" hspace=\"30\" /> "
            }
        }
        return str
    }


    private void addFireFoxIcon() {
        addIcon("/icons/firefox.jpg")
    }

}