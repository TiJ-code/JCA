package tij.jca.core.storage;

public interface IStorageTransaction extends AutoCloseable {
    void commit();

    void rollback();

    @Override
    void close();
}
