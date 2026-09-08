package tij.jca.core.entities;

import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.Objects;

public record UserDeviceAssociation(UserID userId, DeviceID deviceId) {
    public UserDeviceAssociation {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(deviceId, "deviceId");
    }
}
