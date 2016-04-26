package db.CompareDbsBase

import static org.apache.commons.lang3.StringUtils.isBlank

public class DbCompareProperties {
    def fields = [:]
    public isComplete = false
    public skipReason = ""


    public DbCompareProperties(){
        fields["row"] = ""
        fields["sourceDb"] = ""
        fields["sourceSql"] = ""
        fields["targetDb"] = ""
        fields["targetSql"] = ""
        fields["threshold"] = 0
        fields["comments"] = ""
        fields["enabled"] = ""
    }
    public DbCompareProperties(row, String sourceDb, String sourceSql, String targetDb, String targetSql, threshold = 0, comments = "", enabled = ""){
        fields["row"] = row
        fields["sourceDb"] = sourceDb
        fields["sourceSql"] = sourceSql
        fields["targetSql"] = targetSql
        fields["targetDb"] = targetDb

        if(targetSql == "" || targetSql == "-") {
            fields["targetSql"] =  sourceSql
        }
        fields["threshold"] = threshold
        fields["comments"] = comments
        fields["enabled"] = enabled
        if(isBlank(fields["sourceDb"])){
            skipReason += "sourceDb is blank, "
        }
        if(isBlank(fields["targetDb"])){
            skipReason += "targetDb is blank, "
        }
        if(isBlank(fields["sourceSql"])){
            skipReason += "sourceSql is blank, "
        }
        if(isBlank(fields["targetSql"])){
            skipReason += "targetSql is blank, "
        }
        if(isBlank(skipReason)){
            isComplete = true
        }
    }

    def getCommonFieldNames(){
        return ["row", "sourceSql" , "targetSql", "threshold", "comments", "enabled"]
    }

    def getCustomFieldNames(){
        return fields.keySet()
    }

    def String toString(){
        def str = ""
        fields.each {k, v->
            str += "$k:  <$v> \n"
        }
        return str
    }

    def getRow() {
        fields["row"]
    }

    def getSourceDb() {
        fields["sourceDb"]
    }

    def getTargetDb() {
        fields["targetDb"]
    }

    def getSourceSql() {
        fields["sourceSql"]
    }

    def getTargetSql() {
        fields["targetSql"]
    }
    def getComments() {
        fields["comments"]
    }

}
