package tij.jca.core.storage.migration;

import tij.jca.core.storage.IStorageEngine;
import tij.jca.core.storage.IStorageTransaction;
import tij.jca.core.storage.exceptions.MigrationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Applies pending schema migrations to a storage engine.
 *
 * <p>The migration runner is responsible for coordinating the full
 * migration lifecycle: retrieving migrations from an
 * {@link IMigrationProvider}, validating them, checking which versions have
 * already been applied through an {@link IMigrationStateStore}, and executing
 * the remaining migrations in order.</p>
 *
 * <p>Migrations are executed inside a storage transaction so that each schema
 * change and its tracking record remains atomic. A migration failure should
 * leave the database in a consistent state without partially applied schema
 * changes.</p>
 *
 * <p>Validation ensures migration versions are non-negative, descriptions are
 * present, and no duplicate version numbers exist. This prevents invalid or
 * conflicting migration definitions from being applied to the database.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class MigrationRunner {
    private final tij.jca.core.storage.migration.IMigrationProvider provider;
    private final IMigrationStateStore stateStore;

    /**
     * Creates a migration runner with the supplied migration provider and state
     * store.
     *
     * @param provider the source of migrations to execute
     * @param stateStore the component used to track applied versions
     * @throws NullPointerException if either argument is {@code null}
     */
    public MigrationRunner(tij.jca.core.storage.migration.IMigrationProvider provider, IMigrationStateStore stateStore) {
        this.provider = provider;
        this.stateStore = stateStore;
    }

    /**
     * Applies all pending migrations for the supplied storage engine.
     *
     * <p>The method loads all available migrations, validates them, and
     * executes only those whose version has not yet been recorded as applied.
     * Once a migration is executed successfully, its version is persisted by the
     * state store before the transaction is committed.</p>
     *
     * @param storageEngine the storage engine to migrate
     * @throws MigrationException if the migration list is invalid or a migration
     *                           cannot be applied
     */
    public void migrate(IStorageEngine storageEngine) {
        List<IMigration> migrations = provider.getMigrations();

        validateMigrations(migrations);

        Set<Integer> appliedVersions = new HashSet<>(stateStore.getAppliedVersions(storageEngine));

        for (IMigration migration : migrations) {
            if (appliedVersions.contains(migration.version())) {
                continue;
            }

            applyMigration(storageEngine, migration);
        }
    }

    /**
     * Executes a single migration inside a transaction.
     *
     * <p>The migration is applied within a new transaction created from the
     * storage engine. If the migration succeeds, the state store records the
     * applied version and the transaction is committed. Any runtime failure
     * during the migration is wrapped in a {@link MigrationException}.</p>
     *
     * @param storageEngine the storage engine that owns the transaction
     * @param migration the migration to apply
     * @throws MigrationException if the migration fails
     */
    private void applyMigration(IStorageEngine storageEngine, IMigration migration) {
        try (IStorageTransaction transaction = storageEngine.beginTransaction()) {
            migration.apply(transaction);

            stateStore.recordApplied(transaction, migration);

            transaction.commit();
        } catch (MigrationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new MigrationException(
                    "Failed to apply migration "
                            + migration.version()
                            + ": "
                            + migration.description(),
                    e
            );
        }
    }

    /**
     * Validates the supplied migration list before execution.
     *
     * <p>Validation guarantees:
     * <ul>
     *   <li>no migration version is negative</li>
     *   <li>every migration has a non-blank description</li>
     *   <li>no duplicate migration versions exist</li>
     * </ul>
     *
     * @param migrations the migrations to validate
     * @throws MigrationException if a migration definition is invalid
     */
    private void validateMigrations(List<IMigration> migrations) {
        Set<Integer> versions = new HashSet<>();

        for (IMigration migration : migrations) {
            if (migration.version() < 0) {
                throw new MigrationException(
                        "Migration version must not be negative: "
                            + migration.version()
                );
            }

            if (migration.description() == null || migration.description().isBlank()) {
                throw new MigrationException(
                        "Migration " + migration.version()
                            + " has no description"
                );
            }

            if (!versions.add(migration.version())) {
                throw new MigrationException(
                        "Duplicate migration version: "
                            + migration.version()
                );
            }
        }
    }
}
