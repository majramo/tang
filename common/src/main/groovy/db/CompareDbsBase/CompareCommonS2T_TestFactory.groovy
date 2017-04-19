package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class  CompareCommonS2T_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFileColumn", "systemColumn", "tableFieldsFileColumn", "actionTypeColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFileColumn, String systemColumn, @Optional ("") String tableFieldsFileColumn, @Optional ("") String actionTypeColumn) {

        return runCommon(inputFileColumn, systemColumn, tableFieldsFileColumn, actionTypeColumn)
    }
}
