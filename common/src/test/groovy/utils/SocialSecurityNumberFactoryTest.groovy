package utils

import org.testng.Reporter
import org.testng.annotations.Test

/**
 */
public class SocialSecurityNumberFactoryTest {
    SocialSecurityNumberFactory socialSecurityNumberFactory = new SocialSecurityNumberFactory()


    @Test
    public void testGetPersonNummer() {
        printList(socialSecurityNumberFactory.getPersonNummer(3))
    }

    @Test
    public void testGetPersonNummerAtAge() {
        printList(socialSecurityNumberFactory.getPersonNummerAtAge(10, 2))
        printList(socialSecurityNumberFactory.getPersonNummerAtAge(60, 2))
    }

    @Test
    public void testGetFemalePersonNummer() {
        printList(socialSecurityNumberFactory.getFemalePersonNummer(4))
    }

    @Test
    public void testGetFemalePersonNummerAtAge() {
        printList(socialSecurityNumberFactory.getFemalePersonNummerAtAge(10, 2))
    }

    @Test
    public void testGetMalePersonNummer() {
        printList(socialSecurityNumberFactory.getMalePersonNummer(4))
    }

    @Test
    public void testGetMalePersonNummerAtAge() {
        printList(socialSecurityNumberFactory.getMalePersonNummerAtAge(10, 2))
    }

    private void printList(AbstractMap list) {
        Reporter.log("Size: " + list.size().toString())
        list.each {
            println it
            Reporter.log(it.toString())
        }
    }
}
