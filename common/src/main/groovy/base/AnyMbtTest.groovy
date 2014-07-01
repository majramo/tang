package base

import corebase.ISeleniumHelper
import corebase.SeleniumDummyHelper
import corebase.SeleniumHelper
import dtos.SettingsHelper
import dtos.base.SqlHelper
import org.apache.log4j.Logger
import org.graphwalker.generators.PathGenerator
import org.testng.Reporter

public class AnyMbtTest extends org.graphwalker.multipleModels.ModelAPI {

    public ISeleniumHelper driver
    public static VemAssert vemAssert
    SettingsHelper settingsHelper = new SettingsHelper()
    def settings = settingsHelper.settings
    private final static Logger log = Logger.getLogger("AMT  ")

    public AnyMbtTest(File model, boolean efsm, PathGenerator generator, boolean weight, String browser, String outputDirectory) {
        super(model, efsm, generator, weight);
        if (settings.guiRun) {
            driver = new SeleniumHelper().init(browser, outputDirectory)
        } else {
            driver = new SeleniumDummyHelper().init("", "")
        }
        vemAssert = new VemAssert(driver);
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
            settingsHelper = new SettingsHelper()
            settings = settingsHelper.settings
        }
        SqlHelper sqlHelper = new SqlHelper(null, log, "mySqlDb", settings.dbRun, settings)
        def dbResult = sqlHelper.sqlConRun(message, dbRunType, query, ins, dbName)
        return dbResult
    }

}
