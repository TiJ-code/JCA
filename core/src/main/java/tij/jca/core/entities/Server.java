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

/**
 * Represents a JCA server containing users and chatrooms.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class Server {
    private final ServerID id;
    private final String name;
    private final Set<UserID> userIds = new HashSet<>();
    private final Map<ConversationID, ChatRoom> chatRooms = new HashMap<>();

    /**
     * Creates a server.
     *
     * @param id the server identifier
     * @param name the server name
     * @throws NullPointerException if an argument is null
     */
    public Server(ServerID id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Adds a user to this server.
     *
     * @param userId the user identifier
     * @throws NullPointerException if {@code userId} is null
     */
    public void addUser(UserID userId) {
        userIds.add(Objects.requireNonNull(userId, "userId"));
    }

    /**
     * Removes a user from this server.
     *
     * @param userId the user identifier to remove
     * @throws NullPointerException if {@code userId} is null
     */
    public void removeUser(UserID userId) {
        userIds.remove(Objects.requireNonNull(userId, "userId"));
    }

    /**
     * Adds a chatroom to this server.
     *
     * @param chatroom the chatroom to add
     * @throws NullPointerException if {@code chatroom} is null
     * @throws IllegalArgumentException if its identifier already exists
     */
    public void addChatRoom(ChatRoom chatroom) {
        Objects.requireNonNull(chatroom, "chatroom");

        if (chatRooms.putIfAbsent(chatroom.id(), chatroom) != null) {
            throw new IllegalArgumentException("A chatroom with this ID already exists.");
        }
    }

    /**
     * Removes a chatroom from this server.
     *
     * @param conversationId the chatroom identifier to remove
     * @throws NullPointerException if {@code conversationId} is null
     */
    public void removeChatRoom(ConversationID conversationId) {
        chatRooms.remove(Objects.requireNonNull(conversationId, "conversationId"));
    }

    /**
     * Finds a chatroom by identifier.
     *
     * @param conversationId the chatroom identifier
     * @return the chatroom, or null when it is not present
     * @throws NullPointerException if {@code conversationId} is null
     */
    public ChatRoom chatRoom(ConversationID conversationId) {
        return chatRooms.get(Objects.requireNonNull(conversationId, "conversationId"));
    }

    /**
     * Returns this server's identifier.
     *
     * @return this server's identifier
     */
    public ServerID id() {
        return id;
    }

    /**
     * Returns this server's name.
     *
     * @return this server's name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the identifiers of users registered on this server.
     *
     * @return an unmodifiable view of the server's user identifiers
     */
    public Set<UserID> userIds() {
        return Collections.unmodifiableSet(userIds);
    }

    /**
     * Returns the chatrooms hosted by this server.
     *
     * @return an unmodifiable view of the server's chatrooms
     */
    public Map<ConversationID, ChatRoom> chatRooms() {
        return Collections.unmodifiableMap(chatRooms);
    }
}
