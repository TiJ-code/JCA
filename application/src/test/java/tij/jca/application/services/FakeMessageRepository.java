package tij.jca.application.services;

import tij.jca.core.entities.Message;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.repositories.IMessageRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class FakeMessageRepository implements IMessageRepository {
    private final Map<MessageID, Message> messages = new HashMap<>();
    private final Map<ConversationID, List<Message>> byChatRoom = new HashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.id(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(MessageID id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public boolean existsById(MessageID id) {
        return messages.containsKey(id);
    }

    @Override
    public void deleteById(MessageID id) {
        messages.remove(id);
        byChatRoom.values().forEach(list -> list.removeIf(m -> m.id().equals(id)));
    }

    @Override
    public List<Message> findByChatRoomId(ConversationID chatRoomId) {
        return List.copyOf(byChatRoom.getOrDefault(chatRoomId, List.of()));
    }

    @Override
    public void attachToChatRoom(MessageID messageID, ConversationID chatRoomId) {
        byChatRoom.computeIfAbsent(chatRoomId, key -> new ArrayList<>())
                .add(messages.get(messageID));
    }

    @Override
    public void detachFromChatRoom(MessageID messageID, ConversationID chatRoomId) {
        List<Message> list = byChatRoom.get(chatRoomId);
        if (list != null) {
            list.removeIf(m -> m.id().equals(messageID));
        }
    }
}
