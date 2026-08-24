package tij.jca.infrastructure.storage.h2.helper;

import java.util.List;

/**
 * Represents a generated INSERT statement and its parameters.
 *
 * @param sql generated SQL
 * @param parameters statement parameter values
 * @param rowCount number of rows represented by the statement
 * @since 0.1.0
 * @author TiJ
 */
record SQLInsertStatement(String sql, List<Object> parameters, int rowCount) {
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
