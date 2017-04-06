package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider


public final class SystemPropertiesInitation {

    static SettingsHelper settingsHelper = SettingsHelper.getInstance()
    static settings = settingsHelper.settings

    public static List getSystemData(String systemColumn) {
        def targetDb = systemColumn.toLowerCase() + "_Target"
        def sourceDb = systemColumn.toLowerCase() + "_Source"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()

        def systemInputFile = systemColumn.toLowerCase() + settings.systemInputFile

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(systemInputFile)
        [excelObjectProvider, system, targetDb, sourceDb]
    }
}