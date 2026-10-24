package tij.jca.application.services;

import tij.jca.application.exceptions.ResourceNotFoundException;
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
        String validatedUsername = ServiceValidation.requireNonBlank(
                username, "username"
        );
        String validatedDisplayName = ServiceValidation.requireNonBlank(
                globalDisplayName, "globalDisplayName"
        );

        LOGGER.info("Registering user '{}'.", username);

        return ServiceTransactionExecutor.execute(transactionManager, scope -> {
            List<User> existing =
                    scope.users().findByUsername(validatedUsername);

            if (!existing.isEmpty()) {
                throw new IllegalArgumentException(
                        "Username already exists: " + validatedUsername
                );
            }

            User user = new User(
                    new UserID(UUIDGen.random()),
                    validatedUsername,
                    validatedDisplayName
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
        UserID validatedUserId = ServiceValidation.requireNonNull(
                userId,
                "userId"
        );

        LOGGER.debug("Finding user '{}'.", userId);

        return ServiceTransactionExecutor.execute(transactionManager, scope ->
                scope.users().findById(validatedUserId)
        );
    }

    /**
     * Attaches a device to a user and persists the relationship.
     *
     * @param userId the user identifier
     * @param device the device to associate with the user
     * @return the updated user
     * @throws ResourceNotFoundException if the user does not exist
     */
    public User attachDevice(UserID userId, Device device) {
        UserID validatedUserId = ServiceValidation.requireNonNull(
                userId, "userId"
        );
        Device validatedDevice = ServiceValidation.requireNonNull(
                device, "device"
        );

        LOGGER.info(
                "Attaching device '{}' to user '{}'.",
                validatedDevice.id(),
                validatedUserId
        );

        return ServiceTransactionExecutor.execute(transactionManager, scope -> {
            User user = scope.users()
                    .findById(validatedUserId)
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "User with ID " + validatedUserId + " not found."
                            )
                    );

            scope.devices().save(validatedDevice);
            scope.users().attachDevice(
                    validatedUserId,
                    validatedDevice.id()
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
     * @throws ResourceNotFoundException if the user does not exist
     */
    public void detachDevice(UserID userId, DeviceID deviceId) {
        UserID validatedUserId = ServiceValidation.requireNonNull(
                userId, "userId"
        );
        DeviceID validatedDeviceId = ServiceValidation.requireNonNull(
                deviceId, "deviceId"
        );

        LOGGER.info(
                "Detaching device '{}' from user '{}'.",
                validatedDeviceId,
                validatedUserId
        );

        ServiceTransactionExecutor.execute(transactionManager, scope -> {
            if (scope.users().findById(validatedUserId).isEmpty()) {
                throw new EntityNotFoundException(
                        "User with ID " + validatedUserId + " not found."
                );
            }

            scope.users().detachDevice(validatedUserId, validatedDeviceId);
            return null;
        });
    }
}
