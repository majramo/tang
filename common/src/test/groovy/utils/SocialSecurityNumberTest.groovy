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
        Reporter.log new SocialSecurityNumber("5910202695").toString()
        Reporter.log new SocialSecurityNumber("195910202695").toString()
        Reporter.log new SocialSecurityNumber("200005247887").toString()
        Reporter.log new SocialSecurityNumber("0005247887").toString()

    }

    @Test
    void testSocialSecurityNumber2() {
        socialSecurityNumberFactory.getPersonNummer("1959102026", 3).each {
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
        Reporter.log new SocialSecurityNumber("9405056707").toString()
    }

}
