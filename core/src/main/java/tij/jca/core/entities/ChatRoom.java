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

public final class ChatRoom {
    private final ConversationID id;
    private final String name;
    private final Set<UserID> userIds = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    public ChatRoom(ConversationID id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    public void addUser(UserID userId) {
        userIds.add(Objects.requireNonNull(userId, "userId"));
    }

    public void removeUser(UserID userId) {
        userIds.remove(Objects.requireNonNull(userId, "userId"));
    }

    public void addMessage(Message message) {
        Objects.requireNonNull(message, "message");

        if (messages.parallelStream().anyMatch(m -> m.id().equals(message.id()))) {
            throw new IllegalArgumentException("A message with this ID already exists.");
        }

        messages.add(message);
    }

    public void removeMessage(MessageID messageId) {
        Objects.requireNonNull(messageId, "messageId");
        messages.removeIf(m -> m.id().equals(messageId));
    }

    public ConversationID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Set<UserID> userIds() {
        return Collections.unmodifiableSet(userIds);
    }

    public List<Message> messages() {
        return Collections.unmodifiableList(messages);
    }
}
