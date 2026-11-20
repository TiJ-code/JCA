package tij.jca.application.services;

import tij.jca.core.entities.Device;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IDeviceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class FakeDeviceRepository implements IDeviceRepository {
    private final Map<DeviceID, Device> devices = new HashMap<>();
    private final Map<UserID, Set<DeviceID>> userToDevices = new HashMap<>();

    @Override
    public Device save(Device device) {
        devices.put(device.id(), device);
        return device;
    }

    @Override
    public Optional<Device> findById(DeviceID id) {
        return Optional.ofNullable(devices.get(id));
    }

    @Override
    public boolean existsById(DeviceID id) {
        return devices.containsKey(id);
    }

    @Override
    public void deleteById(DeviceID id) {
        devices.remove(id);
        userToDevices.values().forEach(set -> set.remove(id));
    }

    @Override
    public List<Device> findByUserId(UserID userId) {
        return userToDevices.getOrDefault(userId, Set.of()).stream()
                .map(devices::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
