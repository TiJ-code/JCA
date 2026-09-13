package tij.jca.core.repositories;

import tij.jca.core.entities.ChatRoom;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.UserID;

import java.util.List;
import java.util.Set;

/**
 * Provides persistence operations for chatrooms and their members.
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface IChatRoomRepository extends IRepository<ChatRoom, ConversationID> {
    /**
     * Finds chatrooms hosted by a server.
     *
     * @param serverId the server identifier
     * @return the server's chatroom identifiers
     */
    List<ConversationID> findByServerId(String serverId);

    /**
     * Finds users participating in a chatroom.
     *
     * @param chatRoomId the chatrooms identifier
     * @return the chatroom's member identifiers
     */
    Set<UserID> findUserIds(ConversationID chatRoomId);

    /**
     * Adds a user to a chatroom.
     * @param chatRoomId the chatroom identifier
     * @param userId the user identifier
     */
    void addUser(ConversationID chatRoomId, UserID userId);

    /**
     * Removes a user from a chatroom.
     *
     * @param chatRoomId the chatroom identifier
     * @param userId the user identifier
     */
    void removeUser(ConversationID chatRoomId, UserID userId);
}
