package tij.jca.application.exceptions;

/**
 * Indicates that a requested operation conflicts with existing application state.
 *
 * @since 0.1.0
 * @author TiJ
 */
public class ConflictException extends ApplicationException {
    /**
     * Creates a conflict exception with the specified detail message.
     *
     * @param message the detail message describing the conflict
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Creates a conflict exception with the specified detail message and cause.
     *
     * @param message the detail message describing the conflict
     * @param cause the underlying cause of the conflict
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
