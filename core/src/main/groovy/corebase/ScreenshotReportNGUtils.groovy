package corebase

import org.apache.commons.lang3.StringUtils
import org.testng.ITestResult
import org.uncommons.reportng.ReportNGUtils

import static dtos.base.Constants.*

public class ScreenshotReportNGUtils extends ReportNGUtils {


    /* (non-Javadoc)
    * @see org.uncommons.reportng.ReportNGUtils#getTestOutput(org.testng.ITestResult)
    * override to add screenshot from result attribute
    */
    @Override
    public List<String> getTestOutput(ITestResult result) {
        List<String> outputIn = super.getTestOutput(result);
        List<String> outputOut = addTestAttributes(result, outputIn);
        appendPageSourceIfPresent(result, outputOut);
        return outputOut;
    }
    private static List<String> addTestAttributes(ITestResult result, List<String> outputIn) {
        List<String> outputOut = new ArrayList<String>();
        String out = ""
        if (StringUtils.isNotBlank((String) result.getAttribute(TEST_STATUS))) {
            out += result.getAttribute(TEST_STATUS)
        }
        if (StringUtils.isNotBlank((String) result.getAttribute(ICONS))) {
            out += result.getAttribute(ICONS)
        }
        if (StringUtils.isNotBlank((String) result.getAttribute(DESCRIPTION))) {
            out += "<br>Description: " + result.getAttribute(DESCRIPTION)
        }
        if (StringUtils.isNotBlank((String) result.getAttribute(ENVIRONMENT))) {
            out += "<br>Environment: " + result.getAttribute(ENVIRONMENT)
        }
        if (StringUtils.isNotBlank((String) result.getAttribute(BROWSER))) {
            out += "<br>Browser: " + result.getAttribute(BROWSER)
        }
        if (StringUtils.isNotBlank((String) result.getAttribute(ISSUE_LINK_GROUP))) {
            out += "<br>" + result.getAttribute(ISSUE_LINK_GROUP)
        }
        if (StringUtils.isNotBlank((String) result.getAttribute(ISSUE_LINK))) {
            out += "<br>" + result.getAttribute(ISSUE_LINK)
        }
        outputOut.addAll(out)
        outputOut.addAll(outputIn)
        return outputOut
    }
    private static void appendPageSourceIfPresent(ITestResult result, List<String> output) {
        String pageSource = (String) result.getAttribute("pageSource");
        if (pageSource != null) {
            output.add("<br/>pageSource: " + pageSource +"<br><a href=\"" + pageSource + "\" target=\"_blank\">" +
                    "<img src=\"" + pageSource + "\" border=\"2\" width=\"158\" height=\"20\" hspace=\"10\" /></a><br/>");
        }
    }


    @Override
    public String escapeHTMLString(String reporHtmlLine) {
        if(StringUtils.isNotEmpty(reporHtmlLine) && reporHtmlLine.contains("images/") )  {
            return "";
        }
        if(StringUtils.isNotEmpty(reporHtmlLine) && reporHtmlLine.contains("<img src=")){
            return reporHtmlLine;
        }
        return super.escapeHTMLString(reporHtmlLine);
    }
}
