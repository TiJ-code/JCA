package tij.jca.application.services;

import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.core.entities.ChatRoom;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.storage.ITransactionManager;
import tij.jca.core.storage.exceptions.EntityNotFoundException;
import tij.jca.logging.ILogger;
import tij.jca.logging.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service for managing conversations and their membership.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class ConversationService {
    private static final ILogger LOGGER = LoggerFactory.getLogger(ConversationService.class);

    private final ITransactionManager transactionManager;

    /**
     * Creates a conversation service backed by the supplied transaction manager.
     *
     * @param transactionManager the manager used to execute conversation operations
     * @throws NullPointerException if the transaction manager is {@code null}
     */
    public ConversationService(ITransactionManager transactionManager) {
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager");
    }

    /**
     * Creates a new conversation
     *
     * @param name the conversation name
     * @return the created conversation
     */
    public ChatRoom createConversation(String name) {
        String validatedName = ServiceValidation.requireNonBlank(
                name,
                "name"
        );

        LOGGER.info("Creating conversation '{}'.", validatedName);

        return ServiceTransactionExecutor.execute(transactionManager, scope -> {
            ChatRoom conversation = new ChatRoom(
                    new ConversationID(UUIDGen.random()),
                    validatedName
            );

            return scope.conversations().save(conversation);
        });
    }

    /**
     * Finds a conversation by identifier.
     *
     * @param conversationId the conversation identifier
     * @return the matching conversation, if present
     */
    public Optional<ChatRoom> findChatRoom(ConversationID conversationId) {
        ConversationID validatedConversationId =
                ServiceValidation.requireNonNull(
                        conversationId,
                        "conversationId"
                );

        LOGGER.debug("Finding conversation '{}'.", validatedConversationId);

        return ServiceTransactionExecutor.execute(transactionManager, scope ->
                scope.conversations().findById(validatedConversationId)
        );
    }

    /**
     * Finds conversation identifiers associated with a server.
     *
     * @param serverId the server identifier
     * @return the server's chat room identifiers
     */
    public List<ConversationID> findByServerId(ServerID serverId) {
        ServerID validatedServerId = ServiceValidation.requireNonNull(
                serverId,
                "serverId"
        );

        LOGGER.debug("Finding conversations for server '{}'.", validatedServerId);

        return ServiceTransactionExecutor.execute(transactionManager, scope ->
                scope.conversations().findByServerId(validatedServerId)
        );
    }

    /**
     * Adds a user to a chat room.
     *
     * @param conversationId the conversation identifier
     * @param userId the user identifier
     * @throws ResourceNotFoundException if the conversation does not exist
     */
    public void addUser(ConversationID conversationId, UserID userId) {
        ConversationID validatedConversationId =
                ServiceValidation.requireNonNull(
                        conversationId,
                        "conversationId"
                );
        UserID validatedUserId = ServiceValidation.requireNonNull(
                userId,
                "userId"
        );

        LOGGER.info(
                "Adding user '{}' to conversation '{}'.",
                validatedUserId,
                validatedConversationId
        );

        ServiceTransactionExecutor.execute(transactionManager, scope -> {
            if (!scope.conversations()
                    .existsById(validatedConversationId)) {
                throw new EntityNotFoundException(
                        "Conversation with ID "
                                + validatedConversationId
                                + " not found."
                );
            }

            scope.conversations().addUser(
                    validatedConversationId,
                    validatedUserId
            );

            return null;
        });
    }
}
