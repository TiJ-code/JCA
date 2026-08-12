package tij.jca.infrastructure.storage.h2;

import tij.jca.core.storage.StorageTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class H2StorageTransaction implements StorageTransaction {
    private final Connection connection;

    private boolean completed;
    private boolean closed;

    H2StorageTransaction(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    Connection connection() {
        ensureOpen();
        return connection;
    }

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

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException(
                    "H2 transaction is closed"
            );
        }
    }

    private void ensureNotCompleted() {
        if (completed) {
            throw new IllegalStateException(
                    "H2 transaction has already been completed"
            );
        }
    }
}
