package corebase

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager

class Given extends GivenWhenThen{
	protected final static Logger log = LogManager.getLogger("Given")
	protected settings
	protected applicationConf

	public Given(driver){
		super(driver)
	}

    public Given(driver, settings, applicationConf){
        super(driver)
        this.settings = settings
        this.applicationConf = applicationConf
    }

}
