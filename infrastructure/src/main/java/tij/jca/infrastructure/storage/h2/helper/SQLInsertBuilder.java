package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder for parameterised INSERT statements.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class SQLInsertBuilder {

    private final String table;
    private List<String> columns = List.of();

    /**
     * Creates an INSERT builder.
     *
     * @param table table into which rows will be inserted
     */
    SQLInsertBuilder(String table) {
        this.table = StringUtils.requireNonBlank(
                table,
                "table"
        );
    }

    /**
     * Sets the columns receiving parameter values.
     *
     * @param columns column names
     * @return this builder
     * @throws IllegalArgumentException if no columns are supplied
     */
    public SQLInsertBuilder columns(String... columns) {
        if (columns.length == 0) {
            throw new IllegalArgumentException(
                    "INSERT statement requires at least one column."
            );
        }

        this.columns = Arrays.stream(columns)
                .map(column ->
                        StringUtils.requireNonBlank(
                                column,
                                "column"
                        )
                )
                .toList();

        return this;
    }

    /**
     * Builds the INSERT statement with one placeholder per column.
     *
     * @return the generated parameterised SQL statement
     * @throws IllegalStateException if no columns have been configured
     */
    public String build() {
        if (columns.isEmpty()) {
            throw new IllegalStateException(
                    "INSERT statement requires columns."
            );
        }

        String placeholders = String.join(
                ", ",
                java.util.Collections.nCopies(
                        columns.size(),
                        "?"
                )
        );

        return "INSERT INTO "
                + table
                + " ("
                + String.join(", ", columns)
                + ") VALUES ("
                + placeholders
                + ");";
    }
}
