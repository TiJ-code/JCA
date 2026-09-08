package tij.jca.core.storage.migration;

import tij.jca.core.storage.IStorageEngine;
import tij.jca.core.storage.IStorageTransaction;

import java.util.List;

/**
 * Tracks which migrations have already been applied to a storage engine.
 *
 * <p>The state store is responsible for persisting migration metadata in a
 * storage-specific way. It allows the migration runner to determine whether a
 * migration has already been executed and avoid reapplying it.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IMigrationStateStore {
    /**
     * Returns the versions of migrations already applied for the supplied
     * storage engine.
     *
     * @param storageEngine the storage engine whose applied migrations are
     *                      requested
     * @return the list of version numbers that have already been applied
     */
    List<Integer> getAppliedVersions(IStorageEngine storageEngine);

    /**
     * Records that a migration has been applied.
     *
     * <p>The record should happen within the same transaction that performed
     * the migration so that the schema change and the migration bookkeeping
     * remain atomic.</p>
     *
     * @param transaction the active transaction
     * @param migration the migration that was applied
     */
    void recordApplied(IStorageTransaction transaction, IMigration migration);
}
