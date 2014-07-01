package exceptions;

import org.testng.Reporter;
import org.testng.SkipException;

public class TangSkipException extends SkipException {

    private static final long serialVersionUID = -1232313123111L;

    public TangSkipException(String message) {
        super(message);
        Reporter.log(message);

    }

    public TangSkipException(String message, Throwable cause) {
        super(message, cause);
        Reporter.log(message + cause);
    }
}
