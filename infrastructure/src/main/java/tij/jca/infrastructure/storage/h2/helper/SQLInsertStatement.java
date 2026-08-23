package tij.jca.infrastructure.storage.h2.helper;

import java.util.List;

record SQLInsertStatement(String sql, List<Object> parameters, int rowCount) {
    @Override
    public String toString() {
        return sql();
    }
}
