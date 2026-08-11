package tij.jca.core.storage.exceptions;

/**
 * Thrown when an attempt is made to create or store an entity that would
 * result in a duplicate entry.
 *
 * <p>This exception typically indicates that a unique constraint or
 * equivalent storage invariant has been violated.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public class DuplicateEntryException extends StorageException {
    /**
     * Creates a duplicate-entry exception with the specified detail message
     *
     * @param message the detail message describing the duplicate entry
     */
    public DuplicateEntryException(String message) {
        super(message);
    }
}
