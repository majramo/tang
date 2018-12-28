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
    private boolean onlyAaZzCharColumn;
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
        this.onlyAaZzCharColumn = onlyAaZzCharColumnn

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
        reporterLogLn("###");
        reporterLogLn("execute:    <$execute>");

        def targetSql = "UPDATE $table \nSET $column = "

        def settingsEmailDomain  = getSettingsValueIfExistsElseDefault("emailDomain")
        def ddText  = getSettingsValueIfExistsElseDefault("DD_TEXT")
        def ddPhoneNumber  = getSettingsValueIfExistsElseDefault("DD_PHONE_NUMBER")
        def ddPassword  = getSettingsValueIfExistsElseDefault("DD_PASSWORD")
        def ddUrl  = getSettingsValueIfExistsElseDefault("DD_URL")
        def ddEmailAddress  = getSettingsValueIfExistsElseDefault("DD_EMAIL_ADDRESS")
        switch (maskingColumn) {
            case ~/$ddText/:
                targetSql += " 'Text ' || substr( (rownum + 12345678)  ,1,8) "
                break
            case ~/$ddPhoneNumber/:
                targetSql += " '010' || substr( (rownum + 12345678)  ,1,8) "
                break
            case ~/$ddPassword/:
                targetSql += '1234'
                break
            case ~/$ddUrl/:
                targetSql += " 'test.' || substr( (rownum + 12345678)  ,1,8) || '.@settingsEmailDomain' "
                break
            case ~/$ddEmailAddress/:
                if(!onlyAaZzCharColumn){
                    targetSql += " 'test.' || substr( (rownum + 12345678)  ,1,8) || '@$settingsEmailDomain' "
                }else{
                    targetSql += " replace(replace(replace(replace(replace(replace($column, 'å', 'a'), 'ö', 'o'), 'ä', 'a') , 'Ä', 'A') , 'Å', 'A') , 'Ö', 'O')  "
                }
                break
        }

        targetSql += "\nwhere $column IS NOT NULL"
        if(!searchExtraCondition.isEmpty() && searchExtraCondition != "-"){
            if(maskingColumn.equals(ddEmailAddress) && onlyAaZzCharColumn){
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
