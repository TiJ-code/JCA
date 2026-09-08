package tij.jca.core.entities;

import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.UserID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a chatroom with members and messages.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class ChatRoom {
    private final ConversationID id;
    private final String name;
    private final Set<UserID> userIds = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    /**
     * Creates a chatroom.
     *
     * @param id the chatroom identifier
     * @param name the chatroom name
     * @throws NullPointerException if an argument is null
     */
    public ChatRoom(ConversationID id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Adds a user to this chatroom.
     *
     * @param userId the user identifier
     * @throws NullPointerException if {@code userId} is null
     */
    public void addUser(UserID userId) {
        userIds.add(Objects.requireNonNull(userId, "userId"));
    }

    /**
     * Removes a user from this chatroom.
     *
     * @param userId the user identifier to remove
     * @throws NullPointerException if {@code userId} is null
     */
    public void removeUser(UserID userId) {
        userIds.remove(Objects.requireNonNull(userId, "userId"));
    }

    /**
     * Adds a message unless another message has the same identifier.
     *
     * @param message the message to add
     * @throws NullPointerException if {@code message} is null
     * @throws IllegalArgumentException if the message identifier already exists
     */
    public void addMessage(Message message) {
        Objects.requireNonNull(message, "message");

        if (messages.parallelStream().anyMatch(m -> m.id().equals(message.id()))) {
            throw new IllegalArgumentException("A message with this ID already exists.");
        }

        messages.add(message);
    }

    /**
     * Removes a message from this chatroom.
     *
     * @param messageId the message identifier to remove
     * @throws NullPointerException if {@code messageId} is null
     */
    public void removeMessage(MessageID messageId) {
        Objects.requireNonNull(messageId, "messageId");
        messages.removeIf(m -> m.id().equals(messageId));
    }

    /**
     * Returns this chatroom's identifier.
     *
     * @return this chatroom's identifier
     */
    public ConversationID id() {
        return id;
    }

    /**
     * Returns this chatroom's name.
     *
     * @return this chatroom's name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the identifiers of users currently in this chatroom.
     *
     * @return an unmodifiable view of the member identifiers
     */
    public Set<UserID> userIds() {
        return Collections.unmodifiableSet(userIds);
    }

    /**
     * Returns the messages currently stored in this chatroom.
     *
     * @return an unmodifiable view of this chatroom's messages
     */
    public List<Message> messages() {
        return Collections.unmodifiableList(messages);
    }
}
