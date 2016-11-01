package db.CompareDbsBase

import org.testng.annotations.Factory
import org.testng.annotations.Optional
import org.testng.annotations.Parameters

class CompareCommonS2T_TestFactory extends CompareS2T_TestFactoryBase{

    @Parameters(["inputFileColumn", "sourceDbColumn", "targetDbColumn", "tableFieldsFileColumn", "lastSourceColumn"] )
    @Factory
    public Object[] createCommonInstances(String inputFileColumn, String sourceDbColumn, String targetDbColumn, @Optional ("") String tableFieldsFileColumn, @Optional ("") String lastSourceColumn) {

        return runCommon(inputFileColumn, sourceDbColumn, targetDbColumn, tableFieldsFileColumn, lastSourceColumn)
    }
}
