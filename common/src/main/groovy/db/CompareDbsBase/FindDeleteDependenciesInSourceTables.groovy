
package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeRows

public class FindDeleteDependenciesInSourceTables extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("FDDS ")
    def actionTables = [:]
    def actionTablesWhere = [:]
    def childTables = [:]
    def deleteRelations = [:]
    def constraints = [:]
    def deleteStr = ""
    private String SOURCE_TABLE_QUERY_ORACLE_FIND = """
SELECT CHILD_TABLE, CHILDCOL, position, PARENT_TABLE, PARENTCOL, delete_rule, bt,  
'SELECT ' || CHILDCOL || ' FROM ' || CHILD_TABLE || ' WHERE ' || CHILDCOL || ' IN ( ' || 'SELECT ' ||PARENTCOL || ' FROM ' ||PARENT_TABLE || ' WHERE %s )' WHERESTR ,
CHILD_CONSTRAINT,PARENT_CONSTRAINT,
 'ALTER TABLE  ' || CHILD_TABLE ||' DISABLE ALL TRIGGERS;'
 FROM
(
 select 
  b.table_name CHILD_TABLE,b.table_name || '.' || b.column_name CHILDCOL,
        b.position,
        c.table_name PARENT_TABLE, c.table_name || '.' || c.column_name PARENTCOL,
        a.constraint_name,
        a.delete_rule ,
        b.table_name bt ,
        b.constraint_name CHILD_CONSTRAINT,
        c.constraint_name PARENT_CONSTRAINT
    from all_cons_columns b,
        all_cons_columns c,
        all_constraints a
   where b.constraint_name = a.constraint_name
        and a.owner           = b.owner
        and b.position        = c.position
        and c.constraint_name = a.r_constraint_name
        and c.owner           = a.r_owner
        and a.constraint_type = 'R' 
)
order by 7,6,4,2
""";

    def counter = 1
    def indentNo = 0
    def indentString = ""
    def parentTable
    def source_R_Relations

    @Parameters(["systemColumn", "tableColumn", "whereParentDelete"] )
    @Test
    public void findDependenciesInSourceTables_test(String systemColumn, String tableColumn, String whereParentDelete, ITestContext testContext) {
        super.setup()
        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        sourceDb = 'atg_Target'
        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        super.setSourceSqlHelper(testContext, sourceDb)
        source_R_Relations = sourceDbSqlDriver.sqlConRun("Get data ", dbRunTypeRows, SOURCE_TABLE_QUERY_ORACLE_FIND, 0, sourceDb)

        parentTable = tableColumn.trim().toUpperCase()
        def doneTable = []
        indentString = ""
        indentNo = 0
        reporterLogLn("");
        reporterLogLn("# " + "$counter".padLeft(4) + ": Parent: <$parentTable>");

        actionTables[parentTable] = parentTable
        printRelations(parentTable)

        reporterLogLn("\n\n--Delete");
        findDeleteRelations(parentTable, whereParentDelete)
        deleteRelations["$parentTable"] = "DELETE $parentTable $whereParentDelete;"

        def deleteRelationsCount = deleteRelations.size()
        deleteRelations.eachWithIndex { k, v, i->
            reporterLogLn("--${i + 1}:$deleteRelationsCount  $k\n$v")
        }
    }

    private String printRelations(String parent) {
        def parentTable = parent.toUpperCase()
        indentString += "-"

        indentNo++
        source_R_Relations.findAll {it["PARENT_TABLE"]== "$parent"}.each { child->
            def childTable = child["CHILD_TABLE"].toString().toUpperCase()
            if (!this.parentTable.contains(childTable) && childTable != parent)  {
                def deleteRule = child["DELETE_RULE"]
                def indentNoStr = "$indentNo "
                if(indentNo.equals(1)){
                    indentNoStr = "\n$indentNo*"
                }
                if(deleteRule != "CASCADE") {
                    reporterLogLn("$indentNoStr!" + "$counter".padLeft(4) + " $indentString $childTable    $deleteRule");
                    actionTables[childTable] = childTable
                }else {
                    reporterLogLn("$indentNoStr " + "$counter".padLeft(4) + " $indentString $childTable    -- CASCADE IGNORE");
                }
                printRelations(childTable)
                childTables["$parentTable: $childTable"] = childTable
            }
        }
        indentString -= "-"
        indentNo--
    }

    private String findDeleteRelations(String parent, whereParentDelete) {
        def parentTable = parent.toUpperCase()
        indentString += "-"

        indentNo++
        source_R_Relations.findAll {it["PARENT_TABLE"]== "$parent"}.each { actionTable->
            def whereChildtDelete = ""
            def childTable = actionTable["CHILD_TABLE"].toString().toUpperCase()
            def childColumn = actionTable["CHILDCOL"]
            def deleteRule = actionTable["DELETE_RULE"]
            def parentActionTable = actionTable["PARENT_TABLE"]
            def parentColumn = actionTable["PARENTCOL"]
            if(!parentActionTable.equals(childTable)){
                whereChildtDelete = "WHERE $childColumn IN (SELECT $parentColumn FROM $parentActionTable $whereParentDelete)"
            }

            findDeleteRelations(childTable, whereChildtDelete)
            if (deleteRule != "CASCADE") {
                deleteRelations["$parentTable $childTable"] = "DELETE $childTable $whereChildtDelete;\n-- SELECT * FROM $childTable $whereChildtDelete;\n\n"
            }
        }
    }



}