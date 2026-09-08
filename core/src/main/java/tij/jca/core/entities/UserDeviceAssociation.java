package tij.jca.core.entities;

import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.Objects;

/**
 * Associates a user with one of their devices.
 *
 * @param userId the user identifier
 * @param deviceId the device identifier
 * @since 0.1.0
 * @author TiJ
 */
public record UserDeviceAssociation(UserID userId, DeviceID deviceId) {
    /**
     * Creates an association after validating both identifiers.
     *
     * @throws NullPointerException if an identifier is null
     */
    public UserDeviceAssociation {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(deviceId, "deviceId");
    }
}
