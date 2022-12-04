
package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.CompareType.DIFF
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

class FindDeleteDependenciesInSourceTables extends AnySqlCompareTest{
    private final static Logger log = LogManager.getLogger("FDDS ")
    def recurse = false
    def firstRelations = ""
    def alterStrs = ""
    def actionTables = [:]
    def childTables = [:]
    def printRelationsTables = []
    def deleteRelationsTables = []
    def deleteRelations = [:]
    def buffer = ""
    private String SOURCE_TABLE_QUERY_ORACLE_FIND = """
SELECT CHILD_TABLE, CHILDCOL, position, PARENT_TABLE, PARENTCOL, delete_rule, bt,  
'SELECT ' || CHILDCOL || ' FROM ' || CHILD_TABLE || ' WHERE ' || CHILDCOL || ' IN ( ' || 'SELECT ' ||PARENTCOL || ' FROM ' ||PARENT_TABLE || ' WHERE %s )' WHERESTR ,
CHILD_CONSTRAINT,PARENT_CONSTRAINT,
 'ALTER TABLE  ' || CHILD_TABLE ||' DISABLE ALL TRIGGERS;',
 '
ALTER TABLE ' || Child_Table  || ' ENABLE CONSTRAINT ' || Child_Constraint   || ';  --Enable_Child_Constraint      
ALTER TABLE ' || Parent_Table || ' ENABLE CONSTRAINT ' || Parent_Constraint  || ';  --Enable_Parent_Constraint   
ALTER TABLE ' || Child_Table  || ' DROP CONSTRAINT '   || Child_Constraint   || ';   --Drop_Child_Constraint 
ALTER TABLE ' || Parent_Table || ' DROP CONSTRAINT '   || Parent_Constraint  || ';  --Drop_Parent_Constraint  
SELECT ' || Childcol  || ' FROM ' || Child_Table  || ' GROUP BY ' || Childcol  || ' HAVING COUNT(1) > 1;  -- Duplicates_Child  
SELECT ' || Parentcol || ' FROM ' || Parent_Table || ' GROUP BY ' || Parentcol || ' HAVING COUNT(1) > 1;  -- Duplicates_Parent 
SELECT ' || Childcol  || ' FROM ' || Child_Table  || ' WHERE NOT ' || Childcol  || ' IN ( ' || 'SELECT ' || Parentcol || ' FROM ' || Parent_Table || ' ); -- Child_Records_Missing_In_Parent
--DELETE ' || Child_Table  || ' WHERE NOT ' || Childcol  || ' IN ( ' || 'SELECT ' || Parentcol || ' FROM ' || Parent_Table || ' ); -- Child_Records_Missing_In_Parent
SELECT ' || Parentcol || ' FROM ' || Parent_Table || ' WHERE NOT ' || Parentcol || ' IN ( ' || 'SELECT ' || Childcol  || ' FROM ' || Child_Table  || ' );  --Parent_Records_Missing_In_Child 
--DELETE ' || Parent_Table || ' WHERE NOT ' || Parentcol || ' IN ( ' || 'SELECT ' || Childcol  || ' FROM ' || Child_Table  || ' );  --Parent_Records_Missing_In_Child 
ALTER TABLE ' || Child_Table  || ' DISABLE ALL TRIGGERS ;  --Disbale_Child_Triggers 
ALTER TABLE ' || Parent_Table || ' DISABLE ALL TRIGGERS ;  --Disable_Parent_Triggers 
ALTER TABLE ' || Child_Table  || ' ENABLE  ALL TRIGGERS ;  --Enabale_Child_Triggers 
ALTER TABLE ' || Parent_Table || ' ENABLE  ALL TRIGGERS ;  --Enable_Parent_Triggers 
'  TABLESHELP
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
"""

    def counter = 1
    def indentNo = 0
    def indentString = ""
    def startTable
    def source_R_Relations
    def parentRelations

    @Parameters(["systemColumn", "recurseColumn", "startTableColumn", "deleteStatementColumn"] )
    @Test
    void findDependenciesInSourceTables_test(String systemColumn, boolean recurseColumn, String startTableColumn, String deleteStatementColumn, ITestContext testContext) {
        super.setup()
        this.recurse = recurseColumn

        this.recurse = recurseColumn
        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))
        super.setSourceSqlHelper(testContext, sourceDb)
        source_R_Relations = sourceDbSqlDriver.sqlConRun("Get data ", dbRunTypeRows, SOURCE_TABLE_QUERY_ORACLE_FIND, 0, sourceDb)
        def startTablesToRun
        if (startTableColumn.trim().isEmpty()){
            //Read all action tables from Excel
            excelObjectProvider.addColumnsToRetriveFromFile(["System", "Table", "Column", "Action"])
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("System", system)
            excelObjectProvider.addColumnsCapabilitiesToRetrieve("Action", "-", DIFF)
            def excelBodyRows = SystemPropertiesInitation.readExcel(excelObjectProvider)
            excelObjectProvider.printRow(excelBodyRows, ["System", "Table", "Column", "Action"])
            def dbActionTables = excelBodyRows["Table"].unique()
            def childTablesToRun = source_R_Relations.collect{it["CHILD_TABLE"]}.intersect(dbActionTables)
            def parentTablesToRun = source_R_Relations.collect{it["PARENT_TABLE"]}.intersect(dbActionTables)
            startTablesToRun = (childTablesToRun + parentTablesToRun).unique()
        }else{
            startTablesToRun = startTableColumn.trim().split(" ").collect{it.toUpperCase()}.unique()
        }

        super.reporterLogLn("")
        super.reporterLogLn("#####################################################################")
        super.reporterLogLn("#####################################################################")
        super.reporterLogLn("###")
        super.reporterLogLn("startTablesToRun: $startTablesToRun"  )
        super.reporterLogLn("###")
        super.reporterLogLn("#####################################################################")
        super.reporterLogLn("#####################################################################")
        super.reporterLogLn("")
        super.reporterLogLn("")
        startTablesToRun.eachWithIndex { startTableToRun, i ->
            source_R_Relations.findAll { it["PARENT_TABLE"] == startTableToRun }
            def childRelations = source_R_Relations.findAll{it["PARENT_TABLE"] == startTableToRun && it["CHILD_TABLE"] != startTableToRun}.collect{it["CHILD_TABLE"]}.unique()
            if(childRelations.size()>0){
                super.reporterLogLn("${i + 1}: $startTableToRun")
            }else{
                super.reporterLogLn("(${i + 1}: $startTableToRun)")
            }
            def noActionRelations = source_R_Relations.findAll{it["PARENT_TABLE"] == startTableToRun && it["CHILD_TABLE"] != startTableToRun && it["DELETE_RULE"] == "NO ACTION"}
            def noACtionChildTables = noActionRelations.collect{it["CHILD_TABLE"]}.unique()
            def otherRelations    = source_R_Relations.findAll{it["PARENT_TABLE"] == startTableToRun && it["CHILD_TABLE"] != startTableToRun && it["DELETE_RULE"] != "NO ACTION"}
            def otherRelationsChildTables = otherRelations.collect{it["CHILD_TABLE"]}.unique()
            noACtionChildTables.each{childTable->
                super.reporterLogLn("   ---> " + noActionRelations.findAll{it["PARENT_TABLE"] == startTableToRun  && it["CHILD_TABLE"] == childTable}["DELETE_RULE"][0] + ": $childTable")
            }
            otherRelationsChildTables.each{childTable->
                super.reporterLogLn("     -- (" + otherRelations.findAll{it["PARENT_TABLE"] == startTableToRun  && it["CHILD_TABLE"] == childTable}["DELETE_RULE"][0] + ": $childTable)")
            }
        }

        super.reporterLogLn("")

        startTablesToRun.each {startTableToRun ->
            firstRelations = ""
            alterStrs = ""
            actionTables = [:]
            childTables = [:]
            printRelationsTables = []
            deleteRelationsTables = []
            deleteRelations = [:]
            findDependenciesInSourceTables_tests(startTableToRun.trim().toUpperCase(), deleteStatementColumn, testContext)
        }
    }

    protected void reportStartTable(String startTable) {
        reporterLogLn("")
        reporterLogLn("########################################")
        reporterLogLn("########################################")
        reporterLogLn("########################################")
        reporterLogLn("###    Table $startTable")
        reporterLogLn("###    Table $startTable")
        reporterLogLn("###    Table $startTable")
        reporterLogLn("########################################")
        reporterLogLn("########################################")
        reporterLogLn("########################################")
        reporterLogLn("")
    }

    def reporterLogLn(String message){
        buffer += "$message\n"
    }
    void findDependenciesInSourceTables_tests(String startTableToRun, String deleteStatementColumn, ITestContext testContext) {
        startTable = startTableToRun
        buffer = ""
        reportStartTable(startTableToRun)
        firstRelations = "DELETE $startTableToRun;"

        parentRelations = source_R_Relations.findAll {it["PARENT_TABLE"]== "$startTableToRun"}
        reporterLogLn("$startTableToRun: Relations size <" + parentRelations.size() + ">")

        indentString = ""
        indentNo = 0
        reporterLogLn("")
        reporterLogLn("# " + "$counter".padLeft(4) + ": Parent: <$startTableToRun>")

        actionTables[startTableToRun] = startTableToRun
        printRelations(startTableToRun)

        reportStart("DELETE", "DELETE $startTableToRun $deleteStatementColumn;")
        findDeleteRelations(startTableToRun, deleteStatementColumn)
        deleteRelations["$startTableToRun"] = "DELETE $startTableToRun $deleteStatementColumn;"

        def deleteRelationsCount = deleteRelations.size()
        deleteRelations.eachWithIndex { k, v, i->
            reporterLogLn("--${i + 1}:$deleteRelationsCount  ($startTableToRun-->) $k\n$v")
        }
        reportStop()

        reportStart("First relations", "DELETE $firstRelations;")
        reportStart("Alter", "\n$alterStrs")
        log.info("First relations\n### $startTableToRun\n$firstRelations")
        log.info("Alter\n### \n$alterStrs")
        if(deleteRelations.size()> 1){
            super.reporterLogLn(buffer)
        }


    }

    private String printRelations(String parent) {
        def parentTable = parent.toUpperCase()
        if(!printRelationsTables.contains(parentTable)){
            printRelationsTables.add(parentTable)
        }else{
            if(!recurse) {
                return
            }
        }
        indentString += "-"

        indentNo++

        source_R_Relations.findAll {it["PARENT_TABLE"]== "$parent"}.each { child->
            def childTable = child["CHILD_TABLE"].toString().toUpperCase()
            if (!startTable.contains(childTable) && childTable != parent)  {
                def deleteRule = child["DELETE_RULE"]
                def childConstraint = child["CHILD_CONSTRAINT"]
                def childCol = child["CHILDCOL"]
                def parentConstraint = child["PARENT_CONSTRAINT"]
                def parentCol = child["PARENTCOL"]
                def TablesHelp = child["TABLESHELP"]
                def indentNoStr = "--$indentNo "
                if(indentNo.equals(1)){
                    indentNoStr = "\n -- $indentNo*"
                }
                if(deleteRule != "CASCADE") {
                    reporterLogLn("$indentNoStr " + "$counter".padLeft(4) + " $indentString $childTable    -- $deleteRule")
                    def alterStr = "alter table $childTable drop constraint $childConstraint;--    $parentConstraint\n" +
                            "--DELETE $childTable WHERE $childCol NOT IN (SELECT $parentCol FROM $parentTable);\n" +
                            "SELECT COUNT(1) FROM $childTable WHERE $childCol NOT IN (SELECT $parentCol FROM $parentTable);\n" +
                            "SELECT COUNT(1) FROM $parentTable WHERE $parentCol NOT IN (SELECT $childCol FROM $childTable);\n" +
                            TablesHelp
                    if(indentNo.equals(1)){
                        firstRelations += "\n DELETE $childTable;"
                        alterStrs += "$alterStr\n"
                    }
                    reporterLogLn("$indentNoStr!" + "$counter".padLeft(4) + " $indentString $childTable    $deleteRule")
                    reporterLogLn(alterStr)
                    actionTables[childTable] = childTable
                }else {
                    reporterLogLn("$indentNoStr " + "$counter".padLeft(4) + " $indentString $childTable    -- CASCADE IGNORE")
                    def alterStr = "alter table $childTable drop constraint $childConstraint;--    $parentConstraint\n" +
                            "--DELETE $childTable WHERE $childCol NOT IN (SELECT $parentCol FROM $parentTable);\n" +
                            "SELECT COUNT(1) FROM $childTable WHERE $childCol NOT IN (SELECT $parentCol FROM $parentTable);\n" +
                            "SELECT COUNT(1) FROM $parentTable WHERE $parentCol NOT IN (SELECT $childCol FROM $childTable);\n" +
                            TablesHelp
                    reporterLogLn(alterStr)
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
        if(!deleteRelationsTables.contains(parentTable)){
            deleteRelationsTables.add(parentTable)
        }else{
            if(!recurse) {
                return
            }
        }
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

    private reportStart(type, text){
        reporterLogLn("\n\n")
        reporterLogLn("--<<<<<<<<<<<<<<<")
        reporterLogLn("--###############")
        reporterLogLn("--$type             $type             $type")
        reporterLogLn("--")
        reporterLogLn("--$text;")
        reporterLogLn("--###############")
        reporterLogLn("--###############\n")

    }
    private reportStop(){
        reporterLogLn("\n")
        reporterLogLn("--###############")
        reporterLogLn("-->>>>>>>>>>>>>>>")

    }
}
