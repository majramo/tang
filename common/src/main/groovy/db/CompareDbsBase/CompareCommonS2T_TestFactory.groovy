package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class CompareCommonS2T_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFile", "sourceDb", "targetDb", "enabledColumn", "byColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFile, String sourceDb, String targetDb, boolean enabledColumn, String byColumn) {

        return runCommon(inputFile, sourceDb, targetDb, enabledColumn, byColumn)
    }
}
