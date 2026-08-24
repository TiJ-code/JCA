package tij.jca.core.storage.exceptions;

/**
 * Thrown when a database migration cannot be validated or applied.
 *
 * <p>This exception wraps migration-level failures that occur before or during
 * schema changes, such as invalid migration definitions, duplicate versions,
 * or runtime errors while applying a migration.</p>
 *
 * <p>It extends {@link RuntimeException} so callers do not need to declare
 * checked exceptions when working with storage migrations.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public class MigrationException extends RuntimeException {
    /**
     * Creates a migration exception with the specified detail message.
     *
     * @param message the explanation of the migration failure
     */
    public MigrationException(String message) {
        super(message);
    }

    /**
     * Creates a migration exception with the specified detail message and cause.
     *
     * @param message the explanation of the migration failure
     * @param cause the underlying cause of the migration failure
     */
    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
