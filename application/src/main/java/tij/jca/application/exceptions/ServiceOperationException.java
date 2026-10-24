package tij.jca.application.exceptions;

/**
 * Indicates that an application service operation could not be completed.
 *
 * @since 0.1.0
 * @author TiJ
 */
public class ServiceOperationException extends ApplicationException {
    /**
     * Creates a service operation exception with the specified detail message and cause.
     *
     * @param message the detail message describing the failure
     * @param cause the underlying cause of the failure
     */
    public ServiceOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
