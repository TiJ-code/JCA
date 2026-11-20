package tij.jca.infrastructure.storage.h2.repository;

import tij.jca.core.storage.exceptions.DuplicateEntryException;
import tij.jca.core.storage.exceptions.EntityNotFoundException;
import tij.jca.core.storage.exceptions.StorageException;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Base repository implementation for H2-backed persistence.
 *
 * <p>
 * Centralises the common SQL execution and transaction helpers used by concrete
 * repository implementations. Subclasses can focus on entity-specific queries
 * while reusing consistent connection handling, duplicate-key translation, and
 * not-found helpers.
 * </p>
 *
 * @since 0.1.0
 * @author TiJ
 */
abstract class AbstractH2Repository {
    protected final H2StorageTransaction transaction;

    /**
     * Creates a repository bound to the provided transaction context.
     *
     * @param transaction the active H2 storage transaction
     * @throws NullPointerException if {@code transaction} is {@code null}
     */
    protected AbstractH2Repository(H2StorageTransaction transaction) {
        this.transaction = Objects.requireNonNull(transaction, "transaction");
    }

    /**
     * Prepares a SQL statement using the active transaction connection.
     *
     * @param sql the SQL statement to prepare
     * @return the prepared statement instance
     * @throws StorageException if the statement cannot be created
     */
    protected PreparedStatement prepare(String sql) {
        try {
            return transaction.connection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new StorageException("Failed to prepare SQL statement.", e);
        }
    }

    /**
     * Executes an update statement using the supplied parameter setter.
     *
     * @param sql the SQL statement to execute
     * @param setter the callback used to bind parameters onto the statement
     * @throws StorageException if the update fails or a database-specific storage
     *                          exception needs to be translated
     */
    protected void executeUpdate(String sql, SQLParameterSetter setter) {
        try (PreparedStatement statement = prepare(sql)) {
            setter.set(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw translate(e);
        }
    }

    /**
     * Executes a SELECT query that checks whether at least one row matches.
     *
     * @param sql the query to execute
     * @param setter the callback used to bind parameters onto the statement
     * @return {@code true} if the query returns at least one row, otherwise {@code false}
     * @throws StorageException if the query cannot be executed
     */
    protected boolean exists(String sql, SQLParameterSetter setter) {
        try (PreparedStatement statement = prepare(sql)) {
            setter.set(statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to query storage.", e);
        }
    }

    /**
     * Translates low-level SQL exceptions into domain-specific storage exceptions.
     *
     * <p>
     * Duplicate key violations are mapped to {@link DuplicateEntryException},
     * while all other SQL failures are wrapped in a generic {@link StorageException}.
     * </p>
     *
     * @param exception the SQL exception to translate
     * @return the translated storage exception
     */
    protected StorageException translate(SQLException exception) {
        if (exception.getErrorCode() == 23505) {
            return new DuplicateEntryException(exception.getMessage());
        }

        return new StorageException("H2 repository operation failed.", exception);
    }

    /**
     * Creates an entity-not-found exception for a missing domain object.
     *
     * @param entity the human-readable entity name
     * @param id the missing entity identifier
     * @return a configured {@link EntityNotFoundException}
     */
    protected EntityNotFoundException notFound(String entity, Object id) {
        return new EntityNotFoundException(entity + " with ID " + id + " not found.");
    }

    /**
     * Binds SQL parameters onto a prepared statement.
     */
    @FunctionalInterface
    protected interface SQLParameterSetter {
        /**
         * Sets the parameters required by the SQL statement.
         *
         * @param statement the prepared statement to populate
         * @throws SQLException if parameter binding fails
         */
        void set(PreparedStatement statement) throws SQLException;
    }
}
