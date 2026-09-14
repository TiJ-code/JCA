package tij.jca.infrastructure.storage.h2.repository;

import tij.jca.core.storage.exceptions.DuplicateEntryException;
import tij.jca.core.storage.exceptions.EntityNotFoundException;
import tij.jca.core.storage.exceptions.StorageException;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

abstract class AbstractH2Repository {
    protected final H2StorageTransaction transaction;

    protected AbstractH2Repository(H2StorageTransaction transaction) {
        this.transaction = Objects.requireNonNull(transaction, "transaction");
    }

    protected PreparedStatement prepare(String sql) {
        try {
            return transaction.connection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new StorageException("Failed to prepare SQL statement.", e);
        }
    }

    protected void executeUpdate(String sql, SQLParameterSetter setter) {
        try (PreparedStatement statement = prepare(sql)) {
            setter.set(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw translate(e);
        }
    }

    protected boolean exists(String sql, SQLParameterSetter setter) {
        try (PreparedStatement statement = prepare(sql)) {
            setter.set(statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to query storage.", e);
        }
    }

    protected StorageException translate(SQLException exception) {
        if (exception.getErrorCode() == 23505) {
            return new DuplicateEntryException(exception.getMessage());
        }

        return new StorageException("H2 repository operation failed.", exception);
    }

    protected EntityNotFoundException notFound(String entity, Object id) {
        return new EntityNotFoundException(entity + " with ID " + id + " not found.");
    }

    @FunctionalInterface
    protected interface SQLParameterSetter {
        void set(PreparedStatement statement) throws SQLException;
    }
}
