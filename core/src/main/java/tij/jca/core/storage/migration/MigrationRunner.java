package tij.jca.core.storage.migration;

import tij.jca.core.storage.IStorageEngine;
import tij.jca.core.storage.IStorageTransaction;
import tij.jca.core.storage.exceptions.MigrationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MigrationRunner {
    private final tij.jca.core.storage.migration.IMigrationProvider provider;
    private final IMigrationStateStore stateStore;

    public MigrationRunner(tij.jca.core.storage.migration.IMigrationProvider provider, IMigrationStateStore stateStore) {
        this.provider = provider;
        this.stateStore = stateStore;
    }

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
