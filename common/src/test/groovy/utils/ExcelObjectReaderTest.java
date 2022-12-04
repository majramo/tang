package utils;

import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static excel.ExcelObjectProvider.getObject;
import static excel.ExcelObjectProvider.getObjects;

public class ExcelObjectReaderTest {
     private final static String[] columns = new String[] {"Number", "Age", "Name", "Gender"};
     private final static String INPUT_FILE = "/excel/excelTestFile1.xls";

    @Test(dataProvider = "all")
    public void all(String number, String name, String age, String gender) {
        printOut(number, name, age, gender);
    }

    @Test(dataProvider = "firstTwo")
    public void firstTwo(String number, String name, String age, String gender) {
        printOut(number, name, age, gender);
    }

    @Test(dataProvider = "thirdObject")
    public void thirdObject(String number, String name, String age, String gender) {
        printOut(number, name, age, gender);
    }

    @Test(dataProvider = "fourthObject")
    public void fourthObject(String number, String name, String age, String gender) {
        printOut(number, name, age, gender);
    }

    @Test(dataProvider = "ninthObject")
    public void ninthObject(String number, String name, String age, String gender) {
        printOut(number, name, age, gender);
    }


    //######################################################### Providers

    @DataProvider(name = "all")
    public static Object[][] all() {
        return  getObjects(INPUT_FILE, 0, columns);
    }

    @DataProvider(name = "firstTwo")
    public static Object[][] firstTwo() {
        return  getObjects(INPUT_FILE, 2, columns);
    }

    @DataProvider(name = "thirdObject")
    public static Object[][] thirdObject() {
        return  getObject(INPUT_FILE, 3, columns);
    }

    @DataProvider(name = "fourthObject")
    public static Object[][] fourthObject() {
        return  getObject(INPUT_FILE, 4, columns);
    }


    @DataProvider(name = "ninthObject")
    public static Object[][] ninthObject() {
        return  getObject(INPUT_FILE, 9, columns);
    }


    //######################################################### Print out

    private void printOut(String number, String name, String age, String gender) {
        Reporter.log("Number: " + number + " Name: " + name + " Age: " + age + " Gender: " + gender);
    }

}
