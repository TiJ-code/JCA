package tij.jca.infrastructure.storage.h2.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class SQLSelectBuilder {
    private final List<String> columns;
    private final List<String> joins = new ArrayList<>();
    private final List<String> conditions = new ArrayList<>();
    private final List<Object> parameters = new ArrayList<>();
    private final List<String> groupBy = new ArrayList<>();
    private final List<String> having = new ArrayList<>();
    private final List<String> orderBy = new ArrayList<>();

    private String table;
    private Integer limit;
    private Integer offset;
    private boolean distinct;

    SQLSelectBuilder(String... columns) {
        this.columns = (columns == null || columns.length == 0)
                ? List.of("*")
                : Arrays.asList(columns);
    }

    public SQLSelectBuilder distinct() {
        this.distinct = true;
        return this;
    }

    public SQLSelectBuilder from(String table) {
        this.table = Objects.requireNonNull(table, "table");
        return this;
    }

    public SQLSelectBuilder join(String table, String on) {
        joins.add("JOIN " + table + " ON " + on);
        return this;
    }

    public SQLSelectBuilder leftJoin(String table, String on) {
        joins.add("LEFT JOIN " + table + " ON " + on);
        return this;
    }

    public SQLSelectBuilder innerJoin(String table, String on) {
        joins.add("INNER JOIN " + table + " ON " + on);
        return this;
    }

    public SQLSelectBuilder where(String condition, Object... params) {
        this.conditions.add(condition);
        if (params != null) {
            parameters.addAll(Arrays.asList(params));
        }
        return this;
    }

    public SQLSelectBuilder and(String condition, Object... params) {
        return where(condition, params);
    }

    public SQLSelectBuilder groupBy(String... columns) {
        this.groupBy.addAll(Arrays.asList(columns));
        return this;
    }

    public SQLSelectBuilder having(String condition, Object... params) {
        this.having.add(condition);
        if (params != null) {
            parameters.addAll(Arrays.asList(params));
        }
        return this;
    }

    public SQLSelectBuilder orderBy(String... columns) {
        this.orderBy.addAll(Arrays.asList(columns));
        return this;
    }

    public SQLSelectBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SQLSelectBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public String build() {
        if (table == null) {
            throw new IllegalStateException(
                    "SELECT statement requires a FROM table (call from(...))"
            );
        }

        StringBuilder sql = new StringBuilder("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }

        sql.append(String.join(", ", columns));
        sql.append(" FROM ").append(table);

        for (String join : joins) {
            sql.append(' ').append(join);
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        if (!groupBy.isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", groupBy));
        }

        if (!having.isEmpty()) {
            sql.append(" HAVING ").append(String.join(" AND ", having));
        }

        if (!orderBy.isEmpty()) {
            sql.append(" ORDER BY ").append(String.join(", ", orderBy));
        }

        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }

        if (offset != null) {
            sql.append(" OFFSET ").append(offset);
        }

        sql.append(";");

        return new SQLSelectStatement(sql.toString(), List.copyOf(parameters)).sql();
    }
}
