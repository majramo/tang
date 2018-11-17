package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters
import static dtos.base.Constants.CompareType.DIFF

class UpdateTargetDb_TestFactory {


    @Parameters(["systemColumn"])
    @Factory
    public Object[] createTruncateInstances(String systemColumn) {

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Column", "Action"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "Truncate", DIFF)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("RunSql", "-", DIFF)
        def excelBodyRows = SystemPropertiesInitation.readExcel(excelObjectProvider)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Column", "Action", "RunSql"])

        Reporter.log("Lines read <$excelBodyRows.size>")
        def result = [];
        excelBodyRows.eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def action = excelRow["Action"]
            def targetSql = excelRow["RunSql"]
            result.add(new UpdateTargetTable_Test(targetDb, excelRow["System"], table, action, targetSql))
        }
        return result;
    }

}
