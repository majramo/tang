package db.CompareDbsBase

import base.AnySqlCompareTest
import excel.ExcelObjectProvider
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import java.sql.SQLSyntaxErrorException

import static dtos.base.Constants.dbRunTypeRows

public class ReportSchemaSizes_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")

    def SOURCE_QUERY_ORACLE = """(
     select owner, segment_name name, round(bytes/1024/1024, 0) Size_MB
     from dba_segments
     where segment_type = 'TABLE'
     --and NOT segment_name IN (select view_name from all_views)
) """

    def SOURCE_SIZES_QUERY_ORACLE = """
select owner, count(1) count_, sum(Size_MB) sum_ from
$SOURCE_QUERY_ORACLE
group by owner
order by sum_ desc"""

    def SOURCE_SIZE_SYSTEM_QUERY_ORACLE = """Select * from
$SOURCE_QUERY_ORACLE
WHERE OWNER ='%s'
order by Size_MB DESC"""

    @Parameters(["systemColumn", "excelModifiedTablesOnly"] )
    @Test
    public void compareSourceTableSizeEqualsTargetTableSizeTest(String systemColumn, @Optional("false")boolean excelModifiedTablesOnly, ITestContext testContext) {
        super.setup()

        def (ExcelObjectProvider excelObjectProvider, String system, Object targetDb, Object sourceDb) = SystemPropertiesInitation.getSystemData(systemColumn)

        String sourceDbOwner = settings."$sourceDb".owner
        super.setSourceSqlHelper(testContext, sourceDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(sourceDb)))

        reporterLogLn("Source: <$sourceDb>");
        reporterLogLn()

        def sourceTableSql = String.format(SOURCE_SIZE_SYSTEM_QUERY_ORACLE, sourceDbOwner.toUpperCase())
        //read database
        def sourceDbResult
        try {
            sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
        }catch (SQLSyntaxErrorException e){
            skipTest("Can't run, got error:\n$e")
        }

        reporterLogLn("System/owner $sourceDbOwner tables <" + sourceDbResult.size() + ">:")
        reporterLogLn("   No   " + "Size (MB)".padRight(13)+ "Table".padRight(50) + "Owner")
        reporterLogLn("------------------------------------------------------------------------------------------")
        sourceDbResult.eachWithIndex {it, i->
            def size = new BigInteger(it["SIZE_MB"].toString(), 10)
            reporterLogLn("   " + String.format("%04d ", i+1) + "<" + String.format("%,d", size).padLeft(10)+ "> " + it["NAME"].padRight(50) + it["OWNER"])
        }
        reporterLogLn()

        sourceTableSql = String.format(SOURCE_SIZES_QUERY_ORACLE, sourceDbOwner.toUpperCase())
        sourceDbResult = sourceDbSqlDriver.sqlConRun("Get data from $sourceDb", dbRunTypeRows, sourceTableSql, 0, sourceDb)
        reporterLogLn("All systems/owners:")
        reporterLogLn("   No   " + "Size (MB)".padRight(13)+ "Count".padRight(10) + "Owner")
        reporterLogLn("------------------------------------------------------------------------------------------")
        sourceDbResult.eachWithIndex {it, i->
            def icon = " "
            def sum = new BigInteger(it["SUM_"].toString(), 10)
            def count = new BigInteger(it["COUNT_"].toString(), 10)
            if(it["OWNER"].toString().toUpperCase() == sourceDbOwner.toUpperCase()){
                icon = "*"
            }
            reporterLogLn("$icon  " + String.format("%04d ", i + 1) + "<" + String.format("%,d", sum).padLeft(10) + "> <" + String.format("%,d", count).padLeft(7) + "> " + it["OWNER"])
        }
        reporterLogLn()
    }
}
