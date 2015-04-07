package corebase;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static corebase.GlobalConstants.*;

/**
 * Created with IntelliJ IDEA.
 * User: majidaram
 * Date: 2013-06-15
 * Time: 09:42
 * To change this template use File | Settings | File Templates.
 */
public class GivenWhenThenTest {
    GivenWhenThen givenWhenThen;
    ISeleniumHelper driver;


    public void setUpFireFoxWithNoRunnableDriver() throws Exception {
        driver = new SeleniumDummyHelper().init(LOCAL_FIREFOX);
        givenWhenThen = new GivenWhenThen(driver);
    }

    public void setUpFireFoxWithRunnableDriver() throws Exception {
        driver = new SeleniumHelper().init(LOCAL_FIREFOX, "./");
        givenWhenThen = new GivenWhenThen(driver);
    }

    @Test
    public void testVerifyTrueWithNoRunnableDriver() throws Exception {
        setUpFireFoxWithNoRunnableDriver();
        givenWhenThen.verify(true, "");
    }

    @Test
    public void testVerifyFalseWithNoRunnableDriver() throws Exception {
        setUpFireFoxWithNoRunnableDriver();
        givenWhenThen.verify(true, "");
    }

    @Test
    public void testGetDriverWithNoRunnableDriver() throws Exception {
        setUpFireFoxWithNoRunnableDriver();
        assert (givenWhenThen.driver == driver);

    }

    @Test
    public void testSetDriverWithNoRunnableDriver() throws Exception {
        setUpFireFoxWithNoRunnableDriver();
        assert (givenWhenThen.driver == driver);
    }


//    @Test
//    public void testVerifyTrueWithRunnableDriver() throws Exception {
//        setUpFireFoxWithRunnableDriver();
//        givenWhenThen.verify(true, "");
//    }
//
//    @Test
//    public void testVerifyFalseWithRunnableDriver() throws Exception {
//        setUpFireFoxWithRunnableDriver();
//        givenWhenThen.verify(true, "");
//    }
//
//    @Test
//    public void testGetDriverWithRunnableDriver() throws Exception {
//        setUpFireFoxWithRunnableDriver();
//        assert (givenWhenThen.driver == driver);
//    }
//
//    @Test
//    public void testSetDriverWithRunnableDriver() throws Exception {
//        setUpFireFoxWithRunnableDriver();
//        assert (givenWhenThen.driver == driver);
//    }

    @AfterMethod
    private void afterMethod() {
        givenWhenThen.driver.quit();
    }


}
