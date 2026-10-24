package tij.jca.application.services;

import tij.jca.application.exceptions.ConflictException;
import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.application.exceptions.ServiceOperationException;
import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.TransactionWork;
import tij.jca.core.storage.exceptions.DuplicateEntryException;
import tij.jca.core.storage.exceptions.EntityNotFoundException;
import tij.jca.core.storage.exceptions.StorageException;

final class ServiceTransactionExecutor {
    private ServiceTransactionExecutor() {}

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
