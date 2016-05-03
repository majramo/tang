package db.CompareDbsBase

import dtos.SettingsHelper

import static excel.ExcelObjectProvider.getGdcObjects

public class CompareS2T_TestFactoryBase {
    static boolean settingsChanged
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings

    protected ArrayList runCommon(String inputFile, String dbSource, String dbTarget, boolean onlyEnabled) {
        def result = [];
        def columns = new DbCompareProperties().getCommonFieldNames()
        def excelBodyRows = getGdcObjects(inputFile, 0, columns)

        excelBodyRows.eachWithIndex { excelRow, index ->
            def rowLine = index + 1
            int row = Integer.parseInt(excelRow["row"][0].replaceAll(/\..*/, ''))
            boolean rowEnabled = excelRow["enabled"][0] != null && excelRow["enabled"][0] == "true"
            def sourceSql = excelRow["sourceSql"][0]
            def targetSql = excelRow["targetSql"][0]
            def threshold = excelRow["threshold"][0]
            def comments = excelRow["comments"][0]

            addObjectToList(result, onlyEnabled, row, dbSource, sourceSql, dbTarget, targetSql, threshold, comments, rowEnabled, rowLine)
        }
        return result;
    }


    protected ArrayList runCustom(String inputFile, boolean onlyEnabled, dbSourceRun) {
        def result = [];
        def columns = new DbCompareProperties().getCustomFieldNames()
        def excelBodyRows = getGdcObjects(inputFile, 0, columns)

        excelBodyRows.eachWithIndex { excelRow, index ->
            def rowLine = index + 1
            int row = Integer.parseInt(excelRow["row"][0].replaceAll(/\..*/, ''))
            boolean rowEnabled = excelRow["enabled"][0] != null && excelRow["enabled"][0] == "true"
            def dbSource = excelRow["sourceDb"][0]
            def sourceSql = excelRow["sourceSql"][0]
            def targetSql = excelRow["targetSql"][0]
            def dbTarget = excelRow["targetDb"][0]
            def threshold = excelRow["threshold"][0]
            def comments = excelRow["comments"][0]
            if(dbSourceRun != "" ){
                if(dbSourceRun == dbSource){
                    addObjectToList(result, onlyEnabled, row, dbSource, sourceSql, dbTarget, targetSql, threshold, comments, rowEnabled, rowLine)
                }
            }else{
                addObjectToList(result, onlyEnabled, row, dbSource, sourceSql, dbTarget, targetSql, threshold, comments, rowEnabled, rowLine)
            }
        }
        return result;
    }

    protected void addObjectToList(result, onlyEnabled, row, dbSource, sourceSql, dbTarget, targetSql, threshold, comments, rowEnabled, rowLine) {
        def dbCompareProperties
        if (dbSource != "" && sourceSql != "") {
            if (onlyEnabled) {
                if (rowEnabled) {
                    dbCompareProperties = new DbCompareProperties("$rowLine:$row", dbSource, sourceSql, dbTarget, targetSql, threshold, comments)
                    result.add(new CompareS2T_Test(dbCompareProperties))
                }
            } else {
                dbCompareProperties = new DbCompareProperties("$rowLine:$row", dbSource, sourceSql, dbTarget, targetSql, threshold, comments)
                result.add(new CompareS2T_Test(dbCompareProperties))
            }
        }
    }
}
