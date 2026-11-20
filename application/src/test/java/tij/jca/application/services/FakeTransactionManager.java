package tij.jca.application.services;

import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.ITransactionScope;
import tij.jca.core.storage.TransactionPropagation;
import tij.jca.core.storage.TransactionWork;

import java.util.Objects;

final class FakeTransactionManager implements ITransactionManager {
    private final ITransactionScope scope;

    FakeTransactionManager(ITransactionScope scope) {
        this.scope = Objects.requireNonNull(scope, "scope");
    }

    @Override
    public <T> T execute(TransactionWork<T> work) {
        return Objects.requireNonNull(work, "work").execute(scope);
    }

    @Override
    public <T> T execute(
            TransactionPropagation propagation,
            TransactionWork<T> work
    ) {
        Objects.requireNonNull(propagation, "propagation");
        return execute(work);
    }
}
