package tij.jca.infrastructure.storage.h2.helper;

import tij.jca.core.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public final class SQLInsertBuilder {

    private final String table;
    private List<String> columns = List.of();

    SQLInsertBuilder(String table) {
        this.table = StringUtils.requireNonBlank(
                table,
                "table"
        );
    }

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
