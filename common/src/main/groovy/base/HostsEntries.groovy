package base

import dtos.SettingsHelper
import org.apache.log4j.Logger

public class HostsEntries {
    private final static Logger log = Logger.getLogger("HoEn ")

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
                def splittedLines
                try {
                    def allLines = new File(url.toURI()).readLines()

                    //Get rid of empty lines and lines starting with #
                    def validLines = allLines.collect{String row ->
                        def rowCleaned = row.replaceAll(/\t/, " ").replaceAll("^[ ]*", "")
                        if(!rowCleaned.startsWith("#") && !rowCleaned.isEmpty() && rowCleaned.isEmpty() != null){
                            rowCleaned
                        }
                    }

                    org.testng.Reporter.log("<BR>")
                    org.testng.Reporter.log(">>>hostsEntries")

                    //clean each row and keep only ket, value
                    splittedLines = validLines.findAll{it != null}.unique().collect{it.replaceAll("\t", " ").
                            replaceFirst(" ", FIRST_SPACE).
                            replaceAll(/${FIRST_SPACE}[ ]*/, FIRST_SPACE).
                            replaceFirst(" .*", "").
                            split(FIRST_SPACE)}


                    settings.hostsEntries  = splittedLines.collect{it[1] + ":" + it[0] }

                    log.info("settings.hostsEntries:$settings.hostsEntries>")
                    org.testng.Reporter.log(settings.hostsEntries.toString())
                    org.testng.Reporter.log("hostsEntries>>>")
                    org.testng.Reporter.log("<BR>")
                    org.testng.Reporter.log("<BR>")
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    org.testng.Reporter.log("Can't split entries:$splittedLines>")
                    log.error("Can't split entries:$splittedLines>")
                    throw e
                } catch (Exception e) {
                    log.error("splittedLines:$splittedLines>")
                    org.testng.Reporter.log("Exception:$e>")
                    log.error("Exception:$e>")
                    throw e
                }
            }
        }
    }
}
