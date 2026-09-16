package tij.jca.core.storage;

public interface ITransactionManager {
    <T> T execute(TransactionWork<T> work);

    <T> T execute(TransactionPropagation propagation, TransactionWork<T> work);
}
