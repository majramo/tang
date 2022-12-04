package base

import corebase.ISeleniumHelper
import dtos.SettingsHelper
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.codehaus.groovy.runtime.StackTraceUtils
import org.testng.Reporter

import static corebase.GlobalConstants.REPORT_NG_ESCAPE_OUTPUT_PROPERTY
import static dtos.base.Constants.CR

public class AnyPage {
    public ISeleniumHelper driver
    public ISeleniumHelper seleniumHelper
    public TangAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    public applicationConf = settingsHelper.applicationConf
    private final static Logger log = LogManager.getLogger("AnP  ")

    public AnyPage(final ISeleniumHelper driver) {
        this.driver = driver
        seleniumHelper = driver
        this.tangAssert = new TangAssert(driver)
        System.setProperty(REPORT_NG_ESCAPE_OUTPUT_PROPERTY, "false")
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


}
