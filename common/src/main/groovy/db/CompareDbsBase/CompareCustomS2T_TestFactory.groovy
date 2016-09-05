package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class CompareCustomS2T_TestFactory extends CompareS2T_TestFactoryBase {

    @Parameters(["inputFileColumn", "sourceDbColumn"])
    @Factory
    public Object[] createCustomInstances(String inputFileColumn,  @Optional ("") String sourceDbColumn) {
        return runCustom(inputFileColumn, sourceDbColumn)
    }
}
