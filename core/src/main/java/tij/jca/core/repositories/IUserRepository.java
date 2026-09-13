package tij.jca.core.repositories;

import tij.jca.core.entities.User;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.List;
import java.util.Set;

/**
 * Provides persistence operations for users and their device associations.
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IUserRepository extends IRepository<User, UserID> {
    /**
     * Finds a list of users by their username.
     *
     * @param username the user name
     * @return a list of matching users
     */
    List<User> findByUsername(String username);

    /**
     * Finds all devices associated with a user.
     *
     * @param userId the user identifier
     * @return the user's device identifiers
     */
    Set<DeviceID> findDeviceIds(UserID userId);

    /**
     * Associates a device with a user.
     *
     * @param userId the user identifier
     * @param deviceId the device identifier
     */
    void attachDevice(UserID userId, DeviceID deviceId);

    /**
     * Removes a device association from a user.
     *
     * @param userId the user identifier
     * @param deviceId the device identifier
     */
    void detachDevice(UserID userId, DeviceID deviceId);
}
