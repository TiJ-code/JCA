package tij.jca.core.entities;

import tij.jca.core.ids.DeviceID;

import java.util.Objects;

/**
 * Represents a client device belonging to a user.
 *
 * @param id the device identifier
 * @param type the type of device
 * @since 0.1.0
 * @author TiJ
 */
public record Device(DeviceID id, DeviceType type) {
    /**
     * Creates a device after validating its required values.
     *
     * @throws NullPointerException if {@code id} or {@code type} is null
     */
    public Device {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
    }
}
