package tij.jca.application.services;

import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.core.entities.Message;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.exceptions.EntityNotFoundException;
import tij.jca.logging.ILogger;
import tij.jca.logging.LoggerFactory;

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
    private static final ILogger LOGGER = LoggerFactory.getLogger(MessageService.class);

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
        ConversationID validatedConversationId =
                ServiceValidation.requireNonNull(
                        conversationId,
                        "conversationId"
                );
        UserID validatedAuthorId = ServiceValidation.requireNonNull(
                authorId,
                "authorId"
        );
        String validatedText = ServiceValidation.requireNonBlank(
                text,
                "text"
        );

        LOGGER.info(
                "Sending message from user '{}' to conversation '{}'.",
                validatedAuthorId,
                validatedConversationId
        );

        return ServiceTransactionExecutor.execute(transactionManager, scope -> {
            Message message = new Message(
                    new MessageID(UUIDGen.random()),
                    validatedAuthorId,
                    Instant.now(),
                    validatedText
            );

            scope.messages().save(message);
            scope.messages().attachToChatRoom(
                    message.id(),
                    validatedConversationId
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
        MessageID validatedMessageId = ServiceValidation.requireNonNull(
                messageId,
                "messageId"
        );

        LOGGER.debug("Finding message '{}'.", validatedMessageId);

        return ServiceTransactionExecutor.execute(transactionManager, scope ->
                scope.messages().findById(validatedMessageId)
        );
    }

    /**
     * Finds all messages belonging to a chat room.
     *
     * @param conversationId the chat room identifier
     * @return the chat room's messages
     */
    public List<Message> findMessages(ConversationID conversationId) {
        ConversationID validatedConversationId =
                ServiceValidation.requireNonNull(
                        conversationId,
                        "conversationId"
                );

        LOGGER.debug(
                "Finding messages for conversation '{}'.",
                validatedConversationId
        );

        return ServiceTransactionExecutor.execute(transactionManager, scope ->
                scope.messages().findByChatRoomId(validatedConversationId)
        );
    }

    /**
     * Deletes a message from a chat room.
     *
     * @param messageId the message identifier
     * @param conversationId the chat room identifier
     * @throws ResourceNotFoundException if the message does not exist
     */
    public void deleteMessage(MessageID messageId, ConversationID conversationId) {
        MessageID validatedMessageId = ServiceValidation.requireNonNull(
                messageId,
                "messageId"
        );
        ConversationID validatedConversationId =
                ServiceValidation.requireNonNull(
                        conversationId,
                        "conversationId"
                );

        LOGGER.info(
                "Deleting message '{}' from conversation '{}'.",
                validatedMessageId,
                validatedConversationId
        );

        ServiceTransactionExecutor.execute(transactionManager, scope -> {
            if (!scope.messages().existsById(validatedMessageId)) {
                throw new EntityNotFoundException(
                        "Message with ID " + validatedMessageId + " not found."
                );
            }

            scope.messages().detachFromChatRoom(
                    validatedMessageId,
                    validatedConversationId
            );

            scope.messages().deleteById(validatedMessageId);
            return null;
        });
    }
}
