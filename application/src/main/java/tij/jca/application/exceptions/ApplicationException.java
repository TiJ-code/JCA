package tij.jca.application.exceptions;

/**
 * Base exception for failures exposed by the application service layer.
 *
 * @since 0.1.0
 * @author TiJ
 */
public class ApplicationException extends RuntimeException {
    /**
     * Creates an application exception with the specified detail message.
     *
     * @param message the detail message describing the failure
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Creates an application exception with the specified detail message and cause.
     *
     * @param message the detail message describing the failure
     * @param cause the underlying cause of the failure
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
