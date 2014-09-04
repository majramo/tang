package base

import corebase.ISeleniumHelper
import corebase.SeleniumDummyHelper
import corebase.SeleniumHelper
import dtos.SettingsHelper
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import org.testng.Reporter
import org.testng.annotations.AfterMethod

public class AnyCCTest {

    private final static Logger log = Logger.getLogger("ACCT ")
    protected ISeleniumHelper driver
    public TangAssert tangAssert
    private final static DEFAULT_BROWSER = "Firefox"
    private SettingsHelper settingsHelper = SettingsHelper.getInstance()
    private settings = settingsHelper.settings

    public void setup(String browser, String outputDiretory) {
        try {
            log.info(outputDiretory)
            if (settings.guiRun) {
                if (!StringUtils.isBlank(browser)) {
                    driver = new SeleniumHelper().init(browser, outputDiretory)
                } else {
                    driver = new SeleniumHelper().init(DEFAULT_BROWSER, outputDiretory)
                }
            } else {
                driver = new SeleniumDummyHelper().init(DEFAULT_BROWSER, outputDiretory)
            }
            driver.setTestName("cc_")
            tangAssert = new TangAssert(driver)
        } catch (Exception skipException) {
            log.error(skipException)
            throw skipException
        }
    }







    public String getHtmlLinkTag(final String fileName) {
        return "<a href='$fileName'>$fileName</a><br/>"
    }

    public void takeScreenshot(boolean take = true) {
        if (take) {
            takeScreenShot("")
        }
    }

    public void takeScreenShot(String message) {
        driver.takeScreenShot(message)
        Reporter.log(message)
    }

    public void reportLog(message) {
        Reporter.log(message)
    }

    @AfterMethod
    public void teardown() throws Exception {
        driver.quit()
    }
}
