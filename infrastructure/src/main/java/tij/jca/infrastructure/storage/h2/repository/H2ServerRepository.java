package tij.jca.infrastructure.storage.h2.repository;

import tij.jca.core.entities.Server;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;
import tij.jca.core.repositories.IServerRepository;
import tij.jca.core.storage.exceptions.StorageException;
import tij.jca.infrastructure.storage.h2.H2DatabaseConstants;
import tij.jca.infrastructure.storage.h2.H2StorageTransaction;
import tij.jca.infrastructure.storage.h2.helper.SQLBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * H2-backed implementation of {@link IServerRepository}.
 *
 * <p>
 * This repository stores server records and maintains the membership and chatroom
 * relationships between servers, users, and conversations.
 * </p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class H2ServerRepository extends AbstractH2Repository implements IServerRepository {
    /**
     * Creates a repository that uses the provided H2 transaction context.
     *
     * @param transaction the storage transaction used to access the database
     */
    public H2ServerRepository(H2StorageTransaction transaction) {
        super(transaction);
    }

    @Override
    public List<Server> findByName(String name) {
        String sql = SQLBuilder
                .select(
                        H2DatabaseConstants.COLUMN__SERVERS__ID,
                        H2DatabaseConstants.COLUMN__SERVERS__NAME
                )
                .from(H2DatabaseConstants.TABLE__SERVERS)
                .where(H2DatabaseConstants.COLUMN__SERVERS__NAME + " = ?")
                .build();

        List<Server> servers = new ArrayList<>();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    servers.add(readServer(resultSet));
                }
            }

            return List.copyOf(servers);
        } catch (Exception e) {
            throw new StorageException("Failed to find servers.", e);
        }
    }

    @Override
    public Set<UserID> findUserIds(ServerID serverId) {
        String sql = SQLBuilder
                .select(H2DatabaseConstants.COLUMN__USER_SERVER_MAPPING__USER_ID)
                .from(H2DatabaseConstants.TABLE__USER_SERVER_MAPPING)
                .where(H2DatabaseConstants.COLUMN__USER_SERVER_MAPPING__SERVER_ID + " = ?")
                .build();

        return findIds(sql, serverId.id(), H2DatabaseConstants.COLUMN__USER_SERVER_MAPPING__USER_ID)
                .stream()
                .map(UserID::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public List<ConversationID> findChatRoomIds(ServerID serverId) {
        String sql = SQLBuilder
                .select(H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID)
                .from(H2DatabaseConstants.TABLE__SERVER_CHATROOM_MAPPING)
                .where(H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__SERVER_ID + " = ?")
                .build();

        return findIds(sql, serverId.id(), H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID)
                .stream()
                .map(ConversationID::new)
                .toList();
    }

    @Override
    public void addUser(ServerID serverId, UserID userId) {
        String sql = SQLBuilder
                .insertInto(H2DatabaseConstants.TABLE__USER_SERVER_MAPPING)
                .columns(
                        H2DatabaseConstants.COLUMN__USER_SERVER_MAPPING__USER_ID,
                        H2DatabaseConstants.COLUMN__USER_SERVER_MAPPING__SERVER_ID
                )
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, userId.id());
            statement.setString(2, serverId.id());
        });
    }

    @Override
    public void removeUser(ServerID serverId, UserID userId) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__USER_SERVER_MAPPING)
                .where(H2DatabaseConstants.COLUMN__USER_SERVER_MAPPING__SERVER_ID + " = ?")
                .and(H2DatabaseConstants.COLUMN__USER_SERVER_MAPPING__USER_ID + " = ?")
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, serverId.id());
            statement.setString(2, userId.id());
        });
    }

    @Override
    public void attachChatRoom(ServerID serverId, ConversationID chatRoomId) {
        String sql = SQLBuilder
                .insertInto(H2DatabaseConstants.TABLE__SERVER_CHATROOM_MAPPING)
                .columns(
                        H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__SERVER_ID,
                        H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID
                )
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, serverId.id());
            statement.setString(2, chatRoomId.id());
        });
    }

    @Override
    public void detachChatRoom(ServerID serverId, ConversationID chatRoomId) {
        String sql = SQLBuilder
                .deleteFrom(H2DatabaseConstants.TABLE__SERVER_CHATROOM_MAPPING)
                .where(H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__SERVER_ID + " = ?")
                .and(H2DatabaseConstants.COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID + " = ?")
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, serverId.id());
            statement.setString(2, chatRoomId.id());
        });
    }

    @Override
    public Server save(Server server) {
        String sql = SQLBuilder
                .mergeInto(H2DatabaseConstants.TABLE__SERVERS)
                .columns(
                        H2DatabaseConstants.COLUMN__SERVERS__ID,
                        H2DatabaseConstants.COLUMN__SERVERS__NAME
                )
                .key(H2DatabaseConstants.COLUMN__SERVERS__ID)
                .build();

        executeUpdate(sql, statement -> {
            statement.setString(1, server.id().id());
            statement.setString(2, server.name());
        });

        return server;
    }

    @Override
    public Optional<Server> findById(ServerID serverID) {
        String sql = SQLBuilder
                .select(
                        H2DatabaseConstants.COLUMN__SERVERS__ID,
                        H2DatabaseConstants.COLUMN__SERVERS__NAME
                )
                .from(H2DatabaseConstants.TABLE__SERVERS)
                .where(H2DatabaseConstants.COLUMN__SERVERS__ID + " = ?")
                .build();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, serverID.id());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(readServer(resultSet));
            }
        } catch (Exception e) {
            throw new StorageException("Failed to find server.", e);
        }
    }

    @Override
    public boolean existsById(ServerID serverID) {
        return false;
    }

    @Override
    public void deleteById(ServerID serverID) {

    }

    /**
     * Maps the current room from the result set into a {@link Server} domain object.
     *
     * @param resultSet the result set position on a server record
     * @return the mapped server instance
     * @throws Exception if the room cannot be read
     */
    private static Server readServer(ResultSet resultSet) throws Exception {
        return new Server(
                new ServerID(resultSet.getString(H2DatabaseConstants.COLUMN__SERVERS__ID)),
                resultSet.getString(H2DatabaseConstants.COLUMN__SERVERS__NAME)
        );
    }

    /**
     * Loads string identifiers for a relationship query.
     *
     * @param sql the SQL to execute
     * @param value the value used in the WHERE clause
     * @param column the column to read from the result set
     * @return the matching identifiers
     * @throws StorageException if the query fails
     */
    private List<String> findIds(String sql, String value, String column) {
        List<String> ids = new ArrayList<>();

        try (PreparedStatement statement = prepare(sql)) {
            statement.setString(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getString(column));
                }
            }

            return List.copyOf(ids);
        } catch (Exception e) {
            throw new StorageException("Failed to query server relationships.", e);
        }
    }
}
