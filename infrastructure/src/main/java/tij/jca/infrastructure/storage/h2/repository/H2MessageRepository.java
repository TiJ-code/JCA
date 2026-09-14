package tij.jca.infrastructure.storage.h2.repository;

import tij.jca.core.entities.Message;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IMessageRepository;
import tij.jca.core.storage.exceptions.StorageException;
import tij.jca.infrastructure.storage.h2.H2DatabaseConstants;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;
import tij.jca.infrastructure.storage.h2.helper.SQLBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public final class H2MessageRepository extends AbstractH2Repository implements IMessageRepository {
    public H2MessageRepository(H2StorageTransaction transaction) {
        super(transaction);
    }

    @Override
    public List<Message> findByChatRoomId(ConversationID chatRoomId) {
        String sql = SQLBuilder
                .select(
                        "m." + H2DatabaseConstants.COLUMN__MESSAGES__ID,
                        "m." + H2DatabaseConstants.COLUMN__MESSAGES__AUTHOR_ID,
                        "m." + H2DatabaseConstants.COLUMN__MESSAGES__DATE,
                        "m." + H2DatabaseConstants.COLUMN__MESSAGES__TEXT
                )
                .from(H2DatabaseConstants.TABLE__MESSAGES + " m")
                .innerJoin(
                        H2DatabaseConstants.TABLE__MESSAGE_CHATROOM_MAPPING + " mapping",
                        "mapping." + H2DatabaseConstants.COLUMN__MESSAGE_CHATROOM_MAPPING__MESSAGE_ID + " = m." + H2DatabaseConstants.COLUMN__MESSAGES__ID
                )
                .where("mapping." + H2DatabaseConstants.COLUMN__MESSAGE_CHATROOM_MAPPING__CHATROOM_ID + " = ?")
                .orderBy("m." + H2DatabaseConstants.COLUMN__MESSAGES__DATE)
                .build();
        return List.of();
    }

    @Override
    public void attachToChatRoom(MessageID messageID, ConversationID chatRoomId) {
        String sql = SQLBuilder
                .insertInto(H2DatabaseConstants.TABLE__MESSAGE_CHATROOM_MAPPING)
                .columns(
                        H2DatabaseConstants.COLUMN__MESSAGE_CHATROOM_MAPPING__MESSAGE_ID,
                        H2DatabaseConstants.COLUMN__MESSAGE_CHATROOM_MAPPING__CHATROOM_ID
                )
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, messageID.id());
            statement.setString(2, chatRoomId.id());
        });
    }

    @Override
    public void detachFromChatRoom(MessageID messageID, ConversationID chatRoomId) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__MESSAGE_CHATROOM_MAPPING)
                .where(H2DatabaseConstants.COLUMN__MESSAGE_CHATROOM_MAPPING__MESSAGE_ID + " = ?")
                .and(H2DatabaseConstants.COLUMN__MESSAGE_CHATROOM_MAPPING__CHATROOM_ID + " = ?")
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, messageID.id());
            statement.setString(2, chatRoomId.id());
        });
    }

    @Override
    public Message save(Message message) {
        String sql = SQLBuilder
                .mergeInto(H2DatabaseConstants.TABLE__MESSAGES)
                .columns(
                        H2DatabaseConstants.COLUMN__MESSAGES__ID,
                        H2DatabaseConstants.COLUMN__MESSAGES__AUTHOR_ID,
                        H2DatabaseConstants.COLUMN__MESSAGES__DATE,
                        H2DatabaseConstants.COLUMN__MESSAGES__TEXT
                )
                .key(H2DatabaseConstants.COLUMN__MESSAGES__ID)
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, message.id().id());
            statement.setString(2, message.authorId().id());
            statement.setObject(3, message.date());
            statement.setString(4, message.text());
        });

        return message;
    }

    @Override
    public Optional<Message> findById(MessageID messageID) {
        String sql = SQLBuilder
                .select(
                        H2DatabaseConstants.COLUMN__MESSAGES__ID,
                        H2DatabaseConstants.COLUMN__MESSAGES__AUTHOR_ID,
                        H2DatabaseConstants.COLUMN__MESSAGES__DATE,
                        H2DatabaseConstants.COLUMN__MESSAGES__TEXT
                )
                .from(H2DatabaseConstants.TABLE__MESSAGES)
                .where(H2DatabaseConstants.COLUMN__MESSAGES__ID + " = ?")
                .build();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, messageID.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(readMessage(resultSet));
            }
        } catch (Exception e) {
            throw new StorageException("Failed to find message.", e);
        }
    }

    @Override
    public boolean existsById(MessageID messageID) {
        String sql = SQLBuilder
                .select("1")
                .from(H2DatabaseConstants.TABLE__MESSAGES)
                .where(H2DatabaseConstants.COLUMN__MESSAGES__ID + " = ?")
                .build();

        return exists(sql, statement -> statement.setString(1, messageID.id()));
    }

    @Override
    public void deleteById(MessageID messageID) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__MESSAGES)
                .where(H2DatabaseConstants.COLUMN__MESSAGES__ID + " = ?")
                .build();

        executeUpdate(sql, statement -> statement.setString(1, messageID.id()));
    }

    private static Message readMessage(ResultSet resultSet) throws Exception {
        return new Message(
                new MessageID(resultSet.getString(H2DatabaseConstants.COLUMN__MESSAGES__ID)),
                new UserID(resultSet.getString(H2DatabaseConstants.COLUMN__MESSAGES__AUTHOR_ID)),
                resultSet.getTimestamp(H2DatabaseConstants.COLUMN__MESSAGES__DATE).toInstant(),
                resultSet.getString(H2DatabaseConstants.COLUMN__MESSAGES__TEXT)
        );
    }
}
