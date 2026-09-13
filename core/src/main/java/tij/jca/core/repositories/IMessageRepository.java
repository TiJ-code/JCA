package tij.jca.core.repositories;

import tij.jca.core.entities.Message;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;

import java.util.List;

public interface IMessageRepository extends IRepository<Message, MessageID> {
    List<Message> findByChatRoomId(ConversationID chatRoomId);

    void attachToChatRoom(MessageID messageID, ConversationID chatRoomId);

    void detachFromChatRoom(MessageID messageID, ConversationID chatRoomId);
}
