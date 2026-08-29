package tij.jca.infrastructure.storage.h2.helper;

/**
 * Defines a column and its constraints for a CREATE TABLE statement.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class SQLColumnDefinition {
    private final String name;
    private final String type;

    boolean primaryKey;
    boolean notNull;
    boolean unique;
    boolean autoIncrement;
    String defaultValue;
    String references;

    /**
     * Creates a column definition.
     *
     * @param name column name
     * @param type SQL column type
     */
    SQLColumnDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Renders this column definition as SQL.
     *
     * @return SQL representation of this column definition
     */
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
