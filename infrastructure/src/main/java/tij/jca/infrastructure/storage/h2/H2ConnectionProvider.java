package tij.jca.infrastructure.storage.h2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

final class H2ConnectionProvider {
    private final DataSource dataSource;

    H2ConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
