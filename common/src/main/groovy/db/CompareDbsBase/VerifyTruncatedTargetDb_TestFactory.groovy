package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters
import static dtos.base.Constants.CompareType.LIKE

class VerifyTruncatedTargetDb_TestFactory {



    @Parameters(["systemColumn", "actionColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(String systemColumn, String actionColumn) {
        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["Table"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn, LIKE)
        def excelBodyRows = SystemPropertiesInitation.readExcel(excelObjectProvider)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Action"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
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
            result.add(new VerifyTruncatedTargetTable_Test(targetDb, excelRow["System"], table, actionColumn))
        }
        return result;
    }

}
