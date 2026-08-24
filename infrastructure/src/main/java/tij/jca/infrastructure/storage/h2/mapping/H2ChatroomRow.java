package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.ConversationID;

/**
 * Persistence representation of a row in the {@code chatrooms} table.
 *
 * @param id the stored chatroom identifier
 * @param name the chatroom name
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2ChatroomRow(
        ConversationID id,
        String name
) {
}
