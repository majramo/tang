package utils

import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class CalculateCheckDigit {


    @Test(dataProvider = "ocrs")
    public void checkDigitLuhnTest(String ocr) {
        isDigitLuhnNumberCorrect(ocr)
    }

    @Test(dataProvider = "socialSecurityNumber")
    public void checkSocialSecurityNumberTest(String soc) {
        isSocialSecurityNumberCorrect(soc)
    }


    public boolean isDigitLuhnNumberCorrect(String digits) {
        def checkDigit = Integer.parseInt(digits[-1])
        def rest = digits[0..-2]
        def luhnCheckDigit = calculateCheckDigitForLuhn(rest)
        assert (checkDigit == luhnCheckDigit)
    }

    public boolean isSocialSecurityNumberCorrect(String digits) {
        def checkDigit = Integer.parseInt(digits[-1])
        def rest = digits[0..-2]
        def ssnCheckDigit = calculateCheckDigitForSocialSecurityNumber(rest)
        assert (checkDigit == ssnCheckDigit)

    }

    def calculateCheckDigitForLuhn(String digits) {
        ArrayList<String> all = new ArrayList<String>()

        int sum = getSum(digits.reverse())
        int reminder = (sum * 9) % 10

        return reminder
    }


    def calculateCheckDigitForSocialSecurityNumber(String digits) {
        ArrayList<String> all = new ArrayList<String>()

        int sum = getSum(digits)
        int reminder = 10 - (sum % 10)

        return reminder
    }

    private int getSum(String digits) {
        int sum = 0
        boolean multiplier = true
        digits.each {
            int value = Integer.parseInt(it)
            if (multiplier) {
                value *= 2
            }
            if (value > 9) {
                value = 1 + (value - 10)
            }
            sum += value
            multiplier = !multiplier

        }
        sum
    }

    @DataProvider(name = "ocrs")
    public Object[][] ocrs() {
        [
                ["3221579"],
                ["860442020567338"],
                ["1009109500132"],
                ["125"],
//                ["264526771"],
//                ["264627069"],
//                ["265351785"],
//                ["265467047"],
//                ["266314333"],
//                ["266423244"]
        ] as Object[][]
    }

    @DataProvider(name = "socialSecurityNumber")
    public Object[][] socialSecurityNumber() {
        [
                ["8112189876"],
                ["6906123077"],
                ["5206062043"],
                ["2907118893"],
                ["2412243921"],
                ["2007174507"],
                ["2107224327"],
                ["2112132127"],
                ["2205163906"],
                ["2211053927"],
        ] as Object[][]
    }

}

