package tij.jca.infrastructure.storage.h2.migration;

import tij.jca.core.storage.IStorageEngine;
import tij.jca.core.storage.IStorageTransaction;
import tij.jca.core.storage.exceptions.MigrationException;
import tij.jca.core.storage.migration.IMigration;
import tij.jca.core.storage.migration.IMigrationStateStore;
import tij.jca.infrastructure.storage.h2.H2DatabaseConstants;
import tij.jca.infrastructure.storage.h2.H2StorageEngine;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;
import tij.jca.infrastructure.storage.h2.helper.SQLBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class H2MigrationStateStore implements IMigrationStateStore {
    private static final String STATEMENT__CREATE_SCHEMA_MIGRATION_TABLE =
            SQLBuilder.createTable(H2DatabaseConstants.TABLE__JCA_SCHEMA_MIGRATIONS)
                    .ifNotExists()
                    .column(H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION, "INT")
                        .primaryKey()
                    .column(H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION, "VARCHAR(255)")
                        .notNull()
                    .column(H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__APPLIED_AT, "TIMESTAMP")
                        .notNull().defaultValue("CURRENT_TIMESTAMP")
                    .build();

    private static final String STATEMENT__SELECT_VERSIONS_FROM_SCHEMA_MIGRATION_TABLE =
            SQLBuilder.select(H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION)
                    .from(H2DatabaseConstants.TABLE__JCA_SCHEMA_MIGRATIONS)
                    .orderBy(H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION)
                    .build();

    private static final String STATEMENT__INSERT_SCHEMA_MIGRATION =
            SQLBuilder.insertInto(H2DatabaseConstants.TABLE__JCA_SCHEMA_MIGRATIONS)
                    .columns(
                            H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION,
                            H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION
                    )
                    .values("?", "?")
                    .build();

    @Override
    public List<Integer> getAppliedVersions(IStorageEngine storageEngine) {
        Objects.requireNonNull(storageEngine, "storageEngine");

        if (!(storageEngine instanceof H2StorageEngine h2Engine)) {
            throw new MigrationException(
                    "H2 migration state store requires an H2 storage engine."
            );
        }

        ensureTableExists(h2Engine);

        try (IStorageTransaction transaction = h2Engine.beginTransaction()) {
            H2StorageTransaction h2Transaction = requireH2Transaction(transaction);

            Connection connection = h2Transaction.connection();

            List<Integer> versions = new ArrayList<>();

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(STATEMENT__SELECT_VERSIONS_FROM_SCHEMA_MIGRATION_TABLE)) {
                while (resultSet.next()) {
                    versions.add(
                            resultSet.getInt(H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION)
                    );
                }
            } catch (SQLException e) {
                throw new MigrationException(
                        "Failed to read applied H2 migrations.",
                        e
                );
            }

            transaction.rollback();

            return List.copyOf(versions);
        }
    }

    @Override
    public void recordApplied(IStorageTransaction transaction, IMigration migration) {
        Objects.requireNonNull(transaction, "transaction");

        Objects.requireNonNull(migration, "migration");

        H2StorageTransaction h2Transaction = requireH2Transaction(transaction);

        Connection connection = h2Transaction.connection();

        try (PreparedStatement statement = connection.prepareStatement(STATEMENT__INSERT_SCHEMA_MIGRATION)) {
            statement.setInt(
                    1, migration.version()
            );

            statement.setString(
                    2, migration.description()
            );

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new MigrationException(
                    "Failed to record applied H2 migration.",
                    e
            );
        }
    }

    private void ensureTableExists(H2StorageEngine storageEngine) {
        try (IStorageTransaction transaction = storageEngine.beginTransaction()) {
            H2StorageTransaction h2Transaction = requireH2Transaction(transaction);

            Connection connection = h2Transaction.connection();

            try (Statement statement = connection.createStatement()) {
                statement.execute(STATEMENT__CREATE_SCHEMA_MIGRATION_TABLE);
                transaction.commit();
            } catch (SQLException e) {
                throw new MigrationException(
                        "Failed to initialise H2 migration state table.",
                        e
                );
            }
        }
    }

    private static H2StorageTransaction requireH2Transaction(IStorageTransaction transaction) {
        if (!(transaction instanceof H2StorageTransaction h2Transaction)) {
            throw new MigrationException(
                    "H2 migration state store requires an H2 storage transaction."
            );
        }

        return h2Transaction;
    }
}
