package htmls

import dtos.HtmlXmlBaseDto

import static dtos.base.Constants.*
import static dtos.base.Constants.CompareType.*;

public class LinkedInAddDataToDatabaseHtmlDTO extends HtmlXmlBaseDto{

	protected LinkedInAddDataToDatabaseHtmlDTO(Map context, String htmlXmlSource, priority){
		moduleName = this.class.name
		actionName = ASSERT
		htmlXmlSourceType = HTML
		HtmlXmlBaseDto child
		anchor =  '//*[@id="content"]'
        dbName = "mySqlDb"
		super.init(context, htmlXmlSource, priority)
        //INSERT INTO LinkedInProfile (givenName,familyName, locality, industry) VALUES ("Majid","Aram", "Stockholm, Sweden", "Information Technology and Services");

        child = createChild( "Profile, " , PRIORITY_1, REGRESSION_TEST, "/div[1]", "", "", "mySqlDb")
		if(child != null){
			 child.addQuery_Select("SELECT * FROM LinkedInProfile WHERE ")
   			 child.with{
				addQueryConditionByXml("givenName = "					, ".//*[@class='given-name']", true)
				addQueryConditionByXml("familyName = "					, ".//*[@class='family-name']", true)
				assertSqlFieldValue("givenName" 						, ".//*[@class='given-name']" )
				assertSqlFieldValue("familyName" 						, ".//*[@class='family-name']"  )
				assertSqlFieldValue("locality" 							, ".//*[@class='locality']"  )
				assertSqlFieldValue("industry"							, ".//*[@class='industry']"  )
				assertTextValue("Stockholm, Sweden" 					, ".//*[@class='locality']"  )
				assertTextValue("Information Technology and Services" 	, ".//*[@class='industry']"  )

			}
		}

		child = createChild( "Skills" , PRIORITY_1, REGRESSION_TEST, "//*[@id='skills-list']/li", "", "", "mySqlDb")
		if(child != null){
			 child.addQuery_Select("SELECT * FROM LinkedInSkills WHERE ")
			 child.with{
                 addQueryConditionByXml("givenName = "	, "//*[@class='given-name']", true)
                 addQueryConditionByXml("skill = "		, ".//a", true, "", /(^\s*|\s*$)/ )
                 assertSqlFieldValue("skill" 			, ".//a", EQUAL, /(^\s*|\sz*$)/  )
			 }
		 }

        child = createChild( "Skills count" , PRIORITY_1, REGRESSION_TEST, "", "", "", "mySqlDb")
        if(child != null){
            child.addQuery_Count("SELECT count(*) COUNT_SKILLS FROM LinkedInSkills WHERE ")
            child.with{
                addQueryConditionByXml("givenName = "	    , "//*[@class='given-name']", true)
                assertSqlFieldValue("COUNT_SKILLS" 			, "//*[@id='skills-list']/li//a", INT  )
            }
        }
    }

}








