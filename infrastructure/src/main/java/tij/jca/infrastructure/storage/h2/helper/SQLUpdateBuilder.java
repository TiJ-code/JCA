package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SQLUpdateBuilder {
    private final String table;
    private final List<String> assignments = new ArrayList<>();
    private final List<String> conditions = new ArrayList<>();

    SQLUpdateBuilder(String table) {
        this.table = Objects.requireNonNull(table, table);
    }

    public SQLUpdateBuilder set(String column) {
        assignments.add(
                StringUtils.requireNonBlank(column, "column") + " = ?"
        );
        return this;
    }

    public SQLUpdateBuilder setExpression(String expression) {
        assignments.add(StringUtils.requireNonBlank(expression, "expression"));
        return this;
    }

    public SQLUpdateBuilder where(String condition) {
        conditions.add(StringUtils.requireNonBlank(condition, "condition"));
        return this;
    }

    public SQLUpdateBuilder and(String condition) {
        return where(condition);
    }

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
