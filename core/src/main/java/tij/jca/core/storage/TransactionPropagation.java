package tij.jca.core.storage;

/**
 * Defines how work behaves when a transaction is already active.
 *
 * @since 0.1.0
 * @author TiJ
 */
public enum TransactionPropagation {
    /**
     * Joins the active transaction, or create a new transaction when none exists.
     */
    REQUIRED,

    /**
     * Always create a new transaction.
     *
     * <p>
     * An existing transaction is suspended until the new transaction completes.
     * </p>
     */
    REQUIRES_NEW,

    /**
     * Execute only when no transaction is active.
     */
    NEVER
}
