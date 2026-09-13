package tij.jca.core.repositories;

import tij.jca.core.entities.User;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.Optional;
import java.util.Set;

public interface IUserRepository extends IRepository<User, UserID> {
    Optional<User> findByUsername(String username);

    Set<DeviceID> findDeviceIds(UserID userId);

    void attachDevice(UserID userId, DeviceID deviceId);

    void detachDevice(UserID userId, DeviceID deviceId);
}
