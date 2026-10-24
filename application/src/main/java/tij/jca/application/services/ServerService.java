package tij.jca.application.services;

import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.core.entities.Server;
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
 * Application service for creating and managing servers and their relationships.
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class ServerService {
    private static final ILogger LOGGER = LoggerFactory.getLogger(ServerService.class);

    private final ITransactionManager transactionManager;

    /**
     * Creates a server service backed by the supplied transaction manager.
     *
     * @param transactionManager the manager used to execute server operations
     * @throws NullPointerException if the transaction manager is {@code null}
     */
    public ServerService(ITransactionManager transactionManager) {
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager");
    }

    /**
     * Creates a new server.
     *
     * @param name the server name
     * @return the created server
     */
    public Server createServer(String name) {
        String validatedName = ServiceValidation.requireNonBlank(
                name,
                "name"
        );

        LOGGER.info("Creating server '{}'.", validatedName);

        return ServiceTransactionExecutor.execute(transactionManager, scope -> {
            Server server = new Server(
                    new ServerID(UUIDGen.random()),
                    validatedName
            );

            return scope.servers().save(server);
        });
    }

    /**
     * Finds a server by identifier.
     *
     * @param serverId the server identifier
     * @return the matching server, if present
     */
    public Optional<Server> findServer(ServerID serverId) {
        ServerID validatedServerId = ServiceValidation.requireNonNull(
                serverId,
                "serverId"
        );

        LOGGER.debug("Finding server '{}'.", validatedServerId);

        return ServiceTransactionExecutor.execute(transactionManager, scope ->
            scope.servers().findById(validatedServerId)
        );
    }

    /**
     * Finds servers by their name.
     *
     * @param name the server name
     * @return all servers matching the provided name
     */
    public List<Server> findServersByName(String name) {
        String validatedName = ServiceValidation.requireNonBlank(
                name,
                "name"
        );

        LOGGER.debug("Finding servers named '{}'.", validatedName);

        return ServiceTransactionExecutor.execute(transactionManager, scope ->
                scope.servers().findByName(validatedName)
        );
    }

    /**
     * Adds a user to a server.
     *
     * @param serverId the server identifier
     * @param userId the user identifier
     * @throws ResourceNotFoundException if the server does not exist
     */
    public void addUser(ServerID serverId, UserID userId) {
        ServerID validatedServerId = ServiceValidation.requireNonNull(
                serverId,
                "serverId"
        );
        UserID validatedUserId = ServiceValidation.requireNonNull(
                userId,
                "userId"
        );

        LOGGER.info(
                "Adding user '{}' to server '{}'.",
                validatedUserId,
                validatedServerId
        );

        ServiceTransactionExecutor.execute(transactionManager, scope -> {
            if (!scope.servers().existsById(validatedServerId)) {
                throw new EntityNotFoundException(
                        "Server with ID " + validatedServerId + " not found."
                );
            }

            scope.servers().addUser(validatedServerId, validatedUserId);
            return null;
        });
    }

    /**
     * Associates a conversation with a server.
     *
     * @param serverId the server identifier
     * @param conversationId the conversation identifier
     * @throws ResourceNotFoundException if the server does not exist
     */
    public void attachConversation(ServerID serverId, ConversationID conversationId) {
        ServerID validatedServerId = ServiceValidation.requireNonNull(
                serverId,
                "serverId"
        );
        ConversationID validatedConversationId =
                ServiceValidation.requireNonNull(
                        conversationId,
                        "conversationId"
                );

        LOGGER.info(
                "Attaching conversation '{}' to server '{}'.",
                validatedConversationId,
                validatedServerId
        );

        ServiceTransactionExecutor.execute(transactionManager, scope -> {
            if (!scope.servers().existsById(validatedServerId)) {
                throw new EntityNotFoundException(
                        "Server with ID " + validatedServerId + " not found."
                );
            }

            scope.servers().attachConversation(
                    validatedServerId,
                    validatedConversationId
            );

            return null;
        });
    }
}
