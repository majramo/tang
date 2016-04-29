package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class CompareCustomS2T_TestFactory extends CompareS2T_TestFactoryBase {

    @Parameters(["inputFile", "onlyEnabled", "dbSource"])
    @Factory
    public Object[] createCustomInstances(String inputFile, boolean onlyEnabled, @Optional ("") String dbSource) {
        return runCustom(inputFile, onlyEnabled, dbSource)
    }
}
