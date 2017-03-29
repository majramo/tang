package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class CompareTargetToSourceValue_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFileColumn", "enabledColumn", "byColumn",  "targetDbColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFileColumn, @Optional ("") String enabledColumn, @Optional ("") String byColumn, @Optional ("") String targetDbColumn) {
        return runTargetToSourceValue(inputFileColumn, targetDbColumn, enabledColumn, byColumn)
    }
}
