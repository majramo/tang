package excel

import exceptions.TangFileException
import org.apache.log4j.Logger
import org.apache.poi.hssf.record.formula.functions.Row
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.testng.Reporter

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
   public Iterator<Object[]> getBodyRowObjectsNew(lines, HashMap excelCapabilities) {
        setDataFromFile(excelCapabilities, lines)
//        return excelObjectData.getBodyRowObjects(excelCapabilities)
//        return excelObjectData.getBodyRowObjects()
        return excelObjectData.excelBodyMap.iterator()
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

    private void setDataFromFile(HashMap excelCapabilities, int lines = 0) {
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
            readLines(excelRowIterator, lines, excelCapabilities)
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

    private void readLines(Iterator<Row> excelRowIterator, int lines, HashMap excelCapabilities ) {

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
                    try {
                        myInstance."$field" = excelBodyColumn.toString()
                    } catch (MissingPropertyException exception) {
                        log.error("Can't map a header for value <$field> $exception")
                    }
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
                    excelObjectData.excelBodyMap[bodyRowNumber] = a
                    bodyRowNumber++

                    if (lines > 0) {
                        if (lines < bodyRowNumber) {
                            break
                        }
                    }
                }
            }
        }
        println("### Capabilities")
        println("Lines: $lines")
        excelCapabilities.each {key, ExcelCellDataProperty value ->
            println(value.toString())

        }
        println("###")


    }

  }
