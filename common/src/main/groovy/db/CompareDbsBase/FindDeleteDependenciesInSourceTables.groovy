
package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test

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

public class FindDeleteDependenciesInSourceTables extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("FDDS ")
    def actionTables = [:]
    def childTables = [:]
    def deleteRelations = [:]
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
    def startTable
    def source_R_Relations

    @Parameters(["systemColumn", "startTableColumn", "deleteStatementColumn"] )
    @Test
    public void findDependenciesInSourceTables_test(String systemColumn, String startTableColumn, String deleteStatementColumn, ITestContext testContext) {
        super.setup()
        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        super.setSourceSqlHelper(testContext, sourceDb)
        source_R_Relations = sourceDbSqlDriver.sqlConRun("Get data ", dbRunTypeRows, SOURCE_TABLE_QUERY_ORACLE_FIND, 0, sourceDb)

        startTable = startTableColumn.trim().toUpperCase()
        indentString = ""
        indentNo = 0
        reporterLogLn("");
        reporterLogLn("# " + "$counter".padLeft(4) + ": Parent: <$startTable>");

        actionTables[startTable] = startTable
        printRelations(startTable)

        reporterLogLn("\n\n--Delete");
        findDeleteRelations(startTable, deleteStatementColumn)
        deleteRelations["$startTable"] = "DELETE $startTable $deleteStatementColumn;"

        def deleteRelationsCount = deleteRelations.size()
        deleteRelations.eachWithIndex { k, v, i->
            reporterLogLn("--${i + 1}:$deleteRelationsCount  ($startTable-->) $k\n$v")
        }
    }

    private String printRelations(String parent) {
        def parentTable = parent.toUpperCase()
        indentString += "-"

        indentNo++
        source_R_Relations.findAll {it["PARENT_TABLE"]== "$parent"}.each { child->
            def childTable = child["CHILD_TABLE"].toString().toUpperCase()
            if (!startTable.contains(childTable) && childTable != parent)  {
                def deleteRule = child["DELETE_RULE"]
                def childConstraint = child["CHILD_CONSTRAINT"]
                def parentConstraint = child["PARENT_CONSTRAINT"]
                def indentNoStr = "$indentNo "
                if(indentNo.equals(1)){
                    indentNoStr = "\n$indentNo*"
                }
                if(deleteRule != "CASCADE") {
                    reporterLogLn("$indentNoStr!" + "$counter".padLeft(4) + " $indentString $childTable    $deleteRule");
                    reporterLogLn(" -- alter table $childTable drop constraint $childConstraint;--    $parentConstraint");
                    actionTables[childTable] = childTable
                }else {
                    reporterLogLn("$indentNoStr " + "$counter".padLeft(4) + " $indentString $childTable    -- CASCADE IGNORE");
                    reporterLogLn(" -- alter table $childTable drop constraint $childConstraint;--   $parentConstraint");
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
                deleteRelations["$parentTable --> $childTable"] = "DELETE $childTable $whereChildtDelete;\n-- SELECT * FROM $childTable $whereChildtDelete;\n\n"
            }
        }
    }
}
