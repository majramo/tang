package corebase

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Then extends GivenWhenThen{
	protected final static Logger log = LogManager.getLogger("Then ")
	
	public Then(driver){
		super(driver)
	}


}
