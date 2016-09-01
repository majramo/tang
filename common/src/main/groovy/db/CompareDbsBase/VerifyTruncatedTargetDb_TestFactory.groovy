package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class VerifyTruncatedTargetDb_TestFactory extends CompareS2T_TestFactoryBase{



    @Parameters(["inputFileColumn", "schemaColumn", "atgardColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(String inputFileColumn, String schemaColumn, String atgardColumn) {

        def targetDb = schemaColumn.toLowerCase() + "_Target"
        def system = schemaColumn[0].toUpperCase() + schemaColumn[1..-1].toLowerCase()
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile(["Tabell"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Atgard", atgardColumn)
        def excelBodyRows = excelObjectProvider.getGdcObjects(5)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Tabell", "Atgard"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
//            int row = Integer.parseInt(excelRow["row"][0].replaceAll(/\..*/, ''))
            def table = excelRow["Tabell"]

            result.add(new VerifyTruncatedTargetTable_Test(targetDb, excelRow["System"], table, atgardColumn))

        }
        return result;
    }

}