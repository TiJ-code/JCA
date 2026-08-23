package tij.jca.infrastructure.storage.h2.migration;

import tij.jca.core.storage.IStorageTransaction;
import tij.jca.core.storage.exceptions.MigrationException;
import tij.jca.core.storage.migration.IMigration;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public record H2SQLMigration(int version, String description, String sql) implements IMigration {

    public H2SQLMigration {
        if (version < 0) {
            throw new IllegalArgumentException("Migration version must not be negative.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Migration description must not be blank.");
        }

        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("Migration SQL must not be blank.");
        }
    }

    @Override
    public void apply(IStorageTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");

        if (!(transaction instanceof H2StorageTransaction h2Transaction)) {
            throw new MigrationException(
                    "H2 SQL mgiration requires an H2 storage transaction."
            );
        }

        Connection connection = h2Transaction.connection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new MigrationException(
                    "Failed to apply H2 migration "
                    + version
                    + " ("
                    + description
                    + ").",
                    e
            );
        }
    }
}
