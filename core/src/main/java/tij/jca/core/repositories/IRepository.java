package tij.jca.core.repositories;

import java.util.Optional;

public interface IRepository<TEntity, TId> {
    TEntity save(TEntity entity);

    Optional<TEntity> findById(TId id);

    boolean existsById(TId id);

    void deleteById(TId id);
}
