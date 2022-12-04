package dtos.base

import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.htmlcleaner.*

public class HtmlHelper implements XHtmlHelper{
	def htmlCleaner = new HtmlCleaner()
	def sourecAnchor
	def sourceAnchorNode
	def soureCleanedRoot
	private final static Logger log = LogManager.getLogger("HH   ")
	
	public void setXHtmlHelper(String htmlXmlSource, String sourecAnchor){
		this.sourecAnchor = sourecAnchor.replaceAll(/\b(\w+=")\s*(.+?)\s*"/, '$1$2\"').replaceAll (/ +/," ")
		
		CleanerProperties props = htmlCleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);
		soureCleanedRoot = htmlCleaner.clean(htmlXmlSource)
		setRootNode()
		
		if(sourecAnchor != null){ 
				if(sourceAnchorNode == null || sourceAnchorNode == ""){
					log.info ("AnchorNode is not ok <$sourecAnchor>")
				}
			 
		}else{
					log.info ("anchorNode is not ok <$sourecAnchor>")
		}
	}
	
	public Object setRootNode(){
		if(sourecAnchor != null){
			try{
				Object[] foundNodes = soureCleanedRoot.evaluateXPath(sourecAnchor)
				if(foundNodes.size() >0){
					soureCleanedRoot = (TagNode) foundNodes[0];
					sourceAnchorNode = soureCleanedRoot
				}else{
					log.info "anchor<$sourecAnchor> setRootNode_MISSING_VALUE"
				}
			}catch (MissingMethodException e) {
                log.error "Can't find anchor<$sourecAnchor>"
                throw e
			}
		}else{
			log.info "anchor<$sourecAnchor> is null"
		}
	}

    public int getArraySzie(String xpath){
		Object[] foundNodes = soureCleanedRoot.evaluateXPath(xpath)
		return foundNodes[0]
	}

	public String getNodeTextValue(String xpath, String attributeName="", Object regExp = null, boolean checkTagOnly = false	){

		def tempXpath
		 if(xpath != null){
 				try{
					if(attributeName != "" && attributeName != null){
						if(attributeName.toLowerCase() == "count"){
							tempXpath  = "count($xpath)"
						}else{
							tempXpath  = "$xpath/@$attributeName"
						}
					}else{
						if(!xpath.contains("count(") && !checkTagOnly){
							tempXpath  = "$xpath/text()  "
						}else{
							tempXpath  = "$xpath"
						}
					}
					Object[] val

					val = soureCleanedRoot.evaluateXPath(tempXpath)
					log.debug  val.size()+ "  " + tempXpath
					String node = ""
					def xmlValue
					if(val.size() >0){
						 node  = val[0]

						xmlValue = node.replaceAll('&nbsp;','')
                        if(StringUtils.isNotEmpty(xmlValue) && regExp != null){
                            xmlValue = xmlValue.replaceAll(regExp[0], regExp[1])
                        }
						log.debug "HTM node  <$node>"
						log.debug "HTM node  <$tempXpath>"
						log.debug "HTM value <$xmlValue>"
						log.debug "HTM val   <$val>"
					}else{
						if(!checkTagOnly){
							xmlValue =""
						}
					}
					return xmlValue
				}catch (GroovyRuntimeException e) {
					log.error(e)
					return null
				}

		}else{
			//warn "xpath<$xpath> getNodeValue_MISSING_VALUE"
			return null
		}
	}

}
