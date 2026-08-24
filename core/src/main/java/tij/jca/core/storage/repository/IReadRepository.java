package tij.jca.core.storage.repository;

import tij.jca.core.storage.page.Page;
import tij.jca.core.storage.page.PageOffset;

import java.util.Optional;

/**
 * Provides read operations for a persisted entity.
 *
 * <p>Implementations must not expose storage-specific types. In particular,
 * callers should be able to use a repository without depending on JDBC or a
 * concrete database implementation.</p>
 *
 * @param <E> the entity type
 * @param <K> the entity identifier type
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IReadRepository<E, K> {
    /**
     * Finds an entity by its identifier.
     *
     * @param id the identifier to search for
     * @return the matching entity, or an empty optional when it does not exist
     * @throws NullPointerException if {@code id} is {@code null}
     */
    Optional<E> findById(K id);

    /**
     * Retrieves a page of entities.
     *
     * @param pageOffset the requested offset and page size
     * @return a page of entities
     * @throws NullPointerException if {@code pageOffset} is {@code null}
     */
    Page<E> findAll(PageOffset pageOffset);
}
