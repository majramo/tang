package dtos.base

import org.apache.log4j.Logger
import org.w3c.dom.Document
import org.xml.sax.InputSource

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory

public class XmlHelper implements XHtmlHelper {
    private final static Logger log = Logger.getLogger("XH   ")
    def anchor
    public sourceAnchorNode
    def xhtmlRoot
    def root

    Document doc
    DocumentBuilderFactory builderFactory
    DocumentBuilder builder
    InputSource inputSource

    public void setXHtmlHelper(String htmlXmlSource, String sourceAnchor) {

        this.anchor = sourceAnchor

        builderFactory = DocumentBuilderFactory.newInstance()
        builderFactory.setNamespaceAware(true)
        builder = builderFactory.newDocumentBuilder()
        inputSource = new InputSource(new StringReader(htmlXmlSource))
        doc = builder.parse(inputSource)

        root = XPathFactory.newInstance().newXPath()
        root.setNamespaceContext(UniversalNamespaceCache(doc, false))

        if (sourceAnchor != null) {
            sourceAnchorNode = setRootNode(sourceAnchor)

            if (sourceAnchorNode == null || sourceAnchorNode == "") {
                log.info("anchorNode is not ok", sourceAnchor)
            }

        } else {
            log.info("anchorNode is not ok", sourceAnchor)
        }
    }


    public Object setRootNode() {
        if (anchor != null) {
            try {
                root = getNodeValue(anchor, "")
                if (sourceAnchorNode != null) {
                    sourceAnchorNode = root
                    // how to retrieve the contents as a string
                } else {
                    log.info "anchor<$anchor> setRootNode_MISSING_VALUE"
                }
            } catch (GroovyRuntimeException e) {
                log.info "anchor<$anchor> setRootNode_MISSING_VALUE"
                log.info e
            }
        } else {
            log.info "anchor<$anchor> setRootNode_MISSING_VALUE"
        }
    }


    String getNodeTextValue(String xpath, String attributeName, Object regExp, boolean checkTagOnly) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }


    String getNodeTextValue(String xpath, String attributeName, String regExp) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }


    String getNodeTextValue(String xpath, String attributeName, String[] regExp) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }


    String getNodeTextValue(String xpath, String attributeName) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getArraySzie(String xpath) {
        return root.evaluate(xmlNode, doc).toInteger()
    }

    public String getNodeTextValue(String xpath, String attributeName, regExp, checkTagOnly = false) {

        if (xpath != null) {
            try {
                def result
                if (attributeName == "" || attributeName == null) {
                    result = root.evaluate("$xpath", doc)
                } else {
                    result = root.evaluate("$xpath/@$attributeName", doc)
                }
                return result
            } catch (GroovyRuntimeException e) {
                log.info "Could not find <$xpath>"
                log.info e
                return null
            }
        }
    }
}
