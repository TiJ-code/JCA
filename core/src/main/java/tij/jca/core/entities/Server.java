package tij.jca.core.entities;

import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Server {
    private final ServerID id;
    private final String name;
    private final Set<UserID> userIds = new HashSet<>();
    private final Map<ConversationID, ChatRoom> chatRooms = new HashMap<>();

    public Server(ServerID id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    public void addUser(UserID userId) {
        userIds.add(Objects.requireNonNull(userId, "userId"));
    }

    public void removeUser(UserID userId) {
        userIds.remove(Objects.requireNonNull(userId, "userId"));
    }

    public void addChatRoom(ChatRoom chatroom) {
        Objects.requireNonNull(chatroom, "chatroom");

        if (chatRooms.putIfAbsent(chatroom.id(), chatroom) != null) {
            throw new IllegalArgumentException("A chatroom with this ID already exists.");
        }
    }

    public void removeChatRoom(ConversationID conversationId) {
        chatRooms.remove(Objects.requireNonNull(conversationId, "conversationId"));
    }

    public ChatRoom chatRoom(ConversationID conversationId) {
        return chatRooms.get(Objects.requireNonNull(conversationId, "conversationId"));
    }

    public ServerID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Set<UserID> userIds() {
        return Collections.unmodifiableSet(userIds);
    }

    public Map<ConversationID, ChatRoom> chatRooms() {
        return Collections.unmodifiableMap(chatRooms);
    }
}
