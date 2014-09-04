package dtos

import dtos.base.HtmlHelper
import dtos.base.SqlHelper
import dtos.base.XmlHelper
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import org.apache.log4j.Logger
import static dtos.base.Constants.*
import static dtos.base.Constants.CompareType.*

class HtmlXmlBaseDto {
    protected String moduleName
    protected String actionName
    String testId
    def htmlXmlSourceType = HTML
    Map context
    private final static Logger log = Logger.getLogger("HXBD ")
    protected sourceObjects = [:]
    def sutXmlMapTemplate = [:]
    protected anchor
    protected sourceAnchorNode = ""
    protected sectionXpath = ""
    protected arrayXpath = ""
    protected XmlAssertObject xmlAssertObject
    protected isChild
    protected String dbName = DB_DEFAULT
    int priority
    def assertResult = false
    def returnAssertResult = false
    public xmlMarkupFileWithIsoHeader
    public xmlMarkupFileWithUtfHeader
    protected sqlFile
    protected htmlXmlSourceFile
    def comment = ""
    protected SettingsHelper settingsHelper = SettingsHelper.getInstance()
    protected settings = settingsHelper.settings
    protected timeStamp = settingsHelper.timeStamp
    protected SprintPath = settings.SprintPath
    protected String outputDir = settings.outputDir

    protected applicationConf = settings
    //protected applicationConf = settingsHelper.applicationConf

    protected SqlHelper sqlHelper

    static writer = new StringWriter()
    static xmlBuilder
    def xmlOut
    FileHelper fileHelper
    def xhtmlHelper

    public boolean returnAssertResult() {
        returnAssertResult = xmlAssertObject.assertResult
        if (returnAssertResult) {
            sourceObjects.each() { key, HtmlXmlBaseDto partObject ->
                if (returnAssertResult == true && partObject.xmlAssertObject.assertResult == false) {
                    returnAssertResult = false
                }
            }
        }
        log.debug "returnAssertResult $returnAssertResult"
        return returnAssertResult
    }





    public initChild(testId, sqlFile, Map context, xmlBuilder, xhtmlHelper) {

        this.xmlBuilder = xmlBuilder
        this.xhtmlHelper = xhtmlHelper
        this.context = context
        this.sqlFile = sqlFile

        log.info("initChild")
        this.context.each { k, v ->
            log.info("Context values {$k=$v}")
        }
        this.testId = this.context["testId"]
        if(this.testId == null) {
            this.testId = testId
        }
        this.xmlMarkupFileWithIsoHeader = xmlMarkupFileWithIsoHeader
        this.sqlFile = sqlFile
        sqlHelper = new SqlHelper(sqlFile, this.log, dbName, settings.dbRun, settings)
        xmlAssertObject = new XmlAssertObject(moduleName, actionName, xmlBuilder)
    }

    public assertSqlFieldValue(sql, xml, compareType = FORGIVING, String regExp = "", String replace = "") {
        String[] regExpPair = [regExp, replace]
        xmlAssertObject.assertSqlFieldValue(sql, xml, compareType, regExpPair)
        sutXmlMapTemplate[sutXmlMapTemplate.size() + sql] = xml
    }


    public assertTextValue(text, xml, compareType = FORGIVING, regExp = "", String replace = "") {
        xmlAssertObject.assertTextValue(text, xml, compareType, [regExp, replace])
        sutXmlMapTemplate[sutXmlMapTemplate.size() + text] = xml
    }

    public assertTagPresence(text, xml, regExp = "", String replace = "") {
        xmlAssertObject.assertTagPresence(text, xml, TAG_PRESENCE, [regExp, replace])
        sutXmlMapTemplate[sutXmlMapTemplate.size() + text] = xml
    }

    public assertTagAbsence(text, xml, regExp = "", String replace = "") {
        xmlAssertObject.assertTagPresence(text, xml, TAG_ABSENCE, [regExp, replace])
        sutXmlMapTemplate[sutXmlMapTemplate.size() + text] = xml
    }

    public assertTextPresence(text, xml, regExp = "", String replace = "") {
        xmlAssertObject.assertTextValue(text, xml, TEXT_PRESENCE, [regExp, replace])
        sutXmlMapTemplate[sutXmlMapTemplate.size() + text] = xml
    }

    public assertTextAbsence(text, xml, regExp = "", String replace = "") {
        xmlAssertObject.assertTextValue(text, xml, TEXT_ABSENCE, [regExp, replace])
        sutXmlMapTemplate[sutXmlMapTemplate.size() + text] = xml
    }


    public init(Map context, String htmlXmlSource, int priority) {
        if(dbName.isEmpty()){
            dbName = DB_DEFAULT
        }

        comment = (comment != "") ? comment : "P_C: $moduleName"

        isChild = false
        xmlBuilder = new MarkupBuilder(writer)

        this.testId = context["testId"]
        this.context = context
        htmlXmlSource = htmlXmlSource.replaceAll(/\b(\w+=")\s*(.+?)\s*"/, '$1$2\"').replaceAll(/ +/, " ").replaceAll(/\&amp;/, /\&/)

        this.priority = priority


        if (anchor != null) {
            if (htmlXmlSourceType == HTML) {
                xhtmlHelper = new HtmlHelper()
                xhtmlHelper.setXHtmlHelper(htmlXmlSource, anchor)
            } else {
                xhtmlHelper = new XmlHelper()
                xhtmlHelper.setXHtmlHelper(htmlXmlSource, anchor)
            }
            sourceAnchorNode = xhtmlHelper.sourceAnchorNode
        } else {
            notify("sourceAnchorNode is not ok", anchor)
        }
        xmlAssertObject = new XmlAssertObject(moduleName, actionName, xmlBuilder)
        fileHelper = new FileHelper(moduleName, actionName, testId, htmlXmlSourceType, settings, htmlXmlSource, outputDir)
        htmlXmlSourceFile = fileHelper.htmlXmlSourceFile
        xmlMarkupFileWithIsoHeader = fileHelper.xmlMarkupFileWithIsoHeader
        sqlFile = fileHelper.sqlFile
        xmlMarkupFileWithUtfHeader = fileHelper.xmlMarkupFileWithUtfHeader
        htmlXmlSourceFile.write(htmlXmlSource)
        sqlHelper = new SqlHelper(sqlFile, this.log, dbName, settings.dbRun, settings)
    }

    private boolean proirityTest(priorityParam) {
        log.debug "Priority $priority  (priorityParam $priorityParam)"
        switch (priorityParam) {
            case 1:
                return (priority in [1, 3, 5, 7])
                break
            case 2:
                return (priority in [2, 3, 6, 7])
                break
            case 3:
                return (priority in [2, 3, 6, 7])
                break
            case 4:
                return (priority == priorityParam)
                break
            case 5:
                return (priority in [1, 4])
                break
            case 6:
                return (priority in [2, 4])
                break
            case 7:
                return (priority == 7)
                break

            default:
                if (priorityParam.class.toString().contains("java.lang.Integer")) {
                    return (priorityParam <= priority)
                } else {
                    return ("*".equals(priorityParam))
                }
                break
        }

        //0 = no permissions whatsoever; this person cannot read, write, or execute the file
        //1   =   execute only
        //2   =   write only
        //3   =   write and execute (1+2)
        //4   =   read only
        //5   =   read and execute (4+1)
        //6   =   read and write (4+2)
        //7   =   read and write and execute (4+2+1)
        //

    }


    protected createChild(section, int priorityParam, testType, arrayXpath, sectionXpath, commentValue = "", dbNameParam = "") {
        def htmlBDTO = new HtmlXmlBaseDto()

        log.debug "Priotrity $priorityParam"

        if (proirityTest(priorityParam)) {
            def newmoduleName = "${moduleName}: ${section}"
            log.info "Child $newmoduleName"
            commentValue = (commentValue != "") ? commentValue : "C_C: $newmoduleName"
            htmlBDTO.with {
                if (dbNameParam != "" && dbNameParam != null) {
                    dbName = dbNameParam
                } else {
                    dbName = this.dbName
                }
                moduleName = newmoduleName
                context = context
                actionName = this.actionName
                anchor = this.anchor
                sourceAnchorNode = this.sourceAnchorNode
                htmlXmlSourceType = this.htmlXmlSourceType
                priority = priorityParam

                initRef(arrayXpath, sectionXpath)

                isChild = true
                comment = commentValue
            }
            htmlBDTO.initChild(testId, sqlFile, context, xmlBuilder, xhtmlHelper)
            sourceObjects[newmoduleName] = htmlBDTO
            return htmlBDTO
        } else {
            return null
        }
    }

    protected addQuery_Select(query) {
        sqlHelper.dbQueryType = dbRunTypeFirstRow
        sqlHelper.dbQuery = query
    }

    protected addQuery_Selects(query) {
        sqlHelper.dbQueryType = dbRunTypeRowsSelects
        sqlHelper.dbQuery = query
    }

    protected addQuery_Count(query) {
        sqlHelper.dbQueryType = dbRunTypeCount
        sqlHelper.dbQuery = query
    }

    protected addQuery_Order(queryExtension) {
        sqlHelper.dbQueryExtension = queryExtension
    }

    protected addQuery_ConditionText(field, value, boolean isString = false) {
        sqlHelper.queryConditionsTextList[sqlHelper.queryConditionsTextList.size()] = new QueryCondition(field, value, isString)
    }


    protected addQueryConditionByXml(String field, String sutXpath, boolean isString = false, String attributeName = "", String regExp = "", String replace = "") {
        String[] regExpPair = [regExp, replace]
        sqlHelper.queryConditionsXmlList[sqlHelper.queryConditionsXmlList.size()] = new QueryCondition(field, sutXpath, isString, attributeName, regExpPair)
    }




    public Object populateMapInstanceWithData(boolean firstRow = false, line = null, sqlMap_Instance = null) {
        //firstRow is going to be used later

        sqlHelper.queryConditionsXmlList.each { k, v ->
            def sutValue
            def sutXpath
            sutXpath = v.sutXpath
            if (htmlXmlSourceType == HTML) {
                if (!sutXpath.startsWith("//")) {
                    if (arrayXpath != "") {
                        sutXpath = "$arrayXpath[$line]$sectionXpath$sutXpath"
                    } else {
                        sutXpath = "$sectionXpath$sutXpath"
                    }
                }
            } else {
                if (arrayXpath != "") {
                    sutXpath = "$anchor$arrayXpath[$line]$sectionXpath$sutXpath"
                } else {
                    sutXpath = "$anchor$sectionXpath$sutXpath"
                }
            }
            log.info "sutXpath <$sutXpath>"
            sutValue = xhtmlHelper.getNodeTextValue(sutXpath, v.attributeName, v.regExp)

            v.value = sutValue
        }

        def db_result = sqlHelper.getDb_result(dbName)
        if (db_result != null && sqlMap_Instance != null) {
            db_result.each { field, value ->
                //log.info "$field, $value"
                sqlMap_Instance[field] = value
            }
        }
        log.debug "db_result   $db_result $sqlHelper.dbQueryType($line)"
        log.debug "############################################################"

    }






    public printMe(String msg) {
        log.info "$msg	$testId"
    }

    public printPartObjects() {
        this.printMe("printPartObjects")
        if (sourceObjects.size() > 0) {
            log.debug "PartObjects Childrens " + sourceObjects.size()
            log.debug "Modilename $moduleName Children"
            sourceObjects.each() { key, HtmlXmlBaseDto partObject ->
                partObject.printPartObjects()
            }
        }
    }

    public printPartObjectsXml(int priority) {
        printMeXml("printPartObjectsXml PartObjects.size " + sourceObjects.size(), priority)
        if (sourceObjects.size() > 0) {
            log.info "PartObjects Childrens " + sourceObjects.size()
            sourceObjects.each() { key, HtmlXmlBaseDto partObject ->
                //log.info "Key $key"
                partObject.printPartObjectsXml(priority)
            }
        }
    }

    public printMeXml(String msg, priority) {
        if (this.priority <= priority) {
            if (xmlAssertObject != null) {
                xmlAssertObject.printMeXml()
            }
        }
    }






    public printAssertCompare() {
        assertResult = xmlAssertObject.printAssertCompare()
        sourceObjects.each() { key, HtmlXmlBaseDto partObject ->
            partObject.printAssertCompare()
        }
    }

    public gatherXmlResults() {
        xmlBuilder = new groovy.xml.StreamingMarkupBuilder()
        xmlBuilder.encoding = 'ISO-8859-1'

        //xmlMarkupFile.write xmlBuilder.bind(xmlOut).toString()

        xmlMarkupFileWithUtfHeader.write XmlUtil.serialize(xmlBuilder.bind(xmlOut))
        fileHelper.fixEncoding()
        fileHelper.updateRunReports(assertResult)
    }

    public gatherXmlResult() {
        def dbNameShow = dbName
        if (sqlHelper.sqlHelperMock) {
            dbNameShow = "$dbName (Mock = $sqlHelper.sqlHelperMock))"
        }

        if (!isChild) {

            xmlOut = {
                mkp.xmlDeclaration()
                mkp.pi("xml-stylesheet": "type='text/xsl' href='$outputDir/report.xsl'")
                Report() {
                    ParentModuleName(moduleName)
                    time(timeStamp)
                    anchor(anchor)
                    arrayXpath(arrayXpath)
                    sectionXpath(sectionXpath)
                    comment(comment)
                    dbName(dbNameShow)
                    Query(sqlHelper.dbQueryRun)
                    xmlAssertObject(xmlAssertObject.xmlOut)

                    sourceObjects.each() { key, HtmlXmlBaseDto partObject ->
                        DTO(partObject.xmlOut)
                        //DTO(partObject.xmlAssertObject.xmlOut)
                    }
                    assertResult(assertResult)
                }
            }
        } else {
            xmlOut = {
                ChildModuleName(moduleName)
                time(timeStamp)
                anchor(anchor)
                arrayXpath(arrayXpath)
                sectionXpath(sectionXpath)
                comment(comment)
                dbName(dbNameShow)
                Query(sqlHelper.dbQueryRun)
                xmlAssertObject(xmlAssertObject.xmlOut)
                sourceObjects.each() { key, HtmlXmlBaseDto partObject ->
                    DTO(partObject.xmlAssertObject.xmlOut)
                }
            }
        }
    }

    public assertCompare() {
        def dbNameShow = dbName
        if (sqlHelper.sqlHelperMock) {
            dbNameShow = "$dbName (Mock = $sqlHelper.sqlHelperMock))"
        }
        returnAssertResult = true


        assertResult = xmlAssertObject.assertCompare(sqlHelper.sqlHelperMock)
        sourceObjects.each() { key, partObject ->


            partObject.assertCompare()
            if (partObject.assertResult == false && assertResult) {
                assertResult = false
                returnAssertResult = false
            }
        }
        gatherXmlResult()
    }





    public initRef(arrayXpath, sectionXpath) {
        this.arrayXpath = arrayXpath
        this.sectionXpath = sectionXpath
    }


    public printAssertions(priority) {
        if (this.priority <= priority) {
            xmlAssertObject.printAssertions()
        } else {
            log.info "priority printAssertions" + this.priority + " <= " + priority
        }
        sourceObjects.each() { key, partObject ->
            partObject.printAssertions(priority)
        }
    }


    public parsHtmlXmlSourceIntoObjects(Integer priority) {
        /*
         * Denna funktion ska fungera enligt f�ljande
         * 1. Om f�rv�ntat xpath objekt �r en array objekt	--> jobba med arrayen rad f�r rad
         * 2. Om f�rv�ntat xpath �r en enda objekt 			--> jobba med den enda raden
         * 3. Om f�rv�ntat xpath �r Count					--> jobba med Count
         */
        if (this.priority <= priority && sourceAnchorNode != null && sutXmlMapTemplate.size() != 0) {
            if (arrayXpath == "") {
                parseHtmlXmlSourceSectionAtIndexToObject(1)
            } else {
                def xmlNode
                if (htmlXmlSourceType == HTML) {
                    xmlNode = "count($arrayXpath)"
                    log.info "HtmlNode <$xmlNode>"
                } else {
                    xmlNode = "count($anchor$arrayXpath)"
                    log.info "XmlNode <$xmlNode>"
                }
                int arraySize = xhtmlHelper.getArraySzie(xmlNode)
                def recs = setRecordsSizeToRead(arraySize, applicationConf.maxAyyaySizeToWorkWith)
                log.debug "acnhor $anchor"
                log.info "ArraySize $arraySize"
                if (recs > 0) {
                    (0..recs - 1).each { lineNo ->
                        def line = lineNo + 1
                        log.debug "Rec <$recs>  line<$line>"
                        parseHtmlXmlSourceSectionAtIndexToObject(line)
                    }
                }
            }
        }
        //K�r parsning f�r resp child-objekt
        sourceObjects.each() { key, partObject ->
            partObject.parsHtmlXmlSourceIntoObjects(priority)
        }
    }


    private int setRecordsSizeToRead(int arraySize, int maxAyyaySizeToWorkWith) {
        if (arraySize > 0) {
            if (maxAyyaySizeToWorkWith > 0) {
                if (arraySize > maxAyyaySizeToWorkWith) {
                    arraySize = maxAyyaySizeToWorkWith
                }
            }
        }
        return arraySize
    }


    public parseHtmlXmlSourceSectionAtIndexToObject(line) {

        if (sourceAnchorNode != null) {

            XmlAssert xmlAssertInstance = xmlAssertObject.createXmlAssert(line)
            def xmlMap_Instance = xmlAssertInstance.xmlMap
            def sqlMap_Instance = xmlAssertInstance.sqlMap
            def assertionsMap_Template = xmlAssertInstance.assertionsMap_Instance

            if (sqlHelper.isQueryOk()) {
                if (sqlHelper.dbQueryType == dbRunTypeRows) {
                    populateMapInstanceWithData(true, line, sqlMap_Instance)
                } else {
                    populateMapInstanceWithData(false, line, sqlMap_Instance)
                }
                xmlAssertInstance.queryRun = sqlHelper.dbQueryRun
            }

            //assertionsMap_Template
            assertionsMap_Template.each { sql, assertion ->
                def xmlNode
                def xml = assertion.sutElementXpath
                xmlNode = "$sectionXpath   $xml"
                if (arrayXpath != "") {
                    if (htmlXmlSourceType == HTML) {
                        xmlNode = "$arrayXpath   [$line]   $xmlNode"
                    } else {
                        xmlNode = "$anchor $arrayXpath   [$line]   $xmlNode"
                    }
                }

                def node
                if (sqlHelper.dbQueryType == dbRunTypeCount) {
                    xmlNode = "count($xmlNode)"
                    node = xhtmlHelper.getNodeTextValue(xmlNode)
                } else {
                    node = xhtmlHelper.getNodeTextValue("$xmlNode", "", assertion.regExp, assertion.checkTagOnly)
                }
                xmlMap_Instance[xml] = node
                log.debug sprintf('#S< %1$-20s Xv< %2$-30s Xx< %3$-20s %4$-30s ', sql, node, anchor, xmlNode)
            }
        } else {
            notify("sourceAnchorNode is null", anchor)
        }
    }

    def notify(String msg, String item) {
        log.debug "$settings.notifyMsgLine"
        log.debug "$settings.notifyMsgLine"
        log.debug "$settings.notifyMsg"
        log.debug "$settings.notifyMsg		$msg				<$item>"
        log.debug "$settings.notifyMsg"
        log.debug "$settings.notifyMsgLine"
        log.debug "$settings.notifyMsgLine"
    }

    public integerPercentRange(int range) {
        return INT_PERCENT_RANGE + range
    }

    public integerValueRange(int range) {
        return INT_VALUE_RANGE + range
    }

    public floatPercentRange(float range) {
        return FLOAT_PERCENT_RANGE + range
    }

    public floatValueRange(float range) {
        return FLOAT_VALUE_RANGE + range
    }
}