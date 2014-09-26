package base

import org.testng.Assert
import org.testng.Reporter

public class TangDbAssert {

    public void assertTrue(boolean condition, String assertOn, String message = "") {
//        Reporter.log("AssertTrue (" + assertOn + ")")
        if (!condition) {
            Assert.assertTrue(condition, message.replaceAll("&LT", "<").replaceAll("&GT", ">"))
        }
    }

    public void assertNotNull(Object object, String assertOn, String message = "") {
//        Reporter.log("AssertingNotNull (" + assertOn + ")")
        if (object == null) {
            Assert.assertNotNull(object, message.replaceAll("&LT", "<").replaceAll("&GT", ">"))
        }
    }

    public void assertEquals(Object actual, Object expected, String assertOn, String message = "") {
//        Reporter.log("AssertEquals ($assertOn): actual (" + actual + ") expected(" + expected + ")")
        if (actual != expected) {
            Assert.assertEquals(actual, expected, message.replaceAll("&LT", "<").replaceAll("&GT", ">"))
        }
    }

}