package pages.google.actions

import corebase.Then

import org.testng.Assert

import pages.google.GoogleSearchResultPage;

public class ThenGoogle extends Then{

	public ThenGoogle(driver){
		super(driver)
	}

	
	public boolean verifySearchResulsIsShown() {
		GoogleSearchResultPage googleSearchResultPage = new GoogleSearchResultPage(driver)
		return googleSearchResultPage.isSearchResultShown()
	}
	
	public boolean verifySearchResulsContainTexts(String [] searchStrings) {
		GoogleSearchResultPage googleSearchResultPage = new GoogleSearchResultPage(driver)
		def searchResultText = googleSearchResultPage.getResultText().toLowerCase()
		searchStrings.each{
			def searchString = it.toLowerCase()
			if(!searchResultText.contains(searchString)){
				googleSearchResultPage.driver.takeScreenShot()
				Assert.assertTrue(false, "Verify result contains: <" + searchString + ">" )
			}
		}
		return true
	}

	public boolean verifySearchResulsHitsIsAtLeast(numberOfHits) {
		GoogleSearchResultPage googleSearchResultPage = new GoogleSearchResultPage(driver)
		def searchResultsHits = googleSearchResultPage.getSearchResultNoOfItemsOnPage()
        verify(searchResultsHits >= numberOfHits, "Verify number of hits on page is " + numberOfHits + " got " + searchResultsHits)
		Assert.assertTrue(searchResultsHits >= numberOfHits, "Verify number of hits on page is " + numberOfHits + " got " + searchResultsHits)
		return true
	}


}
