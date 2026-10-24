package tij.jca.infrastructure.storage.h2.helper;

import java.util.List;

/**
 * Represents a generated SELECT statement and its parameters.
 *
 * @param sql generated SQL
 * @param parameters statement parameter values
 * @since 0.1.0
 * @author TiJ
 */
record SQLSelectStatement(String sql, List<Object> parameters) {
    /**
     * Returns the generated SQL.
     *
     * @return generated SQL
     */
    @Override
    public String toString() {
        return sql();
    }
}
