package tij.jca.infrastructure.storage.h2;

import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.ITransactionScope;
import tij.jca.core.storage.TransactionPropagation;
import tij.jca.core.storage.TransactionWork;

import java.util.Objects;

public final class H2TransactionManager implements ITransactionManager {
    private final H2StorageEngine storageEngine;

    private final ThreadLocal<ITransactionScope> currentScope = new ThreadLocal<>();

    public H2TransactionManager(H2StorageEngine storageEngine) {
        this.storageEngine = Objects.requireNonNull(storageEngine, "storageEngine");
    }

    @Override
    public <T> T execute(TransactionWork<T> work) {
        return execute(TransactionPropagation.REQUIRED, work);
    }

    @Override
    public <T> T execute(TransactionPropagation propagation, TransactionWork<T> work) {
        Objects.requireNonNull(propagation, "propagation");
        Objects.requireNonNull(work, "work");

        ITransactionScope existingScope = currentScope.get();

        if (propagation == TransactionPropagation.NEVER && existingScope != null) {
            throw new IllegalStateException("A transaction is already active.");
        }

        if (propagation == TransactionPropagation.REQUIRED && existingScope != null) {
            return work.execute(existingScope);
        }

        return executeNew(work);
    }

    private <T> T executeNew(TransactionWork<T> work) {
        try (H2StorageTransaction transaction = storageEngine.beginTransaction()) {
            ITransactionScope scope = new H2TransactionScope(transaction);

            currentScope.set(scope);

            try {
                T result = work.execute(scope);
                transaction.commit();
                return result;
            } catch (RuntimeException | Error failure) {
                rollback(transaction, failure);
                throw failure;
            } finally {
                currentScope.remove();
            }
        }
    }

    private static void rollback(H2StorageTransaction transaction, Throwable originalFailure) {
        try {
            transaction.rollback();
        } catch (RuntimeException rollbackFailure) {
            originalFailure.addSuppressed(rollbackFailure);
        }
    }
}
