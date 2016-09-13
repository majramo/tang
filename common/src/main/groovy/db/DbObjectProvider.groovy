package db

import dtos.SettingsHelper
import dtos.base.SqlHelper
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import static dtos.base.Constants.CompareType.DIFF

import static dtos.base.Constants.dbRunTypeRows

class DbObjectProvider {

    private static SqlHelper sqlHelper;
    private final static Logger log = Logger.getLogger("DOP  ")
    private final static SettingsHelper settingsHelper = SettingsHelper.getInstance()
    private final static def settings = settingsHelper.settings
    private final static def applicationConf = settingsHelper.applicationConf

    public static Object[][] getObjects(dbName, query, int lines = 10) {
        sqlHelper = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        String queryToRun = String.format(query, lines)     //Todo max inläsning?
        def objectsFromDb = sqlHelper.sqlConRun("Fetch rows", dbRunTypeRows, queryToRun, lines, dbName)
        return getDbObjects(objectsFromDb)
    }


    public static Object[][] getObject(dbName, query, int lines = 10) {
        sqlHelper = new SqlHelper(null, log, dbName, settings.dbRun, settings)
        String queryToRun = String.format(query, lines)     //Todo max inläsning?
        def objectsFromDb = sqlHelper.sqlConRun("Fetch rows", dbRunTypeRows, queryToRun, lines, dbName)
        return getDbObject(objectsFromDb, lines)
    }

    private static ArrayList<Object[][]> getDbObjects(objects) {
        ArrayList<Object[][]> valueList = new ArrayList<Object[][]>()
        def values = []
        objects.each { row ->
            values = []
            row.each { k, v ->
                if (v != null ) {
                    values.add((String) v)
                }else{
                    values.add("N/A")
                }
            }
            valueList.add(values)
        }

        return valueList
    }


    private static ArrayList<Object[][]> getDbObject(objects, line) {
        ArrayList<Object[][]> valueList = new ArrayList<Object[][]>()
        def values = []
        def row = objects[-1]

        values = []
        row.each { k, v ->
            if (v != null) {
                values.add((String) v)
            } else {
                values.add("N/A")
            }
        }
        valueList.add(values)


        return valueList
    }

    public static Object[][] sqlFieldJiraTestProvider(String inputFile, boolean smokeTest) {
        def result = [];
        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
        excelObjectProvider.addColumnsToRetriveFromFile(["row","field", "table", "testType", "jira", "comment"])
        if(!smokeTest){
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("testType", "Regression")
        }else{
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("testType", "Regression", DIFF)
        }
        def excelBodyRows = excelObjectProvider.getGdcObjects(0)
        excelBodyRows.eachWithIndex { excelRow, index ->
            def rowLine = index + 1
            String field = excelRow["field"]
            String table = excelRow["table"]
            String jira = excelRow["jira"]
            String comment = excelRow["comment"]
            String testType = excelRow["testType"]
            String rowId = "$rowLine:" +  excelRow["row"]
            if(table == null || table == "-"){
                table = ""
            }
            if(field == null || field == "-"){
                field = ""
            }
            if(jira == null || jira == "-"){
                jira = ""
            }
            if(comment == null || comment == "-"){
                comment = ""
            }
            if(testType == null || testType == "-"){
                testType = ""
            }
            if( field != "" && table != "" ){
                result.add([rowId, field, table, jira, comment, testType])
            }
        }
        return result;
    }

}
