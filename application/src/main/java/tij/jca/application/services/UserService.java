package tij.jca.application.services;

import tij.jca.core.entities.Device;
import tij.jca.core.entities.User;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.exceptions.EntityNotFoundException;
import tij.jca.logging.ILogger;
import tij.jca.logging.LoggerFactory;

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
    private static final ILogger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final ITransactionManager transactionManager;

    /**
     * Creates a user service backed by the supplied transaction manager.
     *
     * @param transactionManager the manager used to execute user operations
     * @throws NullPointerException if the transaction manager is {@code null}
     */
    public UserService(ITransactionManager transactionManager) {
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager");
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
        LOGGER.info("Registering user '{}'.", username);
        return transactionManager.execute(scope -> {
            List<User> existing =
                    scope.users().findByUsername(username);

            if (!existing.isEmpty()) {
                throw new IllegalArgumentException(
                        "Username already exists: " + username
                );
            }

            User user = new User(
                    new UserID(UUIDGen.random()),
                    username,
                    globalDisplayName
            );

            return scope.users().save(user);
        });
    }

    /**
     * Finds a user by identifier.
     *
     * @param userId the user identifier
     * @return the matching user, if present
     */
    public Optional<User> findUser(UserID userId) {
        LOGGER.debug("Finding user '{}'.", userId);
        return transactionManager.execute(scope ->
                scope.users().findById(userId)
        );
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
        LOGGER.info(
                "Attaching device '{}' to user '{}'.",
                device.id(),
                userId
        );
        return transactionManager.execute(scope -> {
            User user = scope.users()
                    .findById(userId)
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "User with ID " + userId + " not found."
                            )
                    );

            scope.devices().save(device);
            scope.users().attachDevice(
                    userId,
                    device.id()
            );

            user.addDevice(device);

            return scope.users().save(user);
        });
    }

    /**
     * Removes a device association from a user.
     *
     * @param userId the user identifier
     * @param deviceId the device identifier to remove
     * @throws EntityNotFoundException if the user does not exist
     */
    public void detachDevice(UserID userId, DeviceID deviceId) {
        LOGGER.info(
                "Detaching device '{}' from user '{}'.",
                deviceId,
                userId
        );
        transactionManager.execute(scope -> {
            if (scope.users().findById(userId).isEmpty()) {
                throw new EntityNotFoundException(
                        "User with ID " + userId + " not found."
                );
            }

            scope.users().detachDevice(userId, deviceId);
            return null;
        });
    }
}
