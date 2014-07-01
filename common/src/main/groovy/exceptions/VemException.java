package exceptions;

public class VemException extends RuntimeException {

    private static final long serialVersionUID = -7493144027151780395L;

    public VemException(String message) {
        super(message);
    }

    public VemException(String message, Throwable cause) {
        super(message, cause);
    }

    public VemException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
