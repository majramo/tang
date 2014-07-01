package pages.linkedin
import base.AnyPage
import corebase.ISeleniumHelper

public class LinkedInProfilePage extends AnyPage{
	private final static String SEACRHED_PERSON_FIRST_NAME = "//*[@id='name']/span/span[1]"
	private final static String SEACRHED_PERSON_LAST_NAME = "//*[@id='name']/span/span[2]"
	private final static String SEACRHED_PERSONS_LIST= "//*[@id='result-set']/li"
	private final static String SEACRHED_RESULT = "//*[@id='content']/ul/li "
	private final static String USER_AGREEMENT= "//*[@id='nav-legal']/li[1]/a"
	 
	public LinkedInProfilePage(final ISeleniumHelper driver){
		super(driver)
	    driver.isTagAvailable(USER_AGREEMENT) 
	}
}

