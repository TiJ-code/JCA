package tij.jca.infrastructure.storage.h2;

import org.h2.jdbcx.JdbcDataSource;
import tij.jca.core.storage.StorageEngine;
import tij.jca.core.storage.StorageTransaction;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class H2StorageEngine implements StorageEngine {
    private final H2ConnectionProvider connectionProvider;

    private boolean open;

    public H2StorageEngine(Path databasePath) {
        this(databasePath, "sa", "");
    }

    public H2StorageEngine(Path databasePath, String username, String password) {
        Objects.requireNonNull(databasePath, "databasePath");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");

        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL(
                "jdbc:h2:file:" + databasePath.toAbsolutePath()
        );
        dataSource.setUser(username);
        dataSource.setPassword(password);

        this.connectionProvider = new H2ConnectionProvider(dataSource);
    }

    @Override
    public void open() {
        if (open) {
            return;
        }

        try (Connection connection = connectionProvider.getConnection()) {
            connection.isValid(2);
            open = true;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to open H2 storage engine.",
                    e
            );
        }
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public StorageTransaction beginTransaction() {
        ensureOpen();

        Connection connection = connectionProvider.getConnection();

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            closeQuietly(connection);

            throw new IllegalStateException(
                    "Failed to begin H2 transaction.",
                    e
            );
        }

        return new H2StorageTransaction(connection);
    }

    @Override
    public void close() {
        open = false;
    }

    private void ensureOpen() {
        if (!open) {
            throw new IllegalStateException("H2 storage engine is not open.");
        }
    }

    private static void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException _) {}
    }
}
