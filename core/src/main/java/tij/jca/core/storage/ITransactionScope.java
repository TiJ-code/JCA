package tij.jca.core.storage;

import tij.jca.core.repositories.IConversationRepository;
import tij.jca.core.repositories.IDeviceRepository;
import tij.jca.core.repositories.IMessageRepository;
import tij.jca.core.repositories.IServerRepository;
import tij.jca.core.repositories.IUserRepository;

/**
 * Provides repository participating in one transaction.
 *
 * <p>
 * All repositories returned by this scope share the same underlying
 * transaction. Changes made through one repository can therefore be
 * committed or rolled back together with changes made through the
 * other repositories.
 * </p>
 *
 * <p>
 * The scope is valid only while the transaction that created it is
 * active. Application services should obtain repositories from this
 * scope instead of storing repository instances directly.
 * </p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface ITransactionScope {
    /**
     * Returns the user repository for this transaction.
     *
     * @return the transaction-scoped user repository
     */
    IUserRepository users();

    /**
     * Returns the device repository for this transaction.
     *
     * @return the transaction-scoped device repository
     */
    IDeviceRepository devices();

    /**
     * Returns the server repository for this transaction.
     *
     * @return the transaction-scoped server repository
     */
    IServerRepository servers();

    /**
     * Returns the conversation repository for this transaction.
     *
     * @return the transaction-scoped conversation repository
     */
    IConversationRepository conversations();

    /**
     * Returns the message repository for this transaction.
     *
     * @return the transaction-scoped conversation repository
     */
    IMessageRepository messages();
}
