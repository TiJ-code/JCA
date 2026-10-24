package tij.jca.application.services;

import tij.jca.core.entities.ChatRoom;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IConversationRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class FakeConversationRepository implements IConversationRepository {
    private final Map<ConversationID, ChatRoom> rooms = new HashMap<>();
    private final Map<String, List<ConversationID>> byServer = new HashMap<>();
    private final Map<ConversationID, Set<UserID>> members = new HashMap<>();

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        rooms.put(chatRoom.id(), chatRoom);
        members.putIfAbsent(chatRoom.id(), new HashSet<>());
        return chatRoom;
    }

    @Override
    public Optional<ChatRoom> findById(ConversationID id) {
        return Optional.ofNullable(rooms.get(id));
    }

    @Override
    public boolean existsById(ConversationID id) {
        return rooms.containsKey(id);
    }

    @Override
    public void deleteById(ConversationID id) {
        rooms.remove(id);
        members.remove(id);
    }

    @Override
    public List<ConversationID> findByServerId(ServerID serverId) {
        return List.copyOf(byServer.getOrDefault(serverId.id(), List.of()));
    }

    @Override
    public Set<UserID> findUserIds(ConversationID chatRoomId) {
        return Set.copyOf(members.getOrDefault(chatRoomId, Set.of()));
    }

    @Override
    public void addUser(ConversationID chatRoomId, UserID userId) {
        members.computeIfAbsent(chatRoomId, key -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeUser(ConversationID chatRoomId, UserID userId) {
        Set<UserID> set = members.get(chatRoomId);
        if (set != null) {
            set.remove(userId);
        }
    }
}
