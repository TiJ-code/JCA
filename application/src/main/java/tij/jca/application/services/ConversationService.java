package tij.jca.application.services;

import tij.jca.core.entities.ChatRoom;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IConversationRepository;
import tij.jca.core.storage.exceptions.EntityNotFoundException;

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
    private final IConversationRepository conversationRepository;

    /**
     * Creates a conversation service backed by the supplied repository.
     *
     * @param conversationRepository the repository used to persist conversations
     * @throws NullPointerException if the repository is {@code null}
     */
    public ConversationService(IConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository, "conversationRepository");
    }

    /**
     * Creates a new conversation
     *
     * @param name the conversation name
     * @return the created conversation
     */
    public ChatRoom createConversation(String name) {
        ChatRoom conversation = new ChatRoom(new ConversationID(UUIDGen.random()), name);
        return conversationRepository.save(conversation);
    }

    /**
     * Finds a conversation by identifier.
     *
     * @param conversationId the conversation identifier
     * @return the matching conversation, if present
     */
    public Optional<ChatRoom> findChatRoom(ConversationID conversationId) {
        return conversationRepository.findById(conversationId);
    }

    /**
     * Finds conversation identifiers associated with a server.
     *
     * @param serverId the server identifier
     * @return the server's chat room identifiers
     */
    public List<ConversationID> findByServerId(ServerID serverId) {
        return conversationRepository.findByServerId(serverId);
    }

    /**
     * Adds a user to a chat room.
     *
     * @param conversationId the conversation identifier
     * @param userId the user identifier
     * @throws EntityNotFoundException if the conversation does not exist
     */
    public void addUser(ConversationID conversationId, UserID userId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new EntityNotFoundException("Conversation with ID " + conversationId + " not found.");
        }

        conversationRepository.addUser(conversationId, userId);
    }
}
