package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider


public final class SystemPropertiesInitation {

    static SettingsHelper settingsHelper = SettingsHelper.getInstance()
    static settings = settingsHelper.settings
    public static final String ENABLED = "enabled"

    public static List getSystemData(String systemColumn) {
        def targetDb = systemColumn.toLowerCase() + "Target"
        def sourceDb = systemColumn.toLowerCase() + "Source"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()

        def systemInputFile = systemColumn.toLowerCase() + settings.systemInputFile

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(systemInputFile)
        [excelObjectProvider, system, targetDb, sourceDb]
    }

    public static ExcelObjectProvider getExcelProvider(String systemColumn) {

        def systemInputFile = systemColumn.toLowerCase() + settings.systemInputFile

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(systemInputFile)
        return excelObjectProvider
    }

    public static List getSystemData(String systemColumn, systemInputFile) {
        def targetDb = systemColumn.toLowerCase() + "_Target"
        def sourceDb = systemColumn.toLowerCase() + "_Source"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(systemInputFile)
        [excelObjectProvider, system, targetDb, sourceDb]
    }

    public static  ArrayList<Object[][]> readExcelEnabled(excelObjectProvider){
        def columnsCapabilitiesToRetrieve = (settingsHelper.settings.columnsCapabilitiesToRetrieve.enabled).toString()
        if(columnsCapabilitiesToRetrieve != "[:]" && columnsCapabilitiesToRetrieve != ""){
            excelObjectProvider.addColumnsCapabilitiesToRetrieve(ENABLED, columnsCapabilitiesToRetrieve )
        }
        def excelBodyRows
        def excelRowsToRead = (settingsHelper.settings.excelRowsToRead).toString()
        if(excelRowsToRead != "[:]" && excelRowsToRead != "") {
            excelBodyRows = excelObjectProvider.getGdcRows(Integer.parseInt(excelRowsToRead))
        }else{
            excelBodyRows = excelObjectProvider.getGdcRows()
        }
        return excelBodyRows
    }

    public static  ArrayList<Object[][]> readExcel(ExcelObjectProvider excelObjectProvider){
        def excelBodyRows
        def excelRowsToRead = (settingsHelper.settings.excelRowsToRead).toString()
        if(excelRowsToRead != "[:]" && excelRowsToRead != "") {
            excelBodyRows = excelObjectProvider.getGdcRows(Integer.parseInt(excelRowsToRead))
        }else{
            excelBodyRows = excelObjectProvider.getGdcRows()
        }
        return excelBodyRows
    }
}