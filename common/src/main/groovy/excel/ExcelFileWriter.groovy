package excel
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.CellRangeAddress
;
/**
 * This class writes an Excel file and adds an autofilter
 *
 */
public class ExcelFileWriter {
    String fileName
    String sheetName
    HSSFSheet hSSFSheet
    HSSFWorkbook workbook
    ArrayList<String> headerColumns

    public ExcelFileWriter(String fileName, sheetName = "") {
        this.fileName = fileName
        this.sheetName = sheetName
        createExcelObject(fileName, sheetName)
    }

    public writeHeader(ArrayList<String> headerColumns) {
        this.headerColumns = headerColumns
        HSSFRow headerRow = hSSFSheet.createRow(0);
        // Create columns/headers
        headerColumns.eachWithIndex { value, int i ->
            HSSFCell cell = headerRow.createCell((Short)i);
            cell.setCellValue(value)
            print "$value "
        }
        println ""
    }

    public writeBody(bodyLines) {
        // Create cells
        bodyLines.eachWithIndex { bodyLine, int rowIndex ->
            HSSFRow bodyRow = hSSFSheet.createRow(rowIndex + 1 );

            //Data from Excel and/or Database
            bodyLine.eachWithIndex { columnValue, int columnIndex ->
                HSSFCell cell = bodyRow.createCell((Short)columnIndex );
                cell.setCellValue(columnValue)
                print "$columnValue, "
            }
            println ""
        }
        hSSFSheet.setAutoFilter(new CellRangeAddress(0, bodyLines.size(), 0, headerColumns.size()))
    }

    private createExcelObject(fileName, sheetname = "Sheet1") {
        workbook = new HSSFWorkbook();
        hSSFSheet = workbook.createSheet(sheetname);
    }


    public flushAndClose() {
        File file = new File(fileName)
        FileOutputStream fileOut = new FileOutputStream(file);
        headerColumns.eachWithIndex { String entry, int i ->
            hSSFSheet.autoSizeColumn((Short)i);
        }
        workbook.write(fileOut);
        fileOut.close();
    }
}