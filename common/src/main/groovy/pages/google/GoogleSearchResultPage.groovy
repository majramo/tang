package pages.google

import base.AnyPage
import corebase.SeleniumHelper

public class GoogleSearchResultPage extends AnyPage {
    private final static String SEARCHED_RESULT = "//*[@id='rso']"
    private final static String SEARCHED_RESULT_LIST = "//*[@id='rso']/li"
    private final static String SEARCHED_RESULT_STATUS = "//*[@id='resultStats']"

    public GoogleSearchResultPage(final SeleniumHelper driver) {
        super(driver)
    }


    public String getResultTextForResultItem(int index) {
        driver.isTagAvailable(SEARCHED_RESULT_LIST)
        def text = driver.getText(SEARCHED_RESULT_LIST + "[1]")
        return text
    }

    public String getResultText() {
        driver.isTagAvailable(SEARCHED_RESULT)
        def text = driver.getText(SEARCHED_RESULT)
        return text
    }

    public int getSearchResultNoOfItemsOnPage() {
        driver.isTagAvailable(SEARCHED_RESULT_LIST)
        return driver.getXpathCount(SEARCHED_RESULT_LIST)
    }

    public boolean isSearchResultShown() {
        return driver.isTagAvailable(SEARCHED_RESULT_LIST) & driver.isTagAvailable(SEARCHED_RESULT_STATUS)
    }

    public String getSearchResultText() {
        return driver.getText(SEARCHED_RESULT_LIST)
    }

}
