package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLCreateTableBuilder {
    private final List<SQLColumnDefinition> columns = new ArrayList<>();
    private final List<String> tableConstraints = new ArrayList<>();
    private final String table;

    private boolean ifNotExists;

    SQLCreateTableBuilder(String table) {
        this.table = StringUtils.requireNonBlank(table, "table");
    }

    public SQLCreateTableBuilder ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    public SQLCreateTableBuilder column(String name, String type) {
        columns.add(new SQLColumnDefinition(name, type));
        return this;
    }

    private SQLColumnDefinition currentColumn() {
        if (columns.isEmpty()) {
            throw new IllegalStateException(
                    "Call column(name, type) before setting column attributes"
            );
        }
        return columns.get(columns.size() - 1);
    }

    public SQLCreateTableBuilder primaryKey() {
        currentColumn().primaryKey = true;
        return this;
    }

    public SQLCreateTableBuilder notNull() {
        currentColumn().notNull = true;
        return this;
    }

    public SQLCreateTableBuilder unique() {
        currentColumn().unique = true;
        return this;
    }

    public SQLCreateTableBuilder autoIncrement() {
        currentColumn().autoIncrement = true;
        return this;
    }

    public SQLCreateTableBuilder defaultValue(String literalSql) {
        currentColumn().defaultValue = literalSql;
        return this;
    }

    public SQLCreateTableBuilder references(String table, String column) {
        currentColumn().references = table + "(" + column + ")";
        return this;
    }

    public SQLCreateTableBuilder constraint(String constraintSql) {
        tableConstraints.add(constraintSql);
        return this;
    }

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
