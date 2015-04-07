package utils

import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static utils.CalculateCheckDigit.isDigitLuhnNumberCorrect
import static utils.CalculateCheckDigit.isSocialSecurityNumberCorrect

/**
 * Created by Tavera on 2014-10-02.
 */
class CalculateCheckDigitTest {


    @Test(dataProvider = "ocrs")
    public void checkDigitLuhnTest(String ocr) {
        isDigitLuhnNumberCorrect(ocr)
    }

    @Test(dataProvider = "socialSecurityNumber")
    public void checkSocialSecurityNumberTest(String soc) {
        isSocialSecurityNumberCorrect(soc)
    }


    @DataProvider(name = "ocrs")
    public Object[][] ocrs() {
        [
                ["3221579"],
                ["860442020567338"],
                ["1009109500132"],
                ["125"],
//                ["264526771"],
//                ["3114091600119790"],
//                ["265351785"],
//                ["265467047"],
//                ["266314333"],
//                ["266423244"]
        ] as Object[][]
    }

    @DataProvider(name = "socialSecurityNumber")
    public Object[][] socialSecurityNumber() {
        [
                ["8910068520"],
                ["6906123077"],
                ["5206062043"],
                ["2907118893"],
                ["2412243921"],
                ["2007174507"],
                ["2107224327"],
                ["2112132127"],
                ["2205163906"],
//                ["2211053927"],
//                ["2211053921"],
        ] as Object[][]
    }
}
