package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class VerifyMaskedTargetDb_TestFactory {


    @Parameters(["systemColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(ITestContext testContext, String systemColumn) {

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Column", "Action"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "Mask")
        def excelBodyRows = excelObjectProvider.getGdcRows()
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Column", "Action"])

        Reporter.log("Lines read <$excelBodyRows.size>")
        Reporter.log("Action <Masking> ")

        def result = [];
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def column = excelRow["Column"]

            result.add(new VerifyMaskedTargetColumn_Test(testContext, targetDb, sourceDb, excelRow["System"], table, column))

        }
        return result;
    }

}
