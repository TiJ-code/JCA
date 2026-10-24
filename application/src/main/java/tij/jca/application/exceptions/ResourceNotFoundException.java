package tij.jca.application.exceptions;

/**
 * Indicates that a resource required by an application service operation does not exist.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class ResourceNotFoundException extends ApplicationException {
    /**
     * Creates a resource-not-found exception with the specified detail message.
     *
     * @param message the detail message describing the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a resource-not-found exception with the specified detail message and cause.
     *
     * @param message the detail message describing the missing resource
     * @param cause the underlying cause of the failure
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
