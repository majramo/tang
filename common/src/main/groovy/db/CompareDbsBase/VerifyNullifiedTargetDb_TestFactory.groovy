package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

import static dtos.base.Constants.CompareType.DIFF

class VerifyNullifiedTargetDb_TestFactory {

    @Parameters(["systemColumn", "actionColumn", "tableColumn", "maskingColumn", "excludeMaskingColumn"] )
    @Factory
    public Object[] createVerifyTNullifiedInstances(ITestContext testContext, String systemColumn, String actionColumn, @Optional ("") String tableColumn, @Optional ("") String maskingColumn, @Optional ("") String  excludeMaskingColumn) {

        def (ExcelObjectProvider excelObjectProviderMaskAction, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def inputFile = excelObjectProviderMaskAction.inputFile
        excelObjectProviderMaskAction.addColumnsToRetriveFromFile(["System", "Table", "Column", "Masking", "Action", "SearchCriteria", "SearchExtraCondition"])
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        if(!tableColumn.isEmpty()){
            excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Table", tableColumn.trim().toUpperCase())
        }
        if(!maskingColumn.isEmpty()){
            excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Masking", maskingColumn.trim())
        }
        if(!excludeMaskingColumn.isEmpty()){
            excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Masking", excludeMaskingColumn.trim(), DIFF)
        }
        def excelBodyRowsMaskAction = SystemPropertiesInitation.readExcel(excelObjectProviderMaskAction)
        excelObjectProviderMaskAction.printRow(excelBodyRowsMaskAction, ["System", "Table", "Column", "Masking", "Action"])

        Reporter.log("Lines read <$excelBodyRowsMaskAction.size>")
        Reporter.log("Action <Masking> ")

        def result = [];
        excelBodyRowsMaskAction.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def column = excelRow["Column"]
            def searchCriteria = excelRow["SearchCriteria"]
            def searchExtraCondition = excelRow["SearchExtraCondition"]
            if (searchCriteria == null || searchCriteria == "-" ){
                searchCriteria = ""
            }
            if (searchExtraCondition == null || searchExtraCondition == "-" ){
                searchExtraCondition = ""
            }

            result.add(new VerifyNullifiedTargetColumn_Test(testContext, targetDb, sourceDb, excelRow["System"], table, column, actionColumn, searchCriteria, searchExtraCondition))

        }

        return result;
    }

}
