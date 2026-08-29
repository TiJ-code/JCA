package tij.jca.infrastructure.storage.h2.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Fluent builder for SELECT statements.
 *
 * @since 0.1.0
 * @author TiJ
 */
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

    /**
     * Creates a SELECT builder.
     *
     * @param columns columns to select, or no columns for {@code *}
     */
    SQLSelectBuilder(String... columns) {
        this.columns = (columns == null || columns.length == 0)
                ? List.of("*")
                : Arrays.asList(columns);
    }

    /** Adds the {@code DISTINCT} modifier. */
    public SQLSelectBuilder distinct() {
        this.distinct = true;
        return this;
    }

    /**
     * Sets the table selected from.
     *
     * @param table source table
     * @return this builder
     */
    public SQLSelectBuilder from(String table) {
        this.table = Objects.requireNonNull(table, "table");
        return this;
    }

    /**
     * Adds an inner join.
     *
     * @param table table to join
     * @param on join condition
     * @return this builder
     */
    public SQLSelectBuilder join(String table, String on) {
        joins.add("JOIN " + table + " ON " + on);
        return this;
    }

    /**
     * Adds a left join.
     *
     * @param table table to join
     * @param on join condition
     * @return this builder
     */
    public SQLSelectBuilder leftJoin(String table, String on) {
        joins.add("LEFT JOIN " + table + " ON " + on);
        return this;
    }

    /**
     * Adds an explicit inner join.
     *
     * @param table table to join
     * @param on join condition
     * @return this builder
     */
    public SQLSelectBuilder innerJoin(String table, String on) {
        joins.add("INNER JOIN " + table + " ON " + on);
        return this;
    }

    /**
     * Adds a WHERE condition and its parameter values.
     *
     * @param condition SQL condition
     * @param params values corresponding to condition placeholders
     * @return this builder
     */
    public SQLSelectBuilder where(String condition, Object... params) {
        this.conditions.add(condition);
        if (params != null) {
            parameters.addAll(Arrays.asList(params));
        }
        return this;
    }

    /**
     * Adds an additional WHERE condition.
     *
     * @param condition SQL condition
     * @param params values corresponding to condition placeholders
     * @return this builder
     */
    public SQLSelectBuilder and(String condition, Object... params) {
        return where(condition, params);
    }

    /**
     * Adds GROUP BY columns.
     *
     * @param columns columns to group by
     * @return this builder
     */
    public SQLSelectBuilder groupBy(String... columns) {
        this.groupBy.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Adds a HAVING condition and its parameter values.
     *
     * @param condition SQL condition
     * @param params values corresponding to condition placeholders
     * @return this builder
     */
    public SQLSelectBuilder having(String condition, Object... params) {
        this.having.add(condition);
        if (params != null) {
            parameters.addAll(Arrays.asList(params));
        }
        return this;
    }

    /**
     * Adds ORDER BY columns.
     *
     * @param columns columns to order by
     * @return this builder
     */
    public SQLSelectBuilder orderBy(String... columns) {
        this.orderBy.addAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Sets the maximum number of rows.
     *
     * @param limit maximum row count
     * @return this builder
     */
    public SQLSelectBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the number of rows to skip.
     *
     * @param offset row offset
     * @return this builder
     */
    public SQLSelectBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Builds the SELECT statement.
     *
     * @return the generated SQL statement
     * @throws IllegalStateException if no source table has been configured
     */
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
