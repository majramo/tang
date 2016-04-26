package db.CompareDbs

import org.testng.annotations.Factory
import org.testng.annotations.Parameters

class CompareCommonS2T_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFile", "dbSource", "dbTarget", "onlyEnabled"] )
    @Factory
    public Object[] createCommonInstances(String inputFile, String dbSource, String dbTarget, boolean onlyEnabled) {

        return runCommon(inputFile, dbSource, dbTarget, onlyEnabled)
    }
}
