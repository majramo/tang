package corebase

import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Created with IntelliJ IDEA.
 * User: majidaram
 * Date: 2013-04-27
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class DriverType {
    public final static DesiredCapabilities Firefox = DesiredCapabilities.firefox()
    public final static DesiredCapabilities Ff = Firefox
    public final static DesiredCapabilities FfBrowser = Firefox
    public final static DesiredCapabilities FfRemote = Firefox
    public final static DesiredCapabilities FfRemoteMac = Firefox
    public final static DesiredCapabilities FfRemoteWindows = Firefox
    public final static DesiredCapabilities FfRemoteWindowsXp = Firefox
    public final static DesiredCapabilities FfRemoteVista = Firefox
    public final static DesiredCapabilities FfRemoteWindows8 = Firefox
    public final static DesiredCapabilities FfRemoteLinux = Firefox

    public final static DesiredCapabilities Safari = DesiredCapabilities.safari()
    public final static DesiredCapabilities Sa = Safari
    public final static DesiredCapabilities SaBrowser = Safari
    public final static DesiredCapabilities SaRemoteMac = Safari


    public final static DesiredCapabilities InternetExplorer = DesiredCapabilities.internetExplorer().setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true)
    public final static DesiredCapabilities IE = InternetExplorer
    public final static DesiredCapabilities IE_BROWSER = InternetExplorer

    public final static DesiredCapabilities Chrome = DesiredCapabilities.chrome()
    public final static DesiredCapabilities Ch = Chrome
    public final static DesiredCapabilities ChBrowser = Chrome
    public final static DesiredCapabilities ChRemote = Chrome
    public final static DesiredCapabilities ChRemoteMac = Chrome
    public final static DesiredCapabilities ChRemoteWindows = Chrome
    public final static DesiredCapabilities ChRemoteWindowsXp = Chrome
    public final static DesiredCapabilities ChRemoteVista = Chrome
    public final static DesiredCapabilities ChRemoteWindows8 = Chrome
    public final static DesiredCapabilities ChRemoteLinux = Chrome


    public final static DesiredCapabilities HtmlUnit = DesiredCapabilities.htmlUnit().setJavascriptEnabled(true)
    public final static DesiredCapabilities HU = HtmlUnit
    public final static DesiredCapabilities HU_BROWSER = HtmlUnit
    public final static DesiredCapabilities HtmlUnitWithJs = HtmlUnit
    public final static DesiredCapabilities HtmlUnitWithoutJS = DesiredCapabilities.htmlUnit().setJavascriptEnabled(false)

}
