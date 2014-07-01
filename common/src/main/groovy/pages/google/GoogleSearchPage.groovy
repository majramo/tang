package pages.google

import base.AnyPage
import corebase.SeleniumHelper

public class GoogleSearchPage extends AnyPage {
    private final static String PAGE_URL = "http://www.google.com/"
    private final static String SEARCH_FIELD = "//*[@id='gbqfq']"
    private final static String SEARCH_ON_GOOGLE_BTN = "//button[@id='gbqfb']"
    private final static String SEARCH_ON_GOOGLE_LUCKY_BTN = "//*[@id='gbqfbb']"

    public GoogleSearchPage(final SeleniumHelper driver) {
        super(driver)
    }

    public void load() {
        driver.openUrl(PAGE_URL)
    }

    public void typeSearchField(data) {
        driver.type(SEARCH_FIELD, data)
        driver.sleep(200)
    }

    public void clickSearch() {
        driver.click(SEARCH_ON_GOOGLE_BTN)
    }

    public void clickSearchLucky() {
        driver.click(SEARCH_ON_GOOGLE_LUCKY_BTN)
    }
}
