package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SQLInsertBuilder {
    private final List<Object[]> rows = new ArrayList<>();
    private final String table;

    private List<String> columns = new ArrayList<>();

    SQLInsertBuilder(String table) {
        this.table = StringUtils.requireNonBlank(table, "table");
    }

    public SQLInsertBuilder columns(String... columns) {
        this.columns = Arrays.asList(columns);
        return this;
    }

    public SQLInsertBuilder values(Object... values) {
        if (!columns.isEmpty() && values.length != columns.size()) {
            throw new IllegalArgumentException(
                    "Expected " + columns.size() + " values but got " + values.length
            );
        }

        if (columns.isEmpty() && !rows.isEmpty() && values.length != rows.get(0).length) {
            throw new IllegalArgumentException(
                    "All rows must have the same number of values"
            );
        }

        rows.add(values);
        return this;
    }

    public String build() {
        if (rows.isEmpty()) {
            throw new IllegalStateException(
                    "INSERT statement requires at least one row (call values(...))"
            );
        }

        int width = columns.isEmpty() ? rows.get(0).length : columns.size();

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(table);
        if (!columns.isEmpty()) {
            sql.append(" (").append(String.join(", ", columns)).append(')');
        }
        sql.append(" VALUES ");

        String placeholderGroup = "(" + String.join(", " + Collections.nCopies(width, "?")) + ")";

        List<String> groups = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        for (Object[] row : rows) {
            groups.add(placeholderGroup);
            parameters.addAll(Arrays.asList(row));
        }

        sql.append(String.join(", ", groups)).append(';');

        return new SQLInsertStatement(sql.toString(), List.copyOf(parameters), rows.size()).sql();
    }
}
