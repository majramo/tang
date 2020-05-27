package utils

import org.testng.Reporter
import org.testng.annotations.Test

/**
 * Created by Tavera on 2014-01-16.
 */
public class SocialSecurityNumberTest {
    SocialSecurityNumber socialSecurityNumber
    SocialSecurityNumberFactory socialSecurityNumberFactory = new SocialSecurityNumberFactory()

    @Test
    void testSocialSecurityNumber1() {
        Reporter.log new SocialSecurityNumber("1212121212").toString()
        Reporter.log new SocialSecurityNumber("191212121212").toString()
        Reporter.log new SocialSecurityNumber("201212121212").toString()

    }

    @Test
    void testSocialSecurityNumber2() {
        socialSecurityNumberFactory.getPersonNummer("191212121212", 3).each {
            it.each {
                print "$it.key "
                it.each {
                    Reporter.log it.value.toString() + " "
                }
            }
            println ""
        }
    }


    @Test
    public void testSocialSecurityNumber3() {
        Reporter.log new SocialSecurityNumber("191212121212").toString()
    }

}
