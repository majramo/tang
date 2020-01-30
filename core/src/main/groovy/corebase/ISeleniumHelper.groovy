package corebase

import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import java.lang.reflect.Method

public interface ISeleniumHelper {

    public setTestName(testName)

    public boolean isAjaxPageLoaded()

    public boolean setLoadingString(loadingString)

    public void openUrl(final String url)

    public slowDown(slowDownTime)

    public setSlowDownOn(slowDownTime)

    public setSlowDownOff()

    public void refresh()

    public boolean isSelected(final String xpath)


    public boolean isSelected(final String xpath, int index)

    public boolean isTagAvailable(final String xpath)

    public boolean isTagAvailable(final String xpath, int changedImplicitlyWait)

    public boolean isTagUnavailable(final String xpath)

    public boolean isTagUnavailable(final String xpath, int changedImplicitlyWait)

    public String getText(final String xpath)

    public boolean isTextPresent(final String xpath, final String text)

    public boolean click(final String xpath)

    public boolean click(final String xpath, int changedImplicitlyWait)

    public boolean clickAction(final String xpath)

    public boolean clickAction(final String xpath, int changedImplicitlyWait)

    public boolean doubleClick(final String xpath)

    public boolean doubleClick(final String xpath, int changedImplicitlyWait)

    public void clickAndSwitchToModalWindow(
            final String linkToClickOn, String elementOneToWaitFor, String elementTwoToWaitFor)

    public boolean clickAndSwitchToModalWindowIfExists(final String linkToClickOn, int sleepTimeSeconds)

    public boolean click(final String xpath, final String subXpath)

    public void switchToDefault()

    public boolean select(final String xpath, final String text)

    public boolean select(final String xpath, final int index)


    public void goBack()

    public int getXpathCount(final String xpath)

    public String getPageSource()

    public String getAttribute(final String xpath, final String attributeName)

    public void printWindows()


    public boolean sendKeys(final String xpath, text)

    public boolean type(final String xpath, text)

    public boolean typeCss(final String xpath, text)

    public Object executeJavascript(final String script)

    public boolean waitForElement(final String xpath)

    public boolean waitForPageReadyStateComplete()

    public boolean waitForJsCondition(final String javascript)

    public int getDefaultImplicitWait()

    public int getDefaultPageLoadTimeout()

    public WebDriver getDriver()

    public WebDriver getWebDriver()

    public File takeScreenShot(String message)

    public void takeScreenShotAndSource(String message)

    public void quit()

    public String getCurrentUrl()

    public void maximizeWindow()

    public Point getPoint()

    public void setWindowPosition()

    public void setWindowPosition(int screenX_Position, int screenY_Position)

    public int getScreenX_Position()

    public int getScreenY_Position()

    String getTitle()

    String requireTitle(String title)

    void requireXpath(String xpath)

    WebElement requireVisibleXpath(String xpath)

    WebElement requireVisibleXpath(String xpath, int changedImplicitlyWait)

    public void hover(String xpath)

    public void switchToFrame(String xpath)

    public void switchToSubFrame(String xpath)

    public void switchToNextWindow()

    public void switchToPreviousWindow()

    public void close()

    boolean typeAndEnter(String xpath, String data)

    public void selectAllAndType(String xpath, String data)

    public void selectAllAndTypeAndEnter(String xpath, String data)

    public void selectAllAndTypeAndEnterAndExpectError(String xpath, String data, Exception e)


    public String getAlertTextAndAccept()

    public boolean isAlertPresent()

    public boolean isDisplayed(String xpath)

    public boolean isDisplayed(String xpath, int changedImplicitlyWait)

    public String getHtmlImgTag(final String filePath)

    public String getHtmlImgTag(final String filePath, String fileName)

    public void scrollIntoView(final String xPath)

    public changeBrowser(String browser)

    public ISeleniumHelper changeBrowserToFirefox()

    public ISeleniumHelper changeBrowserToChrome()

    public ISeleniumHelper changeBrowserToSafari()

    public ISeleniumHelper changeBrowserToInternetExplorer()
    public ISeleniumHelper restartBrowser()

    public void sleep(long milliseconds )
    public List analyseLinksByXpath(String[] xpaths)
    boolean selectVisualOption(java.lang.String xpath, java.lang.String text);
 }
