package tij.jca.core.storage.migration;

import java.util.List;

/**
 * Supplies the ordered list of migrations that should be applied to a storage
 * engine.
 *
 * <p>The provider is responsible for locating and returning all migrations
 * required for a particular storage implementation. The lifecycle of the
 * migrations is managed by the migration runner rather than by callers.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IMigrationProvider {
    /**
     * Returns all available migrations in the order they should be applied.
     *
     * <p>Implementations should return migrations in ascending version order,
     * otherwise the migration runner may apply them in an invalid sequence.</p>
     *
     * @return a list of migrations, ordered by version
     */
    List<IMigration> getMigrations();
}
