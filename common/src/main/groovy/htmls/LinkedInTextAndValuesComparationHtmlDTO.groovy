package htmls

import dtos.HtmlXmlBaseDto

import static dtos.base.Constants.*
import static dtos.base.Constants.CompareType.*

public class LinkedInTextAndValuesComparationHtmlDTO extends HtmlXmlBaseDto {

    protected LinkedInTextAndValuesComparationHtmlDTO(Map context, String htmlXmlSource, priority) {
        moduleName = this.class.name
        actionName = ASSERT
        htmlXmlSourceType = HTML
        HtmlXmlBaseDto child
        anchor = '//*[@id="content"]'

        super.init(context, htmlXmlSource, priority)

        child = createChild("Text comparation", PRIORITY_1, REGRESSION_TEST, "", "//*[@id='overview']")
        if (child != null) {
            child.with {
                assertTextValue("Connections", "/dt", EQUAL)
                assertTextValue("Connections", "/dt", TRIM)
                assertTextValue("connections", "/dt")
                assertTextValue("CONNections", "/dt")
                assertTextValue("Connections", "/dt", XML_CONTAINS)
                assertTextValue("Connections", "/dt", TEXTSQL_CONTAINS)
                assertTextValue("Connections", "/dt", TEXTSQL_CONTAINS, /\s*/)
                assertTextAbsence("Connections", "/dt/dd")
                assertTextPresence("Connections", "/dt")
                assertTagPresence("320", "/dd/p/strong")
                assertTagAbsence("320", "/dt/dd/p/strong")
            }
        }

        child = createChild("Value comparation", PRIORITY_1, REGRESSION_TEST, "", "//*[@id='overview']")
        if (child != null) {
            child.with {
                assertTextValue("327", "/dd/p/strong", INT)
                assertTextValue("327", "/dd/p/strong", FLOAT)
                assertTextValue("300", "/dd/p/strong", integerPercentRange(10))
                assertTextValue("300", "/dd/p/strong", integerValueRange(30))
                assertTextValue("300", "/dd/p/strong", floatPercentRange(10))
                assertTextValue("300", "/dd/p/strong", floatValueRange(27))
            }
        }
    }
}








