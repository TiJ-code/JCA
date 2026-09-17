package tij.jca.application.services;

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
        LOGGER.info("Creating server '{}'.", name);
        return transactionManager.execute(scope -> {
            Server server = new Server(
                    new ServerID(UUIDGen.random()),
                    name
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
        LOGGER.debug("Finding server '{}'.", serverId);
        return transactionManager.execute(scope ->
            scope.servers().findById(serverId)
        );
    }

    /**
     * Finds servers by their name.
     *
     * @param name the server name
     * @return all servers matching the provided name
     */
    public List<Server> findServersByName(String name) {
        LOGGER.debug("Finding servers named '{}'.", name);
        return transactionManager.execute(scope ->
                scope.servers().findByName(name)
        );
    }

    /**
     * Adds a user to a server.
     *
     * @param serverId the server identifier
     * @param userId the user identifier
     * @throws EntityNotFoundException if the server does not exist
     */
    public void addUser(ServerID serverId, UserID userId) {
        LOGGER.info(
                "Adding user '{}' to server '{}'.",
                userId,
                serverId
        );
        transactionManager.execute(scope -> {
            if (!scope.servers().existsById(serverId)) {
                throw new EntityNotFoundException(
                        "Server with ID " + serverId + " not found."
                );
            }

            scope.servers().addUser(serverId, userId);
            return null;
        });
    }

    /**
     * Associates a conversation with a server.
     *
     * @param serverId the server identifier
     * @param conversationId the conversation identifier
     * @throws EntityNotFoundException if the server does not exist
     */
    public void attachConversation(ServerID serverId, ConversationID conversationId) {
        LOGGER.info(
                "Attaching conversation '{}' to server '{}'.",
                conversationId,
                serverId
        );
        transactionManager.execute(scope -> {
            if (!scope.servers().existsById(serverId)) {
                throw new EntityNotFoundException(
                        "Server with ID " + serverId + " not found."
                );
            }

            scope.servers().attachConversation(
                    serverId,
                    conversationId
            );

            return null;
        });
    }
}
