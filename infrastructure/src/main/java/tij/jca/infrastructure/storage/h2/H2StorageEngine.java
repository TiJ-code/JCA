package tij.jca.infrastructure.storage.h2;

import org.h2.jdbcx.JdbcDataSource;
import tij.jca.core.storage.IStorageEngine;
import tij.jca.core.storage.IStorageTransaction;
import tij.jca.core.storage.migration.MigrationRunner;
import tij.jca.infrastructure.storage.h2.migration.H2MigrationProvider;
import tij.jca.infrastructure.storage.h2.migration.H2MigrationStateStore;
import tij.jca.logging.ILogger;
import tij.jca.logging.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * H2-backed implementation of the JCA storage engine.
 *
 * <p>The engine manages file-based H2 connectivity and runs configured
 * database migrations when opened.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public class H2StorageEngine implements IStorageEngine {
    private static final ILogger LOGGER = LoggerFactory.getLogger(H2StorageEngine.class);

    private final H2ConnectionProvider connectionProvider;
    private final MigrationRunner migrationRunner;

    private boolean open;

    /**
     * Creates an H2 storage engine using the default {@code sa} user and
     * an empty password.
     *
     * @param databasePath path to the H2 database
     */
    public H2StorageEngine(Path databasePath) {
        this(databasePath, "sa", "");
    }

    /**
     * Creates an H2 storage engine with the supplied credentials.
     *
     * @param databasePath path to the H2 database
     * @param username database user name
     * @param password database password
     * @throws NullPointerException if any argument is {@code null}
     */
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
        this.migrationRunner = new MigrationRunner(
                new H2MigrationProvider("db/h2", "db/h2/migrations.list"),
                new H2MigrationStateStore()
        );
    }

    /**
     * Opens the H2 database and applies pending migrations.
     *
     * @throws IllegalStateException if the database cannot be opened or
     *                               migrations cannot be applied
     */
    @Override
    public void open() {
        if (open) {
            LOGGER.debug("H2 storage engine is already open.");
            return;
        }

        LOGGER.info("Opening H2 storage engine.");

        try (Connection connection = connectionProvider.getConnection()) {
            connection.isValid(2);
            open = true;
        } catch (SQLException e) {
            LOGGER.error(e, "Failed to open H2 storage engine.");
            throw new IllegalStateException(
                    "Failed to open H2 storage engine.",
                    e
            );
        }

        try {
            migrationRunner.migrate(this);
            LOGGER.info("H2 storage engine opened successfully.");
        } catch (RuntimeException e) {
            LOGGER.error(e, "H2 storage engine migration failed.");
            close();
            throw e;
        }
    }

    /**
     * Determines whether this storage engine is open.
     *
     * @return {@code true} if the engine is open; {@code false} otherwise
     */
    @Override
    public boolean isOpen() {
        return open;
    }

    /**
     * Begins a transaction with auto-commit disabled.
     *
     * @return a new H2 storage transaction
     * @throws IllegalStateException if this engine is not open or a transaction
     *                               cannot be started
     */
    @Override
    public H2StorageTransaction beginTransaction() {
        ensureOpen();

        LOGGER.debug("Beginning H2 storage transaction.");

        Connection connection = connectionProvider.getConnection();

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            closeQuietly(connection);

            LOGGER.error(e, "Failed to begin H2 storage transaction.");
            throw new IllegalStateException(
                    "Failed to begin H2 transaction.",
                    e
            );
        }

        return new H2StorageTransaction(connection);
    }

    /**
     * Closes this engine.
     *
     * <p>After closing, new transactions cannot be started.</p>
     */
    @Override
    public void close() {
        if (open) {
            LOGGER.info("Closing H2 storage engine.");
        }

        open = false;
    }

    /**
     * Ensures that this engine is open.
     *
     * @throws IllegalStateException if this engine is closed
     */
    private void ensureOpen() {
        if (!open) {
            throw new IllegalStateException("H2 storage engine is not open.");
        }
    }

    /**
     * Closes a connection without masking the original failure.
     *
     * @param connection the connection to close
     */
    private static void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException _) {}
    }
}
