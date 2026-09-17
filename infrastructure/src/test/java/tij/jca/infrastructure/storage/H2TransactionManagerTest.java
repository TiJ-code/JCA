package tij.jca.infrastructure.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tij.jca.core.entities.User;
import tij.jca.core.ids.UserID;
import tij.jca.core.storage.ITransactionScope;
import tij.jca.core.storage.TransactionPropagation;
import tij.jca.infrastructure.storage.h2.H2StorageEngine;
import tij.jca.infrastructure.storage.h2.H2TransactionManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

class H2TransactionManagerTest {
    private H2StorageEngine storageEngine;
    private H2TransactionManager transactionManager;

    @BeforeEach
    void setUp() throws Exception {
        Path databasePath = Files.createTempDirectory("jca-h2-manager-").resolve("data");

        storageEngine = new H2StorageEngine(databasePath);
        storageEngine.open();

        transactionManager = new H2TransactionManager(storageEngine);
    }

    @AfterEach
    void tearDown() {
        storageEngine.close();
    }

    @Test
    void successfulWorkIsCommitted() {
        User user = new User(
                new UserID("user-1"),
                "alice",
                "Alice"
        );

        transactionManager.execute(scope -> {
            scope.users().save(user);
            return null;
        });

        boolean persisted = transactionManager.execute(scope ->
                scope.users().existsById(user.id())
        );

        Assertions.assertTrue(persisted);
    }

    @Test
    void failedWorkIsRolledBack() {
        User user = new User(
                new UserID("user-2"),
                "bob",
                "Bob"
        );

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> transactionManager.execute(scope -> {
                    scope.users().save(user);

                    throw new IllegalStateException(
                            "simulated failure"
                    );
                })
        );

        boolean persisted = transactionManager.execute(scope ->
                scope.users().existsById(user.id())
        );

        Assertions.assertFalse(persisted);
    }

    @Test
    void requiredWorkJoinsExistingTransaction() {
        AtomicReference<ITransactionScope> outerScope =
                new AtomicReference<>();

        AtomicReference<ITransactionScope> innerScope =
                new AtomicReference<>();

        transactionManager.execute(outer -> {
            outerScope.set(outer);

            transactionManager.execute(inner -> {
                innerScope.set(inner);
                return null;
            });

            return null;
        });

        Assertions.assertSame(
                outerScope.get(),
                innerScope.get()
        );
    }

    @Test
    void neverPropagationRejectsAnActiveTransaction() {
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> transactionManager.execute(outer -> {
                    transactionManager.execute(
                            TransactionPropagation.NEVER,
                            inner -> null
                    );

                    return null;
                })
        );
    }

    @Test
    void requiredWorkCanBeExecutedWithoutAnExistingTransaction() {
        User user = new User(
                new UserID("user-3"),
                "carol",
                "Carol"
        );

        User saved = transactionManager.execute(scope ->
                scope.users().save(user)
        );

        Assertions.assertEquals(user, saved);
    }
}
