package base

import corebase.ISeleniumHelper
import dtos.SettingsHelper

public class AnyPage {
    public ISeleniumHelper driver
    public TangAssert tangAssert
    SettingsHelper settingsHelper = new SettingsHelper()
    public applicationConf = settingsHelper.applicationConf

    public AnyPage(final ISeleniumHelper driver) {
        this.driver = driver
        this.tangAssert = new TangAssert(driver)
    }

}
