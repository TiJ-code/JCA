package tij.jca.infrastructure.storage.h2.helper;

import java.util.List;

public record SQLSelectStatement(String sql, List<Object> parameters) {
    @Override
    public String toString() {
        return sql();
    }
}
