package corebase

import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.htmlunit.*
import org.testng.SkipException

/**
 * Created with IntelliJ IDEA.
 * User: majidaram
 * Date: 2013-06-15
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
class SeleniumDummyHelper implements ISeleniumHelper {

    public ISeleniumHelper init(String browser, String outputDir = "./", int defaultImplicitlyWait = 10, int defaultPageLoadTimeout = 60) throws MalformedURLException, SkipException {
        Thread.sleep(111)
        return this
    }


    def setTestName(testName) {

    }


    boolean isAjaxPageLoaded() {
        return true
    }


    boolean setLoadingString(loadingString) {
        return true
    }


    void openUrl(String url) {

    }


    def slowDown(slowDownTime) {

    }


    def setSlowDownOn(slowDownTime) {

    }


    def setSlowDownOff() {

    }


    void refresh() {

    }


    boolean isSelected(String xpath) {
        return true
    }


    boolean isSelected(String xpath, int index) {
        return true
    }


    boolean isTagAvailable(String xpath) {
        return true
    }


    boolean isTagAvailable(String xpath, int changedImplicitlyWait) {
        return true
    }


    boolean isTagUnavailable(String xpath) {
        return true
    }


    boolean isTagUnavailable(String xpath, int changedImplicitlyWait) {
        return true
    }


    String getText(String xpath) {
        return "0"
    }


    boolean isTextPresent(String xpath, String text) {
        return true
    }


    boolean click(String xpath) {
        return true
    }


    boolean click(String xpath, int changedImplicitlyWait) {
        return true
    }

    boolean clickAction(String xpath) {
        return true
    }


    boolean clickAction(String xpath, int changedImplicitlyWait) {
        return true
    }


    boolean doubleClick(String xpath) {
        return true
    }


    boolean doubleClick(String xpath, int changedImplicitlyWait) {
        return true
    }


    void clickAndSwitchToModalWindow(String linkToClickOn, String elementOneToWaitFor = "", String elementTwoToWaitFor = "") {
    }

    public boolean clickAndSwitchToModalWindowIfExists(final String linkToClickOn, int sleepTimeSeconds){

    }

    boolean click(String xpath, String subXpath) {
        return true
    }


    void switchToDefault() {

    }


    void setWindowPosition(int screenX_Position = 0, int screenY_Position = 0) {
    }


    boolean select(String xpath, String text) {
        return true
    }


    boolean select(String xpath, int index) {
        return true
    }


    void goBack() {

    }


    int getXpathCount(String xpath) {
        return 0
    }


    String getPageSource() {
        return ""
    }


    String getAttribute(String xpath, String attributeName) {
        return ""
    }


    void printWindows() {

    }


    boolean sendKeys(String xpath, text) {
        return true
    }


    boolean type(String xpath, text) {
        return true
    }


    boolean typeCss(String xpath, text) {
        return true
    }


    Object executeJavascript(String script) {
        return ""
    }


    boolean waitForElement(String xpath) {
        return true
    }


    boolean waitForJsCondition(String javascript) {
        return true
    }


    boolean waitForPageReadyStateComplete() {
        return true
    }


    int getDefaultImplicitWait() {
        return 0
    }


    int getDefaultPageLoadTimeout() {
        return 0
    }


    WebDriver getDriver() {
        return //new HtmlUnitDriver()
    }

    WebDriver getWebDriver() {
        return //new HtmlUnitDriver()
    }


    File takeScreenShot(String message) {
    }


    void takeScreenShotAndSource(String message) {
    }


    void quit() {

    }


    String getCurrentUrl() {
        return ""
    }


    void maximizeWindow() {

    }


    Point getPoint() {
        return new Point(0, 0)
    }


    int getScreenX_Position() {
        return 0
    }


    int getScreenY_Position() {
        return 0
    }


    String getTitle() {
        return "Title"
    }


    String requireTitle(String title = "") {
        return true
    }


    void hover(String xpath) {
    }


    void scrollIntoView(String xpath) {
    }


    void switchToFrame(String xpath) {

    }


    void switchToSubFrame(String xpath) {

    }


    void switchToNextWindow() {


    }


    void switchToPreviousWindow() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    boolean typeAndEnter(String xpath, String data) {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }


    void selectAllAndType(String xpath, String data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    void selectAllAndTypeAndEnter(String xpath, String data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    void selectAllAndTypeAndEnterAndExpectError(String xpath, String data, Exception e) {


    }


    public String getAlertTextAndAccept() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }


    public String getHtmlImgTag(final String filePath, String fileName = "") {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean isAlertPresent() {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }


    void requireXpath(String xpath) {

    }


    void requireXpath(String xpath, int changedImplicitlyWait) {

    }


    WebElement requireVisibleXpath(String xpath) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    WebElement requireVisibleXpath(String xpath, int changedImplicitlyWait) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    boolean isDisplayed(String xpath, int changedImplicitlyWait) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    boolean isDisplayed(String xpath) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public changeBrowser(String browser) {

    }

    public ISeleniumHelper changeBrowserToFirefox() {
    }

    public ISeleniumHelper changeBrowserToChrome() {
    }

    public ISeleniumHelper changeBrowserToSafari() {
    }

    public ISeleniumHelper changeBrowserToInternetExplorer() {
    }

    public ISeleniumHelper restartBrowser() {
    }

    public void sleep(long milliseconds ) {
    }

    @Override
    List analyseLinksByXpath(String[] xpaths) {
    }
    public boolean selectVisualOption(java.lang.String xpath, java.lang.String text) {

    }

}
