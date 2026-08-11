package tij.jca.core.storage;

/**
 * Represents the primary abstraction for persistent storage within JCA.
 *
 * <p>A storage engine is responsible for managing lifecycle of the
 * underlying storage system and for creating transactions through which
 * storage operations can be performed.</p>
 *
 * <p>The concrete implementation may use any persistence mechanism, such as
 * an embedded database, a file-based store, or a remote storage system.
 * The core module must not depend on a specific implementation.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IStorageEngine extends AutoCloseable {
    /**
     * Opens the storage engine and makes it available for use.
     *
     * <p>Calling this method on an already open storage engine should have no
     * effect unless the concrete implementation explicitly documents
     * otherwise.</p>
     *
     * @throws RuntimeException if the storage engine cannot be opened
     */
    void open();

    /**
     * Determines whether the storage engine is currently open.
     *
     * @return {@code true} if the storage engine is open; {@code false} otherwise
     */
    boolean isOpen();

    /**
     * Begins a new storage transaction.
     *
     * <p>The returned transaction groups storage operations into a single
     * unit of work. The caller is responsible for committing or rolling back
     * the transaction and for closing it afterwards.</p>
     *
     * @return a newly created storage transaction
     * @throws IllegalStateException if the storage engine is not open
     */
    IStorageTransaction beginTransaction();

    /**s
     * Closes the storage engine and releases all resources associated within.
     *
     * <p>After this method returns, the storage engine should no longer be
     * used unless it is explicitly opened again.</p>
     */
    @Override
    void close();
}
