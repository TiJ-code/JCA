package tij.jca.infrastructure.storage.h2;

import tij.jca.core.storage.IStorageTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * H2-backed implementation of a storage transaction.
 *
 * <p>The transaction wraps a JDBC {@link Connection} with auto-commit
 * disabled. A transaction must be completed by either {@link #commit()} or
 * {@link #rollback()}; closing an incomplete transaction rolls it back before
 * releasing the connection.</p>
 *
 * <p>Once completed or closed, the transaction cannot be used for further
 * operations. JDBC failures are reported as {@link IllegalStateException}s.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class H2StorageTransaction implements IStorageTransaction {
    private final Connection connection;

    private boolean completed;
    private boolean closed;

    /**
     * Creates a transaction backed by the supplied JDBC connection.
     *
     * @param connection the connection associated with this transaction
     * @throws NullPointerException if {@code connection} is {@code null}
     */
    H2StorageTransaction(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    /**
     * Returns the JDBC connection used by this transaction.
     *
     * <p>The connection remains available until the transaction is closed.
     * Callers should use this connection for all database operations that
     * belong to the transaction.</p>
     *
     * @return the transaction's JDBC connection
     * @throws IllegalStateException if the transaction is closed
     */
    public Connection connection() {
        ensureOpen();
        return connection;
    }

    /**
     * Commits the JDBC transaction.
     *
     * <p>A committed transaction cannot be committed or rolled back again.</p>
     *
     * @throws IllegalStateException if this transaction is closed, has
     *                               already been completed, or the commit fails
     */
    @Override
    public void commit() {
        ensureOpen();
        ensureNotCompleted();

        try {
            connection.commit();
            completed = true;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to commit H2 transaction.",
                    e
            );
        }
    }

    /**
     * Rolls back the JDBC transaction.
     *
     * <p>A rolled-back transaction cannot be committed or rolled back again.</p>
     *
     * @throws IllegalStateException if this transaction is closed, has
     *                               already been completed, or the rollback fails
     */
    @Override
    public void rollback() {
        ensureOpen();
        ensureNotCompleted();

        try {
            connection.rollback();
            completed = true;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to roll back H2 transaction.",
                    e
            );
        }
    }

    /**
     * Rolls back an incomplete transaction and closes its JDBC connection.
     *
     * <p>Closing an already closed transaction has no effect.</p>
     *
     * @throws IllegalStateException if rollback or connection closure fails
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }

        try {
            if (!completed) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to roll back H2 transaction during close.",
                    e
            );
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Failed to close H2 transaction.",
                        e
                );
            } finally {
                closed = true;
            }
        }
    }

    /**
     * Ensures that this transaction has not been closed.
     *
     * @throws IllegalStateException if this transaction is closed
     */
    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException(
                    "H2 transaction is closed"
            );
        }
    }

    /**
     * Ensures that this transaction has not already been completed.
     *
     * @throws IllegalStateException if this transaction was committed or
     *                               rolled back
     */
    private void ensureNotCompleted() {
        if (completed) {
            throw new IllegalStateException(
                    "H2 transaction has already been completed"
            );
        }
    }
}
