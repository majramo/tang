package dtos.base

import org.apache.log4j.Logger

import static dtos.base.Constants.*

public class SqlHelper {

	protected dbRun
	protected dbName = ""
	protected log
	def queryConditionsTextList = [:]
	def queryConditionsXmlList = [:]
	protected boolean sqlHelperMock = false
	def String dbQuery =""
	def dbQueryType =""
	def dbQueryExtension = ""
	def dbResult = [:]
	String dbQueryRun
	private  jdbcConnections = [:]
	private static dbRecordLine
	private File sqlFile
    private final static Logger logger = Logger.getLogger("SqH  ")

	public SqlHelper(File sqlFile, log, dbName, dbRun, settings){
		this.dbRun = dbRun
		this.dbName = dbName
        this.sqlFile = sqlFile
        if(log == null){
            this.log = logger
        }else{
            this.log = log
        }
        if(dbRun == false){
            if(!jdbcConnections [dbName]){
			    jdbcConnections [dbName] = new JdbcConnection()
            }
            sqlHelperMock = true

        }else{
            this.log.info jdbcConnections
            this.log.info jdbcConnections [dbName]
            try{
                JdbcConnection jDbcConnection =null
                if(!jdbcConnections [dbName]){
                    settings."$dbName".with {
                        jDbcConnection  = new JdbcConnection(dbUrl, dbDriverName, dbUserName, dbPassword, dbTestDataBase, dbDriver)
                    }
                    jdbcConnections [dbName] = jDbcConnection
                }

            }catch (RuntimeException e){
                this.log.error ("Can't get connection")
                throw e
            }
        }
	}

    public boolean isConnectionOk(dbName){
        JdbcConnection jDbcConnection = jdbcConnections [dbName]
        return jDbcConnection.isConnectionOk()
    }

	private sqlConRun(dbLoggInfo, dbRunType ,dbQueryRun, dbRecordLine=-1, dbName){
        log.info "dbName $dbName"
        log.debug "$dbLoggInfo\n$dbQueryRun"
		this.dbRecordLine = dbRecordLine
        JdbcConnection jDbcConnection = jdbcConnections [dbName]
		if(jDbcConnection != null){
			if(sqlHelperMock){
				dbResult[DB_MOCK_DATA] =  DB_MOCK_DATA
                return null
			}else{
				switch (dbRunType){
					case dbRunTypeFirstRow:
                        dbResult = jDbcConnection.firstRow (dbQueryRun)
                        break

					case dbRunTypeRows:
						dbResult = jDbcConnection.rows (dbQueryRun)
						break
						
					case dbRunTypeRowsSelects:
						dbResult = jDbcConnection.rows (dbQueryRun)
						if(dbRecordLine>0){
							dbResult = dbResult[dbRecordLine-1]
						}else{
							dbResult = dbResult[0]
						}
						break
				}
			}
		}
		return dbResult
	}
	
	
	public String getQueryConditions(){

		def queryConditions = ""
		
		queryConditionsTextList.each {k, v->
			def val
			if (v.isString){
				val  = "'$v.value'"
			}else{
				val = v.value
			}
			def qCondition =  v.field + " " + val
			if(queryConditions){
				queryConditions += "\n AND " + qCondition
			}else{
				queryConditions = qCondition
			}
		}
		queryConditionsXmlList.each {k, v->
			def val
			if (v.isString){
				val  = "'$v.value'"
			}else{
				val = v.value
			}
			def qCondition =  v.field + " " + val
			if(queryConditions){
				queryConditions += "\n AND " + qCondition
			}else{
				queryConditions = qCondition
			}
		}
		
		if(dbQueryExtension != ""){
			if(queryConditions != ""){
				queryConditions += " $dbQueryExtension"
			}
		}
		return queryConditions
	}


    public String getInsertValues(){

        def queryConditions = ""
        def qValues = ""

        queryConditionsTextList.each {k, v->
            def val
            if (v.isString){
                val  = "'$v.value'"
            }else{
                val = v.value
            }
            def qCondition =  v.field + " " + val
            if(queryConditions){
                queryConditions += " , " + qCondition
            }else{
                queryConditions = qCondition
            }
        }
        queryConditionsXmlList.each {k, v->
            def val
            if (v.isString){
                val  = "'$v.value'"
            }else{
                val = v.value
            }
            def qCondition =  v.field.replaceAll("=", "")
            def qValue =   val
            if(queryConditions){
                queryConditions += " , " + qCondition
                qValues += " , " + qValue
            }else{
                queryConditions = qCondition
                qValues = qValue
            }
        }

        if(dbQueryExtension != ""){
            if(queryConditions != ""){
                queryConditions += " $dbQueryExtension"
            }
        }
        return "( $queryConditions ) VALUES ( $qValues );"
    }


	protected getDb_result(dbName){
		def queryConditions = getQueryConditions()
		dbQueryRun = "$dbQuery\n $queryConditions\n "
		dbResult = [:]
        String dbInsertStatement = dbQuery.replaceAll("SELECT \\* FROM" , "INSERT INTO").replaceAll(" WHERE", "")
        dbInsertStatement += getInsertValues() + "\n"
        log.debug dbQueryRun
        log.debug dbInsertStatement
        if(sqlFile != null && sqlFile.exists()){
            sqlFile.append(dbQueryRun)
            sqlFile.append(dbInsertStatement)
        }
 		if(dbQueryType ==  dbRunTypeCount || dbQueryType ==  dbRunTypeFirstRow ) {
			dbResult =  sqlConRun("dbQueryRun", dbRunTypeFirstRow ,dbQueryRun, dbName)
	    }else{if(dbQueryType ==  dbRunTypeRowsSelects){
			   dbResult = sqlConRun("dbQueryRun",dbRunTypeRowsSelects ,dbQueryRun, dbRecordLine, dbName)
		   }else{
               dbResult = sqlConRun("dbQueryRun",dbRunTypeRows ,dbQueryRun, dbName)
		   }
	   }
	    return dbResult
	}

    protected execute(dbName, dbExecuteStatement){
        log.info dbQueryRun
        log.info dbExecuteStatement
        if(sqlFile != null && sqlFile.exists()){
            sqlFile.append(dbQueryRun)
            sqlFile.append(dbExecuteStatement)
        }
        JdbcConnection jDbcConnection = jdbcConnections [dbName]
        if(jDbcConnection != null){
            return jDbcConnection.execute (dbExecuteStatement)
        }
    }
	public boolean isQueryOk(){
		return (queryConditionsTextList.size() || queryConditionsXmlList.size() || dbQuery != "")
	}
	 
}
