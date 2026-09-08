package tij.jca.core.entities;

import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;

import java.util.Objects;

public record ServerChatRoomAssociation(ServerID serverId, ConversationID chatRoomId) {
    public ServerChatRoomAssociation {
        Objects.requireNonNull(serverId, "serverId");
        Objects.requireNonNull(chatRoomId, "chatRoomId");
    }
}
