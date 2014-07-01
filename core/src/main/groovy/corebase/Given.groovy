package corebase

import org.apache.log4j.Logger

class Given extends GivenWhenThen{
	protected final static Logger log = Logger.getLogger("Given")
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
