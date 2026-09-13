package tij.jca.core.repositories;

import tij.jca.core.entities.Server;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Provides persistence operations for servers and their relationships.
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IServerRepository extends IRepository<Server, ServerID> {
    /**
     * Find servers by their name.
     *
     * @param name the server name
     * @return a list of matching servers
     */
    List<Server> findByName(String name);

    /**
     * Finds users registered on a server.
     *
     * @param serverId the server identifier
     * @return the registered user identifiers
     */
    Set<UserID> findUserIds(ServerID serverId);

    /**
     * Finds chatrooms hosted by a server.
     *
     * @param serverId the server identifier
     * @return the hosted chatroom identifiers
     */
    List<ConversationID> findChatRoomIds(ServerID serverId);

    /**
     * Registers a user on a server.
     *
     * @param serverId the server identifier
     * @param userId the user identifier
     */
    void addUser(ServerID serverId, UserID userId);

    /**
     * Removes a user from a server.
     *
     * @param serverId the server identifier
     * @param userId the user identifier
     */
    void removeUser(ServerID serverId, UserID userId);

    /**
     * Associates a chatroom with a server.
     *
     * @param serverId the server identifier
     * @param chatRoomId the chatroom identifier
     */
    void attachChatRoom(ServerID serverId, ConversationID chatRoomId);

    /**
     * Removes a chatroom association from a server.
     *
     * @param serverId the server identifier
     * @param chatRoomId the chatroom identifier
     */
    void detachChatRoom(ServerID serverId, ConversationID chatRoomId);
}
