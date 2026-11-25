package lk.fitwell.api.common;

/** Thrown when the server holds a newer version of a record than the one being saved. */
public class ConflictException extends RuntimeException {
    private final transient Object current;

    public ConflictException(String message, Object current) {
        super(message);
        this.current = current;
    }

    public Object getCurrent() { return current; }
}
