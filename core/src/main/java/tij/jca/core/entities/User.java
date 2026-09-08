package tij.jca.core.entities;

import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a JCA user and the devices associated with that user.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class User {
    private final UserID id;
    private final String name;
    private final String globalDisplayName;
    private final Set<DeviceID> deviceIds = new HashSet<>();

    /**
     * Creates a user.
     *
     * @param id the user identifier
     * @param name the unique user name
     * @param globalDisplayName the display name used globally
     * @throws NullPointerException if any argument is null
     */
    public User(UserID id, String name, String globalDisplayName) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.globalDisplayName = Objects.requireNonNull(globalDisplayName, "globalDisplayName");
    }

    /**
     * Associates a device with this user.
     *
     * @param device the device to add
     * @throws NullPointerException if {@code device} is null
     */
    public void addDevice(Device device) {
        Objects.requireNonNull(device, "device");
        deviceIds.add(device.id());
    }

    /**
     * Removes a device association.
     *
     * @param deviceId the device identifier to remove
     * @throws NullPointerException if {@code deviceId} is null
     */
    public void removeDevice(DeviceID deviceId) {
        deviceIds.remove(Objects.requireNonNull(deviceId, "deviceId"));
    }

    /**
     * Returns this user's identifier.
     *
     * @return this user's identifier
     */
    public UserID id() {
        return id;
    }

    /**
     * Returns this user's name.
     *
     * @return this user's name
     */
    public String name() {
        return name;
    }

    /**
     * Returns this user's global display name.
     *
     * @return this user's global display name
     */
    public String globalDisplayName() {
        return globalDisplayName;
    }

    /**
     * Returns the associated device identifiers.
     *
     * @return an unmodifiable view of the device identifiers
     */
    public Set<DeviceID> deviceIds() {
        return Collections.unmodifiableSet(deviceIds);
    }
}
