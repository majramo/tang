package db.CompareDbsBase

import dtos.SettingsHelper
import excel.ExcelObjectProvider
import org.testng.ITestContext
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import static dtos.base.Constants.CompareType.DIFF


class  VerifyMaskedTargetDb_TestFactory {

    @Parameters(["systemColumn", "actionColumn", "useHashMaxColumn", "tableColumn", "excludeTableColumn", "maskingColumn", "excludeMaskingColumn"] )
    @Factory
    public Object[] createVerifyMaskedInstances(ITestContext testContext, String systemColumn, String actionColumn, boolean useHashMaxColumn, @Optional ("") String tableColumn, @Optional ("") String excludeTableColumn, @Optional ("") String maskingColumn, @Optional ("") String excludeMaskingColumn) {

        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings

        systemColumn = systemColumn.trim()
        actionColumn = actionColumn.trim()
        tableColumn = tableColumn.trim()
        excludeTableColumn = excludeTableColumn.trim()
        maskingColumn = maskingColumn.trim()
        excludeMaskingColumn = excludeMaskingColumn.trim()

        def (ExcelObjectProvider excelObjectProviderMaskAction, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def inputFile = excelObjectProviderMaskAction.inputFile
        excelObjectProviderMaskAction.addColumnsToRetriveFromFile(["System", "Table", "Column", "Masking", "Action", "SearchCriteria", "SearchExtraCondition", "Type"])
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        if(!tableColumn.isEmpty()){
            excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Table", tableColumn.toUpperCase())
        }
        if(!excludeTableColumn.isEmpty()){
            excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Table", excludeTableColumn.toUpperCase(), DIFF)
        }
        if(!maskingColumn.isEmpty()){
            excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Masking", maskingColumn)
        }
        if(!excludeMaskingColumn.isEmpty()){
            excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Masking", excludeMaskingColumn, DIFF)
        }

//        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Column", "UTDELNINGSADRESS")

        def excelBodyRowsMaskAction = SystemPropertiesInitation.readExcel(excelObjectProviderMaskAction)
        def tableToIgnoreWhenVerification
        URL is = this.class.getResource("/${systemColumn}.tables.to.ignore.txt")
        try{
            tableToIgnoreWhenVerification = new FileReader(is.path).getText().split("\r\n").collect{it}
        } catch (Exception e) {
            tableToIgnoreWhenVerification = ""
        }
        if(tableToIgnoreWhenVerification != ""){
            def tableToIgnoreWhenVerificationUc = tableToIgnoreWhenVerification.collect{it.toUpperCase()}
            excelBodyRowsMaskAction = excelBodyRowsMaskAction.findAll{
                !tableToIgnoreWhenVerificationUc.contains(it.Table)
            }
        }
        excelObjectProviderMaskAction.printRow(excelBodyRowsMaskAction, ["System", "Table", "Column", "Masking", "Action", "Type"])

        Reporter.log("Lines read <$excelBodyRowsMaskAction.size>")
        println("Lines read <$excelBodyRowsMaskAction.size>")
        Reporter.log("Action <Masking> ")
        def maskingCount =[:]
        def maskingVerifyMaxCount = settings.maskingVerifyMaxCount ?: 1000 //maximum number of verification for each domain/masking
        def result = [];
        excelBodyRowsMaskAction.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Table"]
            def column = excelRow["Column"]
            def type= excelRow["Type"]
            def searchCriteria = excelRow["SearchCriteria"]
            def searchExtraCondition = excelRow["SearchExtraCondition"]
            def masking = excelRow["Masking"]
            if (maskingCount[masking] == null){
                maskingCount[masking] = 1
            }

            if (maskingCount[masking] <= maskingVerifyMaxCount) {
                maskingCount[masking] = maskingCount[masking] + 1
                if (searchCriteria == null || searchCriteria == "-") {
                    searchCriteria = ""
                }
                if (searchExtraCondition == null || searchExtraCondition == "-") {
                    searchExtraCondition = ""
                }

                result.add(new VerifyMaskedTargetColumn_Test(testContext, targetDb, sourceDb, excelRow["System"], table, column, type, actionColumn, masking, searchCriteria, searchExtraCondition, useHashMaxColumn))
            }
        }
        return result;

    }
}
