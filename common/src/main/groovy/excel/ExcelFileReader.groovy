package excel

import exceptions.TangFileException
import org.apache.poi.hssf.record.formula.functions.Row
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.testng.Reporter

/**
 * This class reads an Excel file and creates an ExcelData object that contains all rows and columns from the first sheet
 *
 */
public class ExcelFileReader {
    String fileName
    ExcelData excelData

    /**
     *
     * @param fileName
     */
    public ExcelFileReader(String fileName) {
        this.fileName = fileName
    }

    public ExcelFileReader() {
    }

    public Object[][] getBodyRows() {
        excelData = getDataFromFile()
        return excelData.getBodyRows()
    }

    public Object[][] getBodyRows(int lines) {
        excelData = getDataFromFile(lines)
        return excelData.getBodyRows(lines)
    }

    public Object[][] getBodyRow(int line) {
        excelData = getDataFromFile(line)
        return excelData.getBodyRow(line)
    }

    public ExcelData getExcelDataFromFile(String fileName) {
        this.fileName = fileName
        return getDataFromFile()
    }


    private ExcelData getDataFromFile(int lines = 0) {
        Reporter.log("Reading file: $fileName")
        try {
            URL is = this.getClass().getResource(fileName);
            if (is == null) {
                throw new TangFileException("Resource " + fileName + " is not found")
            }
            File file = new File(is.toURI());
            FileInputStream fileInputStream = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> excelRowIterator = sheet.rowIterator();
            fileInputStream.close();
            return readLines(excelRowIterator, lines)
        } catch (FileNotFoundException e) {
            throw new TangFileException("Can't find file " + fileName, e)
        } catch (IOException e) {
            throw new TangFileException("Can't open file " + fileName, e)
        }
    }

    private ExcelData readLines(Iterator<Row> excelRowIterator, int lines) {
        ExcelData excelData = new ExcelData()
        int rowNumber = 1
        int bodyRowNumber = 1

        while (excelRowIterator.hasNext()) {
            int bodyColumnNumber = 1
            HSSFRow excelRow = excelRowIterator.next();
            if (rowNumber++ == 1) {
                //Header
                excelRow.each { excelHeaderColumn ->
                    excelData.addHeaderColumn(excelHeaderColumn.toString())
                }
            } else {
                //Body
                ExcelBodyRow currentBodyRow = excelData.createNewBodyRow()
                excelRow.each { excelBodyColumn ->
                    excelData.setRowColumnData(currentBodyRow, bodyColumnNumber++, excelBodyColumn.toString())
                }
                if (lines > 0 ) {
                    if (lines <= bodyRowNumber++) {
                        break
                    }
                }
            }

        }
        return excelData
    }
}