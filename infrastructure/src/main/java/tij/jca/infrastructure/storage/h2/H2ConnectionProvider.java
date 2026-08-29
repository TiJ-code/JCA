package tij.jca.infrastructure.storage.h2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides JDBC connections from an H2 data source.
 *
 * @since 0.1.0
 * @author TiJ
 */
final class H2ConnectionProvider {
    private final DataSource dataSource;

    /**
     * Creates a connection provider backed by the supplied data source.
     *
     * @param dataSource the data source from which connections are obtained
     */
    H2ConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Obtains a connection from the configured data source.
     *
     * @return a new JDBC connection
     * @throws IllegalStateException if a connection cannot be obtained
     */
    Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to acquire H2 database connection.",
                    e
            );
        }
    }

}
