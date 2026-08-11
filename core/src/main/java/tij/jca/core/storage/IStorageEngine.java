package tij.jca.core.storage;

/**
 *
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IStorageEngine extends AutoCloseable {
    void open();

    boolean isOpen();

    IStorageTransaction beginTransaction();

    @Override
    void close();
}
