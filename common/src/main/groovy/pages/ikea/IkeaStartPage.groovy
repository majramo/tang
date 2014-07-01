package pages.ikea

import base.AnyPage
import corebase.SeleniumHelper
import org.apache.commons.lang3.StringUtils
import org.testng.Assert

public class IkeaStartPage extends AnyPage{
	 final static String PAGE_URL = "http://www.ikea.com/"
     final String SEARCH_FIELD = "//*[@id='allContent']//form/div/input[@id='search']"
     final String SEARCH_SUBMIT = "//*[@id='lnkSearchBtnHeader']/div[2]/input"
     final String SEAECH_RESULT = "//*[@id='main']//*[@class='serpNoHitsWrapper']"
     final String SEARCH_RESULT_COUNT ="//*[@class='serpTabActive']/a"
     final String SEARCH_PRODUCTS_COUNT =".//*[@id='productsTable']/tbody/tr/td/div[contains(@id,'item')]"
     final String FROM =".//*[@id='minPrice']"
     final String To =".//*[@id='maxPrice']"
     final String SUBMIT ="//*[@id='jsButton_narrowDownSearch_01']/div[2]/input"

    public IkeaStartPage(final SeleniumHelper driver){
		super(driver)
	}

    public void typeFrom(int i) {
        driver.type(FROM, i)
    }
    public void typeTo(int i) {
        driver.type(To, i)
    }
    public void clickSubmit() {
        driver.click(SUBMIT)
    }

    public void load(){
        driver.openUrl(PAGE_URL)
    }

    public void load(country){
        driver.openUrl("$PAGE_URL/$country")
    }
	
	public void typeSearchField(data){
		driver.type(SEARCH_FIELD, data)
	}
	
	public void clickSearch(){
		driver.click(SEARCH_SUBMIT)
	}

    void searchResultShouldBePresented() {
        if(!driver.isTagAvailable(SEARCH_RESULT_COUNT)){
            driver.takeScreenShot("Page is not displayed")
        }

    }

    void searchResultShouldBeAtLeast(int foundPagescount) {
        String foundPagescountText = driver.getText(SEARCH_RESULT_COUNT)
        if(!StringUtils.isBlank(foundPagescountText)){
            foundPagescountText = foundPagescountText.replaceAll(".*\\(|\\).*", "")
        }else{
            foundPagescountText = "0"
        }
        if(foundPagescountText.toInteger() < foundPagescount){
            driver.takeScreenShot()
            Assert.fail("Expected $foundPagescount got $foundPagescountText")
        }
    }

    void searchProductsShouldBeAtLeast(int count) {
        int foundProductsCount = driver.getXpathCount(SEARCH_PRODUCTS_COUNT)

        if(foundProductsCount.toInteger() < count){
            driver.takeScreenShot()
            Assert.fail("Expected $count got $foundProductsCount")
        }
    }

}
