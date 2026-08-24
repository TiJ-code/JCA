package tij.jca.infrastructure.storage;

import org.junit.jupiter.api.Test;
import tij.jca.infrastructure.storage.h2.H2DatabaseConstants;
import tij.jca.infrastructure.storage.h2.H2StorageEngine;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;
import tij.jca.infrastructure.storage.h2.helper.SQLBuilder;

import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class StorageIntegrationTest {

    @Test
    void h2OpensAndAppliesMigrations() throws Exception {
        var path = Files.createTempDirectory("jca-h2-").resolve("data");
        var engine = new H2StorageEngine(path);

        assertFalse(engine.isOpen());

        engine.open();

        assertTrue(engine.isOpen());

        try (var transaction = engine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var sql = SQLBuilder
                    .select(
                            H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION,
                            H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION
                    )
                    .from(
                            H2DatabaseConstants.TABLE__JCA_SCHEMA_MIGRATIONS
                    )
                    .orderBy(
                            H2DatabaseConstants.COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION
                    )
                    .build();

            try (Statement statement = h2Transaction.connection().createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                assertTrue(resultSet.next());

                assertEquals(
                        0,
                        resultSet.getInt(
                                H2DatabaseConstants
                                        .COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION
                        )
                );

                assertEquals(
                        "internal version",
                        resultSet.getString(
                                H2DatabaseConstants
                                        .COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION
                        )
                );

                assertTrue(resultSet.next());
            }

            transaction.rollback();
        }

        engine.close();

        assertFalse(engine.isOpen());
    }

    @Test
    void committedTransactionPersistsData() throws Exception {
        var path = Files.createTempDirectory("jca-h2-").resolve("data");
        var engine = new H2StorageEngine(path);

        engine.open();

        try (var transaction = engine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var createTable = SQLBuilder
                    .createTable("test_committed")
                    .ifNotExists()
                    .column("id", "INT")
                    .primaryKey()
                    .column("content", "VARCHAR(255)")
                    .notNull()
                    .build();

            try (Statement statement =
                         h2Transaction.connection().createStatement()) {

                statement.execute(createTable);
            }

            var insert = SQLBuilder
                    .insertInto("test_committed")
                    .columns("id", "content")
                    .build();

            try (PreparedStatement statement = h2Transaction.connection().prepareStatement(insert)) {

                statement.setInt(1, 1);
                statement.setString(2, "committed");

                assertEquals(
                        1,
                        statement.executeUpdate()
                );
            }

            transaction.commit();
        }

        try (var transaction = engine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var select = SQLBuilder
                    .select("content")
                    .from("test_committed")
                    .where("id = 1")
                    .build();

            try (Statement statement = h2Transaction.connection().createStatement();
                 ResultSet resultSet = statement.executeQuery(select)
            ) {
                assertTrue(resultSet.next());

                assertEquals(
                        "committed",
                        resultSet.getString("content")
                );

                assertFalse(resultSet.next());
            }

            transaction.rollback();
        }

        engine.close();
    }

    @Test
    void rolledBackTransactionDoesNotPersistChanges() throws Exception {
        var path = Files.createTempDirectory("jca-h2-").resolve("data");
        var engine = new H2StorageEngine(path);

        engine.open();

        try (var transaction = engine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var createTable = SQLBuilder
                    .createTable("test_rollback")
                    .ifNotExists()
                    .column("id", "INT")
                    .primaryKey()
                    .column("content", "VARCHAR(255)")
                    .notNull()
                    .build();

            try (Statement statement =
                         h2Transaction.connection().createStatement()) {

                statement.execute(createTable);
            }

            transaction.commit();
        }

        try (var transaction = engine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var insert = SQLBuilder
                    .insertInto("test_rollback")
                    .columns("id", "content")
                    .build();

            try (PreparedStatement statement = h2Transaction.connection().prepareStatement(insert)) {

                statement.setInt(1, 1);
                statement.setString(2, "should disappear");

                assertEquals(
                        1,
                        statement.executeUpdate()
                );
            }

            transaction.rollback();
        }

        try (var transaction = engine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var select = SQLBuilder
                    .select("*")
                    .from("test_rollback")
                    .where("id = 1")
                    .build();

            try (Statement statement = h2Transaction.connection().createStatement();
                 ResultSet resultSet = statement.executeQuery(select)) {
                assertFalse(
                        resultSet.next(),
                        "Rolled back INSERT must not persist."
                );
            }

            transaction.rollback();
        }

        engine.close();
    }

    @Test
    void reopeningDatabasePreservesCommittedData() throws Exception {
        var path = Files.createTempDirectory("jca-h2-").resolve("data");

        var firstEngine = new H2StorageEngine(path);
        firstEngine.open();

        try (var transaction = firstEngine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var createTable = SQLBuilder
                    .createTable("test_persistence")
                    .ifNotExists()
                    .column("id", "INT")
                    .primaryKey()
                    .column("content", "VARCHAR(255)")
                    .notNull()
                    .build();

            try (Statement statement = h2Transaction.connection().createStatement()) {
                statement.execute(createTable);
            }

            var insert = SQLBuilder
                    .insertInto("test_persistence")
                    .columns("id", "content")
                    .build();

            try (PreparedStatement statement = h2Transaction.connection().prepareStatement(insert)) {
                statement.setInt(1, 1);
                statement.setString(2, "persistent");

                assertEquals(
                        1,
                        statement.executeUpdate()
                );
            }

            transaction.commit();
        }

        firstEngine.close();

        var secondEngine = new H2StorageEngine(path);
        secondEngine.open();

        assertTrue(secondEngine.isOpen());

        try (var transaction = secondEngine.beginTransaction()) {
            var h2Transaction = assertInstanceOf(
                    H2StorageTransaction.class,
                    transaction
            );

            var select = SQLBuilder
                    .select("content")
                    .from("test_persistence")
                    .where("id = 1")
                    .build();

            try (Statement statement = h2Transaction.connection().createStatement();
                 ResultSet resultSet = statement.executeQuery(select)) {
                assertTrue(resultSet.next());

                assertEquals(
                        "persistent",
                        resultSet.getString("content")
                );

                assertFalse(resultSet.next());
            }

            transaction.rollback();
        }

        secondEngine.close();

        assertFalse(secondEngine.isOpen());
    }

    @Test
    void closingEnginePreventsNewTransactions() throws Exception {
        var path = Files.createTempDirectory("jca-h2-").resolve("data");
        var engine = new H2StorageEngine(path);

        engine.open();
        engine.close();

        assertFalse(engine.isOpen());

        assertThrows(
                Exception.class,
                engine::beginTransaction
        );
    }
}