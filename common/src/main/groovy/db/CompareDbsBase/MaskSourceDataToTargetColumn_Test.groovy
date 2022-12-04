
package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import java.sql.SQLSyntaxErrorException

import static dtos.base.Constants.dbRunTypeRows

/*
 This class uses relationsships between tables to find out in which order the Delete of rows in each table (not Delete Rule = cascade)
 should be executed in proper way to maintain integrity in DB (Oracle)
Inputs are:
the source system
the table to delete records in
the where statement used when deleteing
the "delete statments" will be in the report
 */

class MaskSourceDataToTargetColumn_Test extends AnySqlCompareTest{
    private final static Logger log = LogManager.getLogger("MSTT ")
    def maskings = [:]


    @Parameters(["systemColumn", "tableColumnsColumn1", "tableColumnsColumn2", "tableColumnsColumn3", "tableColumnsColumn4"] )
    @Test
    void findDependenciesInSourceTables_test(String systemColumn, String tableColumnsColumn1, String tableColumnsColumn2, String tableColumnsColumn3, String tableColumnsColumn4, ITestContext testContext) {
        super.setup()
        splittString(tableColumnsColumn1)
        splittString(tableColumnsColumn2)
        splittString(tableColumnsColumn3)
        splittString(tableColumnsColumn4)

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        reporterLogLn("Source: <$sourceDb>")
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        reporterLogLn(reporterHelper.addIcons(getDbType(targetDb)))
        super.setSourceSqlHelper(testContext, sourceDb)
        super.setTargetSqlHelper(testContext, targetDb)

        maskings.eachWithIndex { Map.Entry<Object, Object> tableColumn, int i ->
            def table = tableColumn.value[0]
            def id = tableColumn.value[1]
            def column = tableColumn.value[2]
            def query = "select $id,  DBMS_LOB.SUBSTR( $column, 3800, 1) $column from $table where $column is not null AND rownum < 100".toString()
            println("")
            println("==> $table     $column")
            println(query)
            try {
                sourceDbSqlDriver.getConnection(sourceDb).getJDbcConnection().eachRow(
                        query){cursor->
                    def sourceValue = cursor[column]
                    sourceValue = sourceValue.replaceAll(/'/, '"')
                    def sourceId = cursor[id]
                    def updateStatement = "UPDATE $table SET $column = '$sourceValue' WHERE $id = $sourceId"
                    //println updateStatement
                    targetDbSqlDriver.execute(targetDb, updateStatement)
                }
            } catch (MissingMethodException e) {
                println "Inga rader"
                println "----"
            } catch (SQLSyntaxErrorException e) {
            println "Syntaxfel vid uppdatering"
            println "----"
        }

        }
    }

    def splittString(tableColumnsColumn){
        def table = tableColumnsColumn.split(" ")[0].trim().toUpperCase()
        def id = tableColumnsColumn.split(" ")[1].trim().toUpperCase()
        def columns = tableColumnsColumn.split(" ")[2..-1]
        columns.eachWithIndex { String column, int i ->
            maskings["${table}_$i" ]= [table, id, column.trim().toUpperCase()]
        }
    }

}
