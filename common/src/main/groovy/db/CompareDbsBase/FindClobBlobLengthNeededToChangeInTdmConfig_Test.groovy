package db.CompareDbsBase

import base.AnySqlCompareTest
import dtos.base.SqlHelper
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.CompareType.DIFF
import static dtos.base.Constants.dbRunTypeFirstRow
import static dtos.base.Constants.dbRunTypeRows

public class FindClobBlobLengthNeededToChangeInTdmConfig_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")

    def SOURCE_TABLE_COLUMN_CLOB_BLOB_ORACLE = "SELECT  max( LENGTH(%1))  FROM %2 ;\n" ;

    def SOURCE_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name, column_name\n" +
            "FROM USER_TAB_COLS \n" +
            "WHERE NOT TABLE_NAME IN (SELECT VIEW_NAME FROM ALL_VIEWS) \n" +
            "and upper(data_type) in ('CLOB', 'BLOB')\n"



    @Parameters(["systemColumn", "actionColumn"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String systemColumn, String actionColumn, ITestContext testContext){
        super.setup()

        def (ExcelObjectProvider excelObjectProviderMaskAction, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        def inputFile = excelObjectProviderMaskAction.inputFile
        excelObjectProviderMaskAction.addColumnsToRetriveFromFile(["Table", "Column"])
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("System", system)
        //excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Action", actionColumn)
        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Type", "CLOB")
        def excelBodyRowsMaskAction = SystemPropertiesInitation.readExcel(excelObjectProviderMaskAction)
        def excelBodyRowsMaskActionClob = excelBodyRowsMaskAction.clone()

        excelObjectProviderMaskAction.addColumnsCapabilitiesToRetrieve("Type", "BLOB")
        excelBodyRowsMaskAction = SystemPropertiesInitation.readExcel(excelObjectProviderMaskAction)
        excelObjectProviderMaskAction.printRow(excelBodyRowsMaskAction, ["System", "Table", "Column"])
        excelBodyRowsMaskAction += excelBodyRowsMaskActionClob
        if(getDbType(sourceDb).equals("sqlserver")){
            //Todo for SqlServer
        }

        reporterLogLn("Source: <$sourceDb>");

        super.setSourceSqlHelper(testContext, sourceDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        reportTableSizes(sourceDb, sourceDbSqlDriver, excelBodyRowsMaskAction)



    }

    private reportTableSizes(sourceDb, SqlHelper sourceDbSqlDriver, excelBodyRowsMaskAction) {

        reporterLogLn("Number of table/columns (CLOB/BLOB) to check in $sourceDb <" + excelBodyRowsMaskAction.size() + ">")
        reporterLogLn("")
        def sizeMap = [:]

        def i = 1;
        reporterLogLn(   "No   " + "Max length".padRight(20) +  "Table".padRight(30) + "Column".padRight(30) )
        excelBodyRowsMaskAction.each {
            def table = it.Table
            def column = it.Column
            String sourceTableSql = "SELECT  max( LENGTH($column))maxLength  FROM $table"
            //reporterLogLn("Sql: <$sourceTableSql>");
            println sourceTableSql;
            def sourceDbTableSizeResult = sourceDbSqlDriver.sqlConRun("Get Clob/blob max length of <$column> in table <$table> size from $sourceDb", dbRunTypeFirstRow, sourceTableSql, 0, sourceDb)
            def size = 0
            if(sourceDbTableSizeResult != null && sourceDbTableSizeResult["maxLength"] != null){
                size = new BigInteger(sourceDbTableSizeResult["maxLength"].toString(), 10)
            }
            reporterLogLn( String.format("%04d:", i++) + String.format("%,d", size).padLeft(20) +  table.padRight(30) + column.padRight(30) )

            //reporterLogLn("$size    $table      $column")
        }


    }

}
