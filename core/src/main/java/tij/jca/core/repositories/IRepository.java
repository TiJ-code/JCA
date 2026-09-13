package tij.jca.core.repositories;

import java.util.Optional;

/**
 * Defines common persistence operations for a domain entity.
 *
 * @param <TEntity> the entity type
 * @param <TId> the entity identifier type
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IRepository<TEntity, TId> {
    /**
     * Creates or updates an entity.
     *
     * @param entity the entity to persist
     * @return the persisted entity
     */
    TEntity save(TEntity entity);

    /**
     * Finds an entity by its identifier.
     *
     * @param id the entity identifier
     * @return the matching entity, or an empty optional
     */
    Optional<TEntity> findById(TId id);

    /**
     * Determines whether an entity exists.
     *
     * @param id the entity identifier
     * @return {@code true} if the entity exists
     */
    boolean existsById(TId id);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id the entity identifier
     */
    void deleteById(TId id);
}
