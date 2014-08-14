package exceptions;

public class TangException extends RuntimeException {

    private static final long serialVersionUID = -7493144027151780395L;

    public TangException(String message) {
        super(message);
    }

    public TangException(String message, Throwable cause) {
        super(message, cause);
    }

    public TangException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
