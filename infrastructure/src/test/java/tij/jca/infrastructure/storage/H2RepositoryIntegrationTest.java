package tij.jca.infrastructure.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tij.jca.core.entities.ChatRoom;
import tij.jca.core.entities.Device;
import tij.jca.core.entities.DeviceType;
import tij.jca.core.entities.Message;
import tij.jca.core.entities.Server;
import tij.jca.core.entities.User;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;
import tij.jca.core.storage.exceptions.DuplicateEntryException;
import tij.jca.infrastructure.storage.h2.H2StorageEngine;
import tij.jca.infrastructure.storage.h2.H2TransactionManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

class H2RepositoryIntegrationTest {

    private H2StorageEngine storageEngine;
    private H2TransactionManager transactionManager;

    @BeforeEach
    void setUp() throws Exception {
        Path databasePath = Files.createTempDirectory("jca-h2-repository-")
                .resolve("data");

        storageEngine = new H2StorageEngine(databasePath);
        storageEngine.open();
        transactionManager = new H2TransactionManager(storageEngine);
    }

    @AfterEach
    void tearDown() {
        storageEngine.close();
    }

    @Test
    void usersAndDevicesSupportCrudAndAssociations() {
        UserID userId = new UserID(identifier(1));
        DeviceID deviceId = new DeviceID(identifier(2));
        User user = new User(userId, "alice", "Alice");
        Device device = new Device(deviceId, DeviceType.DESKTOP);

        transactionManager.execute(scope -> {
            scope.users().save(user);
            scope.devices().save(device);
            scope.users().attachDevice(userId, deviceId);
            return null;
        });

        User updatedUser = new User(userId, "alice", "Alice Cooper");
        Device updatedDevice = new Device(deviceId, DeviceType.MOBILE);

        transactionManager.execute(scope -> {
            scope.users().save(updatedUser);
            scope.devices().save(updatedDevice);
            return null;
        });

        transactionManager.execute(scope -> {
            Assertions.assertTrue(scope.users().existsById(userId));
            User persistedUser = scope.users().findById(userId).orElseThrow();
            Assertions.assertEquals(updatedUser.id(), persistedUser.id());
            Assertions.assertEquals(updatedUser.name(), persistedUser.name());
            Assertions.assertEquals(
                    updatedUser.globalDisplayName(),
                    persistedUser.globalDisplayName()
            );
            Assertions.assertEquals(
                    List.of(userId),
                    scope.users().findByUsername("alice").stream()
                            .map(User::id)
                            .toList()
            );
            Assertions.assertEquals(
                    updatedDevice,
                    scope.devices().findById(deviceId).orElseThrow()
            );
            Assertions.assertEquals(
                    List.of(updatedDevice),
                    scope.devices().findByUserId(userId)
            );
            Assertions.assertEquals(
                    java.util.Set.of(deviceId),
                    scope.users().findDeviceIds(userId)
            );
            return null;
        });

        transactionManager.execute(scope -> {
            scope.users().detachDevice(userId, deviceId);
            scope.devices().deleteById(deviceId);
            scope.users().deleteById(userId);
            return null;
        });

        transactionManager.execute(scope -> {
            Assertions.assertFalse(scope.users().existsById(userId));
            Assertions.assertTrue(scope.users().findById(userId).isEmpty());
            Assertions.assertFalse(scope.devices().existsById(deviceId));
            return null;
        });
    }

    @Test
    void serversAndConversationsSupportCrudAndAssociations() {
        UserID userId = new UserID(identifier(1));
        ServerID serverId = new ServerID(identifier(2));
        ConversationID conversationId = new ConversationID(identifier(3));
        User user = new User(userId, "alice", "Alice");
        Server server = new Server(serverId, "JCA");
        ChatRoom conversation = new ChatRoom(conversationId, "general");

        transactionManager.execute(scope -> {
            scope.users().save(user);
            scope.servers().save(server);
            scope.conversations().save(conversation);
            scope.servers().addUser(serverId, userId);
            scope.servers().attachConversation(serverId, conversationId);
            scope.conversations().addUser(conversationId, userId);
            return null;
        });

        Server updatedServer = new Server(serverId, "JCA Updated");
        ChatRoom updatedConversation = new ChatRoom(
                conversationId,
                "general-updated"
        );

        transactionManager.execute(scope -> {
            scope.servers().save(updatedServer);
            scope.conversations().save(updatedConversation);
            return null;
        });

        transactionManager.execute(scope -> {
            Assertions.assertTrue(scope.servers().existsById(serverId));
            Assertions.assertEquals(
                    List.of(serverId),
                    scope.servers().findByName("JCA Updated").stream()
                            .map(Server::id)
                            .toList()
            );
            Assertions.assertEquals(
                    java.util.Set.of(userId),
                    scope.servers().findUserIds(serverId)
            );
            Assertions.assertEquals(
                    List.of(conversationId),
                    scope.servers().findChatRoomIds(serverId)
            );
            Assertions.assertEquals(
                    List.of(conversationId),
                    scope.conversations().findByServerId(serverId)
            );
            Assertions.assertEquals(
                    java.util.Set.of(userId),
                    scope.conversations().findUserIds(conversationId)
            );
            Assertions.assertEquals(
                    "general-updated",
                    scope.conversations().findById(conversationId)
                            .orElseThrow()
                            .name()
            );
            return null;
        });

        transactionManager.execute(scope -> {
            scope.conversations().removeUser(conversationId, userId);
            scope.servers().removeUser(serverId, userId);
            scope.servers().detachChatRoom(serverId, conversationId);
            scope.conversations().deleteById(conversationId);
            scope.servers().deleteById(serverId);
            return null;
        });

        transactionManager.execute(scope -> {
            Assertions.assertFalse(scope.servers().existsById(serverId));
            Assertions.assertTrue(
                    scope.conversations().findById(conversationId).isEmpty()
            );
            return null;
        });
    }

    @Test
    void messagesSupportCrudChatRoomAssociationsAndChronologicalRetrieval() {
        UserID userId = new UserID(identifier(1));
        ConversationID conversationId = new ConversationID(identifier(2));
        Message firstMessage = new Message(
                new MessageID(identifier(3)),
                userId,
                Instant.parse("2026-10-24T10:00:00Z"),
                "First"
        );
        Message secondMessage = new Message(
                new MessageID(identifier(4)),
                userId,
                Instant.parse("2026-10-24T10:01:00Z"),
                "Second"
        );

        transactionManager.execute(scope -> {
            scope.users().save(new User(userId, "alice", "Alice"));
            scope.conversations().save(
                    new ChatRoom(conversationId, "general")
            );
            scope.messages().save(secondMessage);
            scope.messages().save(firstMessage);
            scope.messages().attachToChatRoom(
                    firstMessage.id(),
                    conversationId
            );
            scope.messages().attachToChatRoom(
                    secondMessage.id(),
                    conversationId
            );
            return null;
        });

        Message updatedFirstMessage = new Message(
                firstMessage.id(),
                firstMessage.authorId(),
                firstMessage.date(),
                "Updated first"
        );

        transactionManager.execute(scope -> {
            scope.messages().save(updatedFirstMessage);
            return null;
        });

        transactionManager.execute(scope -> {
            Assertions.assertEquals(
                    updatedFirstMessage,
                    scope.messages().findById(firstMessage.id()).orElseThrow()
            );
            Assertions.assertEquals(
                    List.of(updatedFirstMessage, secondMessage),
                    scope.messages().findByChatRoomId(conversationId)
            );
            return null;
        });

        transactionManager.execute(scope -> {
            scope.messages().detachFromChatRoom(
                    firstMessage.id(),
                    conversationId
            );
            scope.messages().deleteById(firstMessage.id());
            return null;
        });

        transactionManager.execute(scope -> {
            Assertions.assertFalse(
                    scope.messages().existsById(firstMessage.id())
            );
            Assertions.assertEquals(
                    List.of(secondMessage),
                    scope.messages().findByChatRoomId(conversationId)
            );
            return null;
        });
    }

    @Test
    void duplicateAssociationIsTranslatedAndRollsBackTheWholeTransaction() {
        UserID userId = new UserID(identifier(1));
        DeviceID deviceId = new DeviceID(identifier(2));

        Assertions.assertThrows(
                DuplicateEntryException.class,
                () -> transactionManager.execute(scope -> {
                    scope.users().save(new User(userId, "alice", "Alice"));
                    scope.devices().save(
                            new Device(deviceId, DeviceType.DESKTOP)
                    );
                    scope.users().attachDevice(userId, deviceId);
                    scope.users().attachDevice(userId, deviceId);
                    return null;
                })
        );

        transactionManager.execute(scope -> {
            Assertions.assertFalse(scope.users().existsById(userId));
            Assertions.assertFalse(scope.devices().existsById(deviceId));
            return null;
        });
    }

    @Test
    void failuresRollBackChangesAcrossRepositories() {
        UserID userId = new UserID(identifier(1));
        ServerID serverId = new ServerID(identifier(2));

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> transactionManager.execute(scope -> {
                    scope.users().save(new User(userId, "alice", "Alice"));
                    scope.servers().save(new Server(serverId, "JCA"));
                    scope.servers().addUser(serverId, userId);

                    throw new IllegalStateException("simulated failure");
                })
        );

        transactionManager.execute(scope -> {
            Assertions.assertFalse(scope.users().existsById(userId));
            Assertions.assertFalse(scope.servers().existsById(serverId));
            return null;
        });
    }

    private static String identifier(int value) {
        return "%032d".formatted(value);
    }
}
