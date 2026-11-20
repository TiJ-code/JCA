package tij.jca.application.services;

import tij.jca.core.entities.User;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IUserRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class FakeUserRepository implements IUserRepository {
    private final Map<UserID, User> users = new HashMap<>();
    private final Map<UserID, Set<DeviceID>> devices = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.id(), user);
        devices.putIfAbsent(user.id(), new HashSet<>());
        return user;
    }

    @Override
    public Optional<User> findById(UserID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsById(UserID id) {
        return users.containsKey(id);
    }

    @Override
    public void deleteById(UserID id) {
        users.remove(id);
        devices.remove(id);
    }

    @Override
    public List<User> findByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.name().equals(username))
                .toList();
    }

    @Override
    public Set<DeviceID> findDeviceIds(UserID userId) {
        return Set.copyOf(devices.getOrDefault(userId, Set.of()));
    }

    @Override
    public void attachDevice(UserID userId, DeviceID deviceId) {
        devices.computeIfAbsent(userId, key -> new HashSet<>()).add(deviceId);
    }

    @Override
    public void detachDevice(UserID userId, DeviceID deviceId) {
        Set<DeviceID> set = devices.get(userId);
        if (set != null) {
            set.remove(deviceId);
        }
    }
}
