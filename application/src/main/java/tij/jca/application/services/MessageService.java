package tij.jca.application.services;

import tij.jca.core.entities.Message;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.storage.ITransactionManager;
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
    private final ITransactionManager transactionManager;

    /**
     * Creates a message service backed by the supplied transaction manager.
     *
     * @param transactionManager the manager used to execute message operations
     * @throws NullPointerException if the transaction manager is {@code null}
     */
    public MessageService(ITransactionManager transactionManager) {
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager");
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
        return transactionManager.execute(scope -> {
            Message message = new Message(
                    new MessageID(UUIDGen.random()),
                    authorId,
                    Instant.now(),
                    text
            );

            scope.messages().save(message);
            scope.messages().attachToChatRoom(
                    message.id(),
                    conversationId
            );

            return message;
        });
    }

    /**
     * Finds a message by identifier.
     *
     * @param messageId the message identifier
     * @return the matching message, if present
     */
    public Optional<Message> findMessage(MessageID messageId) {
        return transactionManager.execute(scope ->
                scope.messages().findById(messageId)
        );
    }

    /**
     * Finds all messages belonging to a chat room.
     *
     * @param conversationId the chat room identifier
     * @return the chat room's messages
     */
    public List<Message> findMessages(ConversationID conversationId) {
        return transactionManager.execute(scope ->
                scope.messages().findByChatRoomId(conversationId)
        );
    }

    /**
     * Deletes a message from a chat room.
     *
     * @param messageId the message identifier
     * @param conversationId the chat room identifier
     * @throws EntityNotFoundException if the message does not exist
     */
    public void deleteMessage(MessageID messageId, ConversationID conversationId) {
        transactionManager.execute(scope -> {
            if (!scope.messages().existsById(messageId)) {
                throw new EntityNotFoundException(
                        "Message with ID " + messageId + " not found."
                );
            }

            scope.messages().detachFromChatRoom(
                    messageId,
                    conversationId
            );

            scope.messages().deleteById(messageId);
            return null;
        });
    }
}
