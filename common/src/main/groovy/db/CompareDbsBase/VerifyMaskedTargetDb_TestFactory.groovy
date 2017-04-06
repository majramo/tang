package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class VerifyMaskedTargetDb_TestFactory {

    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings

    @Parameters(["systemColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(ITestContext testContext, String systemColumn) {

        def targetDb = systemColumn.toLowerCase() + "_Target"
        def sourceDb = systemColumn.toLowerCase() + "_Source"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()
        def result = [];

        def systemInputFile = settings.systemInputFile + systemColumn.toLowerCase() + ".xls"

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(systemInputFile)
        excelObjectProvider.addColumnsToRetriveFromFile(["Table", "Column", "Action", "Masking"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "Mask")
        def excelBodyRows = excelObjectProvider.getGdcRows()
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Column", "Action"])

        Reporter.log("Lines read <$excelBodyRows.size>")
        Reporter.log("Action <Masking> ")
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def column = excelRow["Column"]

            result.add(new VerifyMaskedTargetColumn_Test(testContext, targetDb, sourceDb, excelRow["System"], table, column))

        }
        return result;
    }

}
