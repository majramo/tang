package pages.google.actions

import corebase.When
import pages.google.GoogleSearchPage

public class WhenGoogle extends When{

	public WhenGoogle(driver){
		super(driver)
	}

	public void typeValueAndClickSearch(String [] searchStrings) {
		String searchString = ""
		searchStrings.each{
			searchString += "$it "
		}
		searchString = searchString.replaceAll(/ $/, "")
		log.info (searchString)
		GoogleSearchPage googleSarchPage = new GoogleSearchPage(driver)
		googleSarchPage.typeSearchField(searchString)
		//googleSarchPage.clickSearch()
	}
}
