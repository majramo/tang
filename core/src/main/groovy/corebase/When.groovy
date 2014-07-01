package corebase

import org.apache.log4j.Logger;

public class When extends GivenWhenThen{
	protected final static Logger log = Logger.getLogger("When ")

	public When(driver){
		super(driver)
	}
}
