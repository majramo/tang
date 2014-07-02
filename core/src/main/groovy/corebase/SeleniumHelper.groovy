package corebase
import dtos.SettingsHelper
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.ScreenshotException
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.testng.Reporter
import org.testng.SkipException

import java.lang.reflect.Method
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

import static corebase.GlobalConstants.*
import static corebase.GlobalConstants.MAC

public class SeleniumHelper implements ISeleniumHelper {

    private static final String TANG = "tang_"
    private String loadingString = ""
    private static final int IMPLICT_WAIT = 1
    private WebDriver driver
    private long HUNDRED_MILLI_SECONDS = 100
    private long defaultImplicitlyWait = 10
    private long defaultPageLoadTimeoutSeconds = 80
    private long defaultPageLoadTimeoutMilliSeconds = defaultPageLoadTimeoutSeconds * 1000
    private boolean slowDown = false
    private int slowDownForMilliSeconds = 2000
    private String testName = ""
    private counter = 1
    private final static Logger log = Logger.getLogger("SH   ")
    protected String HTML
    protected SettingsHelper settingsHelper = new SettingsHelper()
    protected settings = settingsHelper.settings
    public applicationConf = settingsHelper.applicationConf
    protected URL COMPANY_HUB_URL
    protected URL SAUCELABS_HUB
    private static int driversCount = 0
    private static int screenX_Position = 0;
    private static int screenY_Row = 0;
    private static final int SCREEN_ROW_WINDOWS_COUNT = 4;
    private static final int SCREEN_ROWS_COUNT = 2;
    private static final int X_POSITION_MOVE_SIZE = 700;
    private static final int Y_POSITION_MOVE_SIZE = 300
    private static webDrivers = [:]
    public static final String LOCAL = "Local";
    public static final String HUB = "COMPANY_HUB";
    public static final String SAUCELABS = "SAUCELABS_HUB";

    public static final String PC = "PC";
    public static final String MAC = "MAC";
    public static final String OSX = "OS X";
    public static final String LINUX = "LINUX";
    public static final String ANDROID = "ANDROID";
    public static final String IPAD = "IPAD";
    public static final String IPHONE = "IPHONE";
    public static final String XP = "XP";
    public static final String WIN7 = "WIN7";
    public static final String WIN8 = "WIN8";
    public static final String VISTA = "VISTA";


    public static final String CHROME = "chrome";
    public static final String INTERNETEXPLORER = "internetexplorer";
    public static final String OPERA = "opera";
    public static final String SAFARI = "safari";
    public static final String HTMLUNIT = "htmlunit";
    private static final String FIREFOX_PROFILE = "firefox_profile";
    LinkedList<String> windowHandler = new LinkedList<String>()
    boolean macDriver
    private String javascriptToRun
    private String outputDirectory
    private static String os

    public void printWindows(){
        println "   ### windowHandler"
        windowHandler.each {
            println "   ### windowHandler $it"
        }
        println driver.getWindowHandle()
    }


    public ISeleniumHelper changeBrowserToFirefox() {
        return init(LOCAL_FIREFOX, outputDirectory + "/", defaultImplicitlyWait, defaultPageLoadTimeoutMilliSeconds)
    }

    public ISeleniumHelper changeBrowserToChrome() {
        return init(LOCAL_CHROME, outputDirectory + "/", defaultImplicitlyWait, defaultPageLoadTimeoutMilliSeconds)
    }

    public ISeleniumHelper changeBrowserToSafari() {
        return init(LOCAL_SAFARI, outputDirectory + "/", defaultImplicitlyWait, defaultPageLoadTimeoutMilliSeconds)
    }

    public ISeleniumHelper changeBrowserToInternetExplorer() {
        return init(LOCAL_INTERNETEXPLORER, outputDirectory + "/", defaultImplicitlyWait, defaultPageLoadTimeoutMilliSeconds)
    }

    public ISeleniumHelper changeBrowser(String browser) {
        return init(browser, outputDirectory + "/", defaultImplicitlyWait, defaultPageLoadTimeoutMilliSeconds)
    }



    public ISeleniumHelper init(String browser, String outputDir = "./", long implicitlyWait = defaultImplicitlyWait, long defaultPageLoadTimeout = defaultPageLoadTimeoutMilliSeconds) throws MalformedURLException, SkipException {
        switch (OS) {
            case ~/^win.*/:
                os = WIN
            case ~/^mac.*/:
                os = MAC
        }
        outputDirectory = ""
        if(outputDir.contains(File.separator)){
            outputDirectory = outputDir.substring(0, outputDir.lastIndexOf(File.separator))
        }
        browser = browser.trim()
        System.setProperty(REPORT_NG_ESCAPE_OUTPUT_PROPERTY, "true")
        System.setProperty(REPORT_NG_REPORTING_TITLE, "Test Automation NG")
        System.setProperty(OUTPUT_DIRECTORY_PROPERTY, outputDirectory)
        System.setProperty(IMAGE_DIRECTORY_PROPERTY, outputDirectory + IMAGE_DIRECTORY)
        System.setProperty(SOURCE_DIRECTORY_PROPERTY, outputDirectory + SOURCE_DIRECTORY)
        createDir(outputDir)
        createDir(outputDirectory)
        createDir(outputDirectory + IMAGE_DIRECTORY)
        createDir(outputDirectory + SOURCE_DIRECTORY)
        this.defaultImplicitlyWait = implicitlyWait
        this.defaultPageLoadTimeoutMilliSeconds = defaultPageLoadTimeout

        def chromeDriverPath = settings."CHROME_DRIVER_PATH"."$os"
        def ieDriverPath = settings."IE_DRIVER_PATH"."$os"


        COMPANY_HUB_URL = new URL(applicationConf.COMPANY_HUB)
        SAUCELABS_HUB = new URL(applicationConf.SAUCELABS_HUB)

        if (webDrivers.isEmpty()) {
            settings.webDrivers.each() {
                webDrivers[it[0]] = getCapability(it[0], it[1], it[2], it[3], it[4])
            }


        }

        DesiredCapabilities capability = (DesiredCapabilities) webDrivers[browser];
        if (capability == null) {
            Reporter.log("Can't set up driver $browser with capability <$capability>")
            throw new SkipException("Can't set up driver $browser with capability <$capability>")
        }
        log.info "browser <$browser>"
        log.info "capability <$capability>"

        switch (capability.getBrowserName()) {
            case ~/.*explorer.*/:
                setDriverPath(ieDriverPath, "webdriver.ie.driver")
                break

            case ~/chrome/:
                setDriverPath(chromeDriverPath, "webdriver.chrome.driver")
                break
        }

        switch (browser) {
            case ~/^LOCAL.*/:
                driver = getLocalDriver(capability, browser)
                moveDriverWindow(true, browser, driver)
                break
            case ~/^COMPANY_HUB_URL.*/:
                driver = new RemoteWebDriver(COMPANY_HUB_URL, capability)
                moveDriverWindow(true, browser, driver)
                break

            case ~/^SAUCELABS_HUB.*/:
                driver = new RemoteWebDriver(SAUCELABS_HUB, capability)
                break

        }
        macDriver = driver.capabilities["platform"].toString().contains("MAC")

        if (!browser.toString().contains("CH_") && !browser.toString().contains("CHROME") && !browser.toString().contains("SAFARI")) {
            try {
                driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
            } catch (GroovyRuntimeException e) {
                log.info("Driver $browser can't set implicitlyWait " + e)
            }
            try {
                driver.manage().timeouts().pageLoadTimeout(this.defaultPageLoadTimeoutMilliSeconds, TimeUnit.SECONDS)
            } catch (GroovyRuntimeException e) {
                log.info("Driver $browser can't set pageLoadTimeout " + e)
            }
        }

        return this
    }

    private static boolean addExtentionToFirefox(FirefoxProfile firefoxProfile, FireFoxAddon firefoxAddon) {
        File file;
        String path = firefoxAddon.getFullPath();
        if (path == null) {
            log.info("Firefox addon path can not be null");
            return false;
        }
        URL url = this.class.getResource(path);
        if (url == null) {
            log.info("Firefox addon URL can not be null");
            return false;
        }
        try {
            file  = new File(url.toURI());
        } catch (URISyntaxException e) {
            log.info("Failed to generate Firefox addon file: " + e);
            return false;
        }
        try {
            firefoxProfile.addExtension(file);
        } catch (IOException e) {
            log.info("Failed to load Firefox addon: " + e);
            return false;
        }
        if (firefoxAddon.getName().equalsIgnoreCase("firebug")) {
            firefoxProfile.setPreference("extensions.firebug.showFirstRunPage", false);
            firefoxProfile.setPreference("extensions.firebug.console.enableSites", true);
            firefoxProfile.setPreference("extensions.firebug.net.enableSites", true);
            firefoxProfile.setPreference("extensions.firebug.showNetworkErrors", false);
        }

        if (firefoxAddon.getName().equalsIgnoreCase("modify_headers")) {
            firefoxProfile.setPreference("modifyheaders.config.active", true);
            firefoxProfile.setPreference("modifyheaders.config.alwaysOn", true);
            firefoxProfile.setPreference("modifyheaders.headers.count", 1);
            firefoxProfile.setPreference("modifyheaders.headers.action0", "Add");
            firefoxProfile.setPreference("modifyheaders.headers.name0", "DNT");
            firefoxProfile.setPreference("modifyheaders.headers.value0", "1");
            firefoxProfile.setPreference("modifyheaders.headers.enabled0", true);
        }
        return true;
    }



    private static boolean setFirefoxProfileAddOns(FirefoxProfile firefoxProfile) {
        File addOnFile = new File( "/Users/majidaram/dev/tang/firebug@software.joehewitt.com.xpi" );
        if(addOnFile.exists()){
            firefoxProfile.addExtension( addOnFile );
        }
    }

    private WebDriver getLocalDriver(DesiredCapabilities capability, String browser) {
        System.getProperties().each {
            println it
        }
        WebDriver driver = null;
        try {
            switch (capability.getBrowserName()) {
                case ~/firefox/:
                    FirefoxProfile firefoxProfile = new FirefoxProfile();
                    Reporter.log("settings.firefoxProfileFolder: <" + settings.firefoxProfileFolder + ">")
                    if ((settings.firefoxProfileFolder).size()) {
                        String firefoxProfileFolder = settings.firefoxProfileFolder
                        if (firefoxProfileFolder != null && firefoxProfileFolder != "") {
                            firefoxProfile = new FirefoxProfile(new File(firefoxProfileFolder));
                        };
                    }
                    setFirefoxProfileAddOns(firefoxProfile);
                    capability.setCapability(FIREFOX_PROFILE, firefoxProfile)

                    FirefoxBinary binary = new FirefoxBinary();
                    firefoxProfile.setAcceptUntrustedCertificates(true);
                    firefoxProfile.setAssumeUntrustedCertificateIssuer(false);
                    driver = new FirefoxDriver(binary, firefoxProfile);

                    break

                case ~/.*explorer.*/:
                    driver = new InternetExplorerDriver(capability)
                    break

                case ~/chrome/:
                    driver = new ChromeDriver(capability)
                    break

                case ~/safari/:
                    driver = new SafariDriver(capability)
                    break

                case ~/htmlunit|htmlunitwithjs/:
                    driver = new HtmlUnitDriver(capability)
                    break

            }
        } catch (GroovyRuntimeException e) {
            log.info System.getProperty("webdriver.chrome.driver")
            log.info System.getProperty("webdriver.ie.driver")
            log.error("Aborted run. Can't set up driver $browser")
            log.error("Exception $e")
            Reporter.log("Aborted run. Can't set up driver $browser")
            Reporter.log("Exception $e")
            log.error("Can't set up driver $browser")
            Reporter.log("capability $capability")
            Reporter.log("")
            throw new SkipException("Can't set up driver $browser")
        }
        return driver
    }

    DesiredCapabilities getCapability(String name, String location, String browser, String browserVersion, String platform) {

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("name", name)
        capabilities.setCapability("location", location)
        switch (browser.toLowerCase()) {
            case CHROME:
                capabilities = DesiredCapabilities.chrome();
                break;

            case INTERNETEXPLORER:
                capabilities = DesiredCapabilities.internetExplorer();
                break;

            case HTMLUNIT:
                capabilities = DesiredCapabilities.htmlUnit();
                break;

            case OPERA:
                capabilities = DesiredCapabilities.opera();
                break;

            case SAFARI:
                capabilities = DesiredCapabilities.safari();
                break;

            case IPAD:
                capabilities = DesiredCapabilities.ipad();
                break;

            case IPHONE:
                capabilities = DesiredCapabilities.iphone();
                break;

            case ANDROID:
                capabilities = DesiredCapabilities.android();
                break;

            default:
                break;
        };

        if (!browserVersion.isEmpty()) {
            capabilities.setVersion(browserVersion);
        }

        if (location.equals(HUB)) {
            switch (platform) {
                case XP:
                    capabilities.setPlatform(Platform.XP);
                    break;

                case WIN7:
                    capabilities.setPlatform(Platform.VISTA);
                    break;

                case VISTA:
                    capabilities.setPlatform(Platform.VISTA);
                    break;

                case WIN8:
                    capabilities.setPlatform(Platform.WIN8);
                    break;

                case MAC:
                    capabilities.setPlatform(Platform.MAC);
                    break;

                default:
                    break;
            }

        }

        if (location.equals(SAUCELABS) && !platform.isEmpty()) {
            capabilities.setCapability("platform", platform);
        }
        return capabilities;
    }

    public setTestName(testName) {
        this.testName = testName.replaceAll(" ", "_")
    }

    public boolean isAjaxPageLoaded() {
        return waitForJsCondition(loadingString)
    }

    public boolean setLoadingString(loadingString) {
        this.loadingString = loadingString
    }

    public void openUrl(final String url) {
        slowDown()
        driver.get(url)
    }

    public slowDown(slowDownTime = slowDownForMilliSeconds) {
        if (slowDown) {
            sleep(slowDownTime)
        }
    }

    public setSlowDownOn(slowDownTime = slowDownForMilliSeconds) {
        slowDown = true
        slowDownForMilliSeconds = slowDownTime
    }

    public setSlowDownOff() {
        slowDown = false
    }

    public void refresh() {
        driver.navigate().refresh()
    }

    public boolean isSelected(final String xpath) {
        if (!"".equals(xpath)) {
            final WebElement we = findElementByXpathOrId(xpath, true)
            if (we != null) {
                return we.isSelected()
            }
        }
        return false
    }


    public boolean isSelected(final String xpath, int index) {
        if (!"".equals(xpath)) {
            final WebElement we = findElementByXpathOrId(xpath + "/option[$index]")
            if (we != null) {
                return we.isSelected()
            }
        }
        return false
    }

    public boolean isTagAvailable(final String xpath, int changedImplicitlyWait) {
        driver.manage().timeouts().implicitlyWait(changedImplicitlyWait, TimeUnit.SECONDS)
        boolean result = isTagAvailable(xpath)
        driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
        return result
    }


    public boolean isTagAvailable(final String xpath) {
        if (!"".equals(xpath)) {
            final WebElement we = findElementByXpathOrId(xpath, true)
            if (we != null) {
                return true
            }
        }
        return false
    }

    public boolean isTagUnavailable(final String xpath, int changedImplicitlyWait) {
        driver.manage().timeouts().implicitlyWait(changedImplicitlyWait, TimeUnit.SECONDS)
        boolean result = isTagUnavailable(xpath)
        driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
        return result
    }


    public boolean isTagUnavailable(final String xpath) {
        if (!"".equals(xpath)) {
            driver.manage().timeouts().implicitlyWait(IMPLICT_WAIT, TimeUnit.SECONDS)
            final WebElement we = findElementByXpathOrId(xpath, true)
            driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
            if (we == null) {
                return true
            }
        }
        return false
    }

    public String getText(final String xpath) {
        final WebElement we = findElementByXpathOrId(xpath)
        return (we != null) ? we.getText() : null
    }


    public boolean isTextPresent(final String xpath, final String text) {
        final WebElement we = findElementByXpathOrId(xpath, true)
        if (we == null) {
            return false
        }
        final String weText = we.getText()
        return (weText != null) ? weText.contains(text) : false
    }
    /**
     * Click button to open modal window and switch to it
     * @param we webElement handle of a button
     */
    public void clickAndSwitchToModalWindow(String linkToClickOn, String elementOneToWaitFor, String elementTwoToWaitFor = "") {
        final WebElement we = findElementByXpathOrId(linkToClickOn)

        Set<String> initWindowHandles = driver.getWindowHandles();

        //Create new thread and click button to open window
        Thread thread1 = new Thread() {

            public void run() {
                we.click()
            }
        };
        thread1.start();
        //Wait for window to appear
        sleep(2000)
        thread1.interrupt();
        thread1 = null;

        switchToNextWindow()
        requireVisibleXpath(elementOneToWaitFor)
        if(!elementTwoToWaitFor.isEmpty()){
            requireVisibleXpath(elementTwoToWaitFor)
        }
    }

    /**
     * Click button to open modal window and switch to it
     * @param we webElement handle of a button
     */
    public void typeAndSwitchToModalWindows(String xpath, data, String elementOneToWaitFor, String elementToClickOn) {
        final WebElement we = findElementByXpathOrId(xpath)

        Set<String> initWindowHandles = driver.getWindowHandles();

        //Create new thread and click button to open window
        Thread thread1 = new Thread() {

            public void run() {
                selectAll(we)
                data = data.toString()
                if (!"".equals(data)) {

                        we.sendKeys(data)

                }
            }
        };
        thread1.start();
        //Wait for window to appear
        sleep(2000)
        thread1.interrupt();
        thread1 = null;

        switchToNextWindow()
        requireXpath(elementOneToWaitFor)
        click(elementToClickOn)
    }

    /**
     * Click button to open modal window and switch to it
     * @param we webElement handle of a button
     */
    public boolean clickAndSwitchToModalWindowIfExists(String linkToClickOn, int sleepTimeSeconds) {
        final WebElement we = findElementByXpathOrId(linkToClickOn)

        Set<String> initWindowHandles = driver.getWindowHandles();
        def currentWindow = driver.getWindowHandle()
        //Create new thread and click button to open window
        Thread thread1 = new Thread() {

            public void run() {
                we.click()
            }
        };
        thread1.start();
        //Wait for window to appear
        sleep(sleepTimeSeconds * 1000)
        thread1.interrupt();
        thread1 = null;

        switchToNextWindow()
        if(currentWindow.equals( driver.getWindowHandle())){
            switchToPreviousWindow()
            return false
        }
        return true
    }

    public boolean click(final String xpath) {
        log.debug("click $xpath")
        requireVisibleXpath(xpath)
        final WebElement we = findElementByXpathOrId(xpath)
        if (we == null) {
            return false
        }
        we.click()
        return true
    }


    public boolean click(String xpath, int changedImplicitlyWait) {
        driver.manage().timeouts().implicitlyWait(changedImplicitlyWait, TimeUnit.SECONDS)
        boolean result = click(xpath)
        driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
        return result
    }


    public boolean doubleClick(String xpath, int changedImplicitlyWait) {
        driver.manage().timeouts().implicitlyWait(changedImplicitlyWait, TimeUnit.SECONDS)
        boolean result = doubleClick(xpath)
        driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
        return result
    }


    public boolean doubleClick(String xpath) {
        log.debug("doubleClick $xpath")
        requireVisibleXpath(xpath)
        Actions actions = new Actions(driver);
        WebElement webElement = findElementByXpathOrId(xpath, true);
        new Actions(driver).doubleClick(webElement).perform();
       // actions.moveToElement(webElement);
        return true
    }

    public boolean click(final String xpath, final String subXpath) {
        final WebElement we = findElementByXpathOrId(xpath)
        final WebElement subWe = we.findElement(By.xpath(subXpath))

        try {
            subWe.click()
        } catch (ElementNotVisibleException e) {
            log.info(e)
            final String href = subWe.getAttribute("href")
            driver.get(href)
        }
        return true
    }

    public boolean select(final String xpath, final String text) {
        final WebElement we = findElementByXpathOrId(xpath + "/option[.='" + text + "']")
        if (we == null) {
            return false
        }
        we.click()
        return true
    }

    public boolean select(final String xpath, final int index) {
        final WebElement we = findElementByXpathOrId(xpath + "/option[$index]")
        if (we == null) {
            return false
        }
        we.click()
        return true
    }

    private List<WebElement> findElementsByXpath(final String xpath) {
        return driver.findElements(By.xpath(xpath))
    }

    public void goBack() {
        driver.navigate().back()
    }

    public int getXpathCount(final String xpath) {
        final List<WebElement> webElements = findElementsByXpath(xpath)
        return webElements.size()
    }

    public String getPageSource() {
        return driver.getPageSource()
    }

    public String getAttribute(final String xpath, final String attributeName) {
        final WebElement we = findElementByXpathOrId(xpath)
        return (we != null) ? we.getAttribute(attributeName) : null
    }


    public boolean typeAndEnter(final String xpath, String text) {
        requireVisibleXpath(xpath)
        return type(xpath, text + Keys.ENTER)
    }


    void selectAllAndType(String xpath, String text) {
        final WebElement we = findElementByXpathOrId(xpath)
        selectAll(we)
        text = text.toString()
        if (!"".equals(text)) {
            we.sendKeys(text)
        }
    }

    void selectAllAndTypeAndExpectError(String xpath, String text, Exception e) {
        typeAndSwitchToModalWindows(xpath, text, "//*[@id='caption']/img[contains(@src, 'warn')]", "btOk" )
    }


    void selectAll(String xpath){
        final WebElement we = findElementByXpathOrId(xpath)
        we.click()
        if(macDriver){
            we.sendKeys(Keys.chord(Keys.COMMAND, "a"))
        }else{
            we.sendKeys(Keys.chord(Keys.CONTROL, "a"))
        }
    }

    void selectAll(WebElement we){
        we.click()
        if(macDriver){
            we.sendKeys(Keys.chord(Keys.COMMAND, "a"))
        }else{
            we.sendKeys(Keys.chord(Keys.CONTROL, "a"))
        }
    }


    void selectAllAndTypeAndEnter(String xpath, String text) {
        selectAllAndType(xpath, text + Keys.ENTER)
    }

    void selectAllAndTypeAndEnterAndExpectError(String xpath, String text, Exception e) {
        selectAllAndTypeAndExpectError(xpath, text + Keys.ENTER, e)
    }

    public boolean type(final String xpath, text) {
        log.debug("type $xpath $text")
        requireVisibleXpath(xpath)
        final WebElement we = findElementByXpathOrId(xpath)
        if (we == null) {
            return false
        }
        we.click()
        selectAll(we)
        text = text.toString()
        if (!"".equals(text)) {
            we.sendKeys(text)
        }
        return true
    }

    public boolean sendKeys(final String xpath, text) {
        final WebElement we = findElementByXpathOrId(xpath)
        if (we == null) {
            return false
        }
        text = text.toString()
        if (!"".equals(text)) {
            we.sendKeys(text)
        }
        return true
    }

    public boolean typeCss(final String css, text) {
        final WebElement we = findElementByCss(css)
        if (we == null) {
            return false
        }

        we.clear()
        text = text.toString()
        if (!"".equals(text)) {
            we.sendKeys(text)
        }
        return true
    }


    private WebElement findElementByXpathOrId(final String xpath, final boolean skipError) {
        if(xpath.startsWith("/") || xpath.startsWith("\\./") ){
            return findElementByXpath(xpath, skipError)
        }else{
            return findElementById(xpath, skipError)
        }
    }

    private WebElement findElementByXpathOrId(final String xpath) {
        findElementByXpathOrId(xpath, false)
    }

   private WebElement findElementById(final String id, final boolean skipError) {
       log.info("findElementById <$id>")
        try {
            if (!"".equals(id)) {
                slowDown()
                return driver.findElement(By.id(id))
            } else {
                return null
            }
        } catch (NoSuchElementException e) {
            if (!skipError) {
                log.error("id: " + skipError + " " + id)
                takeScreenShot("Couldn't find id: " + id)
                throw new ScreenshotException("Required id ($id) was not found")
            } else {
                log.debug(e)
                log.debug("id: " + skipError + " " + id)
                return null
            }
        } catch (org.openqa.selenium.UnhandledAlertException unhandledAlertExceptio) {
            Reporter.log(getHtmlImgTag("UnhandledAlertException: ", unhandledAlertExceptio))
            takeScreenShotAndSource("UnhandledAlertException")
            switchToNextWindow()
            takeScreenShotAndSource("Modal dialog present: ")
        }
    }

    private WebElement findElementByXpath(final String xpath, final boolean skipError) {
        log.info("findElementByXpath <$xpath>")
        try {
            if (!"".equals(xpath)) {
                slowDown()
                return driver.findElement(By.xpath(xpath))
            } else {
                return null
            }
        } catch (NoSuchElementException e) {
            if (!skipError) {
                log.error("xpath: " + skipError + " " + xpath)
                Reporter.log("xpath: " + skipError + " " + xpath)
                takeScreenShot("Couldn't find xpath: " + xpath)
                throw new ScreenshotException("Required xpath ($xpath) was not found")
            } else {
                log.debug(e)
                log.debug("xpath: " + skipError + " " + xpath)
//                Reporter.log("xpath: " + skipError + " " + xpath)
//                takeScreenShot("Couldn't find xpath: " + xpath)
                return null
            }
        }
    }

    private WebDriverWait createWebDriverWait() {
        return new WebDriverWait(driver, defaultPageLoadTimeoutSeconds)
    }

    public Object executeJavascript(final String script) {
        final Object result = createWebDriverWait().until(new ExpectedCondition<Object>() {

            public Object apply(final WebDriver dri) {
                final JavascriptExecutor javascriptExecutor = (JavascriptExecutor) dri
                return javascriptExecutor.executeScript(script)
            }
        })
        return result
    }

    public boolean waitForElement(final String xpath) {
        final boolean result = createWebDriverWait().until(new ExpectedCondition<Boolean>() {

            public Boolean apply(final WebDriver dri) {
                return null != driver.findElement(By.xpath(xpath))
            }
        })
        return result
    }


    public boolean waitForTitle(final String title) {
        final boolean result = createWebDriverWait().until(new ExpectedCondition<Boolean>() {

            public Boolean apply(final WebDriver dri) {
                return null != driver.findElement(By.xpath(title))
            }
        })
        return result
    }


    public boolean waitForJsCondition(final String javascript) {
        javascriptToRun = javascript
        final boolean result = createWebDriverWait().until(new ExpectedCondition<Boolean>() {

            public Boolean apply(final WebDriver dri) {
                final JavascriptExecutor javascriptExecutor = (JavascriptExecutor) dri;
                try {
                    return (Boolean) javascriptExecutor.executeScript(javascriptToRun);
                }
                catch (Exception e) {
                    return false
                }
            }
        });

        return result
    }


    public boolean waitForPageReadyStateComplete() {
        waitForJsCondition("return (document.readyState == 'complete')");
    }

    public int getDefaultImplicitWait() {
        return defaultImplicitlyWait
    }

    public int getDefaultPageLoadTimeout() {
        return defaultPageLoadTimeoutMilliSeconds
    }

    public WebDriver getDriver() {
        return driver
    }


    void takeScreenShotAndSource(String message = "") {
        takeScreenShot(message)
        addPageSourceToReport();
    }


    File takeScreenShot(String message = "") {

        if (StringUtils.isNotBlank(message)) {
            Reporter.log(message)
        }
        def destinationDirectory = System.getProperty(IMAGE_DIRECTORY_PROPERTY)

        String fileName = getFileName(testName, ".png")
        File tempScreenShotFile = null
        try {
                File destinationFile = createDestinationFile(destinationDirectory, fileName)
                tempScreenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)
                tempScreenShotFile.renameTo(destinationFile)
                log.info(getHtmlImgTag (destinationFile.getAbsolutePath(), fileName))
                Reporter.log(getHtmlImgTag(destinationFile.getAbsolutePath(), fileName) )
                return destinationFile
        } catch (IOException e) {
                Reporter.log("Can't move screenShot. Exists here: " + tempScreenShotFile)
                Reporter.log(getHtmlImgTag(tempScreenShotFile.getAbsoluteFile().getAbsolutePath(), tempScreenShotFile))
                log.info("Moving screenshot file failed. " + e)
        } catch (ClassCastException e) {
                log.info("WebDriver does not support screenShots: " + e)
        }
    }

    private String getFileName(String prefix, String fileExtension) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS")
        return prefix + dateFormat.format(new Date()) + "_" + counter++ + fileExtension
    }

    private void addPageSourceToReport() {
        try {
            def destinationDirectory = System.getProperty(SOURCE_DIRECTORY_PROPERTY)
            String fileName = getFileName(TANG, ".html")
            File destinationFile = createDestinationFile(destinationDirectory, fileName)

            AtomicReference<FileWriter> fileWrite = new AtomicReference<FileWriter>()
            fileWrite.set(new FileWriter(destinationFile.getAbsoluteFile()))
            BufferedWriter bufferedWriter = new BufferedWriter(fileWrite.get())
            bufferedWriter.write(driver.getPageSource())
            bufferedWriter.close()
            log.debug("Page source generated: " + destinationFile.getName())
            Reporter.log(getHtmlSourceTag(destinationFile.getAbsolutePath(), fileName) )
        } catch (GroovyRuntimeException e) {
            log.error("Error generating page source: " + e)
        }
    }

    private File createDestinationFile(String dir, String fileName) {
        File destinationDir = new File(dir)
        if (!destinationDir.isDirectory()) {
            destinationDir.mkdir()
        }
        return new File(destinationDir, fileName)
    }

    private void createDir(String dir) {
        File destinationDir = new File(dir)
        if (!destinationDir.isDirectory()) {
            destinationDir.mkdir()
            Thread.sleep(10)
        }
    }


    public String getHtmlImgTag(final String filePath, String fileName = "") {
        def str = '<br/>screenshot: ' +
                "<a href=\"" + filePath + "\" target=\"_blank\">" +
                fileName + "<br>" +
                "<img src=\"" + filePath + "\" border=\"2\" width=\"528\" height=\"480\" hspace=\"10\" /></a><br/><br/>"
        return str
    }

    private static String getHtmlSourceTag(final String filePath, fileName = "") {
        def fileIcon =  this.class.getResource("/icons/html.jpg")
        def str = '<br/>page source: ' +
                "<a href=\"" + filePath + "\" target=\"_blank\">" +
                fileName + "<br>" +
                "<img src=\"" + fileIcon + "\" border=\"2\" width=\"48\" height=\"40\" hspace=\"10\" /></a><br/>"
        return str
    }

    public void quit() {
        driver.quit()
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl()
    }

    public void maximizeWindow() {
        driver.manage().window().maximize()
    }

    public void switchToNextWindow() {
        //Store the current window handle
        log.info("Before push windowHandler size: " + windowHandler.size() )
        windowHandler.each {
            log.info( "Before push windowHandler $it")
        }
        log.info("Before  W " + driver.getWindowHandles().size()+ " H: " + windowHandler.size())

        while(driver.getWindowHandles().size() <= windowHandler.size()){
            log.info("Waiting W " + driver.getWindowHandles().size()+ " H: " + windowHandler.size())
            sleep(1000)
        }
        def currentWindow = driver.getWindowHandle()
        log.info( "currentWindow $currentWindow")

        windowHandler.push(currentWindow)
        windowHandler.each {
            log.info( "After push windowHandler $it")
        }
        def nextWindow= driver.getWindowHandles()[driver.getWindowHandles().size()-1]
        log.info( "nextWindow $nextWindow")

        driver.switchTo().window(nextWindow);
        //Perform the click operation that opens new window
        log.info("After   W " + driver.getWindowHandles().size()+ " H: " + windowHandler.size())

        //Switch to new window opened
    }

    public void switchToPreviousWindow() {
        log.info("Before pop windowHandler size: " + windowHandler.size() )
        windowHandler.each {
            log.info( "Before pop windowHandler $it")
        }
        def previuosWindow = windowHandler.pop()
        log.info( "previuosWindow $previuosWindow")
        driver.switchTo().window(previuosWindow)
        windowHandler.each {
            log.info( "After pop windowHandler $it")
        }
        log.info("After  pop windowHandler size: " + windowHandler.size() )

    }

    private void setDriverPath(String driverPath, String webDriverProperty) {
        try {
            log.info("setDriverPath <$driverPath> <$webDriverProperty>")
            URL driver = ClassLoader.getSystemResource(driverPath);
            File driverFile = new File(driver.toURI());
            if (!driverFile.canRead()) {
                throw new IllegalArgumentException("Can not read: " + driverPath);
            }
            System.setProperty(webDriverProperty, driverFile.getAbsoluteFile().toString())
        } catch (GroovyRuntimeException e) {
            log.error(e)
        }
    }

    private void moveDriverWindow(boolean arrangeWindows, browser, WebDriver driver) {
        if (arrangeWindows && !isBrowserHtmlUnit(browser)) {
            driver.manage().window().setPosition(getPoint());
            Thread.sleep(100)
        }
    }

    private static boolean isBrowserHtmlUnit(String browser) {
        return (StringUtils.isNotEmpty(browser) && browser.contains(HTML_UNIT))
    }

    public Point getPoint() {
        driversCount++;
        return new Point(getScreenX_Position(), getScreenY_Position());

    }

    public void setWindowPosition(int screenX_Position = 0, int screenY_Position = 0) {
        driver.manage().window().setPosition(new Point(screenX_Position, screenY_Position))
    }

    public int getScreenX_Position() {
        int currentScreenX = screenX_Position;
        if ((driversCount % SCREEN_ROW_WINDOWS_COUNT) == 0) {
            screenX_Position = 0;
        } else {
            screenX_Position += X_POSITION_MOVE_SIZE / SCREEN_ROW_WINDOWS_COUNT;
        }
        return currentScreenX;
    }

    public int getScreenY_Position() {
        if (driversCount > SCREEN_ROWS_COUNT * SCREEN_ROW_WINDOWS_COUNT) {
            driversCount = 1;
            screenY_Row = 0;
        }
        int currentScreenY_Row = screenY_Row;
        int yMod = driversCount % SCREEN_ROW_WINDOWS_COUNT;
        if (yMod == 0) {
            screenY_Row++;
        }

        return currentScreenY_Row * Y_POSITION_MOVE_SIZE / SCREEN_ROWS_COUNT;
    }

    String getTitle() {
        return driver.getTitle()
    }


    String requireTitle(String title = "") {
        int wait = defaultImplicitlyWait * 1000
         while (wait > 0){
            String pageTitle = getTitle()
            if(pageTitle.contains(title)){
                return pageTitle
            }
            wait -= HUNDRED_MILLI_SECONDS
            sleep(HUNDRED_MILLI_SECONDS)
        }
        throw new ScreenshotException("Required title ($title) was not found")
    }

    public void switchToFrame(String frameXpath) {
        driver.switchTo().defaultContent()
        requireVisibleXpath(frameXpath)
        WebElement frame = findElementByXpathOrId(frameXpath, true)
        driver.switchTo().frame(frame);
    }

    public void switchToSubFrame(String frameXpath) {
        WebElement frame = findElementByXpathOrId(frameXpath, true)
        driver.switchTo().frame(frame);
    }

    public void switchToDefault() {
        driver.switchTo().defaultContent()
    }

    public void requireXpath(String xpath) {
        findElementByXpathOrId(xpath, false)
    }


    public void requireXpath(String xpath, int changedImplicitlyWait) {
        driver.manage().timeouts().implicitlyWait(changedImplicitlyWait, TimeUnit.SECONDS)
        requireXpath(xpath)
        driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
    }

    public void requireVisibleXpath(String xpath) {
        WebElement webElement = findElementByXpathOrId(xpath, false)
        for (int second = 0; ; second++) {
            if (second >= 60) {
                throw new ScreenshotException("Required Xpath is not visible")
            }
            try {
                if (!webElement.isDisplayed()){
                    Thread.sleep(1000)
                    webElement = findElementByXpathOrId(xpath, false)
                } else {
                    break
                }
            } catch (Exception e) {
            }
        }
    }

    public void requireVisibleXpath(String xpath, int changedImplicitlyWait) {
        driver.manage().timeouts().implicitlyWait(changedImplicitlyWait, TimeUnit.SECONDS)
        requireVisibleXpath(xpath)
        driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
    }

    public boolean isDisplayed(String xpath, int changedImplicitlyWait) {
        driver.manage().timeouts().implicitlyWait(changedImplicitlyWait, TimeUnit.SECONDS)
        return isDisplayed(xpath)
        driver.manage().timeouts().implicitlyWait(this.defaultImplicitlyWait, TimeUnit.SECONDS)
    }


    public boolean isDisplayed(String xpath) {
        WebElement webElement = findElementByXpathOrId(xpath, true)
        if(webElement != null){
            return webElement.isDisplayed()
        }
        return false
    }

    void hover(String xpath) {
        Actions actions = new Actions(driver);
        WebElement menuHoverLink = findElementByXpathOrId(xpath, true);
        new Actions(driver).moveToElement(menuHoverLink).perform();
        actions.moveToElement(menuHoverLink);
    }

    void scrollIntoView(String xpath) {
        hover(xpath)
    }

    void close() {
        driver.close()
    }

    public boolean isAlertPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 2);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
        } catch (Exception e) {
            //exception handling
        }
    }

    public String getAlertText() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 2);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText()
            return alertText
        } catch (Exception e) {
            //exception handling
        }
    }

    public String getAlertTextAndAccept(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, 2);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText()
            alert.accept()
            return alertText
        } catch (Exception e) {
            //exception handling
        }
    }


}
