package tij.jca.core.storage.repository;

/**
 * Provides the standard read and write operations for a persisted entity.
 *
 * @param <E> the entity type
 * @param <K> the entity identifier type
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IRepository<E, K> extends IReadRepository<E, K>, IWriteRepository<E, K> {
}
