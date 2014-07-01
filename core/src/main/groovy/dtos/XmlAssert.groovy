package dtos

import org.apache.log4j.Logger;

class XmlAssert {
    private final static Logger log = Logger.getLogger("XA   ")

    def moduleName
    def actionName
    def line
    protected xmlMap = [:]
    protected sqlMap = [:]
    def assertMapSql = [:]
    def parMapSql
    def parMapText
    def assertionsMap_Instance = [:]
    def assertResult = true
    String queryRun
    def xmlBuilder
    def xmlResult
    def xmlOut

    public XmlAssert(moduleName, actionName, line, assertionsMap_Template, xmlBuilder) {
        this.moduleName = moduleName
        this.actionName = actionName
        this.line = line
        this.xmlBuilder = xmlBuilder
        assertionsMap_Template.each { text, assertion ->
            def sizeOrder = assertionsMap_Instance.size()
            assertionsMap_Instance["$sizeOrder $text"] = new Assertion(assertion.textOrSqlType, assertion.textValueOrSqlField, assertion.sutElementXpath, assertion.compareType, assertion.regExp, sizeOrder, xmlBuilder, assertion.checkTagOnly)
        }
        printMe()
    }



    public printMe() {
        log.debug "XmlAssert $moduleName $actionName $line"
    }

    public printXml() {
    }

    public printHeader() {
    }

    public printAssert() {
        log.info line + "-> 	"
        parMapSql.each { sql, xml ->
            log.info assertMapSql["$xml"] + "\t"
        }
    }

    public assertCompare(sqlHelperMock) {

        assertionsMap_Instance.each { k, Assertion assertion ->

            if (assertion.textOrSqlType == "SQL") {
                assertion.assertCompare(xmlMap[assertion.sutElementXpath], sqlMap[assertion.textValueOrSqlField], sqlHelperMock)
            } else {
                assertion.assertCompare(xmlMap[assertion.sutElementXpath], assertion.textValueOrSqlField, sqlHelperMock)
            }

            if (!assertion.assertResult && assertResult) {
                assertResult = false
            }
        }



        xmlOut = {
            delegate.line(line)
            delegate.moduleName(moduleName)
            delegate.queryRun(queryRun)
            assertionsMap_Instance.each {
                k, Assertion assertionV ->
                    assertion(assertionV.xmlOut)
            }
        }
        return assertResult
    }

    public printAssertCompare() {

        assertionsMap_Instance.each {
            k, Assertion assertion ->
                assertion.printAssertCompare()
        }
    }
}
