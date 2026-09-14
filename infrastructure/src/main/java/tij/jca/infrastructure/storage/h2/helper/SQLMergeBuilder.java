package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Fluent builder for MERGE statements.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class SQLMergeBuilder {
    private final String table;

    private List<String> columns = List.of();
    private List<String> keyColumns = List.of();

    /**
     * Creates a MERGE builder for the given table.
     *
     * @param table table to merge into
     */
    SQLMergeBuilder(String table) {
        this.table = StringUtils.requireNonBlank(table, "table");
    }

    /**
     * Sets the columns inserted or updated by the merge.
     *
     * @param columns column names
     * @return this builder
     */
    public SQLMergeBuilder columns(String... columns) {
        this.columns = validateColumns(columns, "MERGE statement requires columns");
        return this;
    }

    /**
     * Sets the key columns used to identify the row to merge.
     *
     * @param keyColumns key column names
     * @return this builder
     */
    public SQLMergeBuilder key(String... keyColumns) {
        this.keyColumns = validateColumns(keyColumns, "MERGE statement requires key columns");
        return this;
    }

    /**
     * Builds the MERGE statement.
     *
     * @return generated SQL
     * @throws IllegalStateException if no columns or key columns are configured
     */
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

    /**
     * Validates the supplied column names.
     *
     * @param columns column names to validate
     * @param errorMessage message used for invalid input
     * @return validated column list
     * @throws IllegalArgumentException if the input is empty or blank
     */
    private static List<String> validateColumns(String[] columns, String errorMessage) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException(errorMessage);
        }

        return Arrays.stream(columns)
                .map(column -> StringUtils.requireNonBlank(column, "column"))
                .toList();
    }
}
