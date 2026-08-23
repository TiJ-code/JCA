package tij.jca.infrastructure.storage.h2.helper;

public class SQLColumnDefinition {
    private final String name;
    private final String type;

    boolean primaryKey;
    boolean notNull;
    boolean unique;
    boolean autoIncrement;
    String defaultValue;
    String references;

    SQLColumnDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }

    String toSql() {
        StringBuilder sb = new StringBuilder(name).append(' ').append(type);

        if (primaryKey) {
            sb.append(" PRIMARY KEY");
        }

        if (autoIncrement) {
            sb.append(" AUTO_INCREMENT");
        }

        if (notNull) {
            sb.append(" NOT NULL");
        }

        if (unique) {
            sb.append(" UNIQUE");
        }

        if (defaultValue != null) {
            sb.append(" DEFAULT ").append(defaultValue);
        }

        if (references != null) {
            sb.append(" REFERENCES ").append(references);
        }

        return sb.toString();
    }
}
