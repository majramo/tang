package exceptions;

public class TangDbConnectionException extends TangSkipException {


    public TangDbConnectionException(String message) {

        super(message);
    }

    public TangDbConnectionException(String message, Throwable cause) {

        super(message, cause);
    }
}
