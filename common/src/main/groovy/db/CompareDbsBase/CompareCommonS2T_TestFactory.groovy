package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class CompareCommonS2T_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFile", "sourceDbColumn", "targetDbColumn", "enabledColumn", "byColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFile, String sourceDbColumn, String targetDbColumn, @Optional ("")boolean enabledColumn, @Optional ("") String byColumn) {

        return runCommon(inputFile, sourceDbColumn, targetDbColumn, enabledColumn, byColumn)
    }
}
