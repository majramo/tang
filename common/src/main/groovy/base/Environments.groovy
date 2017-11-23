package base

import dtos.SettingsHelper
import excel.ExcelObjectProvider

public class Environments {
    def fields = []
    HashMap environments = [:]

    public Environments(fieldsToAddToEnv){

        //read enviroments from excel and add to sesttings
        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider("/configFiles/environments.xls")
        excelObjectProvider.addColumnsToRetriveFromFile(fieldsToAddToEnv)
        ArrayList<Object[][]> environmentsFromExcel = excelObjectProvider.getGdcRows()

        environmentsFromExcel.each {environmentFromExcel->
            def environmentName = (environmentFromExcel["name"]).toString().trim()
            if (environmentName != "" && environmentName != null) {
                 EnvironmentProperties environmentProperties = new EnvironmentProperties(environmentFromExcel)
                environments[environmentName] = environmentProperties
            }
        }
        settings.environments = environments
    }
}
