package pages.ikea.actions

import corebase.Then
import org.testng.Reporter
import pages.ikea.IkeaProductPage
import pages.ikea.IkeaResultsPage
import pages.ikea.IkeaStartPage

public class ThenIkea extends Then{

    IkeaStartPage ikeaStartPage
    IkeaProductPage ikeaProductPage
    IkeaResultsPage ikeaResultsPage

    public ThenIkea(driver){
		super(driver)
        ikeaStartPage = new IkeaStartPage(driver)

    }

    public void resultShouldBeAtLeastPages(int foundPagescount) {
        Reporter.log("Expected result at least: <$foundPagescount>")
        ikeaResultsPage = new IkeaResultsPage(driver)
        ikeaResultsPage.searchResultShouldBeAtLeast(foundPagescount)
    }
    public void productsShouldBeAtLeast(int count) {
        Reporter.log("Products: <$count>")
    }

    void wareHouseShouldHaveProductInStock() {
        ikeaProductPage = new IkeaProductPage(driver)
        ikeaProductPage.statusShouldBeInStock()
    }
}
