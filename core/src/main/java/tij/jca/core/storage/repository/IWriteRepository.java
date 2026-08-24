package tij.jca.core.storage.repository;

/**
 * Provides write operations for a persisted entity.
 *
 * @param <E> the entity type
 * @param <K> the entity identifier type
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IWriteRepository<E, K> {
    /**
     * Stores an entity.
     *
     * <p>Implementations should return the stored representation. This allows
     * storage systems to populate generated or normalized values without
     * exposing persistence details.</p>
     *
     * @param entity the entity to store
     * @return the stored entity
     * @throws NullPointerException if {@code entity} is {@code null}
     */
    E save(E entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id the identifier of the entity to delete
     * @throws NullPointerException if {@code id} is {@code null}
     */
    void deleteById(K id);
}
