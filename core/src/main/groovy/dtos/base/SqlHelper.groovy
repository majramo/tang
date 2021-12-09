package dtos.base

import org.apache.log4j.Logger
import org.testng.Reporter
import org.testng.SkipException

import java.sql.PreparedStatement

import static dtos.base.Constants.*

public class SqlHelper {

    protected dbRun
    protected dbName = ""
    protected log
    def queryConditionsTextList = [:]
    def queryConditionsXmlList = [:]
    protected boolean sqlHelperMock = false
    def String dbQuery = ""
    def dbQueryType = ""
    def dbQueryExtension = ""
    def dbResult = [:]
    String dbQueryRun
    private jdbcConnections = [:]
    private static dbRecordLine
    private File sqlFile
    private final static Logger logger = Logger.getLogger("SqH  ")

    public SqlHelper(File sqlFile, log, dbName, dbRun, settings, decrypterPassword = "") {
        this.dbRun = dbRun
        this.dbName = dbName
        this.sqlFile = sqlFile
        if (log == null) {
            this.log = logger
        } else {
            this.log = log
        }
        if (dbRun == false) {
            if (!jdbcConnections[dbName]) {
                jdbcConnections[dbName] = new JdbcConnection()
            }
            sqlHelperMock = true

        } else {
            this.log.info "##SqlHelper " + this
            this.log.info "##Init dbName $dbName"
            this.log.info jdbcConnections
            this.log.info jdbcConnections[dbName]
            try {
                JdbcConnection jDbcConnection = null
                if (!jdbcConnections[dbName]) {
                    def passwordToUse = settings."$dbName".dbPassword
                    if(!decrypterPassword.isEmpty()) {
                        passwordToUse = decrypterPassword
                    }
                    settings."$dbName".with {
                        Reporter.log("### Connection <$dbName>: url<$dbUrl> usr<$dbUserName> db<$dbTestDataBase> driver <$dbDriverName>")
                        this.log.info("### Connection <$dbName>: url<$dbUrl> usr<$dbUserName> db<$dbTestDataBase> driver <$dbDriverName>")
                        jDbcConnection = new JdbcConnection(dbUrl, dbDriverName, dbUserName, passwordToUse, dbTestDataBase, dbDriver)
                        this.log.info "##Init jDbcConnection $jDbcConnection"
                    }
                    jdbcConnections[dbName] = jDbcConnection

                    this.log.info "# JDbcConnection $jdbcConnections"
                    jdbcConnections.each {
                        this.log.info "# jDbcConnection " + it
                    }
                }

            } catch (RuntimeException e) {
                this.log.error("Can't get connection")
                throw e
            }
        }
    }

    public boolean isConnectionOk(dbName) {
        if (!dbRun) {
            return true
        }
        JdbcConnection jDbcConnection = jdbcConnections[dbName]
        if (jDbcConnection == null) {
            Reporter.log("Can't connect to db: $dbName")
            throw new SkipException("Can't connect to db: $dbName")

        }
        return jDbcConnection.isConnectionOk()
    }

    private sqlConRun(dbLoggInfo, dbRunType, dbQueryRun, dbRecordLine = -1, dbName) {
        this.log.info "##SqlHelper " + this
        this.log.info "##Run dbName $dbName"
        jdbcConnections.each {
            this.log.info "##jDbcConnections " + it
        }
        log.debug "$dbLoggInfo\n$dbQueryRun"
        this.dbRecordLine = dbRecordLine
        JdbcConnection jDbcConnection = jdbcConnections[dbName]
        this.log.info "$dbLoggInfo $dbQueryRun "
        this.log.info "##Run jDbcConnection $jDbcConnection"
        dbResult = null

        if (jDbcConnection != null) {
            if (sqlHelperMock) {
                dbResult = [:]
                dbResult[DB_MOCK_DATA] = DB_MOCK_DATA
                return dbResult
            } else {
                switch (dbRunType) {
                    case dbRunTypeFirstRow:
                        dbResult = jDbcConnection.firstRow(dbQueryRun)
                        break

                    case dbRunTypeRows:
                        dbResult = jDbcConnection.rows(dbQueryRun)
                        break

                    case dbRunTypeRowsSelects:
                        dbResult = jDbcConnection.rows(dbQueryRun)
                        if (dbRecordLine > 0) {
                            dbResult = dbResult[dbRecordLine - 1]
                        } else {
                            dbResult = dbResult[0]
                        }
                        break
                }
            }
        }
        return dbResult
    }


    public String getQueryConditions() {

        def queryConditions = ""

        queryConditionsTextList.each { k, v ->
            def val
            if (v.isString) {
                val = "'$v.value'"
            } else {
                val = v.value
            }
            def qCondition = v.field + " " + val
            if (queryConditions) {
                queryConditions += "\n AND " + qCondition
            } else {
                queryConditions = qCondition
            }
        }
        queryConditionsXmlList.each { k, v ->
            def val
            if (v.isString) {
                val = "'$v.value'"
            } else {
                val = v.value
            }
            def qCondition = v.field + " " + val
            if (queryConditions) {
                queryConditions += "\n AND " + qCondition
            } else {
                queryConditions = qCondition
            }
        }

        if (dbQueryExtension != "") {
            if (queryConditions != "") {
                queryConditions += " $dbQueryExtension"
            }
        }
        return queryConditions
    }


    public String getInsertValues() {

        def queryConditions = ""
        def qValues = ""

        queryConditionsTextList.each { k, v ->
            def val
            if (v.isString) {
                val = "'$v.value'"
            } else {
                val = v.value
            }
            def qCondition = v.field + " " + val
            if (queryConditions) {
                queryConditions += " , " + qCondition
            } else {
                queryConditions = qCondition
            }
        }
        queryConditionsXmlList.each { k, v ->
            def val
            if (v.isString) {
                val = "'$v.value'"
            } else {
                val = v.value
            }
            def qCondition = v.field.replaceAll("=", "")
            def qValue = val
            if (queryConditions) {
                queryConditions += " , " + qCondition
                qValues += " , " + qValue
            } else {
                queryConditions = qCondition
                qValues = qValue
            }
        }

        if (dbQueryExtension != "") {
            if (queryConditions != "") {
                queryConditions += " $dbQueryExtension"
            }
        }
        return "( $queryConditions ) VALUES ( $qValues );"
    }


    protected getDb_result(dbName) {
        isConnectionOk(dbName)
        def queryConditions = getQueryConditions()
        dbQueryRun = "$dbQuery\n $queryConditions\n "
        dbResult = [:]
        String dbInsertStatement = dbQuery.replaceAll("SELECT \\* FROM", "INSERT INTO").replaceAll(" WHERE", "")
        dbInsertStatement += getInsertValues() + "\n"
        log.debug dbQueryRun
        log.debug dbInsertStatement
        if (sqlFile != null && sqlFile.exists()) {
            sqlFile.append(dbQueryRun)
            sqlFile.append(dbInsertStatement)
        }
        if (dbQueryType == dbRunTypeCount || dbQueryType == dbRunTypeFirstRow) {
            dbResult = sqlConRun("dbQueryRun", dbRunTypeFirstRow, dbQueryRun, dbName)
        } else {
            if (dbQueryType == dbRunTypeRowsSelects) {
                dbResult = sqlConRun("dbQueryRun", dbRunTypeRowsSelects, dbQueryRun, dbRecordLine, dbName)
            } else {
                dbResult = sqlConRun("dbQueryRun", dbRunTypeRows, dbQueryRun, dbName)
            }
        }
        return dbResult
    }

    public execute(dbName, String dbExecuteStatement) {
        log.info dbQueryRun
        log.info dbExecuteStatement
        if (sqlFile != null && sqlFile.exists()) {
            sqlFile.append(dbQueryRun)
            sqlFile.append(dbExecuteStatement)
        }
        JdbcConnection jDbcConnection = jdbcConnections[dbName]
        if (jDbcConnection != null) {
            jDbcConnection.execute(dbExecuteStatement)
            return true
        }
    }

    protected executeAndSkipException(dbName, String dbExecuteStatement, skipException) {
        log.info dbQueryRun
        log.info dbExecuteStatement
        if (sqlFile != null && sqlFile.exists()) {
            sqlFile.append(dbQueryRun)
            sqlFile.append(dbExecuteStatement)
        }
        JdbcConnection jDbcConnection = jdbcConnections[dbName]
        if (jDbcConnection != null) {
            try {
                return jDbcConnection.execute(dbExecuteStatement)
            } catch (java.sql.SQLSyntaxErrorException | java.sql.SQLException e) {
                if (e.toString().contains(skipException)) {
                    Reporter.log("Error skipped: " + e.toString())
                    throw new SkipException("Skip error: " + e.toString())
                }
                throw e
            }
        }
    }

    protected executeAndIgnoreException(dbName, String dbExecuteStatement, skipException) {
        log.info dbQueryRun
        log.info dbExecuteStatement
        if (sqlFile != null && sqlFile.exists()) {
            sqlFile.append(dbQueryRun)
            sqlFile.append(dbExecuteStatement)
        }
        JdbcConnection jDbcConnection = jdbcConnections[dbName]
        if (jDbcConnection != null) {
            try {
                return jDbcConnection.execute(dbExecuteStatement)
            } catch (java.sql.SQLSyntaxErrorException | java.sql.SQLException e) {
                if (e.toString().contains(skipException)) {
                    Reporter.log("Error skipped: " + e.toString())
                }else{
                    throw e
                }
            }
        }
    }

    public boolean isQueryOk() {
        return (queryConditionsTextList.size() || queryConditionsXmlList.size() || dbQuery != "")
    }

    public boolean disconnect() {
        jdbcConnections[dbName].disconnect()
    }

    public JdbcConnection getConnection(dbName) {
        return jdbcConnections[dbName]
    }

    public boolean setAutoCommitOn(dbName) {
        JdbcConnection jDbcConnection = getConnection(dbName)
        if (jDbcConnection != null) {
            //TODO Error handling
            ((groovy.sql.Sql) jDbcConnection.jDbcConnection).getConnection().setAutoCommit(true)
            return true
        }else{
            //TODO else
        }
    }

    public boolean setAutoCommitOff(dbName) {
        JdbcConnection jDbcConnection = getConnection(dbName)
        if (jDbcConnection != null) {
            //TODO Error handling
            ((groovy.sql.Sql) jDbcConnection.jDbcConnection).getConnection().setAutoCommit(false)
            return true
        }else{
             //TODO else
        }
    }
    public PreparedStatement setPreparedStatement(dbName, preparedStatement) {
        JdbcConnection jDbcConnection = getConnection(dbName)
        if (jDbcConnection != null) {
            //TODO Error handling
            return ((groovy.sql.Sql) jDbcConnection.jDbcConnection).getConnection().prepareStatement(preparedStatement)
        }else{
             //TODO else
        }
    }
}