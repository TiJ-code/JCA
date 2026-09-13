package tij.jca.infrastructure.storage.h2.helper;

/**
 * Entry point for constructing common SQL statements.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class SQLBuilder {
    /** Prevents instantiation of this utility class. */
    private SQLBuilder() {
    }

    /**
     * Starts a SELECT statement.
     *
     * @param columns columns to select, or no columns for {@code *}
     * @return a SELECT statement builder
     */
    public static SQLSelectBuilder select(String... columns) {
        return new SQLSelectBuilder(columns);
    }

    /**
     * Starts an INSERT statement.
     *
     * @param table table into which rows will be inserted
     * @return an INSERT statement builder
     */
    public static SQLInsertBuilder insertInto(String table) {
        return new SQLInsertBuilder(table);
    }

    /**
     * Starts a CREATE TABLE statement.
     *
     * @param table table to create
     * @return a CREATE TABLE statement builder
     */
    public static SQLCreateTableBuilder createTable(String table) {
        return new SQLCreateTableBuilder(table);
    }
}
