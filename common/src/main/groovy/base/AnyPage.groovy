package base

import corebase.ISeleniumHelper
import dtos.SettingsHelper

public class AnyPage {
    public ISeleniumHelper driver
    public ISeleniumHelper seleniumHelper
    public TangAssert tangAssert
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    public applicationConf = settingsHelper.applicationConf

    public AnyPage(final ISeleniumHelper driver) {
        this.driver = driver
        seleniumHelper = driver
        this.tangAssert = new TangAssert(driver)
    }

}
