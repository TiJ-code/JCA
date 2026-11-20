package tij.jca.infrastructure.storage.h2;

import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.ITransactionScope;
import tij.jca.core.storage.TransactionPropagation;
import tij.jca.core.storage.TransactionWork;

import java.util.Objects;

/**
 * Executes application work inside H2 storage transactions.
 *
 * <p>
 * The manager creates a transaction-scoped repository set, executes
 * the supplied work, commits successful work, and rolls back failed
 * work.
 * </p>
 *
 * <p>
 * With {@link TransactionPropagation#REQUIRED}, nested operations
 * reuse the active transaction. This ensures that multiple application
 * services participating in one operation share the same transaction.
 * </p>
 *
 * <p>
 * The JDBC connection remains encapsulated inside the H2 storage layer.
 * Application services interact only with {@link ITransactionScope}.
 * </p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class H2TransactionManager implements ITransactionManager {
    private final H2StorageEngine storageEngine;

    private final ThreadLocal<ITransactionScope> currentScope = new ThreadLocal<>();

    /**
     * Creates a transaction manager backed by the supplied H2 engine.
     *
     * @param storageEngine the H2 storage engine used to create transactions
     * @throws NullPointerException if {@code storageEngine} is {@code null}
     */
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

    /**
     * Creates and executes a new H2 transaction.
     *
     * @param work the work to execute
     * @return the work result
     * @param <T> the result type
     */
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

    /**
     * Attempts to roll back the transaction without replacing the original
     * application failure.
     *
     * @param transaction the transaction to roll back
     * @param originalFailure the original failure
     */
    private static void rollback(H2StorageTransaction transaction, Throwable originalFailure) {
        try {
            transaction.rollback();
        } catch (RuntimeException rollbackFailure) {
            originalFailure.addSuppressed(rollbackFailure);
        }
    }
}
