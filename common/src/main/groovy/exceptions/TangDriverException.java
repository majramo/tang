package exceptions;

public class TangDriverException extends TangSkipException {


    public TangDriverException(String message) {

        super(message);
    }

    public TangDriverException(String message, Throwable cause) {

        super(message, cause);
    }
}
