package base

import corebase.ISeleniumHelper
import dtos.SettingsHelper
import org.apache.log4j.Logger
import org.testng.Reporter

import static dtos.base.Constants.CR

public class AnyPage {
    public ISeleniumHelper driver
    public ISeleniumHelper seleniumHelper
    public TangAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    public applicationConf = settingsHelper.applicationConf
    private final static Logger log = Logger.getLogger("AnP  ")

    public AnyPage(final ISeleniumHelper driver) {
        this.driver = driver
        seleniumHelper = driver
        this.tangAssert = new TangAssert(driver)
    }

    public reporterLogLn(message, String htmlTag = "") {
        if(htmlTag.isEmpty()) {
            Reporter.log("$message$CR")
        }else{
            Reporter.log("<$htmlTag>$message</$htmlTag>$CR")
        }
        log.info(message)
    }


}
