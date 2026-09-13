package tij.jca.core.repositories;

import tij.jca.core.entities.ChatRoom;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.UserID;

import java.util.List;
import java.util.Set;

public interface IChatRoomRepository extends IRepository<ChatRoom, ConversationID> {
    List<ConversationID> findByServerId(String serverId);

    Set<UserID> findUserIds(ConversationID chatRoomId);

    void addUser(ConversationID chatRoomId, UserID userId);

    void removeUser(ConversationID chatRoomId, UserID userId);
}
