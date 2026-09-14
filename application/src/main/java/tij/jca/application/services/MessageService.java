package tij.jca.application.services;

import tij.jca.core.entities.Message;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IMessageRepository;
import tij.jca.core.storage.exceptions.EntityNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MessageService {
    private final IMessageRepository messageRepository;

    public MessageService(IMessageRepository messageRepository) {
        this.messageRepository = Objects.requireNonNull(messageRepository, "messageRepository");
    }

    public Message sendMessage(ConversationID conversationId, UserID authorId, String text) {
        Message message = new Message(
                new MessageID(UUIDGen.random()),
                authorId,
                Instant.now(),
                text
        );

        messageRepository.save(message);
        messageRepository.attachToChatRoom(message.id(), conversationId);

        return message;
    }

    public Optional<Message> findMessage(MessageID messageId) {
        return messageRepository.findById(messageId);
    }

    public List<Message> findMessages(ConversationID conversationId) {
        return messageRepository.findByChatRoomId(conversationId);
    }

    public void deleteMessage(MessageID messageId, ConversationID conversationId) {
        if (!messageRepository.existsById(messageId)) {
            throw new EntityNotFoundException("Message with ID " + messageId + " not found.");
        }

        messageRepository.detachFromChatRoom(messageId, conversationId);
        messageRepository.deleteById(messageId);
    }
}
