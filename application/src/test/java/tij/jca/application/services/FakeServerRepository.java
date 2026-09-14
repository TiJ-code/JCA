package tij.jca.application.services;

import tij.jca.core.entities.Server;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IServerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class FakeServerRepository implements IServerRepository {
    private final Map<ServerID, Server> servers = new HashMap<>();
    private final Map<ServerID, Set<UserID>> users = new HashMap<>();
    private final Map<ServerID, List<ConversationID>> chatRooms = new HashMap<>();

    @Override
    public Server save(Server server) {
        servers.put(server.id(), server);
        users.putIfAbsent(server.id(), new HashSet<>());
        chatRooms.putIfAbsent(server.id(), new ArrayList<>());
        return server;
    }

    @Override
    public Optional<Server> findById(ServerID id) {
        return Optional.ofNullable(servers.get(id));
    }

    @Override
    public boolean existsById(ServerID id) {
        return servers.containsKey(id);
    }

    @Override
    public void deleteById(ServerID id) {
        servers.remove(id);
        users.remove(id);
        chatRooms.remove(id);
    }

    @Override
    public List<Server> findByName(String name) {
        return servers.values().stream()
                .filter(s -> s.name().equals(name))
                .toList();
    }

    @Override
    public Set<UserID> findUserIds(ServerID serverId) {
        return Set.copyOf(users.getOrDefault(serverId, Set.of()));
    }

    @Override
    public List<ConversationID> findChatRoomIds(ServerID serverId) {
        return List.copyOf(chatRooms.getOrDefault(serverId, List.of()));
    }

    @Override
    public void addUser(ServerID serverId, UserID userId) {
        users.computeIfAbsent(serverId, key -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeUser(ServerID serverId, UserID userId) {
        Set<UserID> set = users.get(serverId);
        if (set != null) {
            set.remove(userId);
        }
    }

    @Override
    public void attachConversation(ServerID serverId, ConversationID chatRoomId) {
        chatRooms.computeIfAbsent(serverId, key -> new ArrayList<>()).add(chatRoomId);
    }

    @Override
    public void detachChatRoom(ServerID serverId, ConversationID chatRoomId) {
        List<ConversationID> list = chatRooms.get(serverId);
        if (list != null) {
            list.remove(chatRoomId);
        }
    }
}
