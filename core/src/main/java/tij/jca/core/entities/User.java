package tij.jca.core.entities;

import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class User {
    private final UserID id;
    private final String name;
    private final String globalDisplayName;
    private final Set<DeviceID> deviceIds = new HashSet<>();

    public User(UserID id, String name, String globalDisplayName) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.globalDisplayName = Objects.requireNonNull(globalDisplayName, "globalDisplayName");
    }

    public void addDevice(Device device) {
        Objects.requireNonNull(device, "device");
        deviceIds.add(device.id());
    }

    public void removeDevice(DeviceID deviceId) {
        deviceIds.remove(Objects.requireNonNull(deviceId, "deviceId"));
    }

    public UserID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String globalDisplayName() {
        return globalDisplayName;
    }

    public Set<DeviceID> deviceIds() {
        return Collections.unmodifiableSet(deviceIds);
    }
}
