package utils

import org.apache.log4j.Logger
import org.testng.Reporter

class CalculateCheckDigit {

    private final static Logger logger = Logger.getLogger("CCD ")

    public static boolean isDigitLuhnNumberCorrect(String digits) {
        def checkDigit = Integer.parseInt(digits[-1])
        def rest = digits[0..-2]
        def luhnCheckDigit = calculateCheckDigitForLuhn(rest)
        if(checkDigit != luhnCheckDigit){
            logger.error("$digits should have Luhn check digit ==> $luhnCheckDigit <== and not $checkDigit")
            Reporter.log("$digits should have Luhn check digit ==> $luhnCheckDigit <== and not $checkDigit")
        }
        assert (checkDigit == luhnCheckDigit)
    }

    public static boolean isSocialSecurityNumberCorrect(String digits) {
        def checkDigit = Integer.parseInt(digits[-1])
        def rest = digits[0..-2]
        def ssnCheckDigit = calculateCheckDigitForSocialSecurityNumber(rest)
        if(checkDigit != ssnCheckDigit){
            logger.error( "$digits should have SSN check digit ==> $ssnCheckDigit <== and not $checkDigit")
            Reporter.log("$digits should have SSN check digit ==> $ssnCheckDigit <== and not $checkDigit")
        }
        assert (checkDigit == ssnCheckDigit)

    }

    public static int calculateCheckDigitForLuhn(String digits) {
        ArrayList<String> all = new ArrayList<String>()

        int sum = getSum(digits.reverse())
        int reminder = (sum * 9) % 10

        return reminder
    }


    public static int calculateCheckDigitForSocialSecurityNumber(String digits) {
        ArrayList<String> all = new ArrayList<String>()

        int sum = getSum(digits)
        int reminder = 10 - (sum % 10)
        if(reminder < 10){
            return reminder
        }
        return 0
    }

    private static int getSum(String digits) {
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


}

