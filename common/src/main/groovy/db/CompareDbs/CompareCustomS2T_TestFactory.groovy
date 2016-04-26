package db.CompareDbs

import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class CompareCustomS2T_TestFactory extends CompareS2T_TestFactoryBase {

    @Parameters(["inputFile", "onlyEnabled"])
    @Factory
    public Object[] createCustomInstances(String inputFile, boolean onlyEnabled) {
        return runCustom(inputFile, onlyEnabled)
    }
}
