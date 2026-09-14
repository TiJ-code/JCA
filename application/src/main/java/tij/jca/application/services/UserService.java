package tij.jca.application.services;

import tij.jca.core.entities.Device;
import tij.jca.core.entities.User;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IDeviceRepository;
import tij.jca.core.repositories.IUserRepository;
import tij.jca.core.storage.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class UserService {
    private final IUserRepository userRepository;
    private final IDeviceRepository deviceRepository;

    public UserService(IUserRepository userRepository, IDeviceRepository deviceRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.deviceRepository = Objects.requireNonNull(deviceRepository, "deviceRepository");
    }

    public User registerUser(String username, String globalDisplayName) {
        List<User> existing = userRepository.findByUsername(username);

        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        User user = new User(
                new UserID(UUIDGen.random()),
                username,
                globalDisplayName
        );

        return userRepository.save(user);
    }

    public Optional<User> findUser(UserID userId) {
        return userRepository.findById(userId);
    }

    public User attachDevice(UserID userId, Device device) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found."));

        deviceRepository.save(device);
        userRepository.attachDevice(userId, device.id());
        user.addDevice(device);

        return userRepository.save(user);
    }

    public void detachDevice(UserID userId, DeviceID deviceId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }

        userRepository.detachDevice(userId, deviceId);
    }
}
