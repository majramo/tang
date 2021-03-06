package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class  CompareCommonS2T_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFileColumn", "systemColumn", "considerSystemTableColumnAnalyse", "actionTypeColumn", "enabledColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFileColumn, String systemColumn, @Optional ("") String considerSystemTableColumnAnalyse, @Optional ("") String actionTypeColumn, @Optional ("") String enabledColumn) {

        return runCommon(inputFileColumn, systemColumn, considerSystemTableColumnAnalyse, actionTypeColumn, enabledColumn)
    }
}
