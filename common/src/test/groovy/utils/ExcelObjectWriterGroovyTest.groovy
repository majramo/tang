package utils

import base.SystemProfile
import excel.ExcelFileWriter
import org.testng.annotations.Test

public class ExcelObjectWriterGroovyTest {
    private ArrayList<String> headers = ["System", "Table", "Column"]
    private ArrayList<String> bodyRow1 = ["A1", "AA1", "AAA1"]
    private ArrayList<String> bodyRow2 = ["A2", "AA2", "AAA2"]
    private SystemProfile excelSystemProfile
    private SystemProfile dbSystemProfile
    private String INPUT_FILE = "/excel/excelTestFile1.xls";

    @Test
    public void allTest() {

        ExcelFileWriter excelFileWriter = new ExcelFileWriter("C:/tmp/diverse/excelWriteTest.xls", "System")
        excelFileWriter.writeHeader(headers)
        excelFileWriter.writeBody([bodyRow1, bodyRow2])
        excelFileWriter.flushAndClose()
    }




}
