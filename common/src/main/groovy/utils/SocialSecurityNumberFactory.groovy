package utils

import org.apache.log4j.Logger

import java.text.Format
import java.text.SimpleDateFormat

import static org.apache.log4j.Logger.getLogger
import static corebase.GlobalConstants.*

public class SocialSecurityNumberFactory implements Serializable {
    private static final long serialVersionUID = -160928058318117179L;

    private static final Logger LOG = getLogger(SocialSecurityNumberFactory.class.getName());
    private String yearMonthDayN
    String year
    String month
    String day
    String n1
    String n2
    String n3
    String num
    int yearNum
    int monthNum
    int dayNum
    int n1Num
    int n2Num
    int n3Num
    int count = 0
    int numberOfDaysInMonth
    def socialSecurityNumbers = [:]
    int maxNoOfSocialSecurityNumbers = 10000
    String gender = ""


    public AbstractMap getPersonNummer(int maxNoOfPnrs = 0) {
        gender = ""
        return getPersonNummerAtAge(20, maxNoOfPnrs)
    }


    public AbstractMap getPersonNummerAtAge(int age, int maxNoOfPnrs = 0) {
        count = 0
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.add(Calendar.YEAR, -age)
        Format f = new SimpleDateFormat("yyyyMMddSS");
        yearMonthDayN = f.format(date.getTime())
        yearMonthDayN = yearMonthDayN.substring(0, 10)
//        yearMonthDayN = yearMonthDayN.substring(0, 8) + yearMonthDayN.substring(yearMonthDayN.length() - 1, yearMonthDayN.length())
        return getPersonNummer(yearMonthDayN, maxNoOfPnrs)
    }

    public AbstractMap getMalePersonNummer(int maxNoOfPnrs = 0) {
        gender = MALE
        getPersonNummerAtAge(20, maxNoOfPnrs * 2)
    }

    public AbstractMap getFemalePersonNummer(int maxNoOfPnrs = 0) {
        gender = FEMALE
        return getPersonNummerAtAge(20, maxNoOfPnrs * 2)
    }


    public AbstractMap getMalePersonNummerAtAge(int age, int maxNoOfPnrs = 0) {
        gender = MALE
        return getPersonNummerAtAge(age, maxNoOfPnrs * 2)
    }

    public AbstractMap getFemalePersonNummerAtAge(int age, int maxNoOfPnrs = 0) {
        gender = FEMALE
        return getPersonNummerAtAge(age, maxNoOfPnrs * 2)
    }

    public AbstractMap getPersonNummer(String yearMonthDayNumIn, int maxNoOfPnrs = 0) {
//        LOG.info("\n###yearMonthDayNumIn <$yearMonthDayNumIn>")
        socialSecurityNumbers.clear()

        this.yearMonthDayN = yearMonthDayNumIn.replaceAll("[^0-9]", "")
        if (maxNoOfPnrs != 0) {
            this.maxNoOfSocialSecurityNumbers = maxNoOfPnrs
        }
        if (yearMonthDayN.length() > 11) {
            this.yearMonthDayN = yearMonthDayN.subSequence(0, 11)
        }
        setDate()
//        LOG.info("\n###$yearMonthDayN")
//        LOG.info("\n###yearMonthDayN     <$yearMonthDayN> $maxNoOfPnrs")
        switch (yearMonthDayN.length()) {
            case 11:
                createPnr11()
                break

            case 10:
                createPnr10()
                break

            case 9:
                createPnr9()
                break

            case 8:
                createPnr8()
                break

            case 6:
                createPnr6()
                break
        }
        return socialSecurityNumbers
    }

    private void setDate() {
        switch (yearMonthDayN.length()) {
            case (11..11):
                n3 = yearMonthDayN.substring(10, 11)

            case (10..11):
                n2 = yearMonthDayN.substring(9, 10)


            case (9..11):
                n1 = yearMonthDayN.substring(8, 9)


            case (8..11):
                setDay(yearMonthDayN.substring(6, 8))

            case (6..11):
                setMonth(yearMonthDayN.substring(4, 6))

            case (2..11):
                setYear(yearMonthDayN.substring(0, 4))
        }
    }


    private void setYear(String data) {
        this.year = data
        yearNum = getVal(data[2]) + Integer.parseInt(data[3])
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearNum);
        calendar.set(Calendar.MONTH, monthNum - 1);
        numberOfDaysInMonth = calendar.getActualMaximum(Calendar.DATE);
    }

    private void setMonth(String inData) {
        def data = "0$inData"
        data = data.substring(data.length() - 2, data.length())
        this.month = data
        monthNum = getVal(data[0]) + Integer.parseInt(data[1])
    }

    private void setDay(String inData) {
        def data = "0$inData"
        data = data.substring(data.length() - 2, data.length())
        this.day = data
        dayNum = getVal(data[0]) + Integer.parseInt(data[1])
    }

    private int getVal(String data) {
        return getVal(Integer.parseInt(data))
    }

    private int getVal(int data) {
        def i = data * 2
        if (i > 9) {
            String ii = i
            return Integer.parseInt(ii[0]) + Integer.parseInt(ii[1])
        }
        return i
    }


    def createPnr6() {
        (1..numberOfDaysInMonth).each { day ->
            setDay(day.toString())
            (0..9).each { n1 ->
                (0..9).each { n2 ->
                    (0..9).each { n3 ->
                        checkSum(n1.toString(), n2.toString(), n3.toString())
                    }
                }
            }
        }
    }


    def createPnr8() {
        (0..9).each { n1 ->
            (0..9).each { n2 ->
                (0..9).each { n3 ->
                    checkSum(n1.toString(), n2.toString(), n3.toString())
                }
            }
        }
    }

    def createPnr9() {
        (0..9).each { n2 ->
            (0..9).each { n3 ->
                checkSum(n1, n2.toString(), n3.toString())
            }
        }
    }

    def createPnr10() {
        (0..9).each { n3 ->
            checkSum(n1, n2, n3.toString())
        }
    }

    def createPnr11() {
        checkSum(n1, n2, n3)
    }

    def checkSum(int n1, int n2, int n3) {
        checkSum(n1.toString(), n2.toString(), n3.toString())
    }

    def checkSum(String n1, String n2, String n3) {
        if (count++ < maxNoOfSocialSecurityNumbers) {
            n1Num = getVal(n1)
            n2Num = Integer.parseInt(n2)
            n3Num = getVal(n3)

            def i = n1Num + n2Num + n3Num + yearNum + monthNum + dayNum
            def modulo = i % 10
            int n4 = 10 - modulo
            if (modulo == 0) {
                n4 = modulo
            }
            num = ""
            def year2 = year.substring(2, 4)
//                socialSecurityNumbers["$year2$month$day$n1$n2$n3$n4"] = "$year$month$day-$n1$n2$n3$n4"
            if (gender.isEmpty()) {
                addToList(n1, n2, n3, n4, year2)
            } else {
                if (isGenferFemaleRequired(n3)) {
                    addToList(n1, n2, n3, n4, year2)
                } else {
                    if (isGenferMaleRequired(n3)) {
                        addToList(n1, n2, n3, n4, year2)

                    }
                }

            }
        }
    }

    private void addToList(String n1, String n2, String n3, int n4, String year2) {
        socialSecurityNumbers["$year2$month$day$n1$n2$n3$n4"] = new SocialSecurityNumber(year, month, day, n1, n2, n3, n4)
    }

    boolean isGenferFemaleRequired(String n3) {
        return (gender.equals(FEMALE) && (["0", "2", "4", "6", "8"].contains(n3)))
    }

    boolean isGenferMaleRequired(String n3) {
        return (gender.equals(MALE) && !(["0", "2", "4", "6", "8"].contains(n3)))
    }
}
