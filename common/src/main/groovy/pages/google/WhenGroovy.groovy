package pages.google

import corebase.When

public class WhenGroovy extends When{

	public WhenGroovy(driver){
		super(driver)
	}

	public void typeValueAndClickSearch(String [] searchStrings) {
		String searchString = ""
		searchStrings.each{
			searchString += "$it "
		}
		searchString = searchString.replaceAll(/ $/, "")
		log.info (searchString)
		GoogleSearchPage googleSearchPage = new GoogleSearchPage(driver)
		googleSearchPage.typeSearchField(searchString)
	}
}
