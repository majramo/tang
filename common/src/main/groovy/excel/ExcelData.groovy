package excel

import static dtos.base.Constants.EXCEL_BODY_ROW_NUMBER

/**
 * Created by majidaram on 2014-04-11.
 */
class ExcelData {
    Map excelHeaderMap = [:]
    Map excelBodyMap = [:]
    private int headersColumnNumber = 1
    private int bodyRowNumber = 1

    public addHeaderColumn(String field) {
        excelHeaderMap[headersColumnNumber++] = field
    }

    ExcelBodyRow createNewBodyRow() {
        ExcelBodyRow excelBodyRow = new ExcelBodyRow()
        excelBodyRow.setBodyRowValue(EXCEL_BODY_ROW_NUMBER, bodyRowNumber)
        excelBodyMap[bodyRowNumber++] = excelBodyRow
        return excelBodyRow
    }

    void setRowColumnData(ExcelBodyRow bodyRow, int columnNumber, String value) {
        def columnName = excelHeaderMap[columnNumber]
//        bodyRow[columnName] = value
        bodyRow.setBodyRowValue(columnName, value)
    }

    Map header() {
        return excelHeaderMap
    }

    int size() {
        return excelBodyMap.size()
    }

    Object[][] getBodyRows(int lines) {
        if (lines > 0) {
            if (lines > excelBodyMap.size()) {
                lines = excelBodyMap.size()
            }
            List<Object> valueList = new ArrayList<Object>();
            excelBodyMap.take(lines).each {
                    valueList.add([it.value])
            }
            return valueList
        }

    }

    Object[][] getBodyRows() {
        List<Object> valueList = new ArrayList<Object>();
        excelBodyMap.each {
            valueList.add([it.value])
        }
        return valueList
    }

    Object[][] getBodyRow(int lines) {

            List<Object> valueList = new ArrayList<Object>();
            excelBodyMap.getAt(lines).each {
                    valueList.add([it])
            }
            return valueList

    }




}
