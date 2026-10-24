package tij.jca.application.services;

import tij.jca.application.exceptions.ConflictException;
import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.application.exceptions.ServiceOperationException;
import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.TransactionWork;
import tij.jca.core.storage.exceptions.DuplicateEntryException;
import tij.jca.core.storage.exceptions.EntityNotFoundException;
import tij.jca.core.storage.exceptions.StorageException;

/**
 * Executes application service transactions and translates storage failures.
 *
 * @since 0.1.0
 * @author TiJ
 */
final class ServiceTransactionExecutor {
    private ServiceTransactionExecutor() {}

    /**
     * Executes transactional service work and translates storage failures to
     * application-layer exceptions.
     *
     * @param transactionManager the manager used to execute the transaction
     * @param work the transactional work to execute
     * @param <T> the result type produced by the work
     * @return the result returned by the work
     * @throws ConflictException if storage detects a duplicate entry
     * @throws ResourceNotFoundException if storage cannot find a required entity
     * @throws ServiceOperationException if another storage failure occurs
     */
    static <T> T execute(
            ITransactionManager transactionManager,
            TransactionWork<T> work
    ) {
        try {
            return transactionManager.execute(work);
        } catch (DuplicateEntryException exception) {
            throw new ConflictException(
                    exception.getMessage(),
                    exception
            );
        } catch (EntityNotFoundException exception) {
            throw new ResourceNotFoundException(
                    exception.getMessage(),
                    exception
            );
        } catch (StorageException exception) {
            throw new ServiceOperationException(
                    "The service operation could not be completed.",
                    exception
            );
        }
    }
}
