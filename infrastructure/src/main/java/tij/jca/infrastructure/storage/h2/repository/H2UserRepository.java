package tij.jca.infrastructure.storage.h2.repository;

import tij.jca.core.entities.User;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IUserRepository;
import tij.jca.core.storage.exceptions.StorageException;
import tij.jca.infrastructure.storage.h2.H2DatabaseConstants;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;
import tij.jca.infrastructure.storage.h2.helper.SQLBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class H2UserRepository extends AbstractH2Repository implements IUserRepository {
    public H2UserRepository(H2StorageTransaction transaction) {
        super(transaction);
    }

    @Override
    public List<User> findByUsername(String username) {
        String sql = SQLBuilder
                .select(
                        H2DatabaseConstants.COLUMN__USERS__ID,
                        H2DatabaseConstants.COLUMN__USERS__NAME,
                        H2DatabaseConstants.COLUMN__USERS__GLOBAL_DISPLAY_NAME
                )
                .from(H2DatabaseConstants.TABLE__USERS)
                .where(H2DatabaseConstants.COLUMN__USERS__NAME + " = ?")
                .build();

        List<User> users = new ArrayList<>();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(readUser(resultSet));
                }
            }

            return List.copyOf(users);
        } catch (Exception e) {
            throw new StorageException("Failed to find users.", e);
        }
    }

    @Override
    public Set<DeviceID> findDeviceIds(UserID userId) {
        String sql = SQLBuilder
                .select(H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__DEVICE_ID)
                .from(H2DatabaseConstants.TABLE__USER_DEVICE_MAPPING)
                .where(H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__USER_ID + " = ?")
                .build();

        Set<DeviceID> deviceIds = new HashSet<>();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, userId.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    deviceIds.add(new DeviceID(
                            resultSet.getString(H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__DEVICE_ID)
                    ));
                }
            }

            return Set.copyOf(deviceIds);
        } catch (Exception e) {
            throw new StorageException("Failed to find user devices.", e);
        }
    }

    @Override
    public void attachDevice(UserID userId, DeviceID deviceId) {
        String sql = SQLBuilder
                .insertInto(H2DatabaseConstants.TABLE__USER_DEVICE_MAPPING)
                .columns(H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__USER_ID, H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__DEVICE_ID)
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, userId.id());
            statement.setString(2, deviceId.id());
        });
    }

    @Override
    public void detachDevice(UserID userId, DeviceID deviceId) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__USER_DEVICE_MAPPING)
                .where(H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__USER_ID + " = ?")
                .and(H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__DEVICE_ID + " = ?")
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, userId.id());
            statement.setString(2, deviceId.id());
        });
    }

    @Override
    public User save(User user) {
        String sql = SQLBuilder
                .mergeInto(H2DatabaseConstants.TABLE__USERS)
                .columns(
                        H2DatabaseConstants.COLUMN__USERS__ID,
                        H2DatabaseConstants.COLUMN__USERS__NAME,
                        H2DatabaseConstants.COLUMN__USERS__GLOBAL_DISPLAY_NAME
                )
                .key(H2DatabaseConstants.COLUMN__USERS__ID)
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, user.id().id());
            statement.setString(2, user.name());
            statement.setString(3, user.globalDisplayName());
        });

        return user;
    }

    @Override
    public Optional<User> findById(UserID userID) {
        String sql = SQLBuilder
                .select(
                        H2DatabaseConstants.COLUMN__USERS__ID,
                        H2DatabaseConstants.COLUMN__USERS__NAME,
                        H2DatabaseConstants.COLUMN__USERS__GLOBAL_DISPLAY_NAME
                )
                .from(H2DatabaseConstants.TABLE__USERS)
                .where(H2DatabaseConstants.COLUMN__USERS__ID + " = ?")
                .build();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, userID.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(readUser(resultSet));
            }
        } catch (Exception e) {
            throw new StorageException("Failed to find user.", e);
        }
    }

    @Override
    public boolean existsById(UserID userID) {
        String sql = SQLBuilder
                .select("1")
                .from(H2DatabaseConstants.TABLE__USERS)
                .where(H2DatabaseConstants.COLUMN__USERS__ID + " = ?")
                .build();

        return exists(sql, statement -> statement.setString(1, userID.id()));
    }

    @Override
    public void deleteById(UserID userID) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__USERS)
                .where(H2DatabaseConstants.COLUMN__USERS__ID + " = ?")
                .build();

        executeUpdate(sql, statement -> statement.setString(1, userID.id()));
    }

    private User readUser(ResultSet resultSet) throws Exception {
        return new User(
                new UserID(resultSet.getString(H2DatabaseConstants.COLUMN__USERS__ID)),
                resultSet.getString(H2DatabaseConstants.COLUMN__USERS__NAME),
                resultSet.getString(H2DatabaseConstants.COLUMN__USERS__GLOBAL_DISPLAY_NAME)
        );
    }
}
