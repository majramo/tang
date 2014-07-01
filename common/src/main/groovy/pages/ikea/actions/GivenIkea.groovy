package pages.ikea.actions

import corebase.Given
import org.testng.Reporter
import pages.ikea.IkeaResultsPage
import pages.ikea.IkeaStartPage

class GivenIkea extends Given{
    private IkeaStartPage ikeaStartPage
    private IkeaResultsPage ikeaResultPage

	public GivenIkea(driver){
		super(driver)
	}

    public startPageIsLoaded(){
        IkeaStartPage ikeaStartPage = new pages.ikea.IkeaStartPage(driver)
        ikeaStartPage.load()
    }

    public countryStartPageIsLoaded(country){
        Reporter.log("Country: <$country>")
        ikeaStartPage = new pages.ikea.IkeaStartPage(driver)
        ikeaStartPage.load(country)
    }
    public void searchFor(value) {
        Reporter.log("Search: <$value>")
        ikeaStartPage.typeSearchField(value)
        ikeaStartPage.clickSearch()
        ikeaResultPage = new IkeaResultsPage(driver)
        ikeaResultPage.searchResultShouldBePresented()
    }

}
