package pages.ikea

import base.AnyPage
import corebase.SeleniumHelper
import org.apache.commons.lang3.StringUtils

public class IkeaResultsPage extends AnyPage {
    final String SEARCH_PRODUCTS_COUNT = "//*[@class='serpTabActive']//*[@id='active-1']"
    final String PRODUCT_NAME = "//div[@id='productInfoWrapper2']//*[@id='name']"

    public IkeaResultsPage(final SeleniumHelper driver) {
        super(driver)
    }

    void searchedProductResultShouldBePresented() {
        if (!driver.isTagAvailable(PRODUCT_NAME)) {
            driver.takeScreenShot("Page is not displayed")
        }
    }


    void searchResultShouldBePresented() {
        if (!driver.isTagAvailable(SEARCH_PRODUCTS_COUNT)) {
            driver.takeScreenShot("Page is not displayed")
        }
    }

    void searchResultShouldBeAtLeast(int expectedProductsCount) {
        String foundProductsCountText = driver.getText(SEARCH_PRODUCTS_COUNT)
        int foundProductsCount = 0
        if (!StringUtils.isBlank(foundProductsCountText)) {
            foundProductsCount = foundProductsCountText.replaceAll(".*\\(|\\).*", "").toInteger()
        }
        if (foundProductsCount < expectedProductsCount) {
            driver.takeScreenShot("searchResultShouldBeAtLeast")
            vemAssert.assertTrue(false, "Expected $expectedProductsCount got $foundProductsCount")
        }
    }


}
