package db.CompareDbsBase

import static org.apache.commons.lang3.StringUtils.isBlank

public class DbTargetCompareProperties {
    def fields = [:]
    public isComplete = false
    public skipReason = ""


    public DbTargetCompareProperties(){
        fields["row"] = ""
        fields["sourceValue"] = ""
        fields["targetDb"] = ""
        fields["targetSql"] = ""
        fields["threshold"] = 0
        fields["comments"] = ""
        fields["enabled"] = ""
    }
    public DbTargetCompareProperties(row, String sourceValue, String targetDb, String targetSql, threshold = 0, comments = "", by = ""){
        fields["row"] = row
        fields["sourceValue"] = sourceValue
        fields["targetSql"] = targetSql
        fields["targetDb"] = targetDb
        fields["by"] = by

        if(targetSql == "" || targetSql == "-") {
            fields["targetSql"] =  sourceValue
        }
        fields["threshold"] = threshold
        fields["comments"] = comments
        fields["by"] = by

        if(isBlank(fields["targetDb"])){
            skipReason += "targetDb is blank, "
        }
        if(isBlank(fields["sourceValue"])){
            skipReason += "sourceValue is blank, "
        }
        if(isBlank(fields["targetSql"])){
            skipReason += "targetSql is blank, "
        }
        if(isBlank(skipReason)){
            isComplete = true
        }
    }

    def getCommonFieldNames(){
        return ["row", "sourceValue" , "targetSql", "threshold", "comments", "enabled"]
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

    def getTargetDb() {
        fields["targetDb"]
    }

    def getSourceValue() {
        fields["sourceValue"]
    }

    def getTargetSql() {
        fields["targetSql"]
    }
    def getComments() {
        fields["comments"]
    }

    def getBy() {
        fields["by"]
    }

    

}
