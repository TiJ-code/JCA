package tij.jca.core.entities;

import tij.jca.core.ids.DeviceID;

import java.util.Objects;

public record Device(DeviceID id, DeviceType type) {
    public Device {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
    }
}
