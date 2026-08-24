package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;

/**
 * Persistence representation of a row in the {@code chatroom_server_mapping} table.
 *
 * @param serverId the associated server identifier
 * @param chatroomId the associated chatroom identifier
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2Chatroom2ServerRow(
        ServerID serverId,
        ConversationID chatroomId
) {
}
