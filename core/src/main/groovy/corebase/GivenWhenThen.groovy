package corebase

import static org.testng.Assert.assertFalse

public class GivenWhenThen{

    public ISeleniumHelper driver

	public GivenWhenThen(driver){
		this.driver = driver
	}

    public void verify(boolean value, String message) {
        if(!value){
            driver.takeScreenShot(message)
            assertFalse(true, message )
        }
    }
}
