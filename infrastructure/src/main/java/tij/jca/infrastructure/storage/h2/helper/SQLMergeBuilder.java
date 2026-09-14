package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SQLMergeBuilder {
    private final String table;

    private List<String> columns = List.of();
    private List<String> keyColumns = List.of();

    SQLMergeBuilder(String table) {
        this.table = StringUtils.requireNonBlank(table, "table");
    }

    public SQLMergeBuilder columns(String... columns) {
        this.columns = validateColumns(columns, "MERGE statement requires columns");
        return this;
    }

    public SQLMergeBuilder key(String... keyColumns) {
        this.keyColumns = validateColumns(keyColumns, "MERGE statement requires key columns");
        return this;
    }

    public String build() {
        if (columns.isEmpty()) {
            throw new IllegalStateException("MERGE statement requires columns");
        }

        if (keyColumns.isEmpty()) {
            throw new IllegalStateException("MERGE statement requires key columns");
        }

        String placeholders = String.join(", ", Collections.nCopies(columns.size(), "?"));

        return "MERGE INTO "
                + table
                + String.join(", ", columns)
                + ") KEY ("
                + String.join(", ", keyColumns)
                + ") VALUES ("
                + placeholders
                + ");";
    }

    private static List<String> validateColumns(String[] columns, String errorMessage) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException(errorMessage);
        }

        return Arrays.stream(columns)
                .map(column -> StringUtils.requireNonBlank(column, "column"))
                .toList();
    }
}
