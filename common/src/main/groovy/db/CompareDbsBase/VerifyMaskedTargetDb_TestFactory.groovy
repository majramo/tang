package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class VerifyMaskedTargetDb_TestFactory {



    @Parameters(["inputFileColumn", "schemaColumn", "atgardColumn"] )
    @Factory
    public Object[] createVerifyTruncatedInstances(String inputFileColumn, String schemaColumn, String atgardColumn) {

        def targetDb = schemaColumn.toLowerCase() + "_Target"
        def sourceDb = schemaColumn.toLowerCase() + "_Source"
        targetDb = schemaColumn.toLowerCase() + "_Source"
        sourceDb = schemaColumn.toLowerCase() + "_Target"
        def system = schemaColumn[0].toUpperCase() + schemaColumn[1..-1].toLowerCase()
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile(["Tabell", "Kolumn"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Atgard", atgardColumn)
        def excelBodyRows = excelObjectProvider.getGdcObjects(5)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Tabell", "Atgard"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Tabell"]
            def column = excelRow["Kolumn"]

            result.add(new VerifyMaskedTargetColumn_Test(targetDb, sourceDb, excelRow["System"], table, column, atgardColumn))

        }
        return result;
    }

}
