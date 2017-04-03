package excel

import dtos.SettingsHelper
import exceptions.TangFileException
import org.apache.log4j.Logger
import org.apache.poi.hssf.record.formula.functions.Row
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.testng.Reporter

import static dtos.base.Constants.CompareType.DIFF
import static dtos.base.Constants.CompareType.EQUAL
import static dtos.base.Constants.CompareType.NOT_EMPTY

/**
 * This class reads an Excel file and creates an ExcelData object that contains all rows and columns from the first sheet
 *
 */
public class ExcelFileObjectReader {
    public ClassBuilder
    String fileName
    ExcelObjectData excelObjectData
    final GroovyClassLoader groovyLoader = new GroovyClassLoader();
    def builder = new ClassBuilder(groovyLoader)
    private final static Logger log = Logger.getLogger(getClass())
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
/**
 *
 * @param fileName
 */
    public ExcelFileObjectReader(String fileName) {
        builder.setName("Gdc");
        Reporter.log("Reading excel file <$fileName>")
        log.info("Reading excel file <$fileName>")
        this.fileName = fileName
    }


    public Iterator<Object[]> getBodyRowObjects() {
        setDataFromFile()
        return excelObjectData.getBodyRowObjects()
    }

    public Iterator<Object[]> getBodyRowObjects(int lines) {
        setDataFromFile(lines)
        return excelObjectData.getBodyRowObjects()
    }

    public Iterator<Object[]> getBodyRowObject(int lines) {
        setDataFromFile(lines)
        return excelObjectData.getBodyRowObject()
    }

    public Iterator<Object[]> getBodyRowObjects(lines, HashMap capabilities) {
        setDataFromFile(capabilities, lines)
        return excelObjectData.getBodyRowObjects()
    }
   public Iterator<Object[]> getBodyRowObjectsNew(to, HashMap excelCapabilities, from = 0) {
        setDataFromFile(excelCapabilities, to, from)
//        return excelObjectData.getBodyRowObjects(excelCapabilities)
//        return excelObjectData.getBodyRowObjects()
        return excelObjectData.excelBodyMap.iterator()
    }

   public ArrayList<Object[][]> getBodyRows(to, HashMap excelCapabilities, from = 0) {
        getDataFromFile(excelCapabilities, to, from)
    }




    private void setDataFromFile(int lines = 0) {
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
            readLines(excelRowIterator, lines)
        } catch (FileNotFoundException e) {
            throw new TangFileException("Can't find file " + fileName, e)
        } catch (IOException e) {
            throw new TangFileException("Can't open file " + fileName, e)
        }
    }

    private void setDataFromFile(HashMap excelCapabilities, int to = 0, int from = 0) {
        println("\n\n###\nReading file: $fileName")
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
            readLines(excelRowIterator, to, excelCapabilities, from)
        } catch (FileNotFoundException e) {
            throw new TangFileException("Can't find file " + fileName, e)
        } catch (IOException e) {
            throw new TangFileException("Can't open file " + fileName, e)
        }
    }

    private  ArrayList<Object[][]> getDataFromFile(HashMap excelCapabilities, int to = 0, int from = 0) {
        println("\n\n###\nReading file: $fileName")
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
            getLines(excelRowIterator, to, excelCapabilities, from)
        } catch (FileNotFoundException e) {
            throw new TangFileException("Can't find file " + fileName, e)
        } catch (IOException e) {
            throw new TangFileException("Can't open file " + fileName, e)
        }
    }



    private void readLines(Iterator<Row> excelRowIterator, int lines) {
        excelObjectData = new ExcelObjectData()
        int rowNumber = 1
        int bodyRowNumber = 1

        while (excelRowIterator.hasNext()) {
            int bodyColumnNumber = 1
            int headerColumnNumber = 1
            HSSFRow excelRow = excelRowIterator.next();
            if (rowNumber++ == 1) {
                //Header
                def toString = ""
                excelRow.each { excelHeaderColumn ->
                    def field = excelHeaderColumn.toString()

                    excelObjectData.addHeaderColumn(field)
                    builder.addField(field, String)
                    headerColumnNumber++
                }
            } else {
                //Body
                Class myClass = builder.getCreatedClass()
                def myInstance = myClass.newInstance()
                int column = 1

                excelRow.each { excelBodyColumn ->
                    String field = excelObjectData.excelHeaderMap[column++]
                    try{
                        myInstance."$field" = excelBodyColumn.toString()
                    }catch(MissingPropertyException exception){
                        log.error("Can't map a header for value <$field> $exception")
                    }
                }
                excelObjectData.excelBodyMap[bodyRowNumber] = myInstance
                if (lines > 0 ) {
                    if (lines <= bodyRowNumber) {
                        break
                    }
                }
                bodyRowNumber++
            }

        }
    }

    private void readLines(Iterator<Row> excelRowIterator, int to, HashMap excelCapabilities, int from ) {

        excelObjectData = new ExcelObjectData()
        Map<Integer, ClassBuilder> excelBodyMap = new ExcelObjectData().excelBodyMap
        int rowNumber = 1
        int bodyRowNumber = 1

        while (excelRowIterator.hasNext()) {
            int bodyColumnNumber = 1
            int headerColumnNumber = 1
            HSSFRow excelRow = excelRowIterator.next();
            if (rowNumber++ == 1) {
                //Header
                def toString = ""
                //Here we add which columns that should be included
                excelRow.each { excelHeaderColumn ->
                    def field = excelHeaderColumn.toString()
                    if(excelCapabilities.containsKey(field)){
                        excelObjectData.addHeaderColumn(field, headerColumnNumber)
                        builder.addField(field, String)
                    }
                    headerColumnNumber++
                }
            } else {
                //Body
                Class myClass = builder.getCreatedClass()
                def myInstance = myClass.newInstance()
                int column = 1

                excelRow.each { excelBodyColumn ->
                    String field = excelObjectData.excelHeaderMap[column]
                    try {
                        myInstance."$field" = excelBodyColumn.toString()
                    } catch (MissingPropertyException exception) {
                      //  log.info("Can't map a header for value <$field> column<$column> $exception")
                    }
                    column++
                }
//                def addRow = true
//                if(excelCapabilities.size()) {
//                    excelCapabilities.each { key, ExcelCellDataProperty value ->
//                        if (addRow) {
//                            if (value.compare(myInstance."$key")) {
//                                addRow = true
//                            } else {
//                                addRow = false
//                            }
//                        }
//                    }
//                }

                    def a = [:]
                    if(excelCapabilities.size()) {
                        excelCapabilities.each { key, ExcelCellDataProperty value ->
                            a[key] = myInstance."$key"
                        }
                    }else{
                        myInstance.each {row->
                            row.each{
                               it.each{ee->
                                   it
                               }
                            }
                        }
                    }
                    excelBodyMap[bodyRowNumber++] = a
                if(bodyRowNumber > 300){
                    break
                }
                }

        }
        println("### Capabilities")
        println("Lines: $to")

        excelCapabilities.each {key, ExcelCellDataProperty value ->
            println(value.toString())
            if ((value.compareType == EQUAL)) {
                excelBodyMap = excelBodyMap.findAll { excelBodyRow->
                    excelBodyRow.value[value["column"]] == value["valueToComprae"]
                }
            } else {
                if (value.compareType == DIFF) {
                    excelBodyMap = excelBodyMap.findAll { excelBodyRow->
                        excelBodyRow.value[value["column"]] != value["valueToComprae"]
                    }
                }else {
                    if (value.compareType == NOT_EMPTY) {
                        excelBodyMap = excelBodyMap.findAll { excelBodyRow->
                            excelBodyRow.value[value["column"]] != ""
                        }
                    }
                }
            }

        }
        println("###")
        if (from > 0 && excelBodyMap.size() > from){
            excelBodyMap = excelBodyMap.subMap(from..excelBodyMap.size()-1)
        }
        if (to > 0 && excelBodyMap.size() > to){
            excelBodyMap = excelBodyMap.subMap(0..to)
        }
        excelObjectData.excelBodyMap = excelBodyMap

    }


    private  ArrayList<Object[][]>  getLines(Iterator<Row> excelRowIterator, int to, HashMap excelCapabilities, int from ) {

        excelObjectData = new ExcelObjectData()
        Map<Integer, ClassBuilder> excelBodyMap = new ExcelObjectData().excelBodyMap
        int rowNumber = 1
        int bodyRowNumber = 1
        int maxRowsToReadFromFile
        try {
            maxRowsToReadFromFile = Integer.parseInt(settings.maxRowsToReadFromFile.toString())
            if(maxRowsToReadFromFile < 100){
                maxRowsToReadFromFile = 100
            }
        } catch (NumberFormatException exception) {
            log.info("Can't find maxRowsToReadFromFile in settings:$exception")
        }

        while (excelRowIterator.hasNext()) {
            int bodyColumnNumber = 1
            int headerColumnNumber = 1
            HSSFRow excelRow = excelRowIterator.next();
            if (rowNumber++ == 1) {
                //Header
                def toString = ""
                //Here we add which columns that should be included
                excelRow.each { excelHeaderColumn ->
                    def field = excelHeaderColumn.toString()
                    if(excelCapabilities.containsKey(field)){
                        excelObjectData.addHeaderColumn(field, headerColumnNumber)
                        builder.addField(field, String)
                    }
                    headerColumnNumber++
                }
            } else {
                //Body
                Class myClass = builder.getCreatedClass()
                def myInstance = myClass.newInstance()
                int column = 1

                excelRow.each { excelBodyColumn ->
                    String field = excelObjectData.excelHeaderMap[column]
                    try {
                        myInstance."$field" = excelBodyColumn.toString()
                    } catch (MissingPropertyException exception) {
                      //  log.info("Can't map a header for value <$field> column<$column> $exception")
                    }
                    column++
                }
//                def addRow = true
//                if(excelCapabilities.size()) {
//                    excelCapabilities.each { key, ExcelCellDataProperty value ->
//                        if (addRow) {
//                            if (value.compare(myInstance."$key")) {
//                                addRow = true
//                            } else {
//                                addRow = false
//                            }
//                        }
//                    }
//                }

                    def a = [:]
                    if(excelCapabilities.size()) {
                        excelCapabilities.each { key, ExcelCellDataProperty value ->
                            a[key] = myInstance."$key"
                        }
                    }else{
                        myInstance.each {row->
                            row.each{
                               it.each{ee->
                                   it
                               }
                            }
                        }
                    }
                    excelBodyMap[bodyRowNumber++] = a
                if(maxRowsToReadFromFile > 0 && bodyRowNumber > maxRowsToReadFromFile){
                    break
                }
            }

        }
        println("### Capabilities")
        println("Lines: $to")

        excelCapabilities.each {key, ExcelCellDataProperty value ->
            println(value.toString())
            if ((value.compareType == EQUAL)) {
                excelBodyMap = excelBodyMap.findAll { excelBodyRow->
                    excelBodyRow.value[value["column"]] == value["valueToComprae"]
                }
            } else {
                if (value.compareType == DIFF) {
                    excelBodyMap = excelBodyMap.findAll { excelBodyRow->
                        excelBodyRow.value[value["column"]] != value["valueToComprae"]
                    }
                }else {
                    if (value.compareType == NOT_EMPTY) {
                        excelBodyMap = excelBodyMap.findAll { excelBodyRow->
                            excelBodyRow.value[value["column"]] != ""
                        }
                    }
                }
            }

        }
        ArrayList<Object[][]> valueList = new ArrayList<Object[][]>()
        excelBodyMap.each { row ->
            def gdc = [:]
            row.each{columns->
                columns.value.each { k, v ->
                    gdc[k] = v
                }
            }
            valueList.add(gdc)
        }
        valueList = valueList.unique()

        println("###")
        if (from > 0 && valueList.size() > from){
            if (to > 0 && valueList.size() > to){
                valueList = valueList[from-1..to-1]
            }else{
                valueList = valueList[from-1..valueList.size()-1]
            }
        }else{
            if (to > 0 && valueList.size() > to){
                valueList = valueList[0..to-1]
            }
        }
         return valueList

    }

 private void readLinesOrg(Iterator<Row> excelRowIterator, int to, HashMap excelCapabilities, int from ) {

        excelObjectData = new ExcelObjectData()
        int rowNumber = 1
        int bodyRowNumber = 1

        while (excelRowIterator.hasNext()) {
            int bodyColumnNumber = 1
            int headerColumnNumber = 1
            HSSFRow excelRow = excelRowIterator.next();
            if (rowNumber++ == 1) {
                //Header
                def toString = ""
                //Here we add which columns that should be included
                excelRow.each { excelHeaderColumn ->
                    def field = excelHeaderColumn.toString()
                    if(excelCapabilities.containsKey(field)){
                        excelObjectData.addHeaderColumn(field, headerColumnNumber)
                        builder.addField(field, String)
                    }
                    headerColumnNumber++
                }
            } else {
                //Body
                Class myClass = builder.getCreatedClass()
                def myInstance = myClass.newInstance()
                int column = 1

                excelRow.each { excelBodyColumn ->
                    String field = excelObjectData.excelHeaderMap[column]
                    try {
                        myInstance."$field" = excelBodyColumn.toString()
                    } catch (MissingPropertyException exception) {
                      //  log.info("Can't map a header for value <$field> column<$column> $exception")
                    }
                    column++
                }
                def addRow = true
                if(excelCapabilities.size()) {
                    excelCapabilities.each { key, ExcelCellDataProperty value ->
                        if (addRow) {
                            if (value.compare(myInstance."$key")) {
                                addRow = true
                            } else {
                                addRow = false
                            }
                        }
                    }
                }


                if(addRow) {
                    def a = [:]
                    if(excelCapabilities.size()) {
                        excelCapabilities.each { key, ExcelCellDataProperty value ->
                            a[key] = myInstance."$key"
                        }
                    }else{
                        myInstance.each {row->
                            row.each{
                               it.each{ee->
                                   it
                               }
                            }
                        }
                    }
                    if ((from > 0 && bodyRowNumber >= from ) || (from <= 0)){
                        excelObjectData.excelBodyMap[bodyRowNumber] = a
                    }
                    bodyRowNumber++

                    if (to > 0) {
                        if (to < bodyRowNumber) {
                            break
                        }
                    }
                }
            }
        }
        println("### Capabilities")
        println("Lines: $to")
        excelCapabilities.each {key, ExcelCellDataProperty value ->
            println(value.toString())

        }
        println("###")


    }

  }
