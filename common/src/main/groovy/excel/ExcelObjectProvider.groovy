package excel

class ExcelObjectProvider {


    public static Object[][] getObject(file, int line, String[] columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObject(line)
        return getExcelObjects(objectsFromExcel, columns)
    }

    public static Object[][] getObjects(file, int lines, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObjects(lines)
        return getExcelObjects(objectsFromExcel, columns)
    }

    public static Object[][] getObject(file, int line, columns) {
        Iterator<Object[]> objectsFromExcel = new ExcelFileObjectReader(file).getBodyRowObject(line)
        return getExcelObjects(objectsFromExcel, columns)
    }

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


}
