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

/**
 * Application service for managing users and their associated devices.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class UserService {
    private final IUserRepository userRepository;
    private final IDeviceRepository deviceRepository;

    /**
     * Creates a user service backed by the supplied repositories.
     *
     * @param userRepository the repository used to persist users
     * @param deviceRepository the repository used to persist devices
     * @throws NullPointerException if either repository is {@code null}
     */
    public UserService(IUserRepository userRepository, IDeviceRepository deviceRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.deviceRepository = Objects.requireNonNull(deviceRepository, "deviceRepository");
    }

    /**
     * Registers a new user with the given username and global display name.
     *
     * @param username the unique username to register
     * @param globalDisplayName the display name shown across the application
     * @return the created user
     * @throws IllegalArgumentException if the username is already taken
     */
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

    /**
     * Finds a user by identifier.
     *
     * @param userId the user identifier
     * @return the matching user, if present
     */
    public Optional<User> findUser(UserID userId) {
        return userRepository.findById(userId);
    }

    /**
     * Attaches a device to a user and persists the relationship.
     *
     * @param userId the user identifier
     * @param device the device to associate with the user
     * @return the updated user
     * @throws EntityNotFoundException if the user does not exist
     */
    public User attachDevice(UserID userId, Device device) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found."));

        deviceRepository.save(device);
        userRepository.attachDevice(userId, device.id());
        user.addDevice(device);

        return userRepository.save(user);
    }

    /**
     * Removes a device association from a user.
     *
     * @param userId the user identifier
     * @param deviceId the device identifier to remove
     * @throws EntityNotFoundException if the user does not exist
     */
    public void detachDevice(UserID userId, DeviceID deviceId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }

        userRepository.detachDevice(userId, deviceId);
    }
}
