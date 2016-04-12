package dtos.base


public class Constants {

    public static final String TEST_STATUS = "TEST_STATUS";
    public static final String ICONS = "ICONS";
    public static final String BROWSER = "DEFAULT_BROWSER";
    public static final String APPLICATION = "APPLICATION";
    public static final String BROWSER_ICON = "BROWSER_ICON";
    public static final String ENVIRONMENT = "ENVIRONMENT";

    public enum CompareType {
        EQUAL, EMPTY, NOT_EMPTY, DIFF, LIKE, FORGIVING, TRIM,
        TAG_PRESENCE, TAG_ABSENCE, TEXT_PRESENCE, TEXT_ABSENCE,
        INT,
        FLOAT, FLOAT_COMPARISON_SKIP_IF_SUT_VALUE_NULL, FLOAT_COMPARISON_SKIP_IF_EXPECTED_VALUE_NULL,
        XML_CONTAINS,
        TEXTSQL_CONTAINS
    }
    public static final String HTML_LT = "&lt;"
    public static final String HTML_GT = "&gt;"
    public static final String dbRunTypeRows = "dbRunTypeRows"
    public static final String dbRunTypeFirstRow = "dbRunTypeFirstRow"
    public static final String dbRunTypeRowsSelects = "dbRunTypeRowsSelects"
    public static final String dbRunTypeCount = "dbRunTypeCount"


    public static final String COMPARE_RESULT_EQUAL = " = "
    public static final String COMPARE_RESULT_NOT_EQUAL = " != "
    public static final Integer PRIORITY_1 = 1
    public static final Integer PRIORITY_2 = 2
    public static final Integer PRIORITY_3 = 3
    public static final Integer PRIORITY_x = 100

    public static final String CLASS = "CLASS"
    public static final String STYLE = "STYLE"
    public static final String COUNT = "COUNT"
    public static final String CKECK_SUM = "CKECK_SUM"

    public static final String DATA_GROUPID = "DATA-GROUPID"
    public static final String FOR = "FOR"
    public static final String HREF = "href"
    public static final String SRC = "SRC"
    public static final String ONCLICK = "onclick"
    public static final String TEXTSQL_TYPE_SQL = "SQL"
    public static final String TEXTSQL_TYPE_TEXT = "TEXT"
    public static final String UNDEFINED = "UNDEFINED"
    public static final String U = UNDEFINED
    public static final String UND = UNDEFINED
    public static final String UNDEF = UNDEFINED
    public static final String MANUAL_CHECK = "MANUAL_CHECK"
    public static final String M = MANUAL_CHECK
    public static final String MAN = MANUAL_CHECK
    public static final String MANUAL = MANUAL_CHECK
    public static final String REGRESSION_TEST = "REGRESSION_TEST"
    public static final String DB_DEFAULT = "defaultDatabase"
    public static final String DB_DB2Server_1 = "Db2tFtDb_1"
    public static final String DB_DB2Server_2 = "Db2tFtDb_2"
    public static final String DB_LOCAL_1 = "localDb_1"
    public static final String DB_LOCAL_2 = "localDb_2"
    public static final String DB_MYSQL = "mySqlDb"
    public static final String DB_MOCK_DATA = "mockData"
    public static final String DB_MOCK = "mockDb"
    public static final Integer DB_MOCK_FIELD_VALUE_INT = Integer.MIN_VALUE
    public static final Float DB_MOCK_FIELD_VALUE_FLOAT = Float.MIN_VALUE
    public static final String DB_MOCK_FIELD_VALUE_TEXT = "Sql_MockData_Value_"
    public static final Integer DB_MOCK_FIELD_VALUE_INT_COUNT = 2
    public static HTML = "html"
    public static XML = "xml"

    public static final String ASSERT = "Assert"
    public static final String TAG_ONLY = true
    public static final TABB = "\t"
    public static final CR = "<BR>"
    public static final FLOAT_COMPARISON_SKIP_IF_SUT_VALUE_NULL = "FLOAT_COMPARISON_SKIP_IF_SUT_VALUE_NULL"
    public static final FLOAT_COMPARISON_SKIP_IF_EXPECTED_VALUE_NULL = "FLOAT_COMPARISON_SKIP_IF_EXPECTED_VALUE_NULL"
    public static final INT_PERCENT_RANGE = "INT_PERCENT_RANGE_"
    public static final INT_VALUE_RANGE = "INT_VALUE_RANGE_"
    public static final FLOAT_PERCENT_RANGE = "FLOAT_PERCENT_RANGE_"
    public static final FLOAT_VALUE_RANGE = "FLOAT_VALUE_RANGE_"
    public static final CLASS_NAME_START = "Class_"
    public static final Assertion = "Assertion"
    public static final ASSERT8 = "Assert"
    public static final TYPE = "TYPE"
    public static final FUNCTION = "FUNCTION"
    public static final String MAX = "MAX"

    public static final FLOAT_MAX = [TYPE: CompareType.FLOAT, FUNCTION: MAX]
    public static final DESCRIPTION = "DESCRIPTION"
    public static final String SQL_HELPER = "SQL_HELPER"
    public static final String SOURCE_SQL_HELPER = "SOURCE_SQL_HELPER"
    public static final String TARGET_SQL_HELPER = "TARGET_SQL_HELPER"
    public static final String DATABASE = "DATABASE"
    public static final String DATABASE_VENDOR_1 = "DATABASE_VENDOR_1"
    public static final String DATABASE_VENDOR_2 = "DATABASE_VENDOR_2"

    public static final String EXCEL_BODY_ROW_NUMBER = "EXCEL_BODY_ROW_NUMBER"

    public static final int MAX_DB_RESULTS_TO_PRINT = 50

}

