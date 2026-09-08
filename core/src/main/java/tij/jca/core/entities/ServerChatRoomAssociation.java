package tij.jca.core.entities;

import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;

import java.util.Objects;

/**
 * Associates a server with one of its chatrooms.
 *
 * @param serverId the server identifier
 * @param chatRoomId the chatroom identifier
 * @since 0.1.0
 * @author TiJ
 */
public record ServerChatRoomAssociation(ServerID serverId, ConversationID chatRoomId) {
    /**
     * Creates an association after validating both identifiers.
     *
     * @throws NullPointerException if an identifier is null
     */
    public ServerChatRoomAssociation {
        Objects.requireNonNull(serverId, "serverId");
        Objects.requireNonNull(chatRoomId, "chatRoomId");
    }
}
