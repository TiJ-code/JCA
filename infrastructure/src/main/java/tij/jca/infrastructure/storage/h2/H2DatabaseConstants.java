package tij.jca.infrastructure.storage.h2;

public final class H2DatabaseConstants {
    private H2DatabaseConstants() {
    }

    public static final String TABLE__JCA_SCHEMA_MIGRATIONS = "jca_schema_migrations";

    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION = "version";
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION = "description";
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__APPLIED_AT = "applied_at";
}
