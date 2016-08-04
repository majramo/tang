package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class CompareTargetToSourceValue_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFile", "enabledColumn", "byColumn",  "targetDbColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFile, boolean enabledColumn, String byColumn, String targetDbColumn) {

        return runTargetToSourceValue(inputFile, targetDbColumn, enabledColumn, byColumn)
    }
}
