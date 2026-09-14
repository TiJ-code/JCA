package tij.jca.infrastructure.storage.h2.repository;

import tij.jca.core.entities.Device;
import tij.jca.core.entities.DeviceType;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IDeviceRepository;
import tij.jca.core.storage.exceptions.StorageException;
import tij.jca.infrastructure.storage.h2.H2DatabaseConstants;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;
import tij.jca.infrastructure.storage.h2.helper.SQLBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * H2-backed implementation of {@link IDeviceRepository}.
 *
 * <p>
 * This repository stores device records and exposes the relationship between
 * devices and the users who own or use them.
 * </p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class H2DeviceRepository extends AbstractH2Repository implements IDeviceRepository {
    /**
     * Creates a repository that uses the provided H2 transaction context.
     *
     * @param transaction the storage transaction used to access the database
     */
    public H2DeviceRepository(H2StorageTransaction transaction) {
        super(transaction);
    }

    @Override
    public List<Device> findByUserId(UserID userId) {
        String sql = SQLBuilder
                .select(
                        "d." + H2DatabaseConstants.COLUMN__DEVICES__ID,
                        "d." + H2DatabaseConstants.COLUMN__DEVICES__TYPE
                )
                .from(H2DatabaseConstants.TABLE__DEVICES + " d")
                .innerJoin(
                        H2DatabaseConstants.TABLE__USER_DEVICE_MAPPING + " m",
                        "m." + H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__DEVICE_ID + " = d." + H2DatabaseConstants.COLUMN__DEVICES__ID
                )
                .where("m." + H2DatabaseConstants.COLUMN__USER_DEVICE_MAPPING__USER_ID + " = ?")
                .build();

        List<Device> devices = new ArrayList<>();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, userId.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    devices.add(readDevice(resultSet));
                }
            }

            return List.copyOf(devices);
        } catch (Exception e) {
            throw new StorageException("Failed to find devices for user.", e);
        }
    }

    @Override
    public Device save(Device device) {
        String sql = SQLBuilder
                .mergeInto(H2DatabaseConstants.TABLE__DEVICES)
                .columns(H2DatabaseConstants.COLUMN__DEVICES__ID, H2DatabaseConstants.COLUMN__DEVICES__TYPE)
                .key(H2DatabaseConstants.COLUMN__DEVICES__ID)
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, device.id().id());
            statement.setString(2, device.type().databaseValue());
        });

        return device;
    }

    @Override
    public Optional<Device> findById(DeviceID deviceID) {
        String sql = SQLBuilder
                .select(
                        H2DatabaseConstants.COLUMN__DEVICES__ID,
                        H2DatabaseConstants.COLUMN__DEVICES__TYPE
                )
                .from(H2DatabaseConstants.TABLE__DEVICES)
                .where(H2DatabaseConstants.COLUMN__DEVICES__ID + " = ?")
                .build();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, deviceID.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(readDevice(resultSet));
            }
        } catch (Exception e) {
            throw new StorageException("Failed to find device.", e);
        }
    }

    @Override
    public boolean existsById(DeviceID deviceID) {
        String sql = SQLBuilder
                .select("1")
                .from(H2DatabaseConstants.TABLE__DEVICES)
                .where(H2DatabaseConstants.COLUMN__DEVICES__ID + " = ?")
                .build();

        return exists(sql, statement -> statement.setString(1, deviceID.id()));
    }

    @Override
    public void deleteById(DeviceID deviceID) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__DEVICES)
                .where(H2DatabaseConstants.COLUMN__DEVICES__ID + " = ?")
                .build();

        executeUpdate(sql, statement -> statement.setString(1, deviceID.id()));
    }

    /**
     * Maps the current row from the result set into a {@link Device} domain object.
     *
     * @param resultSet the result set position on a device record
     * @return the mapped device instance
     * @throws Exception if the row cannot be read
     */
    private static Device readDevice(ResultSet resultSet) throws Exception {
        return new Device(
                new DeviceID(resultSet.getString(H2DatabaseConstants.COLUMN__DEVICES__ID)),
                DeviceType.valueOf(
                        resultSet.getString(H2DatabaseConstants.COLUMN__DEVICES__TYPE).toUpperCase()
                )
        );
    }
}
