package tij.jca.core.storage.exceptions;

/**
 * Base exception for errors occurring within the storage layer.
 *
 * <p>This exceptions represents failures that are specific to storage
 * operations and allows callers to handle storage-related errors without
 * depending on a concrete storage implementation.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public class StorageException extends RuntimeException {
    /**
     * Creates a storage exception with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates a storage exception with the specified detail message and cause
     *
     * @param message the detail message describing the error
     * @param cause the underlying cause of the error
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
