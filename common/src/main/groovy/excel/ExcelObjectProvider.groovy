package excel

import static dtos.base.Constants.CompareType.EQUAL
import static dtos.base.Constants.CompareType.NOT_EMPTY

class ExcelObjectProvider {

    public ExcelObjectProvider(inputFile){
        this.inputFile = inputFile
        excelCapabilities = [:]
        inputFile = ""

    }

    private static HashMap excelCapabilities = [:]
    private static String inputFile = ""


    public void printRow(excelBodyRows, ArrayList<String> columnsToPrint){
        println "### Resultat"
        println "Done $inputFile"
        excelBodyRows.eachWithIndex { excelRow, index ->
            def rowLine = index + 1
            def str = ""
            columnsToPrint.each { columnName ->
                def data =  excelRow[columnName]
                str += " $columnName <$data> \t"
            }
            println "$rowLine: $str"
        }
        println "###"
    }

    public static addColumnsToRetriveFromFile(ArrayList<String> columns){
        columns.each { columnName ->
            if (excelCapabilities[columnName] == null) {
                excelCapabilities[columnName] = new ExcelCellDataProperty(columnName)
            }
        }
    }

    public static addColumnsCapabilitiesToRetrieve(cellName, cellCompareValue, cellCompareType = EQUAL){
        ExcelCellDataProperty excelCellDataProperty = excelCapabilities[cellName]
        if(excelCellDataProperty == null){
            excelCellDataProperty = new ExcelCellDataProperty(cellName)
            excelCapabilities[cellName] = excelCellDataProperty
        }
        excelCellDataProperty.valueToComprae = cellCompareValue
        excelCellDataProperty.compareType = cellCompareType


    }


    public static addColumnsCapabilitiesNotEmptyToRetrieve(cellName){
        ExcelCellDataProperty excelCellDataProperty = excelCapabilities[cellName]
        if(excelCellDataProperty == null){
            excelCellDataProperty = new ExcelCellDataProperty(cellName)
            excelCapabilities[cellName] = excelCellDataProperty
        }
        excelCellDataProperty.valueToComprae = ""
        excelCellDataProperty.compareType = NOT_EMPTY

    }
    //Java
    public static Object[][] getObject(file, int line, String[] columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObject(line)
        return getExcelObjects(objectsFromExcel, columns)
    }

    //Groovy
    public static Object[][] getObjects(file, int lines, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(lines)
        return getExcelObjects(objectsFromExcel, columns)
    }

    //Groovy
    public static  ArrayList<Object[][]> getObjectsArray(file, int lines, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(lines)
        return getExcelObjects(objectsFromExcel, columns)
    }

    //Groovy
    public static Object[][] getObjects(file, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(0)
        return getExcelObjects(objectsFromExcel, columns)
    }

    //Groovy
    public static  ArrayList<Object[][]> getObjectsArray(file, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(0)
        return getExcelObjects(objectsFromExcel, columns)
    }

    //Groovy 2
    public static  ArrayList<Object[][]> getGdcObjects(file, int lines, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(lines)
        return getExcelGdcObjects(objectsFromExcel, columns)
    }

    //Groovy 1
    public static ArrayList<Object[][]> getGdcObjects(file, int lines, HashMap excelCapabilities) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjectsNew(lines, excelCapabilities)
        return getExcelGdcObjects(objectsFromExcel)
    }


    //Groovy 1
    public static  ArrayList<Object[][]> getGdcRows(int to = 0) {
        return getGdcRowsTo( to)
    }

    //Groovy 1
    public static  ArrayList<Object[][]> getGdcRowsTo(int to = 0) {
        int from = 0
        new ExcelFileObjectReader(inputFile).getBodyRows(to, excelCapabilities, from)
    }

    //Groovy 1
    public static  ArrayList<Object[][]> getGdcRowsFrom(int from = 0) {
        int to = 0
        new ExcelFileObjectReader(inputFile).getBodyRows(to, excelCapabilities, from)
    }

    //Groovy 1
    public static  ArrayList<Object[][]> getGdcRowsFromTo(int from = 0, int to) {
        new ExcelFileObjectReader(inputFile).getBodyRows(to, excelCapabilities, from)
    }


    //Groovy 1
    public static ArrayList<Object[][]> getGdcObjects(file, int lines, columns, HashMap capabilities) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(lines, capabilities)
        return getExcelGdcObjects(objectsFromExcel, columns)
    }


    //Groovy
    public static Object[][] getObject(file, int line, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObject(line)
        return getExcelObjects(objectsFromExcel, columns)
    }

    //Java
    public static Object[][] getObjects(file, int lines, String[] columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(lines)
        return getExcelObjects(objectsFromExcel, columns)
    }

    private static ArrayList<Object[][]> getExcelObjects(objects, columns) {
        ArrayList<Object[][]> valueList = new ArrayList<Object[][]>()
        def values = []
        objects.each { row ->
            values = []
            columns.each { column ->
                values.add(row."$column")
            }
            valueList.add(values)
        }
        return valueList
    }

    private static ArrayList<Object[][]> getExcelGdcObjects(objects, columns) {
        ArrayList<Object[][]> valueList = new ArrayList<Object[][]>()
        objects.each { row ->
            def gdc = [:]
            columns.each{
                gdc[it] = row[it]
            }
            valueList.add(gdc)
        }
        return valueList
    }

    private static ArrayList<Object[][]> getExcelGdcObjects(objects, HashMap columns) {
        ArrayList<Object[][]> valueList = new ArrayList<Object[][]>()
        objects.each { row ->
            def gdc = [:]
            columns.each{
                gdc[it] = row[it]
            }
            valueList.add(gdc)
        }
        return valueList
    }

    private static ArrayList<Object[][]> getExcelGdcObjects(objects) {
        ArrayList<Object[][]> valueList = new ArrayList<Object[][]>()
        objects.each { row ->
            def gdc = [:]
            row.each{columns->
                columns.value.each { k, v ->
                    gdc[k] = v
                }
            }
            valueList.add(gdc)
        }
        return valueList
    }


}
