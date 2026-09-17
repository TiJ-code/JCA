package tij.jca.core.storage;

/**
 * Represents application work executed within a transaction.
 *
 * <p>
 * The supplied scope provides access to repositories that all
 * participate in the same transaction.
 * </p>
 *
 * @param <T> the result type produced by the work
 *
 * @since 0.1.0
 * @author TiJ
 */
@FunctionalInterface
public interface TransactionWork<T> {
    /**
     * Executes application work using a transaction-scoped repository set.
     *
     * @param scope the active transaction scope
     * @return the result of the work
     * @throws RuntimeException if the work cannot be completed
     */
    T execute(ITransactionScope scope);
}
