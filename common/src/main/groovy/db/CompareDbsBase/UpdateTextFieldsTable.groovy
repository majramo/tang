package db.CompareDbsBase

import base.AnySqlCompareTest
import org.testng.ITestContext
import org.testng.annotations.Test
/*
 this class uses split of incoming query by ";" and will execute each sub query
 */

public class UpdateTextFieldsTable extends AnySqlCompareTest{
    private static int row = 0
    private String targetDb;
    private String action;
    private String table;
    private String column;
    private String searchExtraCondition;
    private String maskingColumn;
    private boolean execute;
    private boolean onlyAaZzCharColumnn;
    private static String settingsEmailDomain = "test.addtest.se"

    public UpdateTextFieldsTable(targetDb, system, table, action, column, searchExtraCondition, String maskingColumn, boolean execute = false, boolean onlyAaZzCharColumnn = false) {
        super.setup()
        this.targetDb = targetDb
        this.action = action
        this.table = table
        this.column = column
        this.maskingColumn = maskingColumn
        this.searchExtraCondition = searchExtraCondition
        this.execute = execute
        this.onlyAaZzCharColumnn = onlyAaZzCharColumnn

        String dbTargetOwner = settings."$targetDb".owner
    }

    @Test
    public void updateTargetTest(ITestContext testContext){
        super.setTargetSqlHelper(testContext, targetDb)
        reporterLogLn(reporterHelper.addIcons(getDbType(), getDbType(targetDb)))

        row++
        reporterLogLn("Row: <$row> UPDATE TABLE ");
        reporterLogLn("Target Db: <$targetDb> ");
        reporterLogLn("table: <$table   $table   $table   $table> ");
        reporterLogLn("Action:    <$action> ");
        reporterLogLn("Masking:   <$maskingColumn   $maskingColumn   $maskingColumn   $maskingColumn>");
        reporterLogLn("Column:    <$column>");

        def targetSql = "UPDATE $table \nSET $column = "
        if(settings["emailDomain"].size() != 0 && settings["emailDomain"] != ""){
            settingsEmailDomain = settings.emailDomain
        }
        switch (maskingColumn) {
            case ~/AF_Fritext/:
                targetSql += " 'Text ' || substr( (rownum + 12345678)  ,1,8) "
                break
            case ~/AF_Telefonnummer/:
                targetSql += " '010' || substr( (rownum + 12345678)  ,1,8) "
                break
            case ~/AF_Losenord/:
                targetSql += '1234'
                break
            case ~/AF_Url/:
                targetSql += " 'test.' || substr( (rownum + 12345678)  ,1,8) || '.@settingsEmailDomain' "
                break
            case ~/AF_Epost/:
                if(!onlyAaZzCharColumnn){
                    targetSql += " 'test.' || substr( (rownum + 12345678)  ,1,8) || '@$settingsEmailDomain' "
                }else{
                    targetSql += " replace(replace(replace(replace(replace(replace($column, 'å', 'a'), 'ö', 'o'), 'ä', 'a') , 'Ä', 'A') , 'Å', 'A') , 'Ö', 'O')  "
                }
                break
        }

        targetSql += "\nwhere $column IS NOT NULL"
        if(!searchExtraCondition.isEmpty() && searchExtraCondition != "-"){
            if(maskingColumn.equals("AF_Epost") && onlyAaZzCharColumnn){
                targetSql += "\n--Ignore for Epost $searchExtraCondition"
            }else {
                targetSql += "\nAND $searchExtraCondition"
            }
        }
        targetSql += "-- Execute is <$execute>"
        reporterLogLn("")
        reporterLogLn("Target query: \n$targetSql")
        if(execute) {
            execute(targetDbSqlDriver, targetSql)
        }
    }

}
