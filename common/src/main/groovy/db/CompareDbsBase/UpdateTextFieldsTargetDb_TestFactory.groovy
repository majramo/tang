package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class UpdateTextFieldsTargetDb_TestFactory {


    @Parameters(["systemColumn", "actionColumn", "maskingColumn", "executeColumn", "tableColumn", "onlyAaZzCharColumnn"])
    @Factory
    public Object[] createInstances(String systemColumn, String actionColumn, String maskingColumn, boolean executeColumn, String tableColumn, @Optional("false")boolean onlyAaZzCharColumnn) {

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Column", "Action", "SearchExtraCondition"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        if(!actionColumn.isEmpty()){
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn.trim())
        }
        if(!maskingColumn.isEmpty()){
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("Masking", maskingColumn.trim())
        }
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
            result.add(new UpdateTextFieldsTable(targetDb, excelRow["System"], table, action, column, searchExtraCondition, maskingColumn, executeColumn, onlyAaZzCharColumnn))
        }
        return result;
    }

}
