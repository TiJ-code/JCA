package tij.jca.infrastructure.storage.h2.helper;

import java.util.List;

record SQLSelectStatement(String sql, List<Object> parameters) {
    @Override
    public String toString() {
        return sql();
    }
}
