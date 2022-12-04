package base

import dtos.SettingsHelper
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager

public class HostsEntries {
    private final static Logger log = LogManager.getLogger("HoEn ")

    /*
    Get rid of everything after "#"
    split lines and make entry key:values
    add entries to settings
     */
    public HostsEntries(browser, environment){
        def FIRST_SPACE = "___FIRST_SPACE___"

        //read host files and add to settings
        SettingsHelper settingsHelper = SettingsHelper.getInstance()
        def settings = settingsHelper.settings
        settings.hosts = []

        if(browser.contains("REMOTE_") || browser.contains("SELENOID_")) {
            def envHostsFile = environment.replaceAll("_env", "")
            URL url = this.class.getResource("/hosts/hosts-${envHostsFile}.txt");
            if (url != null) {
                try {
                    def allLines = new File(url.toURI()).readLines()

                    //Get rid of empty lines and data after # sign
                    def validLines = []
                    allLines.each{String row ->
                        def rowCleaned = row.replaceAll("#.*", "")
                        rowCleaned = rowCleaned.replaceAll(/\s+/, " ").replaceAll("^[ ]*", "")
                        if(!rowCleaned.isEmpty() && rowCleaned != null){
                            validLines.add(rowCleaned)
                        }
                    }

                    def entries = []
                    validLines.each{
                        def splittedLine = it.split(" ")
                        def ip = splittedLine[0]
                        if(splittedLine.size()<2){
                         log.error("Line must have a dns entry:$splittedLine>")
                        }
                        splittedLine[1..splittedLine.size()-1].each {
                         entries.add(it + ":"  + ip)
                        }
                    }

                    org.testng.Reporter.log("<BR>")
                    org.testng.Reporter.log(">>>hostsEntries")


                    settings.hostsEntries  = entries.unique()

                    log.info("settings.hostsEntries:$settings.hostsEntries>")
                    org.testng.Reporter.log(settings.hostsEntries.toString())
                    org.testng.Reporter.log("hostsEntries>>>")
                    org.testng.Reporter.log("<BR>")
                    org.testng.Reporter.log("<BR>")
                } catch (Exception e) {
                    org.testng.Reporter.log("url:$url>")
                    log.error("url:$url>")
                    org.testng.Reporter.log("Exception:$e>")
                    log.error("Exception:$e>")
                    throw e
                }
            }
        }
    }
}
