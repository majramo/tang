package corebase

import org.apache.log4j.Logger;


public class Then extends GivenWhenThen{
	protected final static Logger log = Logger.getLogger("Then ")
	
	public Then(driver){
		super(driver)
	}


}
