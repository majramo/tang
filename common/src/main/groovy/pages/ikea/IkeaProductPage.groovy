package pages.ikea

import base.AnyPage
import corebase.SeleniumHelper

public class IkeaProductPage extends AnyPage {
    String WARE_HOUSE_BUTTON = "//*[@id='ikeaStoreNumber1']"
    String OK_BUTTON = "//*[@id='jsButton_stockCheck_lnk']//input"
    String STATUS_TEXT = "//*[@id=\"stockInfoDiv\"][not(contains(@style,'none'))]//*[@id='stockInfo']"
    String STATUS_COLOR = "img/static/stock_check_green.gif"
    String IN_STOCK_TEXT = "Finns i lager på"
    String IMAGE = "//*[@id='stockImg']"
    String IMAGE_SRC = "src"

    public IkeaProductPage(final SeleniumHelper driver) {
        super(driver)
    }

    def selectWareHouse(int warehouseIndex) {
        driver.select(WARE_HOUSE_BUTTON, warehouseIndex)
        driver.isSelected(WARE_HOUSE_BUTTON, warehouseIndex)
    }

    def clickOk() {
        driver.click(OK_BUTTON)
    }

    void statusShouldBeInStock() {
        String statusText = driver.getText(STATUS_TEXT)
        String statusColor = driver.getAttribute(IMAGE, IMAGE_SRC)
        tangAssert.assertTrue(statusText.contains(IN_STOCK_TEXT))
        tangAssert.assertTrue(statusColor.contains(STATUS_COLOR))
    }
}
