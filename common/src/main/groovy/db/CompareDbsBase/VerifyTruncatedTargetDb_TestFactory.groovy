package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class VerifyTruncatedTargetDb_TestFactory {



    @Parameters(["inputFileColumn", "systemColumn", "actionColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(String inputFileColumn, String systemColumn, String actionColumn) {

        def targetDb = systemColumn.toLowerCase() + "_Target"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile(["Table"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsToRetriveFromFile("Action", actionColumn)
        def excelBodyRows = excelObjectProvider.getGdcObjects(0)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "action"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]

            result.add(new VerifyTruncatedTargetTable_Test(targetDb, excelRow["System"], table, actionColumn))

        }
        return result;
    }

}
