package tij.jca.core.storage.exceptions;

/**
 * Thrown when a requested entity cannot be found in storage.
 *
 * <p>This exception indicates that a storage operation expected an entity
 * to exist, but no matching entity was found.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public class EntityNotFoundException extends StorageException {
    /**
     * Creates an entity-not-found exception with the specified detail message.
     *
     * @param message the detail message describing the error
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
