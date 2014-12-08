package pages.linkedin

import base.AnyPage
import corebase.ISeleniumHelper

public class LinkedInSearchResultPage extends AnyPage {
    private final static String SEACRHED_PERSON_FIRST_NAME = "//*[@id='name']/span/span[1]"
    private final static String SEACRHED_PERSON_LAST_NAME = "//*[@id='name']/span/span[2]"
    private final static String SEACRHED_PERSONS_LIST = "//*[@id='result-set']/li"
    private final static String SEACRHED_RESULT = "//*[@id='result-set']/li "
    private final static String USER_AGREEMENT = "//*[@id='nav-legal']/li[1]/a"
    private final static String SEARCH_RESULTS = "//*[@id='result-set']/li"
    private final static String SEARCH_STRING_IN_RESULT = SEARCH_RESULTS + "//dl/*[descendant::text()[contains(.,'%s')]]   /../..//strong/a"
    private final static String FIRST_RESULT_PROFILE = "//*[@id='result-set']/li[1]/h2//a"

    public LinkedInSearchResultPage(final ISeleniumHelper driver) {
        super(driver)
        driver.isTagAvailable(USER_AGREEMENT)
        driver.isTagAvailable(SEACRHED_PERSONS_LIST)
    }

    public String getFoundPersonsFirstName() {
        return driver.getText(SEACRHED_PERSON_FIRST_NAME)
    }

    public String getFoundPersonsLastName() {
        driver.isTagAvailable(SEACRHED_PERSON_LAST_NAME)
        return driver.getText(SEACRHED_PERSON_LAST_NAME)
    }

    public String getFoundPersonsName() {
        return getFoundPersonsFirstName() + getFoundPersonsLastName()
    }

    public int getNumberOfFoundPersonsOnPage() {
        driver.isTagAvailable(SEACRHED_PERSONS_LIST)
        return driver.getXpathCount(SEACRHED_PERSONS_LIST)
    }

    public int getNumberOfFoundPersons() {
        driver.isTagAvailable(SEACRHED_RESULT)
        return driver.getXpathCount(SEACRHED_RESULT)
    }

    public String getTextOfFoundPersons() {
        driver.isTagAvailable(SEACRHED_PERSONS_LIST)
        driver.isTagAvailable(SEACRHED_PERSONS_LIST + "/..")
        String result = driver.getText(SEACRHED_PERSONS_LIST + "/..")
        return result
    }

    public LinkedInProfilePage clickFirstResult() {
        driver.click(FIRST_RESULT_PROFILE)
        return new LinkedInProfilePage(driver)

    }

    public clickOnResultContaining(String searchString) {
        String xpath = String.format(SEARCH_STRING_IN_RESULT, searchString)
        driver.click(xpath)
        sleep(2000)
        return new LinkedInProfilePage(driver)


    }
}

