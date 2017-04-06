package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class TruncateTargetDb_TestFactory {


    @Parameters(["systemColumn", "actionColumn"])
    @Factory
    public Object[] createTruncateInstances(String systemColumn, String actionColumn) {

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Action"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        ArrayList<Object[][]> excelBodyRows = excelObjectProvider.getGdcRows()
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Action"])

        Reporter.log("Lines read <$excelBodyRows.size>")
        def result = [];
        excelBodyRows.eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            result.add(new TruncateTargetTable_Test(targetDb, excelRow["System"], table, actionColumn))
        }
        return result;
    }

}
