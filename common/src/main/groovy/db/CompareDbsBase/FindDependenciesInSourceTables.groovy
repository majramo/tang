
package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeRows

public class FindDependenciesInSourceTables extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")
    def childTables = [:]
    def constraints = [:]
    def processedTables = []
    def SOURCE_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE NOT table_name IN (select view_name from all_views) AND OWNER = '%s' ORDER BY 1"
    def SOURCE_TABLE_SIZE_QUERY_ORACLE = "SELECT COUNT(1) COUNT_  FROM %s "
    def TARGET_TABLE_QUERY_ORACLE = "SELECT DISTINCT table_name FROM all_tab_cols WHERE NOT table_name IN (select view_name from all_views) AND OWNER = '%s' ORDER BY 1"
    def SOURCE_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    def TARGET_TABLE_QUERY_SQLSERVER = "SELECT DISTINCT Table_name FROM Information_schema.columns ORDER BY 1"
    private String SOURCE_TABLE_QUERY_ORACLE_FIND = """
SELECT
  C1_CONSTRAINT_TYPE, C1_CONSTRAINT_NAME,
  C_CONSTRAINT_NAME,
  P_CONSTRAINT_NAME,
  C1_TABLE_NAME,
  C_TABLE_NAME,  C_COLUMN,
  P_table_name, P_column,
 '-- DELETE FROM '   || C_TABLE_NAME || ' WHERE NOT (' || C_COLUMN ||') in ( SELECT ' || P_column || ' FROM ' || P_TABLE_NAME || '); -- ' ||  C1_CONSTRAINT_NAME D1,
 'SELECT count(*) FROM ' || C_TABLE_NAME || '; -- ' ||  C1_CONSTRAINT_NAME C0,
 'SELECT count(*) FROM ' || C_TABLE_NAME || ' WHERE NOT (' || C_COLUMN ||') in ( SELECT ' || P_column || ' FROM ' || P_TABLE_NAME || '); -- ' ||  C1_CONSTRAINT_NAME C1,
 'SELECT count(*) FROM ' || C_TABLE_NAME || ' WHERE ('     || C_COLUMN ||') in ( SELECT ' || P_column || ' FROM ' || P_TABLE_NAME || '); -- ' ||  C1_CONSTRAINT_NAME C2,
 'SELECT count(*) FROM ' || P_TABLE_NAME || ' WHERE NOT ('     || P_column ||') in ( SELECT ' || C_column || ' FROM ' || C_TABLE_NAME || '); -- ' ||  C1_CONSTRAINT_NAME P1,
 'SELECT count(*) FROM ' || P_TABLE_NAME || ' WHERE ('     || P_column ||') in ( SELECT ' || C_column || ' FROM ' || C_TABLE_NAME || '); -- ' ||  C1_CONSTRAINT_NAME P2,
 '-- ALTER TABLE ' || C_TABLE_NAME || ' drop constraint ' || C1_CONSTRAINT_NAME ||'; -- ' ||  C1_CONSTRAINT_NAME D2
FROM
(
  SELECT * FROM
  (
    WITH CONSTRAINT_COLUM_LIST AS (
      SELECT OWNER, TABLE_NAME, CONSTRAINT_NAME, LISTAGG(COLUMN_NAME,',') WITHIN GROUP ( ORDER BY POSITION ) AS COLUMN_LIST
      FROM USER_CONS_COLUMNS GROUP BY OWNER, TABLE_NAME, CONSTRAINT_NAME
    )
    SELECT DISTINCT
         C1.CONSTRAINT_TYPE C1_CONSTRAINT_TYPE, C1.CONSTRAINT_NAME C1_CONSTRAINT_NAME,
         C.CONSTRAINT_NAME C_CONSTRAINT_NAME,
         P.CONSTRAINT_NAME P_CONSTRAINT_NAME,
         C1.TABLE_NAME C1_TABLE_NAME,
         C.TABLE_NAME C_TABLE_NAME, C.COLUMN_LIST C_COLUMN,
         P.table_name P_table_name, P.column_list P_column
    from USER_CONSTRAINTS c1
    JOIN constraint_colum_list C ON c1.CONSTRAINT_NAME=C.CONSTRAINT_NAME and c1.owner=C.owner
    JOIN CONSTRAINT_COLUM_LIST P ON C1.R_CONSTRAINT_NAME=P.CONSTRAINT_NAME AND C1.R_OWNER=P.OWNER
  )
  WHERE  C1_CONSTRAINT_TYPE = 'R'
  --AND  P_TABLE_NAME = 'FTG'
)
""";


    def counter = 1
    def indentNo = 0
    def indentString = ""
    def parentTables
    def sourceDbResult

    @Parameters(["systemColumn", "tablesColumn"] )
    @Test
    public void findDependenciesInSourceTables_test(String systemColumn, String tablesColumn, ITestContext testContext) {
        super.setup()
        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        super.setSourceSqlHelper(testContext, sourceDb)
        sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data ", dbRunTypeRows, SOURCE_TABLE_QUERY_ORACLE_FIND, 0, sourceDb)

        parentTables = tablesColumn.toUpperCase().split(" ")
        parentTables.eachWithIndex { String table, i ->
            indentString = ""
            indentNo = 0
            reporterLogLn("");
            reporterLogLn("     " + "$counter".padLeft(4) + "Parent: <$table>");
            getTables(table)
            childTables["$table: $table"] = table
        }
        def childCounter = 0
        childTables.each { k, v ->
            indentString = ""
            reporterLogLn("")
            if(!parentTables.contains(k.toString().replaceAll(":.*", ""))){
                indentString = "--   "
            }else{
                indentString = "-----"
            }
            reporterLogLn("${indentString}--> $k, $v")
            def processChild = []
            sourceDbResult.findAll {it["C_TABLE_NAME"]== "$v"}.each {child->
                def childTable = child["C_TABLE_NAME"]
                if(!processChild.contains(childTable)){
                    childCounter++
                    reporterLogLn(indentString + "$childCounter".padLeft(4));
                    reporterLogLn("");
                    reporterLogLn(child["C0"]);
                    reporterLogLn(child["C1"]);
                    reporterLogLn(child["C2"]);
                    reporterLogLn(child["P1"]);
                    reporterLogLn(child["P2"]);
                    reporterLogLn(child["D1"]);
                    reporterLogLn(child["D2"]);
                    processChild.add(childTable)
                }
            }        }
        constraints.each {
            it.value.each {
                //reporterLogLn(it["D1"])
            }
        }
    }

    private String getTables(String parent) {
        def parentTable = parent.toUpperCase()
        indentString += "-"
        indentNo++
        sourceDbResult.findAll {it["P_TABLE_NAME"]== "$parent"}.each {child->
            def childTable = child["C_TABLE_NAME"].toString().toUpperCase()
            if (!parentTables.contains(childTable)){
                processedTables.add(childTable)
                counter++
                reporterLogLn("$indentNo " + "$counter".padLeft(4) + " $indentString $childTable");
                childTables["$parentTable: $childTable"] = childTable
                getTables(childTable)
            }
        }
        indentString -= "-"
        indentNo--
    }


}
