package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class TruncateTargetDb_TestFactory {


    @Parameters(["systemColumn", "actionColumn"])
    @Factory
    public Object[] createTruncateInstances(String systemColumn, String actionColumn) {
        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Action"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        def excelBodyRows = SystemPropertiesInitation.readExcel(excelObjectProvider)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Action"])

        Reporter.log("Lines read <$excelBodyRows.size>")
        def result = [];
        def excludeTablesStr = settings.truncateExcludeTables
        if(!excludeTablesStr.isEmpty()){
           def excludeTables = excludeTablesStr.split(";")
            excludeTables.each {tableNameToExclude->
                excelBodyRows = excelBodyRows.findAll{!it["Table"].contains(tableNameToExclude)}
            }
            Reporter.log("Lines read after removing settings.excludeTables <$excludeTablesStr> <$excelBodyRows.size>")

        }
        excelBodyRows.eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
           result.add(new TruncateTargetTable_Test(targetDb, excelRow["System"], table, actionColumn))
        }
        return result;
    }

}
