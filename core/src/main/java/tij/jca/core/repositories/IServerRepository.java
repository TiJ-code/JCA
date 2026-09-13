package tij.jca.core.repositories;

import tij.jca.core.entities.Server;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IServerRepository extends IRepository<Server, ServerID> {
    Optional<Server> findByName(String name);

    Set<UserID> findUserIds(ServerID serverId);

    List<ConversationID> findChatRoomIds(ServerID serverId);

    void addUser(ServerID serverId, UserID userId);

    void removeUser(ServerID serverId, UserID userId);

    void attachChatRoom(ServerID serverId, ConversationID chatRoomId);

    void detachChatRoom(ServerID serverId, ConversationID chatRoomId);
}
