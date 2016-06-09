package base

import dtos.SettingsHelper
import excel.ExcelObjectProvider

/**
 * Created by majaraaa on 2016-06-09.
 */
class InitDbSettings {

    public static void setupDatabases() {
        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider("/configFiles/databases.xls")
        excelObjectProvider.addColumnsToRetriveFromFile(["dbName", "owner", "dbDriverName", "dbDriver", "dbUrl", "dbUserName", "dbPassword", "dbTestDataBase"])

        def databases = excelObjectProvider.getGdcObjects(0)
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
                dbSettings['dbPassword'] = (it["dbPassword"]).toString().trim()
                dbSettings['dbTestDataBase'] = (it["dbTestDataBase"]).toString().trim()
                settings."${dbName}" = dbSettings
            }
        }
    }
}
