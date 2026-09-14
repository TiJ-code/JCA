package tij.jca.infrastructure.storage.h2.repository;

import tij.jca.core.entities.ChatRoom;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IChatRoomRepository;
import tij.jca.core.storage.exceptions.StorageException;
import tij.jca.infrastructure.storage.h2.H2DatabaseConstants;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;
import tij.jca.infrastructure.storage.h2.helper.SQLBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class H2ChatRoomRepository extends AbstractH2Repository implements IChatRoomRepository {
    public H2ChatRoomRepository(H2StorageTransaction transaction) {
        super(transaction);
    }

    @Override
    public List<ConversationID> findByServerId(String serverId) {
        String sql = SQLBuilder
                .select(H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID)
                .from(H2DatabaseConstants.TABLE__SERVER_CHATROOM_MAPPING)
                .where(H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__SERVER_ID + " = ?")
                .build();

        List<ConversationID> chatRoomIds = new ArrayList<>();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, serverId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    chatRoomIds.add(new ConversationID(
                            resultSet.getString(H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID)
                    ));
                }
            }

            return List.copyOf(chatRoomIds);
        } catch (Exception e) {
            throw new StorageException("Failed to find server chatrooms.", e);
        }
    }

    @Override
    public Set<UserID> findUserIds(ConversationID chatRoomId) {
        String sql = SQLBuilder
                .select(H2DatabaseConstants.COLUMN__CHATROOM_USER_MAPPING__USER_ID)
                .from(H2DatabaseConstants.TABLE__CHATROOM_USER_MAPPING)
                .where(H2DatabaseConstants.COLUMN__CHATROOM_USER_MAPPING__CHATROOM_ID + " = ?")
                .build();

        Set<UserID> userIds = new HashSet<>();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, chatRoomId.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    userIds.add(new UserID(
                            resultSet.getString(H2DatabaseConstants.COLUMN__CHATROOM_USER_MAPPING__USER_ID)
                    ));
                }
            }

            return Set.copyOf(userIds);
        } catch (Exception e) {
            throw new StorageException("Failed to find chatroom users.", e);
        }
    }

    @Override
    public void addUser(ConversationID chatRoomId, UserID userId) {
        String sql = SQLBuilder
                .insertInto(H2DatabaseConstants.TABLE__CHATROOM_USER_MAPPING)
                .columns(
                        H2DatabaseConstants.COLUMN__CHATROOM_USER_MAPPING__CHATROOM_ID,
                        H2DatabaseConstants.COLUMN__CHATROOM_USER_MAPPING__USER_ID
                )
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, chatRoomId.id());
            statement.setString(2, userId.id());
        });
    }

    @Override
    public void removeUser(ConversationID chatRoomId, UserID userId) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__CHATROOM_USER_MAPPING)
                .where(H2DatabaseConstants.COLUMN__CHATROOM_USER_MAPPING__CHATROOM_ID + " = ?")
                .and(H2DatabaseConstants.COLUMN__CHATROOM_USER_MAPPING__USER_ID + " = ?")
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, chatRoomId.id());
            statement.setString(2, userId.id());
        });
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        String sql = SQLBuilder
                .mergeInto(H2DatabaseConstants.COLUMN__CHATROOMS__ID)
                .columns(
                        H2DatabaseConstants.COLUMN__CHATROOMS__ID,
                        H2DatabaseConstants.COLUMN__CHATROOMS__NAME
                )
                .key(H2DatabaseConstants.COLUMN__CHATROOMS__ID)
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, chatRoom.id().id());
            statement.setString(2, chatRoom.name());
        });

        return chatRoom;
    }

    @Override
    public Optional<ChatRoom> findById(ConversationID conversationID) {
        String sql = SQLBuilder
                .select(
                        H2DatabaseConstants.COLUMN__CHATROOMS__ID,
                        H2DatabaseConstants.COLUMN__CHATROOMS__NAME
                )
                .from(H2DatabaseConstants.TABLE__CHAT_ROOMS)
                .where(H2DatabaseConstants.COLUMN__CHATROOMS__ID + " = ?")
                .build();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, conversationID.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(readChatRoom(resultSet));
            }
        } catch (Exception e) {
            throw new StorageException("Failed to find chatroom.", e);
        }
    }

    @Override
    public boolean existsById(ConversationID conversationID) {
        String sql = SQLBuilder
                .select("1")
                .from(H2DatabaseConstants.TABLE__CHAT_ROOMS)
                .where(H2DatabaseConstants.COLUMN__CHATROOMS__ID + " = ?")
                .build();

        return exists(sql, statement ->
                statement.setString(1, conversationID.id())
        );
    }

    @Override
    public void deleteById(ConversationID conversationID) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__CHAT_ROOMS)
                .where(H2DatabaseConstants.COLUMN__CHATROOMS__ID + " = ?")
                .build();

        executeUpdate(sql, statement ->
                statement.setString(1, conversationID.id())
        );
    }

    private static ChatRoom readChatRoom(ResultSet resultSet) throws Exception {
        return new ChatRoom(
                new ConversationID(resultSet.getString(H2DatabaseConstants.COLUMN__CHATROOMS__ID)),
                resultSet.getString(H2DatabaseConstants.COLUMN__CHATROOMS__NAME)
        );
    }
}
