package htmls

import dtos.HtmlXmlBaseDto

import static dtos.base.Constants.*
import static dtos.base.Constants.CompareType.EQUAL
import static dtos.base.Constants.CompareType.INT

public class LinkedInDatabaseVerificationHtmlDTO extends HtmlXmlBaseDto {

    protected LinkedInDatabaseVerificationHtmlDTO(Map context, String htmlXmlSource, priority) {
        moduleName = this.class.name
        actionName = ASSERT
        htmlXmlSourceType = HTML
        HtmlXmlBaseDto child
        anchor = '//*[@id="content"]'
        dbName = "mySqlDb"
        super.init(context, htmlXmlSource, priority)

        child = createChild("Profile, ", PRIORITY_1, REGRESSION_TEST, "/div[1]", "", "", "mySqlDb")
        if (child != null) {
            child.addQuery_Select("SELECT * FROM LinkedInProfile WHERE ")
            child.with {
                addQueryConditionByXml("givenName = ", ".//*[@class='given-name']", true)
                addQueryConditionByXml("familyName = ", ".//*[@class='family-name']", true)
                assertSqlFieldValue("givenName", ".//*[@class='given-name']")
                assertSqlFieldValue("familyName", ".//*[@class='family-name']")
                assertSqlFieldValue("locality", ".//*[@class='locality']")
                assertSqlFieldValue("industry", ".//*[@class='industry']")
                assertTextValue("Stockholm, Sweden", ".//*[@class='locality']")

            }
        }

        child = createChild("Skills", PRIORITY_1, REGRESSION_TEST, "//*[@id='skills-list']/li", "", "", "mySqlDb")
        if (child != null) {
            child.addQuery_Select("SELECT * FROM LinkedInSkills WHERE ")
            child.with {
                addQueryConditionByXml("givenName = ", "//*[@class='given-name']", true)
                addQueryConditionByXml("skill = ", ".//span", true, "", /(^\s*|\s*$)/)
                assertSqlFieldValue("skill", ".//span", EQUAL, /(^\s*|\s*$)/)
            }
        }

        child = createChild("Skills count", PRIORITY_1, REGRESSION_TEST, "", "", "", "mySqlDb")
        if (child != null) {
            child.addQuery_Count("SELECT count(*) COUNT_SKILLS FROM LinkedInSkills WHERE ")
            child.with {
                addQueryConditionByXml("givenName = ", "//*[@class='given-name']", true)
                assertSqlFieldValue("COUNT_SKILLS", "//*[@id='skills-list']/li//span", INT)
            }
        }
    }

}








