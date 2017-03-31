package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class VerifyMaskedTargetDb_TestFactory {



    @Parameters(["inputFileColumn", "systemColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(ITestContext testContext, String inputFileColumn, String systemColumn) {

        def targetDb = systemColumn.toLowerCase() + "_Target"
        def sourceDb = systemColumn.toLowerCase() + "_Source"
//        targetDb = systemColumn.toLowerCase() + "_Source"
//        sourceDb = systemColumn.toLowerCase() + "_Target"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile(["Table", "Column", "Action", "Masking"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        def excelBodyRows = excelObjectProvider.getGdcObjects(0)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Column", "Action"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
        Reporter.log("Action <Masking> ")
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def column = excelRow["Column"]

            result.add(new VerifyMaskedTargetColumn_Test(testContext, targetDb, sourceDb, excelRow["System"], table, column))

        }
        return result;
    }

}
