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

public final class ServerService {
    private final IServerRepository serverRepository;

    public ServerService(IServerRepository serverRepository) {
        this.serverRepository = Objects.requireNonNull(serverRepository, "serverRepository");
    }

    public Server createServer(String name) {
        Server server = new Server(new ServerID(UUIDGen.random()), name);
        return serverRepository.save(server);
    }

    public Optional<Server> findServer(ServerID serverId) {
        return serverRepository.findById(serverId);
    }

    public List<Server> findServersByName(String name) {
        return serverRepository.findByName(name);
    }

    public void addUser(ServerID serverId, UserID userId) {
        if (!serverRepository.existsById(serverId)) {
            throw new EntityNotFoundException("Server with ID " + serverId + " not found.");
        }

        serverRepository.addUser(serverId, userId);
    }

    public void attachConversation(ServerID serverId, ConversationID conversationId) {
        if (!serverRepository.existsById(serverId)) {
            throw new EntityNotFoundException("Server with ID " + serverId + " not found.");
        }

        serverRepository.attachConversation(serverId, conversationId);
    }
}
