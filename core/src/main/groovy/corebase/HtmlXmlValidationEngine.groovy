package corebase

import dtos.HtmlXmlBaseDto
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager
import static dtos.base.Constants.HTML
import static dtos.base.Constants.XML

public class HtmlXmlValidationEngine {
    private final static Logger log = LogManager.getLogger("HXVE ")
    private HtmlXmlBaseDto hxDto = null
    private dtoNamePath

    private static printMessage(msg) {
        log.info ""
        log.info "############################################"
        log.info "$msg"
        log.info ""
    }

    public boolean returnAssertResult() {
        if (hxDto != null) {
            return hxDto.returnAssertResult()
        }
        return true
    }

    public String returnAssertFile() {
        if (hxDto != null) {
            hxDto.gatherXmlResult()
            return hxDto.xmlMarkupFileWithUtfHeader
        }
        return null
    }

    public HtmlXmlValidationEngine(Map context, String dtoName, String htmlXmlSource, Integer priority) {
        ValidationResponseEngineRun(context, dtoName, htmlXmlSource, priority)
    }

    public HtmlXmlValidationEngine ValidationResponseEngineRun(Map context, String dtoName, String htmlXmlSource, Integer priority) {
        if (dtoName.toLowerCase().contains(HTML)) {
            dtoNamePath = "htmls.$dtoName"
        } else {
            if (dtoName.toLowerCase().contains(XML)) {
                dtoNamePath = "xmls.$dtoName"
            } else {
                log.info "$dtoName is not $XML or $HTML"
                return null
            }
        }
        log.info "Start $dtoName $dtoNamePath"

        try {

            def domainClass = this.class.classLoader.loadClass(dtoNamePath)
            hxDto = (HtmlXmlBaseDto) domainClass.newInstance(context, htmlXmlSource, priority)

            printMessage "parsHtmlXmlSourceIntoObjects"
            hxDto.parsHtmlXmlSourceIntoObjects(priority)

            printMessage "printPartObjectsXml"
            hxDto.printPartObjectsXml(priority)

            printMessage "assertCompare"
            hxDto.assertCompare()
            hxDto.gatherXmlResults()

            printMessage "printAssertResult"
            hxDto.printAssertCompare()

            hxDto.returnAssertResult()
            return this
        } catch (ClassNotFoundException exception1) {
            log.error "Can't find class <$dtoNamePath>"
            log.error(exception1)
            throw exception1
        } catch (GroovyRuntimeException exception2) {
            log.error "Can't init class  <$dtoNamePath>"
            log.error(exception2)
            throw exception2
        }
    }
}