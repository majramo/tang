package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class CompareCustomS2T_TestFactory extends CompareS2T_TestFactoryBase {

    @Parameters(["inputFile", "enabledColumn", "byColumn", "sourceDbColumn"])
    @Factory
    public Object[] createCustomInstances(String inputFile, @Optional ("") boolean enabledColumn, @Optional ("") String byColumn, @Optional ("") String sourceDbColumn) {
        return runCustom(inputFile, enabledColumn, byColumn, sourceDbColumn)
    }
}
