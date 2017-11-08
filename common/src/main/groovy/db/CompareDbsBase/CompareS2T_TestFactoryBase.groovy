package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider

public class CompareS2T_TestFactoryBase {
    static boolean settingsChanged
    public static final String ENABLED = "enabled"
    public static final String SOURCE_VALUE = "sourceValue"
    public static final String SOURCE_DB = "sourceDb"
    public static final String SOURCE_SQL = "sourceSql"
    public static final String TARGET_SQL = "targetSql"
    public static final String TARGET_DB = "targetDb"
    public static final String THRESHOLD = "threshold"
    public static final String COMMENTS = "comments"
    public static final String ROW = "row"
    public static final String BY = "by"
    public static final String TABLE_FIELD_TO_EXCLUDE = "tableFieldToExclude"
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings

    protected ArrayList runCommon(String inputFileColumn, String systemColumn, String considerSystemTableColumnAnalyse, String actionTypeColumn, String enabledColumn) {
        def result = [];
        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn, inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile([ROW, ENABLED, SOURCE_SQL, TARGET_SQL, THRESHOLD, COMMENTS, TABLE_FIELD_TO_EXCLUDE, BY])
        if(enabledColumn != ""){
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(ENABLED, enabledColumn)
        }
        def excelBodyRows = SystemPropertiesInitation.readExcelEnabled(excelObjectProvider)

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
            def tableFieldToExclude = excelRow[TABLE_FIELD_TO_EXCLUDE]
            def by = excelRow[BY]
            addObjectToList(result, row, sourceDb, sourceSql, targetDb, targetSql, threshold, comments, rowLine, by, considerSystemTableColumnAnalyse, tableFieldToExclude, actionTypeColumn)
        }
        return result;
    }


    protected ArrayList runCustom(String inputFile, sourceDbColumn, enabledColumn, byColumn) {
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFile)
        excelObjectProvider.addColumnsToRetriveFromFile([ROW, ENABLED, SOURCE_DB, TARGET_DB, SOURCE_SQL, TARGET_SQL, THRESHOLD, COMMENTS, BY])

        if (sourceDbColumn != "") {
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(SOURCE_DB, sourceDbColumn)
        }
        if (enabledColumn != "") {
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(ENABLED, enabledColumn)
        }
        if (byColumn != "") {
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(BY, byColumn)
        }
        def excelBodyRows = SystemPropertiesInitation.readExcelEnabled(excelObjectProvider)

        excelBodyRows.unique().eachWithIndex { excelRow, index ->
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
            def by = excelRow[BY]
            addObjectToList(result, row, sourceDb, sourceSql, targetDb, targetSql, threshold, comments, rowLine, by)
        }
        return result;
    }

    protected ArrayList runTargetToSourceValue(String inputFileColumn, targetDbColumn, enabledColumn, byColumn) {
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile([ROW, ENABLED, SOURCE_VALUE, TARGET_DB, TARGET_SQL, THRESHOLD, COMMENTS, BY])

        if (targetDbColumn != "") {
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(TARGET_DB, targetDbColumn)
        }
        if (enabledColumn != "") {
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(ENABLED, enabledColumn)
        }
        if (byColumn != "") {
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(BY, byColumn)
        }
        def excelBodyRows = SystemPropertiesInitation.readExcelEnabled(excelObjectProvider)

        excelBodyRows.eachWithIndex { excelRow, index ->
            def rowLine = index + 1
            int row = 0
            try {
                row = Integer.parseInt(excelRow[ROW].replaceAll(/\..*/, ''))
            } catch (java.lang.NumberFormatException e) {
            }
            def sourceValue = excelRow[SOURCE_VALUE]
            def targetSql = excelRow[TARGET_SQL]
            def targetDb = excelRow[TARGET_DB]
            def threshold = excelRow[THRESHOLD]
            def comments = excelRow[COMMENTS]
            def by = excelRow[BY]
            addObjectToTargetList(result, row, sourceValue, targetDb, targetSql, threshold, comments, rowLine, by)
        }
        return result;
    }


    protected void addObjectToList(result, row, sourceDb, sourceSql, targetDb, targetSql, threshold, comments, rowLine, by, considerSystemTableColumnAnalyse = "", String tableFieldToExclude = "", String actionTypeColumn = "" ) {
        def dbCompareProperties
        if (sourceDb != "" && sourceSql != "") {
            dbCompareProperties = new DbCompareProperties("$row : $rowLine", sourceDb, sourceSql, targetDb, targetSql, threshold, comments, by, considerSystemTableColumnAnalyse, tableFieldToExclude, actionTypeColumn)
            result.add(new CompareS2T_Test(dbCompareProperties))
        }
    }

    protected void addObjectToTargetList(result, row, sourceValue, targetDb, targetSql, threshold, comments, rowLine, by) {
        def dbTargetCompareProperties
        if (sourceValue != "" && targetDb != "" && targetSql != "" && comments != "" ) {
            dbTargetCompareProperties = new DbTargetCompareProperties("$row : $rowLine", sourceValue, targetDb, targetSql, threshold, comments, by)
            result.add(new CompareT2S_Value_Test(dbTargetCompareProperties))
        }
    }
}
