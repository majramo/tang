package dtos

import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager

import static dtos.base.Constants.*
import static dtos.base.Constants.CompareType.*

class Assertion {
    final static Logger log = LogManager.getLogger("ASS  ")
    String textOrSqlType
    String textOrSqlTypePresentation
    String textOrSqlFieldShort
    String textValueOrSqlField
    String expectedValueOrg
    String textValueOrSqlFieldPresentation
    String sutElementXpath
    def regExp
    def xmlOut
    def compareType
    boolean assertResult = false
    String assertResultCompare

    String msg
    String msgFastWidth
    String msgTab
    String msgXmL
    def assertionResult = [:]
    int sizeOrder
    String sutValueOriginal
    String sutValuePresentation
    boolean checkTagOnly
    def xmlBuilder

    public Assertion(String textOrSqlType, String textValueOrSqlField, sutElementXpath, compareType, regExp, int sizeOrder, xmlBuilder, checkTagOnly = false) {
        this.textOrSqlType = textOrSqlType
        this.textOrSqlTypePresentation = textOrSqlType[0..0]
        this.textValueOrSqlField = textValueOrSqlField.toString()
        this.textOrSqlFieldShort = textValueOrSqlField
        this.regExp = regExp
        this.xmlBuilder = xmlBuilder
        this.checkTagOnly = checkTagOnly
        if (this.textValueOrSqlField.length() > 11) {
            textOrSqlFieldShort = this.textValueOrSqlField[0..4] + " " + this.textValueOrSqlField[textValueOrSqlField.length() - 5..this.textValueOrSqlField.length() - 1]
        }
        this.sutElementXpath = sutElementXpath
        this.compareType = compareType
        this.sizeOrder = sizeOrder + 1
    }


    public printMe() {
        log.info "$Assertion 	$textOrSqlType, $textValueOrSqlField, $sutElementXpath, $compareType"
    }

    public boolean assertCompare(sutValue, textValueOrSqlField, sqlHelperMock) {
        compare(sutValue, textValueOrSqlField, sqlHelperMock)
        return assertResult
    }

    public printAssertCompare() {
        log.debug "$Assertion $msgFastWidth"
    }


    private getFloat(value) {
        if (value != null) {
            def floatValue = value.toString().replaceAll(/[^0-9.,-]/, '').replaceAll(/,/, '.')
            if (floatValue != "") {
                return Float.parseFloat(floatValue)
            }
        }
    }

    private getInt(value) {
        if (value != null) {
            if(value.equals("")){
                 return 0
            }else{
                def intValue = value.toString().replaceAll(/[^0-9.,-]/, '').replaceAll(/,.*/, '').replaceAll(/\..*/, '')
                if (intValue != "") {
                    return Integer.parseInt(intValue)
                }
            }
        }
    }



    public compare(sutValue, expectedValue, sqlHelperMock) {
        assertResult = true
        expectedValueOrg = expectedValue
        sutValueOriginal = sutValue
        log.info("regExp<$regExp>")
        log.info("sutValue<$sutValue>")
        if (StringUtils.isNotEmpty(sutValue) && regExp != null) {
            sutValue = sutValue.replaceAll(regExp[0], regExp[1])
        }
        assertResultCompare = compareType
        assertResultCompare = COMPARE_RESULT_EQUAL
        switch (compareType) {
            case FORGIVING:
                //TRIM and UPPERACASE values will be compared
                sutValue = sutValue.toString().trim().toUpperCase()
                if (sqlHelperMock && textOrSqlType == TEXTSQL_TYPE_SQL) {
                    expectedValue = DB_MOCK_FIELD_VALUE_TEXT
                    expectedValueOrg = expectedValue
                } else {
                    expectedValue = expectedValue.toString().trim()
                }
                expectedValue = expectedValue.toUpperCase()
                break

            case TRIM:
                //TRIM values will be compared
                sutValue = sutValue.toString().trim()
                if (sqlHelperMock && textOrSqlType == TEXTSQL_TYPE_SQL) {
                    expectedValue = DB_MOCK_FIELD_VALUE_TEXT
                    expectedValueOrg = expectedValue
                } else {
                    expectedValue = expectedValue.toString().trim()
                }
                break

            case [
                    FLOAT,
                    FLOAT_COMPARISON_SKIP_IF_SUT_VALUE_NULL,
                    FLOAT_COMPARISON_SKIP_IF_EXPECTED_VALUE_NULL,
            ]:
                sutValue = getFloat(sutValue)
                if (sqlHelperMock && textOrSqlType == TEXTSQL_TYPE_SQL) {
                    expectedValue = DB_MOCK_FIELD_VALUE_FLOAT
                    expectedValueOrg = expectedValue
                } else {
                    expectedValue = getFloat(expectedValue)
                }
                break

            case INT:
                sutValue = getInt(sutValue)
                if (sqlHelperMock && textOrSqlType == TEXTSQL_TYPE_SQL) {
                    expectedValue = DB_MOCK_FIELD_VALUE_INT
                    expectedValueOrg = expectedValue
                } else {
                    expectedValue = getInt(expectedValue)
                }
                break

            case ~/^FLOAT_.*_RANGE_.*/:
                sutValue = getFloat(sutValue)
                if (sqlHelperMock && textOrSqlType == TEXTSQL_TYPE_SQL) {
                    expectedValue = DB_MOCK_FIELD_VALUE_FLOAT
                    expectedValueOrg = expectedValue
                } else {
                    expectedValue = getFloat(expectedValue)
                }
                break

            case ~/^INT_.*_RANGE_.*/:
                sutValue = getInt(sutValue)
                if (sqlHelperMock && textOrSqlType == TEXTSQL_TYPE_SQL) {
                    expectedValue = DB_MOCK_FIELD_VALUE_INT
                    expectedValueOrg = expectedValue
                } else {
                    expectedValue = getInt(expectedValue)
                }
                break

            default:
                if (sqlHelperMock && textOrSqlType == TEXTSQL_TYPE_SQL) {
                    if (expectedValue == COUNT) {
                        expectedValue = DB_MOCK_FIELD_VALUE_INT_COUNT
                    } else {
                        expectedValue = DB_MOCK_FIELD_VALUE_TEXT
                    }
                    expectedValueOrg = expectedValue
                }
                break
        }

        assertResultCompare = COMPARE_RESULT_EQUAL
        if (sutValue != expectedValue) {
            assertResult = false
            assertResultCompare = COMPARE_RESULT_NOT_EQUAL
        }
        switch (compareType) {
            case TAG_PRESENCE:
                if (sutValue != null) {
                    assertResult = true
                }
                break

        //TBD: xmlValue m�ste bli ett objekt s� vi vet om den har hittats och �r tom eller om den inte hittats och �r tomt
            case TAG_ABSENCE:
                if (sutValue == null) {
                    assertResult = true
                }
                break

            case TEXT_PRESENCE:
                if (sutValue.toString().contains(expectedValue.toString())) {
                    assertResult = true
                }
                break

            case TEXT_ABSENCE:
                if (!sutValue.toString().contains(expectedValue.toString())) {
                    assertResult = true
                }
                break

            case FLOAT_COMPARISON_SKIP_IF_SUT_VALUE_NULL:
                if (sutValue == null) {
                    assertResult = true
                }
                break

            case FLOAT_COMPARISON_SKIP_IF_EXPECTED_VALUE_NULL:
                if (expectedValue == null) {
                    assertResult = true
                }
                break

            case XML_CONTAINS:
                if (sutValue.toString().contains(expectedValue.toString())) {
                    assertResult = true
                    assertResultCompare = compareType
                }
                break

            case TEXTSQL_CONTAINS:
                if (expectedValue.toString().contains(sutValue.toString())) {
                    assertResult = true
                    assertResultCompare = compareType
                }
                break

            case EMPTY:
                if (sutValue.toString().isEmpty()) {
                    assertResult = true
                    assertResultCompare = compareType
                }
                break

            case NOT_EMPTY:
                if (!sutValue.toString().isEmpty()) {
                    assertResult = true
                    assertResultCompare = compareType
                }
                break

            case DIFF:
                if (!expectedValue.toString().equals(sutValue.toString())) {
                    assertResult = true
                    assertResultCompare = compareType
                }
                break

            case [UNDEFINED, MANUAL_CHECK]:
                assertResultCompare = compareType
                break


            case ~/^.*_RANGE.*/:
                assertResultCompare = compareType
                assertResult = false
                def tempSutValue = getFloat(sutValue)
                def range
                def upperRange = null
                def lowerRange = null
                if (sutValue != null && expectedValue != null) {
                    switch (compareType) {
                        case ~/^FLOAT_PERCENT_RANGE.*/:
                            range = getFloat(compareType.replaceAll(FLOAT_PERCENT_RANGE, ""))
                            upperRange = getFloat(getFloat(expectedValue) * (100 + range) / 100)
                            lowerRange = getFloat(getFloat(expectedValue) * (100 - range) / 100)
                            break

                        case ~/^FLOAT_VALUE_RANGE.*/:
                            tempSutValue = getFloat(sutValue)
                            range = getFloat(compareType.replaceAll(FLOAT_VALUE_RANGE, ""))
                            upperRange = getFloat(getFloat(expectedValue)) + range
                            lowerRange = getFloat(getFloat(expectedValue)) - range
                            break

                        case ~/^INT_PERCENT_RANGE.*/:
                            tempSutValue = getInt(sutValue)
                            range = getFloat(compareType.replaceAll(INT_PERCENT_RANGE, ""))
                            upperRange = getInt(getInt(expectedValue) * (100 + range) / 100)
                            lowerRange = getInt(getInt(expectedValue) * (100 - range) / 100)
                            break

                        case ~/^INT_VALUE_RANGE.*/:
                            tempSutValue = getInt(sutValue)
                            range = getInt(compareType.replaceAll(INT_VALUE_RANGE, ""))
                            upperRange = getInt(getInt(expectedValue)) + range
                            lowerRange = getInt(getInt(expectedValue)) - range
                            break
                    }
                    //sutValueOriginal = "$sutValueOriginal($lowerRange|$upperRange)"
                    expectedValue = "[$lowerRange..$upperRange]"

                    if (tempSutValue >= lowerRange && tempSutValue <= upperRange) {
                        assertResult = true
                        assertResultCompare = compareType
                    }
                }
                break

            default:
                if (sutValue != null && (sutValue == expectedValue)) {
                    assertResult = true
                    assertResultCompare = COMPARE_RESULT_EQUAL
                }
                break
        }


        sutValuePresentation = "$sutValue"
        if (sutValue != sutValueOriginal) {
            sutValuePresentation = "$sutValuePresentation($sutValueOriginal)"
        }
        textValueOrSqlFieldPresentation = expectedValue
        if (expectedValue != expectedValueOrg) {
            textValueOrSqlFieldPresentation = "$textValueOrSqlFieldPresentation($expectedValueOrg)"
        }
        if (!assertResult) {
            textValueOrSqlFieldPresentation = "]$textValueOrSqlFieldPresentation["
            sutValuePresentation = "]$sutValuePresentation["
        }
        if (compareType in [
                TAG_PRESENCE,
                TAG_ABSENCE
        ]) {
            textValueOrSqlFieldPresentation = "]$compareType["
            sutValuePresentation = "]$sutValuePresentation["
        }
        if (compareType in [
                TEXT_PRESENCE,
                TEXT_ABSENCE
        ]) {
            textValueOrSqlFieldPresentation = "]$compareType($expectedValue)["
            sutValuePresentation = "]$sutValuePresentation["
        }

        msg = "$assertResult $TABB <$sutValuePresentation> $compareType <$textValueOrSqlFieldPresentation> $TABB (Xml)$sutElementXpath $assertResultCompare (Value)$expectedValue"
        msgFastWidth = sprintf('%10$-2s %11$2s %9$-11s> %1$-6s %2$-30s %3$-11s %4$-30s (XML)-> %5$-55s %6$-8s %7$-30s %8$-8s',
                [
                        assertResult,
                        "<$sutValuePresentation>",
                        "($compareType)",
                        "<$textValueOrSqlFieldPresentation>",
                        "<$sutElementXpath>",
                        "($assertResultCompare)",
                        expectedValue,
                        "($textOrSqlType)",
                        textOrSqlFieldShort,
                        textOrSqlType[0..0],
                        sizeOrder
                ])
        msgTab = [
                assertResult,
                "<$sutValuePresentation>",
                "($compareType)",
                "<$textValueOrSqlFieldPresentation>",
                sutElementXpath,
                "($assertResultCompare)",
                expectedValue,
                "($textOrSqlType)"
        ].join(TABB)

        log.debug("<$sutValuePresentation>")
        msgXmL = """
			<assertion>
				<assertResult>$assertResult</assertResult>
				<xml>$sutElementXpath</xml>
				<text_sql>$expectedValue</text_sql>
				<xmlValue>$sutValuePresentation</xmlValue>
				<textValueOrSqlField>$textValueOrSqlFieldPresentation</textValueOrSqlField>
				<compareType>$compareType</compareType>
				<assertResultCompare>$assertResultCompare</assertResultCompare>
				<textOrSqlType>$textOrSqlType</textOrSqlType>
				<sizeOrder>$sizeOrder</sizeOrder>
				<regExp>$regExp</regExp>
				
			</assertion>"""

        assertionResult = [assertResult, msgFastWidth]
        xmlOut = {
            delegate.assertResult(assertResult)
            delegate.xml(sutElementXpath)
            delegate.text_sql(textValueOrSqlField)
            delegate.xmlValue(sutValuePresentation)
            delegate.text_sqlValue(textValueOrSqlFieldPresentation)
            delegate.compareType(compareType)
            delegate.assertResultCompare(assertResultCompare)
            delegate.text_sqlType("$textOrSqlType")
            delegate.sizeOrder(sizeOrder)
            delegate.regExp(regExp)
        }

    }

}
