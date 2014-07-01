package pages.google

import corebase.SeleniumHelper
import base.AnyPage

public class GoogleStartPage extends AnyPage{
	private final static String PAGE_URL = "http://www.google.com/"
	private final static String SEACRH_FIELD = "//*[@id='gbqfq']"
	private final static String SEACRH_ON_GOOGLE_BTN = "//button[@id='gbqfb']"
	private final static String SEACRH_ON_GOOGLE_LUCKY_BTN = "//*[@id='gbqfbb']"
	 
	public GoogleStartPage(final SeleniumHelper driver){
		super(driver)
	}

	public void load(){
		driver.openUrl(PAGE_URL)
	}
	
	public void typeSearchField(data){
		driver.type(SEACRH_FIELD, data)
	}
	
	public GoogleSearchResultPage clicSeacrh(){
		driver.click(SEACRH_ON_GOOGLE_BTN)
		return new GoogleSearchResultPage(driver)
	}
	
	public GoogleSearchResultPage clicSeacrhLucky(){
		driver.click(SEACRH_ON_GOOGLE_LUCKY_BTN)
		return new GoogleSearchResultPage(driver)
	}
}
