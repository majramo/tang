package dtos

import org.apache.log4j.Logger
import org.testng.SkipException

public class SettingsHelper {

    public static settings
    private static timeStamp
    public static applicationConf = null
    private final static Logger log = Logger.getLogger("StH  ")
    private static final INSTANCE = new SettingsHelper()
    private static int count = 1
    static getInstance() {
        log.debug ("## Count <" + count++ +">")
        return INSTANCE }

    private SettingsHelper() {
        def settingsFile = "/configFiles/settings.groovy"
        log.info "reading settingsFile <$settingsFile>"
        InputStream inputStream = this.getClass().getResourceAsStream(settingsFile);
        String content = inputStream.text
        settings = new ConfigSlurper().parse(content)
        if (settings == null) {
            throw new SkipException("Settings is null")
        }
        settings.configFiles.each {
            String configFile = '/configFiles/' + it.value
            log.info "reading configFile <$configFile>"
            inputStream = this.getClass().getResourceAsStream(configFile);
            content = inputStream.text
            if (applicationConf == null) {
                applicationConf = new ConfigSlurper().parse(content)
            } else {
                applicationConf.merge(new ConfigSlurper().parse(content))
            }
        }
        timeStamp = new Date().format("$settings.TimeStampFormat")
    }
}
