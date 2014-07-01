package excel

/**
 * Created by Tavera on 2014-04-22.
 */
class ExcelBodyRow {
    Map excelBodyMap = [:]

    public void setBodyRowValue(k, v) {
        excelBodyMap[k] = v
    }

    public String getColumn(k) {
        return excelBodyMap[k]
    }

    public String toString() {
        return excelBodyMap.toString()
    }
}
