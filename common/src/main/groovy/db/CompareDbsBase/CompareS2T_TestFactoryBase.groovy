package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider

import static excel.ExcelObjectProvider.getGdcObjects

public class CompareS2T_TestFactoryBase {
    static boolean settingsChanged
    public static final String ENABLED = "enabled"
    public static final String SOURCE_DB = "sourceDb"
    public static final String SOURCE_SQL = "sourceSql"
    public static final String TARGET_SQL = "targetSql"
    public static final String TARGET_DB = "targetDb"
    public static final String THRESHOLD = "threshold"
    public static final String COMMENTS = "comments"
    public static final String ROW = "row"
    public static final String BY = "by"
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings

    protected ArrayList runCommon(String inputFile, String sourceDb, String targetDb, boolean enabledColumn, String byColumn) {
        def result = [];
        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
        excelObjectProvider.addColumnsToRetriveFromFile([ROW, ENABLED, SOURCE_SQL, TARGET_SQL, THRESHOLD, COMMENTS])
        if (enabledColumn) {
            excelObjectProvider.addColumnsCapabiliteisToRetrive(ENABLED, "true")
        }
        if (byColumn != "") {
            excelObjectProvider.addColumnsCapabiliteisToRetrive(BY, byColumn)
        }

        def excelBodyRows = excelObjectProvider.getGdcObjects(0)


        excelBodyRows.eachWithIndex { excelRow, index ->
            def rowLine = index + 1
            int row = 0
            try {
                row = Integer.parseInt(excelRow[ROW].replaceAll(/\..*/, ''))
            } catch (java.lang.NumberFormatException e) {
            }
            def sourceSql = excelRow[SOURCE_SQL]
            def targetSql = excelRow[TARGET_SQL]
            def threshold = excelRow[THRESHOLD]
            def comments = excelRow[COMMENTS]

            addObjectToList(result, row, sourceDb, sourceSql, targetDb, targetSql, threshold, comments, rowLine)
        }
        return result;
    }


    protected ArrayList runCustom(String inputFile, boolean enabledColumn, byColumn, sourceDbColumn) {
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
        excelObjectProvider.addColumnsToRetriveFromFile([ROW, ENABLED, SOURCE_DB, TARGET_DB, SOURCE_SQL, TARGET_SQL, THRESHOLD, COMMENTS])
        if (enabledColumn) {
            excelObjectProvider.addColumnsCapabiliteisToRetrive(ENABLED, "true")
        }
        if (byColumn != "") {
            excelObjectProvider.addColumnsCapabiliteisToRetrive(BY, byColumn)
        }
        if (sourceDbColumn != "") {
            excelObjectProvider.addColumnsCapabiliteisToRetrive(SOURCE_DB, sourceDbColumn)
        }
        def excelBodyRows = excelObjectProvider.getGdcObjects(0)

        excelBodyRows.eachWithIndex { excelRow, index ->
            def rowLine = index + 1
            int row = 0
            try {
                row = Integer.parseInt(excelRow[ROW].replaceAll(/\..*/, ''))
            } catch (java.lang.NumberFormatException e) {
            }
            def sourceDb = excelRow[SOURCE_DB]
            def sourceSql = excelRow[SOURCE_SQL]
            def targetSql = excelRow[TARGET_SQL]
            def targetDb = excelRow[TARGET_DB]
            def threshold = excelRow[THRESHOLD]
            def comments = excelRow[COMMENTS]
            addObjectToList(result, row, sourceDb, sourceSql, targetDb, targetSql, threshold, comments, rowLine)
        }
        return result;
    }

    protected void addObjectToList(result, row, sourceDb, sourceSql, targetDb, targetSql, threshold, comments, rowLine) {
        def dbCompareProperties
        if (sourceDb != "" && sourceSql != "") {
            dbCompareProperties = new DbCompareProperties("$rowLine:$row", sourceDb, sourceSql, targetDb, targetSql, threshold, comments)
            result.add(new CompareS2T_Test(dbCompareProperties))
        }
    }
}
