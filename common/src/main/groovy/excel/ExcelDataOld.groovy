package excel

import static dtos.base.Constants.EXCEL_BODY_ROW_NUMBER

/**
 * Created by majidaram on 2014-04-11.
 */
class ExcelDataOld {
    Map headerMap = [:]
    Map bodyMap = [:]
    private int headersColumnNumber = 1
    private int bodyRowNumber = 1

    public addHeaderColumn(String field) {
        headerMap[headersColumnNumber++] = field
    }

    Map createNewBodyRow() {
        Map bodyRow = new HashMap()
        bodyRow[EXCEL_BODY_ROW_NUMBER] = bodyRowNumber
        bodyMap[bodyRowNumber++] = bodyRow
        return bodyRow
    }

    void setRowColumnData(Map bodyRow, int columnNumber, String value) {
        def columnName = headerMap[columnNumber]
        bodyRow[columnName] = value
    }

    Map header() {
        return headerMap
    }

    int size() {
        return bodyMap.size()
    }

    Map getBodyRows(int lines) {
        if (lines > 0) {
            if (lines > bodyMap.size()) {
                lines = bodyMap.size()
            }
            return bodyMap.subMap(1..lines)
        }

    }

    Map getBodyRows() {
        return bodyMap
    }


    Map getBodyRow(int rowNumber) {
        if (rowNumber > 0 && rowNumber <= bodyMap.size()) {
            return bodyMap[rowNumber]
        }
    }


}
