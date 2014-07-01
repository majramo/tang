package base

import corebase.ISeleniumHelper
import org.testng.Assert
import org.testng.Reporter

import static org.testng.Assert.assertNotNull

public class VemAssert {
    private static ISeleniumHelper driver

    public VemAssert(ISeleniumHelper driver) {
        this.driver = driver
    }

    final static AnyTest anyTest = new AnyTest()

    public static void assertTrue(boolean condition, String assertOn, String message = "") {
        Reporter.log("AssertTrue (" + assertOn + ")")
        if (!condition) {
            Assert.assertTrue(condition, message.replaceAll("&LT", "<").replaceAll("&GT", ">"))
        }
    }

    public static void assertNotNull(Object object, String assertOn, String message = "") {
        Reporter.log("AssertingNotNull (" + assertOn + ")")
        if (object == null) {
            Assert.assertNotNull(object, message.replaceAll("&LT", "<").replaceAll("&GT", ">"))
        }
    }


    public static void assertNull(Object object, String assertOn, String message = "") {
        Reporter.log("AssertingNull (" + assertOn + ")")
        if (object != null) {
            Assert.assertNull(object, message.replaceAll("&LT", "<").replaceAll("&GT", ">"))
        }
    }

    public static void assertEquals(Object actual, Object expected, String assertOn, String message = "") {
        Reporter.log("AssertEquals ($assertOn): actual (" + actual + ") expected(" + expected + ")")
        if (actual != expected) {
            Assert.assertEquals(actual, expected, message.replaceAll("&LT", "<").replaceAll("&GT", ">"))
        }
    }

    public static void fail(String message) {
        Assert.fail(message)
    }
}