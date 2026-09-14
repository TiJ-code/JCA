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

public final class ConversationService {
    private final IConversationRepository conversationRepository;

    public ConversationService(IConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository, "conversationRepository");
    }

    public ChatRoom createConversation(String name) {
        ChatRoom conversation = new ChatRoom(new ConversationID(UUIDGen.random()), name);
        return conversationRepository.save(conversation);
    }

    public Optional<ChatRoom> findChatRoom(ConversationID conversationId) {
        return conversationRepository.findById(conversationId);
    }

    public List<ConversationID> findByServerId(ServerID serverId) {
        return conversationRepository.findByServerId(serverId);
    }

    public void addUser(ConversationID conversationId, UserID userId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new EntityNotFoundException("Conversation with ID " + conversationId + " not found.");
        }

        conversationRepository.addUser(conversationId, userId);
    }
}
