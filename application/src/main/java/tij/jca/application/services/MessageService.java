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

/**
 * Application service for creating, retrieving, and deleting messages.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class MessageService {
    private final IMessageRepository messageRepository;

    /**
     * Creates a message service backed by the supplied repository.
     *
     * @param messageRepository the repository used to persist messages
     * @throws NullPointerException if the repository is {@code null}
     */
    public MessageService(IMessageRepository messageRepository) {
        this.messageRepository = Objects.requireNonNull(messageRepository, "messageRepository");
    }

    /**
     * Sends a new message to a chat room.
     *
     * @param conversationId the target chat room identifier
     * @param authorId the author identifier
     * @param text the textual content of the message
     * @return the stored message
     */
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

    /**
     * Finds a message by identifier.
     *
     * @param messageId the message identifier
     * @return the matching message, if present
     */
    public Optional<Message> findMessage(MessageID messageId) {
        return messageRepository.findById(messageId);
    }

    /**
     * Finds all messages belonging to a chat room.
     *
     * @param conversationId the chat room identifier
     * @return the chat room's messages
     */
    public List<Message> findMessages(ConversationID conversationId) {
        return messageRepository.findByChatRoomId(conversationId);
    }

    /**
     * Deletes a message from a chat room.
     *
     * @param messageId the message identifier
     * @param conversationId the chat room identifier
     * @throws EntityNotFoundException if the message does not exist
     */
    public void deleteMessage(MessageID messageId, ConversationID conversationId) {
        if (!messageRepository.existsById(messageId)) {
            throw new EntityNotFoundException("Message with ID " + messageId + " not found.");
        }

        messageRepository.detachFromChatRoom(messageId, conversationId);
        messageRepository.deleteById(messageId);
    }
}
