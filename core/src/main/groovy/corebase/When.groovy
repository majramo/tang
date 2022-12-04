package corebase

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class When extends GivenWhenThen{
	protected final static Logger log = LogManager.getLogger("When ")

	public When(driver){
		super(driver)
	}
}
