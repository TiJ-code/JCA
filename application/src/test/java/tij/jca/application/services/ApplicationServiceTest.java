package tij.jca.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.application.exceptions.ValidationException;
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

import java.time.Instant;
import java.util.List;

class ApplicationServiceTest {

    private UserService userService;
    private ServerService serverService;
    private ConversationService conversationService;
    private MessageService messageService;

    private FakeUserRepository fakeUserRepository;
    private FakeDeviceRepository fakeDeviceRepository;
    private FakeServerRepository fakeServerRepository;
    private FakeConversationRepository fakeConversationRepository;
    private FakeMessageRepository fakeMessageRepository;
    private FakeTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        fakeUserRepository = new FakeUserRepository();
        fakeDeviceRepository = new FakeDeviceRepository();
        fakeServerRepository = new FakeServerRepository();
        fakeConversationRepository = new FakeConversationRepository();
        fakeMessageRepository = new FakeMessageRepository();

        transactionManager = new FakeTransactionManager(
                new FakeTransactionScope(
                        fakeUserRepository,
                        fakeDeviceRepository,
                        fakeServerRepository,
                        fakeConversationRepository,
                        fakeMessageRepository
                )
        );

        userService = new UserService(transactionManager);
        serverService = new ServerService(transactionManager);
        conversationService = new ConversationService(transactionManager);
        messageService = new MessageService(transactionManager);
    }

    @Test
    void registerUser_persistsUser() {
        User user = userService.registerUser("alice", "Alice");

        Assertions.assertNotNull(user.id());
        Assertions.assertEquals("alice", user.name());
        Assertions.assertEquals("Alice", user.globalDisplayName());
        Assertions.assertTrue(fakeUserRepository.existsById(user.id()));
    }

    @Test
    void registerUser_throwsWhenUsernameTaken() {
        userService.registerUser("alice", "Alice");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser("alice", "Another Alice")
        );

        Assertions.assertTrue(ex.getMessage().contains("Username already exists"));
    }

    @Test
    void userService_rejectsInvalidArguments() {
        Device device = new Device(new DeviceID("device-1"), DeviceType.DESKTOP);
        UserID userId = new UserID("user-1");
        DeviceID deviceId = new DeviceID("device-1");

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> userService.registerUser(" ", "Alice")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> userService.registerUser("alice", " ")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> userService.findUser(null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> userService.attachDevice(null, device)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> userService.attachDevice(userId, null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> userService.detachDevice(null, deviceId)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> userService.detachDevice(userId, null)
                )
        );
    }

    @Test
    void attachDevice_persistsDeviceAndUserRelationship() {
        User user = userService.registerUser("alice", "Alice");
        Device device = new Device(new DeviceID("device-1"), DeviceType.DESKTOP);

        User updated = userService.attachDevice(user.id(), device);

        Assertions.assertTrue(fakeDeviceRepository.existsById(device.id()));
        Assertions.assertTrue(fakeUserRepository.findDeviceIds(user.id()).contains(device.id()));
        Assertions.assertTrue(updated.deviceIds().contains(device.id()));
    }

    @Test
    void detachDevice_throwsWhenUserMissing() {
        UserID missingUserId = new UserID("missing-user");

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> userService.detachDevice(missingUserId, new DeviceID("device-1"))
        );

        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void createServer_persistsServer() {
        Server server = serverService.createServer("JCA");

        Assertions.assertNotNull(server.id());
        Assertions.assertEquals("JCA", server.name());
        Assertions.assertTrue(fakeServerRepository.existsById(server.id()));
    }

    @Test
    void addUser_throwsWhenServerMissing() {
        ServerID missingServerId = new ServerID("missing-server");

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> serverService.addUser(missingServerId, new UserID("user-1"))
        );

        Assertions. assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void serverService_rejectsInvalidArguments() {
        ServerID serverId = new ServerID("server-1");
        UserID userId = new UserID("user-1");
        ConversationID conversationId = new ConversationID("room-1");

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> serverService.createServer(" ")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> serverService.findServer(null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> serverService.findServersByName(" ")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> serverService.addUser(null, userId)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> serverService.addUser(serverId, null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> serverService.attachConversation(null, conversationId)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> serverService.attachConversation(serverId, null)
                )
        );
    }

    @Test
    void attachChatRoom_registersAssociation() {
        Server server = serverService.createServer("JCA");
        ChatRoom chatRoom = conversationService.createConversation("general");

        serverService.attachConversation(server.id(), chatRoom.id());

        Assertions.assertTrue(fakeServerRepository.findChatRoomIds(server.id()).contains(chatRoom.id()));
    }

    @Test
    void createChatRoom_persistsChatRoom() {
        ChatRoom chatRoom = conversationService.createConversation("general");

        Assertions.assertNotNull(chatRoom.id());
        Assertions.assertEquals("general", chatRoom.name());
        Assertions.assertTrue(fakeConversationRepository.existsById(chatRoom.id()));
    }

    @Test
    void addUserToChatRoom_throwsWhenRoomMissing() {
        ConversationID missingRoom = new ConversationID("missing-room");

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> conversationService.addUser(missingRoom, new UserID("user-1"))
        );

        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void conversationService_rejectsInvalidArguments() {
        ConversationID conversationId = new ConversationID("room-1");
        ServerID serverId = new ServerID("server-1");
        UserID userId = new UserID("user-1");

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> conversationService.createConversation(" ")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> conversationService.findChatRoom(null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> conversationService.findByServerId(null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> conversationService.addUser(null, userId)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> conversationService.addUser(conversationId, null)
                )
        );
    }

    @Test
    void sendMessage_persistsAndAssociatesWithChatRoom() {
        ConversationID roomId = new ConversationID("room-1");
        UserID authorId = new UserID("user-1");

        Message message = messageService.sendMessage(roomId, authorId, "hello");

        Assertions.assertNotNull(message.id());
        Assertions.assertEquals("hello", message.text());
        Assertions.assertEquals(authorId, message.authorId());
        Assertions.assertTrue(fakeMessageRepository.existsById(message.id()));
        Assertions.assertTrue(fakeMessageRepository.findByChatRoomId(roomId).contains(message));
    }

    @Test
    void findMessages_returnsChatRoomMessages() {
        ConversationID roomId = new ConversationID("room-1");
        UserID authorId = new UserID("user-1");

        Message first = new Message(new MessageID("m1"), authorId, Instant.now(), "first");
        Message second = new Message(new MessageID("m2"), authorId, Instant.now(), "second");

        fakeMessageRepository.save(first);
        fakeMessageRepository.attachToChatRoom(first.id(), roomId);
        fakeMessageRepository.save(second);
        fakeMessageRepository.attachToChatRoom(second.id(), roomId);

        List<Message> messages = messageService.findMessages(roomId);

        Assertions.assertEquals(2, messages.size());
        Assertions.assertTrue(messages.stream().anyMatch(m -> m.id().equals(first.id())));
        Assertions.assertTrue(messages.stream().anyMatch(m -> m.id().equals(second.id())));
    }

    @Test
    void deleteMessage_removesAssociationAndEntity() {
        ConversationID roomId = new ConversationID("room-1");
        UserID authorId = new UserID("user-1");

        Message message = messageService.sendMessage(roomId, authorId, "bye");

        messageService.deleteMessage(message.id(), roomId);

        Assertions.assertFalse(fakeMessageRepository.existsById(message.id()));
        Assertions.assertFalse(fakeMessageRepository.findByChatRoomId(roomId).contains(message));
    }

    @Test
    void deleteMessage_throwsWhenMessageMissing() {
        MessageID missingId = new MessageID("missing-message");

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> messageService.deleteMessage(missingId, new ConversationID("room-1"))
        );

        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void messageService_rejectsInvalidArguments() {
        ConversationID conversationId = new ConversationID("room-1");
        UserID userId = new UserID("user-1");
        MessageID messageId = new MessageID("message-1");

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> messageService.sendMessage(null, userId, "hello")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> messageService.sendMessage(conversationId, null, "hello")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> messageService.sendMessage(conversationId, userId, " ")
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> messageService.findMessage(null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> messageService.findMessages(null)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> messageService.deleteMessage(null, conversationId)
                ),
                () -> Assertions.assertThrows(
                        ValidationException.class,
                        () -> messageService.deleteMessage(messageId, null)
                )
        );
    }
}
