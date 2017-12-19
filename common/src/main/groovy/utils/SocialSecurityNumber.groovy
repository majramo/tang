package utils

import java.text.Format
import java.text.SimpleDateFormat

import static corebase.GlobalConstants.*
import java.time.*
/**
 * Created with IntelliJ IDEA.
 * User: majidaram
 * Date: 2013-12-10
 * Time: 07:54
 * To change this template use File | Settings | File Templates.
 */
class SocialSecurityNumber {
    public String socialSecurityNumberShort
    public String socialSecurityNumberShortDashLess
    public String socialSecurityNumberLong
    public String socialSecurityNumberLongDashLess
    public String birthDateLong
    public String birthDateShort
    public String socialSecurityNumberPostFix
    public String year
    public String month
    public String day
    def n1
    def n2
    def n3
    def n4
    String gender
    int age
    Calendar date = Calendar.getInstance()
    Format f = new SimpleDateFormat("yyyy");
    int currentYear

    public SocialSecurityNumber(String year, String month, String day, n1, n2, n3, n4) {
        initateSocicalSecurityNumber(year, month, day, n1, n2, n3, n4)

    }

    private void initateSocicalSecurityNumber(String year, String month, String day, n1, n2, n3, n4) {
        currentYear = Integer.parseInt(f.format(date.getTime()))
        this.year = year
        this.month = month
        this.day = day
        this.n1 = n1
        this.n2 = n2
        this.n3 = n3
        if ((["0", "2", "4", "6", "8"].contains(n3))) {
            gender = FEMALE
        } else {
            gender = MALE
        }
        this.n4 = n4
        socialSecurityNumberPostFix = "$n1$n2$n3$n4"
        birthDateLong = "$year$month$day"
        birthDateShort = year.substring(2, 4) + "$month$day"
        socialSecurityNumberLong = "${birthDateLong}-$socialSecurityNumberPostFix"
        socialSecurityNumberLongDashLess = "${birthDateLong}$socialSecurityNumberPostFix"
        socialSecurityNumberShort = "${birthDateShort}-$socialSecurityNumberPostFix"
        socialSecurityNumberShortDashLess = "${birthDateShort}$socialSecurityNumberPostFix"
        age = Period.between(LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day)), LocalDate.now()).years
    }

    public SocialSecurityNumber(String socialSecurityNumber) {
        if (socialSecurityNumber.length() == 10) {
            this.year = "19" + socialSecurityNumber.substring(0, 2)
        } else {
            this.year = socialSecurityNumber.substring(0, 4)
            socialSecurityNumber = socialSecurityNumber.subSequence(2, 12)
        }
        month = socialSecurityNumber.substring(2, 4)
        day = socialSecurityNumber.substring(4, 6)
        n1 = socialSecurityNumber.substring(6, 7)
        n2 = socialSecurityNumber.substring(7, 8)
        n3 = socialSecurityNumber.substring(8, 9)
        n4 = socialSecurityNumber.substring(9, 10)

        initateSocicalSecurityNumber(year, month, day, n1, n2, n3, n4)
    }

    public String toString() {
        return "$socialSecurityNumberShort $socialSecurityNumberLong gender: $gender age: $age"
    }
}
