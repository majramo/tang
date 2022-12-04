package db.CompareDbsBase

import base.AnySqlCompareTest
import dtos.base.SqlHelper
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import org.testng.ITestContext
import org.testng.annotations.Parameters
import org.testng.annotations.Test

import static dtos.base.Constants.dbRunTypeRows

public class ReportPdbData_Test extends AnySqlCompareTest{
    private final static Logger log = LogManager.getLogger("CSC  ")

    def PDB_QUERY = """SELECT REPLACE(NAME, 'PDB', '') pdb, NAME, OPEN_MODE, RESTRICTED, TOTAL_SIZE, RECOVERY_STATUS 
From V\$containers
ORDER BY 1"""
    def CHANGE_DB_MODE_SET_ROOT = 'Alter Session Set Container=Cdb\$root'

    @Parameters(["pdbDbName"])
    @Test
    public void changePdbStartMode( String pdbDbName, ITestContext testContext){
        super.setup()

        SqlHelper admDbSqlHelper = new SqlHelper(null, log, pdbDbName, settings.dbRun, settings)
        admDbSqlHelper.execute(pdbDbName, CHANGE_DB_MODE_SET_ROOT)

        testContext.setAttribute("ADM_SOURCE_SQL_HELPER", admDbSqlHelper)
        reporterLogLn(reporterHelper.addIcons(getDbType(admDbSqlHelper)))

        reporterLogLn("Source close sql <$PDB_QUERY>")

        def debResult = admDbSqlHelper.sqlConRun("Get data ", dbRunTypeRows, PDB_QUERY, 0, pdbDbName)
        debResult.each{
            reporterLogLn(it)
        }
    }
}
