package tij.jca.infrastructure.storage.h2.migration;

import tij.jca.core.storage.IStorageTransaction;
import tij.jca.core.storage.exceptions.MigrationException;
import tij.jca.core.storage.migration.IMigration;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * SQL migration implementation for H2.
 *
 * @param version migration version
 * @param description human-readable migration description
 * @param sql SQL statement(s) to execute
 * @since 0.1.0
 * @author TiJ
 */
public record H2SQLMigration(int version, String description, String sql) implements IMigration {

    /**
     * Validates the migration version, description, and SQL.
     *
     * @throws IllegalArgumentException if the version is negative or the
     *                                  description or SQL is blank
     */
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

    /**
     * Executes this migration within the supplied H2 transaction.
     *
     * @param transaction transaction in which to execute the migration
     * @throws NullPointerException if {@code transaction} is {@code null}
     * @throws MigrationException if the transaction is not H2-backed or SQL
     *                            execution fails
     */
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
