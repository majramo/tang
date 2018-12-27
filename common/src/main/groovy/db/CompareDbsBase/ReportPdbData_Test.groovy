package db.CompareDbsBase

import base.AnySqlCompareTest
import dtos.base.SqlHelper
import org.apache.log4j.Logger
import org.testng.ITestContext
import org.testng.annotations.Optional
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeRows

public class ReportPdbData_Test extends AnySqlCompareTest{
    private final static Logger log = Logger.getLogger("CSC  ")

    def GET_PDB_DATA_ORACLE = '''SELECT NAME, OPEN_MODE, RESTRICTED, TOTAL_SIZE, RECOVERY_STATUS 
From V$containers'''

    @Parameters(["pdbDbName"])
    @Test
    public void changePdbStartMode( String pdbDbName, ITestContext testContext){
        super.setup()

        SqlHelper admDbSqlHelper = new SqlHelper(null, log, pdbDbName, settings.dbRun, settings)

        testContext.setAttribute("ADM_SOURCE_SQL_HELPER", admDbSqlHelper)
        reporterLogLn(reporterHelper.addIcons(getDbType(admDbSqlHelper)))

        reporterLogLn("Source close sql <$GET_PDB_DATA_ORACLE>")

        def debResult = admDbSqlHelper.sqlConRun("Get data ", dbRunTypeRows, GET_PDB_DATA_ORACLE, 0, pdbDbName)
        debResult.each{
            reporterLogLn(it)

        }
//




    }


}
