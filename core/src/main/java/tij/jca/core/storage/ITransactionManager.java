package tij.jca.core.storage;

/**
 * Coordinates application work within storage transactions.
 *
 * <p>
 * The transaction manager creates or joins a transaction,
 * provides the transaction-scoped repositories to the work,
 * commits successful work, and rolls back failed work.
 * </p>
 *
 * <p>
 * Application services should depend on this interface
 * instead of directly accessing {@link IStorageEngine}
 * {@link IStorageTransaction}, or JDBC connections.
 * </p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface ITransactionManager {
    /**
     * Executes work using {@link TransactionPropagation#REQUIRED}.
     *
     * <p>
     * The work joins an existing transaction when one is active.
     * Otherwise, a new transaction is created.
     * </p>
     *
     * @param work the application work to execute
     * @return the result returned by the work
     * @param <T> the result type
     * @throws NullPointerException if {@code work} is {@code null}
     * @throws RuntimeException if the work or transaction fails
     */
    <T> T execute(TransactionWork<T> work);

    /**
     * Executes work using the requested propagation policy.
     *
     * @param propagation the transaction propagation policy
     * @param work the application work to execute
     * @return the result returned by the work
     * @param <T> the result type
     * @throws NullPointerException if an argument is {@code null}
     * @throws IllegalStateException if the propagation policy is invalid
     * @throws RuntimeException if the work or transaction fails
     */
    <T> T execute(TransactionPropagation propagation, TransactionWork<T> work);
}
