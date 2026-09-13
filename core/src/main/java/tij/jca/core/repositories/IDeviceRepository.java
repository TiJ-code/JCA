package tij.jca.core.repositories;

import tij.jca.core.entities.Device;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.List;

/**
 * Provides persistence operations for devices.
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IDeviceRepository extends IRepository<Device, DeviceID> {
    /**
     * Finds all devices associated with a user.
     *
     * @param userId the user identifier
     * @return the user's devices
     */
    List<Device> findByUserId(UserID userId);
}
