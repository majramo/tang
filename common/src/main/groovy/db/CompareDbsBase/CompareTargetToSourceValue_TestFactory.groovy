package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class CompareTargetToSourceValue_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFileColumn",  "targetDbColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFileColumn, String targetDbColumn) {

        return runTargetToSourceValue(inputFileColumn, targetDbColumn)
    }
}
