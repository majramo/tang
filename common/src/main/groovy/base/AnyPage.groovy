package base

import corebase.ISeleniumHelper
import dtos.SettingsHelper

public class AnyPage {
    public ISeleniumHelper driver
    public VemAssert vemAssert
    SettingsHelper settingsHelper = new SettingsHelper()
    public applicationConf = settingsHelper.applicationConf

    public AnyPage(final ISeleniumHelper driver) {
        this.driver = driver
        this.vemAssert = new VemAssert(driver)
    }

}
