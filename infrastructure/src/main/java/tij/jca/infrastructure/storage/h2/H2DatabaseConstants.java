package tij.jca.infrastructure.storage.h2;

/**
 * Names of tables and columns used by the H2 storage implementation.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class H2DatabaseConstants {
    /**
     * Prevents instantiation of this constants class.
     */
    private H2DatabaseConstants() {
    }

    /**
     * Name of the table that stores applied schema migrations.
     */
    public static final String TABLE__JCA_SCHEMA_MIGRATIONS = "jca_schema_migrations";

    /**
     *  Name of the schema migration version column.
     */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION = "version";

    /**
     * Name of the schema migration description column.
     */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION = "description";

    /**
     *  Name of the schema migration application timestamp column.
     *  */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__APPLIED_AT = "applied_at";
}
