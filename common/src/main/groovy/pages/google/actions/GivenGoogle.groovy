package pages.google.actions

import corebase.Given
import pages.google.GoogleSearchPage

class GivenGoogle extends Given{

	public GivenGoogle(driver){
		super(driver)
	}
	
	public googleSeacrhPageIsLoaded(){
		GoogleSearchPage googleSarchPage = new GoogleSearchPage(driver)
		googleSarchPage.load()
	}
}
