package base

import corebase.ISeleniumHelper
import corebase.SeleniumDummyHelper
import corebase.SeleniumHelper
import dtos.SettingsHelper
import dtos.base.SqlHelper
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.graphwalker.generators.PathGenerator
import org.testng.Reporter

public class AnyMbtTest extends org.graphwalker.multipleModels.ModelAPI {

    public ISeleniumHelper driver
    public static TangAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    private final static Logger log = LogManager.getLogger("AMT  ")

    public AnyMbtTest(File model, boolean efsm, PathGenerator generator, boolean weight, String browser, String outputDirectory) {
        super(model, efsm, generator, weight);
        if (settings.guiRun) {
            driver = new SeleniumHelper().init(browser, outputDirectory)
        } else {
            driver = new SeleniumDummyHelper().init("", "")
        }
        tangAssert = new TangAssert(driver);
        driver.setTestName(AnyMbtTest.class.getSimpleName())
    }

    public static String getHtmlLinkTag(final String fileName) {
        return "<a href='$fileName'>$fileName</a><br/>"
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
            settingsHelper = SettingsHelper.getInstance()
            settings = settingsHelper.settings
        }
        SqlHelper sqlHelper = new SqlHelper(null, log, "mySqlDb", settings.dbRun, settings)
        def dbResult = sqlHelper.sqlConRun(message, dbRunType, query, ins, dbName)
        return dbResult
    }

}
