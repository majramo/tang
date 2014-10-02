package utils

class CalculateCheckDigit {

    public static boolean isDigitLuhnNumberCorrect(String digits) {
        def checkDigit = Integer.parseInt(digits[-1])
        def rest = digits[0..-2]
        def luhnCheckDigit = calculateCheckDigitForLuhn(rest)
        assert (checkDigit == luhnCheckDigit)
    }

    public static boolean isSocialSecurityNumberCorrect(String digits) {
        def checkDigit = Integer.parseInt(digits[-1])
        def rest = digits[0..-2]
        def ssnCheckDigit = calculateCheckDigitForSocialSecurityNumber(rest)
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

        return reminder
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

