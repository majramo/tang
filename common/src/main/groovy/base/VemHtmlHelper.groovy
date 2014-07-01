package base

import org.testng.Reporter

public class VemHtmlHelper {
    public String getHtmlImgTags(String title, String urlTitle = "", final String url = "") {
        def str = '<br/>' + title +
                "<a href=\"" + url + "\" target=\"_blank\">" +
                urlTitle + "<br>" +
                "<img src=\"" + url + "\" border=\"2\" width=\"88\" height=\"80\" hspace=\"10\" /></a><br/><br/>"
        return str
    }


    public String addHtmlTagToReport(String title, String urlTitle, String url) {
        def str = getHtmlImgTags(title, urlTitle, url)
        Reporter.log(str)
        return str
    }

}