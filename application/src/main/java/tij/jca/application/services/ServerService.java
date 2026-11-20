package tij.jca.application.services;

import tij.jca.core.entities.Server;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IServerRepository;
import tij.jca.core.storage.exceptions.EntityNotFoundException;

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
    private final IServerRepository serverRepository;

    /**
     * Creates a server service backed by the supplied repository.
     *
     * @param serverRepository the repository used to persist servers
     * @throws NullPointerException if the repository is {@code null}
     */
    public ServerService(IServerRepository serverRepository) {
        this.serverRepository = Objects.requireNonNull(serverRepository, "serverRepository");
    }

    /**
     * Creates a new server.
     *
     * @param name the server name
     * @return the created server
     */
    public Server createServer(String name) {
        Server server = new Server(new ServerID(UUIDGen.random()), name);
        return serverRepository.save(server);
    }

    /**
     * Finds a server by identifier.
     *
     * @param serverId the server identifier
     * @return the matching server, if present
     */
    public Optional<Server> findServer(ServerID serverId) {
        return serverRepository.findById(serverId);
    }

    /**
     * Finds servers by their name.
     *
     * @param name the server name
     * @return all servers matching the provided name
     */
    public List<Server> findServersByName(String name) {
        return serverRepository.findByName(name);
    }

    /**
     * Adds a user to a server.
     *
     * @param serverId the server identifier
     * @param userId the user identifier
     * @throws EntityNotFoundException if the server does not exist
     */
    public void addUser(ServerID serverId, UserID userId) {
        if (!serverRepository.existsById(serverId)) {
            throw new EntityNotFoundException("Server with ID " + serverId + " not found.");
        }

        serverRepository.addUser(serverId, userId);
    }

    /**
     * Associates a conversation with a server.
     *
     * @param serverId the server identifier
     * @param conversationId the conversation identifier
     * @throws EntityNotFoundException if the server does not exist
     */
    public void attachConversation(ServerID serverId, ConversationID conversationId) {
        if (!serverRepository.existsById(serverId)) {
            throw new EntityNotFoundException("Server with ID " + serverId + " not found.");
        }

        serverRepository.attachConversation(serverId, conversationId);
    }
}
