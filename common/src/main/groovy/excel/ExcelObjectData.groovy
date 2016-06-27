package excel

import static dtos.base.Constants.EXCEL_BODY_ROW_NUMBER

/**
 * Created by majidaram on 2014-04-11.
 */
class ExcelObjectData {
    Map excelHeaderMap = [:]
    Map<Integer, ClassBuilder> excelBodyMap =  new HashMap<Integer, ClassBuilder>();
    private int headersColumnNumber = 1
    private int bodyRowNumber = 1

    public addHeaderColumn(String field) {
        excelHeaderMap[headersColumnNumber++] = field
    }

    public addHeaderColumn(String field, int headersColumnNumber) {
        excelHeaderMap[headersColumnNumber] = field
    }



    Map header() {
        return excelHeaderMap
    }

    int size() {
        return excelBodyMap.size()
    }


    Iterator<Object[]> getBodyRowObjects() {
        List<Object> valueList = new ArrayList<Object>();
        excelBodyMap.each {row->
            valueList.add(row.value)
        }
        return valueList.iterator()
    }


    Iterator<Object[]> getBodyRowObject() {
        List<Object> valueList = new ArrayList<Object>();
        valueList.add(excelBodyMap[excelBodyMap.size()])
        return valueList.iterator()
    }
}
