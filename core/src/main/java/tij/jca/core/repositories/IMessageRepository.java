package tij.jca.core.repositories;

import tij.jca.core.entities.Message;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;

import java.util.List;

/**
 * Provides persistence operations for messages and chatroom associations.
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IMessageRepository extends IRepository<Message, MessageID> {
    /**
     * Finds messages belonging to a chatroom.
     *
     * @param chatRoomId the chatroom identifier
     * @return the chatroom's messages, preferably ordered chronologically
     */
    List<Message> findByChatRoomId(ConversationID chatRoomId);

    /**
     * Associates a message with a chatroom.
     *
     * @param messageID the message identifier
     * @param chatRoomId the chatroom identifier
     */
    void attachToChatRoom(MessageID messageID, ConversationID chatRoomId);

    /**
     * Removes a message association from a chatroom.
     *
     * @param messageID the message identifier
     * @param chatRoomId the chatroom identifier
     */
    void detachFromChatRoom(MessageID messageID, ConversationID chatRoomId);
}
