package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fluent builder for CREATE TABLE statements.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class SQLCreateTableBuilder {
    private final List<SQLColumnDefinition> columns = new ArrayList<>();
    private final List<String> tableConstraints = new ArrayList<>();
    private final String table;

    private boolean ifNotExists;

    /**
     * Creates a table builder.
     *
     * @param table table to create
     */
    SQLCreateTableBuilder(String table) {
        this.table = StringUtils.requireNonBlank(table, "table");
    }

    /** Adds the {@code IF NOT EXISTS} clause. */
    public SQLCreateTableBuilder ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Adds a column to the table.
     *
     * @param name column name
     * @param type SQL column type
     * @return this builder
     */
    public SQLCreateTableBuilder column(String name, String type) {
        columns.add(new SQLColumnDefinition(name, type));
        return this;
    }

    /**
     * Returns the most recently added column.
     *
     * @return the current column
     * @throws IllegalStateException if no column has been added
     */
    private SQLColumnDefinition currentColumn() {
        if (columns.isEmpty()) {
            throw new IllegalStateException(
                    "Call column(name, type) before setting column attributes"
            );
        }
        return columns.getLast();
    }

    /** Marks the current column as the primary key. */
    public SQLCreateTableBuilder primaryKey() {
        currentColumn().primaryKey = true;
        return this;
    }

    /** Marks the current column as non-nullable. */
    public SQLCreateTableBuilder notNull() {
        currentColumn().notNull = true;
        return this;
    }

    /** Marks the current column as unique. */
    public SQLCreateTableBuilder unique() {
        currentColumn().unique = true;
        return this;
    }

    /** Marks the current column as auto-incrementing. */
    public SQLCreateTableBuilder autoIncrement() {
        currentColumn().autoIncrement = true;
        return this;
    }

    /**
     * Sets the default SQL expression for the current column.
     *
     * @param literalSql SQL literal or expression used as the default
     * @return this builder
     */
    public SQLCreateTableBuilder defaultValue(String literalSql) {
        currentColumn().defaultValue = literalSql;
        return this;
    }

    /**
     * Adds a foreign-key reference to the current column.
     *
     * @param table referenced table
     * @param column referenced column
     * @return this builder
     */
    public SQLCreateTableBuilder references(String table, String column) {
        currentColumn().references = table + "(" + column + ")";
        return this;
    }

    /**
     * Adds a table-level constraint.
     *
     * @param constraintSql SQL constraint expression
     * @return this builder
     */
    public SQLCreateTableBuilder constraint(String constraintSql) {
        tableConstraints.add(constraintSql);
        return this;
    }

    /**
     * Builds the CREATE TABLE statement.
     *
     * @return the generated SQL statement
     * @throws IllegalStateException if no columns have been added
     */
    public String build() {
        if (columns.isEmpty()) {
            throw new IllegalStateException(
                    "CREATE TABLE statement requires at least one column"
            );
        }

        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append(table).append(" (\n");

        List<String> parts = columns.stream()
                .map(SQLColumnDefinition::toSql)
                .collect(Collectors.toCollection(ArrayList::new));
        parts.addAll(tableConstraints);

        sql.append(parts.stream()
                .map(part -> "    " + part)
                .collect(Collectors.joining(",\n")));
        sql.append("\n);");

        return sql.toString();
    }
}
