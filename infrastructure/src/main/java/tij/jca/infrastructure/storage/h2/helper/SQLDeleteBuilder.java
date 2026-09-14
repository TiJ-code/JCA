package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class SQLDeleteBuilder {
    private final String table;
    private final List<String> conditions = new ArrayList<>();

    SQLDeleteBuilder(String table) {
        this.table = StringUtils.requireNonBlank(table, "table");
    }

    public SQLDeleteBuilder where(String condition) {
        conditions.add(
                StringUtils.requireNonBlank(condition, "condition")
        );
        return this;
    }

    public SQLDeleteBuilder and(String condition) {
        return where(condition);
    }

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
