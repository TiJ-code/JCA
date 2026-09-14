package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fluent builder for UPDATE statements.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class SQLUpdateBuilder {
    private final String table;
    private final List<String> assignments = new ArrayList<>();
    private final List<String> conditions = new ArrayList<>();

    /**
     * Creates an UPDATE builder for the given table.
     *
     * @param table table to update
     */
    SQLUpdateBuilder(String table) {
        this.table = Objects.requireNonNull(table, table);
    }

    /**
     * Adds a column assignment of the form {@code column = ?}.
     *
     * @param column column name
     * @return this builder
     */
    public SQLUpdateBuilder set(String column) {
        assignments.add(
                StringUtils.requireNonBlank(column, "column") + " = ?"
        );
        return this;
    }

    /**
     * Adds a raw SQL assignment expression.
     *
     * @param expression SQL expression, such as {@code column = NOW()}
     * @return this builder
     */
    public SQLUpdateBuilder setExpression(String expression) {
        assignments.add(StringUtils.requireNonBlank(expression, "expression"));
        return this;
    }

    /**
     * Adds a WHERE condition.
     *
     * @param condition SQL condition
     * @return this builder
     */
    public SQLUpdateBuilder where(String condition) {
        conditions.add(StringUtils.requireNonBlank(condition, "condition"));
        return this;
    }

    /**
     * Adds an additional WHERE condition.
     *
     * @param condition SQL condition
     * @return this builder
     */
    public SQLUpdateBuilder and(String condition) {
        return where(condition);
    }

    /**
     * Builds the UPDATE statement
     *
     * @return generated SQL
     * @throws IllegalStateException if no assignments or WHERE conditions exist
     */
    public String build() {
        if (assignments.isEmpty()) {
            throw new IllegalStateException("UPDATE statement requires assignments");
        }

        if (conditions.isEmpty()) {
            throw new IllegalStateException("UPDATE statement requires a WHERE condition");
        }

        return "UPDATE "
                + table
                + " SET "
                + String.join(", ", assignments)
                + " WHERE "
                + String.join(" AND ", conditions)
                + ";";
    }
}
