package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class UpdatePhoneNumberFieldsTargetDb_TestFactory {


    @Parameters(["systemColumn", "actionColumn", "maskingColumn", "executeColumn"])
    @Factory
    public Object[] createInstances(String systemColumn, String actionColumn, String maskingColumn, boolean executeColumn) {

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Column", "Action", "SearchExtraCondition"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Masking", maskingColumn)
        def excelBodyRows = SystemPropertiesInitation.readExcel(excelObjectProvider)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Column", "Action", "SearchExtraCondition"])

        Reporter.log("Lines read <$excelBodyRows.size>")
        def result = [];
        excelBodyRows.eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def action = excelRow["Action"]
            def column = excelRow["Column"]
            def searchExtraCondition = excelRow["SearchExtraCondition"]
            result.add(new UpdatePhoneNumberFields(targetDb, excelRow["System"], table, action, column, searchExtraCondition, executeColumn))
        }
        return result;
    }

}
