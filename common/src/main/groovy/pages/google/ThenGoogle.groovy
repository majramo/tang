package pages.google

import corebase.Then
import org.apache.commons.lang3.StringUtils

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
		def searchResultText = googleSearchResultPage.getResultText()
        if(StringUtils.isNotEmpty(searchResultText)){
            searchResultText = searchResultText.toLowerCase()
        }else{
            verify(false, "searchResultText is null" )
        }
		searchStrings.each{
			def searchString = it.toLowerCase()
			if(!searchResultText.contains(searchString)){
				googleSearchResultPage.driver.takeScreenShot()
                verify(false, "Verify result contains: <" + searchString + ">" )
			}
		}
		return true
	}

	public boolean verifySearchResultsHitsIsAtLeast(int numberOfHits) {
		GoogleSearchResultPage googleSearchResultPage = new GoogleSearchResultPage(driver)
		def searchResultsHits = googleSearchResultPage.getSearchResultNoOfItemsOnPage() 
		verify(searchResultsHits >= numberOfHits, "Verify number of hits on page is " + numberOfHits + " got " + searchResultsHits)
		return true
	}
}
