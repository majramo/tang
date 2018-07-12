package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

import static dtos.base.Constants.CompareType.DIFF

class NullifyTargetDb_TestFactory {


    @Parameters(["systemColumn", "actionColumn", "executeColumn", "tableColumn"])
    @Factory
    public Object[] createNullifyInstances(String systemColumn, String actionColumn, boolean executeColumn, String tableColumn) {

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Column", "Action", "SearchExtraCondition"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        if(!tableColumn.isEmpty()){
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("Table", tableColumn.trim().toUpperCase())
        }
        def excelBodyRows = SystemPropertiesInitation.readExcel(excelObjectProvider)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Column", "Action", "SearchExtraCondition"])

        Reporter.log("Lines read <$excelBodyRows.size>")
        def result = [];
        excelBodyRows.eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def action = excelRow["Action"]
            def column = excelRow["Column"]
            def searchExtraCondition = excelRow["SearchExtraCondition"]
            result.add(new NullifyTargetTable(targetDb, excelRow["System"], table, action, column, searchExtraCondition, executeColumn))
        }
        return result;
    }

}
