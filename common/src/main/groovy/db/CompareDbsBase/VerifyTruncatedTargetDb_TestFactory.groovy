package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class VerifyTruncatedTargetDb_TestFactory {



    @Parameters(["systemColumn", "actionColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(String systemColumn, String actionColumn) {

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["Table"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        ArrayList<Object[][]> excelBodyRows = excelObjectProvider.getGdcRows(2)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "action"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
        def result = [];
        excelBodyRows.eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]

            result.add(new VerifyTruncatedTargetTable_Test(targetDb, excelRow["System"], table, actionColumn))

        }
        return result;
    }

}
