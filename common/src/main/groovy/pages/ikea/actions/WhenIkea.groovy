package pages.ikea.actions

import org.testng.Reporter
import pages.ikea.IkeaProductPage
import pages.ikea.IkeaResultsPage
import pages.ikea.IkeaStartPage
import corebase.When

public class WhenIkea extends When{
    IkeaStartPage ikeaStartPage
    IkeaResultsPage ikeaResultPage
    IkeaProductPage ikeaProductPage

    public WhenIkea(driver){
		super(driver)
        ikeaStartPage = new IkeaStartPage(driver)

    }

    public void searchForProduct(value) {
        Reporter.log("Search: <$value>")
        ikeaStartPage.typeSearchField(value)
        ikeaStartPage.clickSearch()
        ikeaResultPage = new IkeaResultsPage(driver)
        ikeaResultPage.searchedProductResultShouldBePresented()
    }

    public void searchFor(value) {
        Reporter.log("Search: <$value>")
        ikeaStartPage.typeSearchField(value)
        ikeaStartPage.clickSearch()
        ikeaResultPage = new IkeaResultsPage(driver)
        ikeaResultPage.searchResultShouldBePresented()
    }

    public void  filter(int from, int to) {
        Reporter.log("Filter: <$from> <$to>")

    }

    def chooseWareHouse(int warehouswIndex) {
        ikeaProductPage = new IkeaProductPage(driver)
        ikeaProductPage.selectWareHouse(warehouswIndex)
        ikeaProductPage.clickOk()

    }
}

