package dtos

import org.apache.log4j.Logger

import static dtos.base.Constants.*

class XmlAssertObject {
    protected xmlAssertMap = [:]
    def moduleName
    def actionName
    protected assertionsMap_Template = [:]
    public assertResult = true
    static int noOfInstances;
    int myInstanceNo;
    private final static Logger log = Logger.getLogger("XAO  ")
    def xmlBuilder
    def xmlOut

    public XmlAssertObject(moduleName, actionName, xmlBuilder) {
        this.moduleName = moduleName
        this.actionName = actionName
        this.xmlBuilder = xmlBuilder
        noOfInstances++
        myInstanceNo = noOfInstances
    }

    public printAssertions() {
        assertionsMap_Template.each { k, v ->
            v.printMe()
        }
    }

    public assertCompare(Object sqlHelperMock) {

        xmlAssertMap.each { k, XmlAssert vXmlAssert ->

            vXmlAssert.assertCompare(sqlHelperMock)
            if (!vXmlAssert.assertResult && assertResult) {
                assertResult = false
            }

        }

        xmlOut = {
            delegate.myInstanceNo(myInstanceNo)
            delegate.noOfInstances(noOfInstances)
            delegate.moduleName(moduleName)

            xmlAssertMap.each { k, XmlAssert vXmlAssert ->
                xmlAssert(vXmlAssert.xmlOut)
            }
            delegate.assertResult(assertResult)

        }

        return assertResult
    }

    public printAssertCompare() {
        log.info ""
        log.info "$moduleName $actionName assertResult: $assertResult"

        xmlAssertMap.each { k, XmlAssert vXmlAssert ->
            vXmlAssert.printAssertCompare()
        }

    }



    public assertTextValue(text, xml, comparasionType, regExp) {
        def newXmlItem = getUniqXmlItem(xml)
        assertionsMap_Template[assertionsMap_Template.size() + " " + text] = new Assertion(TEXTSQL_TYPE_TEXT, text, newXmlItem, comparasionType, regExp, assertionsMap_Template.size(), xmlBuilder)
    }

    public assertTagPresence(text, xml, comparasionType, regExp) {
        def newXmlItem = getUniqXmlItem(xml)
        assertionsMap_Template[assertionsMap_Template.size() + " " + text] = new Assertion(TEXTSQL_TYPE_TEXT, text, newXmlItem, comparasionType, regExp, assertionsMap_Template.size(), xmlBuilder, TAG_ONLY)
    }




    public assertSqlFieldValue(sql, xml, comparasionType, regExp) {
        def newXmlItem = getUniqXmlItem(xml)
        assertionsMap_Template[assertionsMap_Template.size() + " " + sql] = new Assertion(TEXTSQL_TYPE_SQL, sql, newXmlItem, comparasionType, regExp, assertionsMap_Template.size(), xmlBuilder)
    }


    protected String getUniqXmlItem(currentXmlItem) {
        String newXmlItem = currentXmlItem
        assertionsMap_Template.each { k, v ->
            if ("$v.sutElementXpath" == "$newXmlItem") {
                newXmlItem += " "
            }
        }
        return newXmlItem

    }

    public Object createXmlAssert(line) {
        xmlAssertMap[line] = new XmlAssert(moduleName, actionName, line, assertionsMap_Template, xmlBuilder)
        printMe()
        return xmlAssertMap[line]
    }

    public printMe() {
        log.info "$moduleName $actionName"
    }


    public printMeXml() {

        xmlAssertMap.each() { line, XmlAssert xmlAssert ->
            if (line == 1) {
                xmlAssert.printHeader()
            }
            xmlAssert.printXml()
        }
        log.info ""
    }
}
