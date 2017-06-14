package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class  VerifyMaskedTargetDb_TestFactory {


    @Parameters(["systemColumn", "actionColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(ITestContext testContext, String systemColumn, String actionColumn) {

        def (ExcelObjectProvider excelObjectProviderMaskAction, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def inputFile = excelObjectProviderMaskAction.inputFile
        excelObjectProviderMaskAction.addColumnsToRetriveFromFile(["System", "Table", "Column", "Action"])
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        def excelBodyRowsMaskAction = SystemPropertiesInitation.readExcel(excelObjectProviderMaskAction)
        excelObjectProviderMaskAction.printRow(excelBodyRowsMaskAction, ["System", "Table", "Column", "Action"])

        Reporter.log("Lines read <$excelBodyRowsMaskAction.size>")
        Reporter.log("Action <Masking> ")

        def result = [];
        excelBodyRowsMaskAction.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def column = excelRow["Column"]

            result.add(new VerifyMaskedTargetColumn_Test(testContext, targetDb, sourceDb, excelRow["System"], table, column, actionColumn))

        }

        return result;
    }

}
