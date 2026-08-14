package tij.jca.core.events.exceptions;

/**
 * Base exception for errors occurring within the event pipeline
 * <p>
 * This exception represents failures that occur on the event bus.
 * </p>
 *
 * @author Jakob
 * @since 0.1.0
 */
public class EventException extends RuntimeException {

    /**
     * Creates an Event exception with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public EventException(String message) {
        super(message);
    }
    /**
     * Creates an event exception with the specified detail message and cause
     *
     * @param message the detail message describing the error
     * @param cause the underlying cause of the error
     */
    public EventException(String message, Throwable cause) {
        super(message, cause);
    }
}