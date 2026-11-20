package tij.jca.application.services;

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
import tij.jca.core.storage.exceptions.EntityNotFoundException;

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

    @BeforeEach
    void setUp() {
        fakeUserRepository = new FakeUserRepository();
        fakeDeviceRepository = new FakeDeviceRepository();
        fakeServerRepository = new FakeServerRepository();
        fakeConversationRepository = new FakeConversationRepository();
        fakeMessageRepository = new FakeMessageRepository();

        userService = new UserService(fakeUserRepository, fakeDeviceRepository);
        serverService = new ServerService(fakeServerRepository);
        conversationService = new ConversationService(fakeConversationRepository);
        messageService = new MessageService(fakeMessageRepository);
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

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
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

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> serverService.addUser(missingServerId, new UserID("user-1"))
        );

        Assertions. assertTrue(ex.getMessage().contains("not found"));
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

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> conversationService.addUser(missingRoom, new UserID("user-1"))
        );

        Assertions.assertTrue(ex.getMessage().contains("not found"));
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

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> messageService.deleteMessage(missingId, new ConversationID("room-1"))
        );

        Assertions.assertTrue(ex.getMessage().contains("not found"));
    }
}
