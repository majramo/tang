package exceptions;

public class TangFileException extends TangSkipException {


    public TangFileException(String message) {

        super(message);
    }

    public TangFileException(String message, Throwable cause) {

        super(message, cause);
    }
}
