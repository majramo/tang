package db.CompareDbsBase

import excel.ExcelObjectProvider
import org.testng.Reporter
import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class TruncateTargetDb_TestFactory extends CompareS2T_TestFactoryBase {


    @Parameters(["inputFileColumn", "schemaColumn", "atgardColumn"])
    @Factory
    public Object[] createTruncateInstances(String inputFileColumn, String schemaColumn, String atgardColumn) {

        def targetDb = schemaColumn.toLowerCase() + "_Target"
        def system = schemaColumn[0].toUpperCase() + schemaColumn[1..-1].toLowerCase()
        def result = [];

        ExcelObjectProvider excelObjectProvider = new ExcelObjectProvider(inputFileColumn)
        excelObjectProvider.addColumnsToRetriveFromFile(["Tabell"])
        excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
//        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Tabell", "AMSULOG")
//        excelObjectProvider.addColumnsCapabilitiesToRetrieve("Atgard", atgardColumn)
        def excelBodyRows = excelObjectProvider.getGdcObjects(2,1)
        excelObjectProvider.printRow(excelBodyRows, ["System", "Tabell", "Atgard"])

        Reporter.log("Number of lines read <$excelBodyRows.size>")
        excelBodyRows.unique().eachWithIndex { excelRow, index ->
            def table = excelRow["Tabell"]
            result.add(new TruncateTargetTable_Test(targetDb, excelRow["System"], table, atgardColumn))


        }
        return result;
    }

}
