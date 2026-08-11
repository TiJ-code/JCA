package tij.jca.core.storage;

/**
 * Represents a transaction within a {@link IStorageEngine}.
 *
 * <p>A storage transaction provides atomicity for a group of storage
 * operations. Changes made within a transaction can either be permanently
 * applied by committing the transaction or discarded by rolling it back.</p>
 *
 * <p>Transactions should be closed after use, preferably through a
 * try-with-resources statement.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IStorageTransaction extends AutoCloseable {
    /**
     * Commits all changes made within this transaction.
     *
     * <p>After a successful commit, the changes performed within the
     * transaction become permanent and cannot be rolled back.</p>
     *
     * @throws IllegalStateException if the transaction cannot be committed
     */
    void commit();

    /**
     * Rolls back all changes made within this transaction.
     *
     * <p>After a successful rollback, changes performed within the
     * transaction are discarded.</p>
     *
     * @throws IllegalStateException if the transaction cannot be rolled back
     */
    void rollback();

    /**
     * Closes the transaction and releases any resources associated within.
     *
     * <p>If the transaction has neither been committed or rolled back,
     * implementations should define whether closing it implicitly rolls
     * back the transaction. The recommended behaviour is to roll back
     * any uncommitted changes.</p>
     */
    @Override
    void close();
}
