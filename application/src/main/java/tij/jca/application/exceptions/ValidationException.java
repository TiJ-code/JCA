package tij.jca.application.exceptions;

/**
 * Indicates that a value supplied to the application service layer is invalid.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class ValidationException extends ApplicationException {
    /**
     * Creates a validation exception with the specified detail message.
     *
     * @param message the detail message describing the invalid value
     */
    public ValidationException(String message) {
        super(message);
    }
}
