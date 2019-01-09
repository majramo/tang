package base

import dtos.SettingsHelper
import excel.ExcelObjectProvider
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Workbook

/**
 * Created by majaraaa on 2016-06-09.
 */
class InitDbSettings {

    public static void setupDatabases() {
        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider("/configFiles/databases.xls")
        excelObjectProvider.addColumnsToRetriveFromFile(["dbName", "owner", "dbDriverName", "dbDriver", "dbUrl", "dbUserName", "pwd", "dbPassword", "dbTestDataBase"])
        ArrayList<Object[][]> databases = excelObjectProvider.getGdcRows()

        databases.each {
            def dbName = (it["dbName"]).toString().trim()
            if (dbName != "" && dbName != null) {
                def dbSettings = [:]
                dbSettings['dbName'] = dbName
                dbSettings['owner'] = (it["owner"]).toString().trim()
                dbSettings['dbDriverName'] = (it["dbDriverName"]).toString().trim()
                dbSettings['dbDriver'] = (it["dbDriver"]).toString().trim()
                dbSettings['dbUrl'] = (it["dbUrl"]).toString().trim()
                dbSettings['dbUserName'] = (it["dbUserName"]).toString().trim()
                def dbPassword = (it["dbPassword"]).toString().trim()
                if (dbPassword.matches("B.*&G.*")){
                    //If dbPassword is a formula  like above then we put owner and pwd togheter
                    def pwd = (it["pwd"]).toString().trim().replaceAll(/\..*/, "")
                    dbPassword =  dbSettings['owner'] + pwd
                }
                dbSettings['dbPassword'] = dbPassword
                dbSettings['dbTestDataBase'] = (it["dbTestDataBase"]).toString().trim()
                settings."${dbName}" = dbSettings
            }
        }
    }
}
