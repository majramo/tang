package dtos;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dtos.base.Constants.CompareType.*;
import static dtos.base.Constants.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AssertionTest {

    private Object xmlBuilder;
    List<String> regexpReplaceSpace = new ArrayList<String>(Arrays.asList(" ", ""));
    List<String> regexpReplaceXYZ = new ArrayList<String>(Arrays.asList("XYZ", "ABC"));
    List<String> regexpReplaceNothing = new ArrayList<String>(Arrays.asList("", ""));


    @Test
    public void testCompareValuesAreEqualWhenEqualType() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", EQUAL, regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare("Text", "Text", ""));
    }

    @Test
    public void testCompareValuesAreNotEqualWhenEqualTyoe() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", EQUAL, regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare("Text", "Text ", ""));
    }

    @Test
    public void testCompareValueAreEqualWhenTrimType() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FORGIVING, regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare("    text    ", "text", ""));
    }


    @Test
    public void testCompareValueAreEqualWhenForgivingType() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FORGIVING, regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare(" TeXt     ", "text  ", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenIntType() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", INT, regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare(" 100     ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreNotEqualWhenIntType() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", INT, regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(" 100     ", " 101  ", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenIntRangeType_1() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", INT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare(" 99 ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenIntRangeType_2() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", INT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare(" 101 ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreNotEqualWhenIntRangeType_1() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", INT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(" 98 ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreNotEqualWhenIntRangeType_2() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", INT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(" 102 ", " 100  ", ""));
    }


    @Test
    public void testCompareValuesAreEqualWhenFloatType() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FLOAT, regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare(" 100.001     ", " 100 . 001  ", ""));
    }

    @Test
    public void testCompareValuesAreNotEqualWhenFloatType() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FLOAT, regexpReplaceSpace, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(" 100 . 000     ", " 100 . 001  ", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenFloatRangeType_1() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FLOAT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare(" 99 ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenFloatRangeType_2() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FLOAT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare(" 101 ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreNotEqualWhenFloatRangeType_1() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FLOAT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(" 98 ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreNotEqualWhenFloatRangeType_2() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", FLOAT_VALUE_RANGE + "1", regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(" 102 ", " 100  ", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenEqualTypeAndRegExp_1() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", EQUAL, regexpReplaceXYZ, 0, xmlBuilder);
        assertTrue(assertion.assertCompare("XYZ Text XYZ", "ABC Text ABC", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenEqualTypeAndRegExp_2() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", EQUAL, regexpReplaceXYZ, 0, xmlBuilder);
        assertFalse(assertion.assertCompare("Text XYZ", "Text ABc", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenTagPresence() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", TAG_PRESENCE, regexpReplaceNothing, 0, xmlBuilder);
        assertTrue(assertion.assertCompare("some value", "", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenNotTagPresence() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", TAG_PRESENCE, regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(null, "", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenTagAbsence() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", TEXT_ABSENCE, regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare(null, "", ""));
    }

    @Test
    public void testCompareValuesAreEqualWhenNotTagAbsence() throws Exception {
        Assertion assertion = new Assertion(TEXTSQL_TYPE_TEXT, "", "", TEXT_ABSENCE, regexpReplaceNothing, 0, xmlBuilder);
        assertFalse(assertion.assertCompare("some value", "", ""));
    }

}
