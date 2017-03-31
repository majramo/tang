package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class TruncateTargetDb_TestFactory {


    @Parameters(["inputFileColumn", "systemColumn", "actionColumn"])
    @Factory
    public Object[] createTruncateInstances(String inputFileColumn, String systemColumn, String actionColumn) {

        def targetDb = systemColumn.toLowerCase() + "_Target"
        def system = systemColumn[0].toUpperCase() + systemColumn[1..-1].toLowerCase()
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile(["Table"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        def excelBodyRows = excelObjectProvider.getGdcObjects(15)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Action"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            result.add(new TruncateTargetTable_Test(targetDb, excelRow["System"], table, actionColumn))


        }
        return result;
    }

}
