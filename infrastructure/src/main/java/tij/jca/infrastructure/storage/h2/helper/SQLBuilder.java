package tij.jca.infrastructure.storage.h2.helper;

public final class SQLBuilder {
    private SQLBuilder() {
    }

    public static SQLSelectBuilder select(String... columns) {
        return new SQLSelectBuilder(columns);
    }

    public static SQLInsertBuilder insertInto(String table) {
        return new SQLInsertBuilder(table);
    }

    public static SQLCreateTableBuilder createTable(String table) {
        return new SQLCreateTableBuilder(table);
    }
}
