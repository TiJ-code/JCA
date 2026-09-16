package tij.jca.core.storage;

@FunctionalInterface
public interface TransactionWork<T> {
    T execute(ITransactionScope scope);
}
