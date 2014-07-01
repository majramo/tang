package dtos.base;

public interface XHtmlHelper {

	public Object setRootNode() ;
	public String getNodeTextValue(String xpath, String attributeName, Object regExp, boolean checkTagOnly) ;
	public void setXHtmlHelper(String htmlXmlSource, String sourceAnchor) ;
	public int getArraySzie(String xpath);
}
