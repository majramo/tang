package pages.google

import corebase.Given

public class GivenGoogle extends Given{

	public GivenGoogle(driver){
		super(driver)
	}
	
	public googleSearchPageIsLoaded(){
		GoogleSearchPage googleSearchPage = new GoogleSearchPage(driver)
		googleSearchPage.load()
	}
}

