package db

import dtos.SettingsHelper
import dtos.base.SqlHelper
import org.apache.log4j.Logger

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
                if (v != null ) {
                    values.add((String)v)
                }else{
                    values.add("N/A")
                }
            }
            valueList.add(values)


        return valueList
    }


}
