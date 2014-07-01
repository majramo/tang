package dtos

import dtos.base.Constants


class FileHelper {
    public static final String REPORT_XSLT_FILE = "report.xsl"
    protected String moduleName
    protected String actionName
    protected String testId
    protected String htmlXmlSourceType
    private String settings

    protected htmlXmlSourceFile
    protected sqlFile
    protected xmlMarkupFileWithUtfHeader
    protected xmlMarkupFileWithIsoHeader
    protected SprintPath
    protected reportFileNameTimeFormat
    protected reportFileName

    protected String path
    protected String fileName
    protected htmlXmlSourceFileName
    def today = new Date()

    public FileHelper(moduleName, actionName, testId, htmlXmlSourceType, settings, htmlXmlSource, String outputDir) {
        this.moduleName = moduleName
        this.actionName = actionName
        this.testId = testId
        this.htmlXmlSourceType = htmlXmlSourceType
        this.settings = settings

        SprintPath = settings.SprintPath

        File sprintPathDir = new File(SprintPath)
        if (!sprintPathDir.isDirectory()) {
            sprintPathDir.mkdir()
            Thread.sleep(10)
        }
        reportFileNameTimeFormat = settings.ReportFileNameTimeFormat
        path = "$SprintPath${moduleName}/${actionName}/${testId}"
        def pathDir = new File(path)
        if (!pathDir.isDirectory()) {
            def pathCreated = new File(path).mkdirs()
            assert pathCreated
        }
        fileName = "${path}/${today.format("$settings.FileNameDateTimeFormat")}_$testId"
        if (htmlXmlSourceType == Constants.HTML) {
            htmlXmlSourceFile = new File("${fileName}_source.html")
        } else {
            htmlXmlSourceFile = new File("${fileName}_source.xml")
        }
        sqlFile = new File("${fileName}.sql")
        xmlMarkupFileWithUtfHeader = new File("${fileName}_xmlMarkupUtf.xml").getAbsoluteFile()
        xmlMarkupFileWithIsoHeader = new File("${fileName}_xmlMarkupIso.xml").getAbsoluteFile()

        def reportXsltFileTarget = new File("$outputDir/$REPORT_XSLT_FILE")
        def text = ""

        URL reportXsltFileOriginalPath = ClassLoader.getSystemResource(REPORT_XSLT_FILE);
        File reportXsltFileOriginalFile = new File(reportXsltFileOriginalPath.toURI());

        if (!reportXsltFileTarget.exists()) {
            if (reportXsltFileOriginalFile.exists()) {
                text = reportXsltFileOriginalFile.getText()
            }
            reportXsltFileTarget.write(text)
        }


        sqlFile.write("--Sql: ${moduleName}\n")
        htmlXmlSourceFile.write("")
        xmlMarkupFileWithUtfHeader.write("")
        xmlMarkupFileWithIsoHeader.write("")
    }


    protected updateRunReports(assertResult) {

        def color = assertResult ? "lime" : "tomato"
        def newAssertText = """
		   <a href='$xmlMarkupFileWithIsoHeader'>
			   <tr>
				   <td bgcolor='$color'>$assertResult </td>
				   <td>$moduleName </td>
				   <td>${this.testId} </td>
				   <td>$xmlMarkupFileWithIsoHeader</td>
			   </tr>
		   </a>\n
		   """


        def reportText = new File("$SprintPath/reports_${today.format("$reportFileNameTimeFormat")}.txt")
        def text = ""
        if (reportText.exists()) {
            text = reportText.getText()
        }
        reportText.write(newAssertText + "\n" + text)

        text = reportText.getText()
        def reportFile = new File("$SprintPath/reports_${today.format("$reportFileNameTimeFormat")}.html")
        reportFile.write("""
		   <html lang="en" xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" id="ext-gen4" class="  ext-strict">
		   <body>
			   <table cellpadding="5" bgcolor="LightCyan" border="1">
				   $text
			   </table>
		   </body></html>""")

        def latestMarkupFile = new File("$SprintPath/latestM.xml")
        latestMarkupFile.delete()
        new File("$SprintPath/latestM.xml") << xmlMarkupFileWithUtfHeader.text
    }


    protected fixEncoding() {
        def text = xmlMarkupFileWithUtfHeader.text
        text = text.replace('encoding="UTF-8"', 'encoding="ISO-8859-1"')
        xmlMarkupFileWithIsoHeader.write(text)

        def latestFile = new File("$SprintPath/latest.xml")
        latestFile.write(text)


    }
}
