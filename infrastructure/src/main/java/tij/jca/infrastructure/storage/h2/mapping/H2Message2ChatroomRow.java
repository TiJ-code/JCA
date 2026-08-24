package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;

/**
 * Persistence representation of a row in the {@code message_chatroom_mapping} table.
 *
 * @param conversationId the associated conversation identifier
 * @param messageId the associated message identifier
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2Message2ChatroomRow(
        ConversationID conversationId,
        MessageID messageId
) {
}
