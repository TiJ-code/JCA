package tij.jca.core.storage.migration;

import tij.jca.core.storage.IStorageTransaction;

/**
 * Represents a single, versioned database migration.
 *
 * <p>A migration describes one atomic change to the storage schema.
 * Implementations must expose a unique version number and a human-readable
 * description so the migration system can track, validate, and report the
 * schema evolution lifecycle.</p>
 *
 * <p>Each migration is executed inside a storage transaction. If the migration
 * fails, the transaction should be rolled back so the database remains in a
 * consistent state.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IMigration {
    /**
     * Returns the migration version.
     *
     * <p>Versions are used to determine which migrations are pending and to
     * prevent duplicate or conflicting schema changes.</p>
     *
     * @return the migration version number
     */
    int version();

    /**
     * Returns a short description of what this migration changes.
     *
     * @return a human-readable migration description
     */
    String description();

    /**
     * Applies this migration to the supplied storage transaction.
     *
     * <p>Implementations should perform all required schema changes within
     * the provided transaction and must not assume they are running outside a
     * transaction boundary.</p>
     *
     * @param transaction the transaction in which the schema change is applied
     */
    void apply(IStorageTransaction transaction);
}
