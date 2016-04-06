package dtos.base

import dtos.SettingsHelper
import groovy.sql.Sql
import org.apache.log4j.Logger

public class JdbcConnection {
    private static final String DB_NAME = ";databaseName="
    private static final String ERROR_DB_CONNECTION = "Could not establish connection to the database."
    private jDbcConnection
    private final static Logger log = Logger.getLogger("JdC  ")
    SettingsHelper settingsHelper = SettingsHelper.getInstance()
    def settings = settingsHelper.settings
    public boolean mockDb = false

    public JdbcConnection() {
        mockDb = true
    }

    public JdbcConnection(dbUrl, dbDriverName, dbUserName, dbPassword,  dbTestDataBase, dbDriver ) {
        String connnectionString
        if ( (null != dbUrl) && (null != dbDriverName) && (null != dbUserName) && (null != dbPassword)&& (null != dbDriver) ){
            try {
                if(dbDriverName  == settings.jdbcSqlDriverName || dbDriverName  == settings.jdbcJtdsSqlDriverName){
                    connnectionString = dbDriverName + ":" + dbUrl + DB_NAME + dbTestDataBase + " " + dbUserName + " "   + dbDriver
                    log.info connnectionString + " " + dbUserName + " "   + dbDriver
                    jDbcConnection= Sql.newInstance(dbDriverName + ":" + dbUrl + DB_NAME + dbTestDataBase, dbUserName, dbPassword, dbDriver)
                }else{
                    connnectionString = dbDriverName + ":" + dbUrl + "/" + dbTestDataBase
                    log.info connnectionString + " " + dbUserName + " "   + dbDriver
                    jDbcConnection= Sql.newInstance(connnectionString, dbUserName, dbPassword, dbDriver)
                }

            } catch (ClassNotFoundException e) {
                log.info "Can't find class" + e
            } catch (RuntimeException e) {
                log.info ERROR_DB_CONNECTION + e
            }
        }
    }

    public firstRow(query) {
        return jDbcConnection.firstRow(query)
    }

    public rows(query) {
        return jDbcConnection.rows(query)
    }

    public execute(query) {
        return jDbcConnection.execute(query)
    }

    public boolean isConnectionOk() {
        return jDbcConnection != null
    }
}
