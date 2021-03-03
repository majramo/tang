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


    public static final String PUBLIC_DATABASES = "/configFiles/databases.xls"
    public static final String PRIVATE_DATABASES = "/configFiles/databasesPrivate.xls"

    public static void setupDatabases(boolean readPrivateFiles = false) {
        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(PUBLIC_DATABASES)
        excelObjectProvider.addColumnsToRetriveFromFile(["dbName", "owner", "dbDriverName", "dbDriver", "dbUrl", "dbIp", "dbUserName", "pwd", "dbPassword", "dbTestDataBase", "dbKeyStore"])
        ArrayList<Object[][]> databases = excelObjectProvider.getGdcRows()

        if(readPrivateFiles){
            URL is = this.getClass().getResource(PRIVATE_DATABASES)
            if (is != null) {
                ExcelObjectProvider excelObjectProviderPrivate = new ExcelObjectProvider(PRIVATE_DATABASES)
                excelObjectProviderPrivate.addColumnsToRetriveFromFile(["dbName", "owner", "dbDriverName", "dbDriver", "dbUrl", "dbIp", "dbUserName", "pwd", "dbPassword", "dbTestDataBase", "dbKeyStore"])
                ArrayList<Object[][]> databasesPrivate = excelObjectProviderPrivate.getGdcRows()
                databases += databasesPrivate
            }
        };
        if(settings.debug == "true"){
            println "No  " + "DB name".padRight(25) + "User".padRight(20) + "Database".padRight(20) + "Url".padRight(30) + "Driver".padRight(40) + "Driver name".padRight(30)
            println "-"*160
        }
        databases.eachWithIndex {it,i->
            def dbName = (it["dbName"]).toString().trim()
            if (dbName != "" && dbName != null) {
                def dbSettings = [:]
                dbSettings['dbName'] = dbName
                dbSettings['owner'] = (it["owner"]).toString().trim()
                dbSettings['dbDriverName'] = (it["dbDriverName"]).toString().trim()
                dbSettings['dbDriver'] = (it["dbDriver"]).toString().trim()
                dbSettings['dbUrl'] = (it["dbUrl"]).toString().trim()
                dbSettings['dbIp'] = (it["dbIp"]).toString().trim()
                dbSettings['dbKeyStore'] = (it["dbKeyStore"]).toString().trim()
                dbSettings['dbUserName'] = (it["dbUserName"]).toString().trim()
                def dbPassword = (it["dbPassword"]).toString().trim()
                dbSettings['dbPassword'] = dbPassword
                dbSettings['dbTestDataBase'] = (it["dbTestDataBase"]).toString().trim()
                settings."${dbName}" = dbSettings
                if(settings.debug == true){
                    println "${i + 1}".padLeft(3, "0") + " " + "$dbName".padRight(25) + dbSettings['dbUserName'].padRight(20) + dbSettings['dbTestDataBase'].padRight(20) + dbSettings['dbUrl'].padRight(30)+ dbSettings['dbIp'].padRight(30)+ dbSettings['dbDriver'].padRight(40) + dbSettings['dbDriverName'].padRight(30)
                }
            }
        }
    }
}
