import corebase.SeleniumHelper
import org.testng.annotations.BeforeClass
import org.testng.annotations.AfterClass
import org.testng.annotations.Test

/**
 * Created by majidaram on 2014-04-05.
 */
public class SeleniumHelperTest {
    SeleniumHelper driver

    @BeforeClass
    void setUp() {
        driver = new SeleniumHelper().init("LOCAL_FIREFOX", "./");
    }

    @AfterClass
    void tearDown() {
        driver.quit()
    }

    void testRequireTitle() {

    }

    void testRequireXpath() {

    }

    void testRequireXpath1() {

    }

    void testRequireVisibleXpath() {

    }

    void testRequireVisibleXpath1() {

    }

    @Test
    void testIsDisplayed() {
        driver.openUrl("http://www.google.se/")
        driver.requireTitle("Google")
    }

    @Test
    void testIsDisplayed1() {
        driver.openUrl("http://www.google.se/")
        println driver.isDisplayed("epb-notice")
        println driver.isDisplayed("epb-notice1", 5)

        println driver.requireXpath("prt", 5)
        println driver.requireVisibleXpath("//*[@id='viewport']/a", 5)

    }
}
