package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for DELETE statements.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class SQLDeleteBuilder {
    private final String table;
    private final List<String> conditions = new ArrayList<>();

    /**
     * Creates a DELETE builder for the given table.
     *
     * @param table table from which rows will be deleted
     */
    SQLDeleteBuilder(String table) {
        this.table = StringUtils.requireNonBlank(table, "table");
    }

    /**
     * Adds a WHERE condition to the delete statement.
     *
     * @param condition SQL condition
     * @return this builder
     */
    public SQLDeleteBuilder where(String condition) {
        conditions.add(
                StringUtils.requireNonBlank(condition, "condition")
        );
        return this;
    }

    /**
     * Adds an additional WHERE condition.
     *
     * @param condition SQL condition
     * @return this builder
     */
    public SQLDeleteBuilder and(String condition) {
        return where(condition);
    }

    /**
     * Builds the DELETE statement.
     *
     * @return generated SQL
     * @throws IllegalStateException if no WHERE condition has been configured
     */
    public String build() {
        if (conditions.isEmpty()) {
            throw new IllegalStateException("DELETE statement requires a WHERE condition");
        }

        return "DELETE FROM "
                + table
                + " WHERE "
                + String.join(" AND ", conditions)
                + ";";
    }
}
